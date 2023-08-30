import {Injectable} from '@angular/core';
import {map, mergeMap, reduce, switchMap} from 'rxjs/operators';
import {PagedSelfdataDataset, SelfdataDataset} from '../../../personal-space/components/selfdata-datasets-table/selfdata-dataset.interface';
import {EMPTY, forkJoin, from, Observable, of} from 'rxjs';
import {
    Address,
    FieldType,
    MatchingData,
    PagedSelfdataDatasetList,
    PagedSelfdataInformationRequestList,
    SelfdataDataset as BackSelfdataDataset,
    SelfdataInformationRequest,
    SelfdataInformationRequestSearchCriteria,
    SelfdataService,
    TaskService
} from '../../../selfdata/selfdata-api';
import {Metadata} from '../../../api-kaccess';
import {MetadataUtils} from '../../../shared/utils/metadata-utils';
import {SelfdataRequestType} from './selfdata-request-type';
import {SelfdataDatasetLatestRequests} from './selfdata-dataset-latest-requests';
import {SelfdataInformationRequestStatus} from '../../../selfdata/selfdata-model';
import {BarChartData} from './tpbcData.interface';
import {LanguageService} from '../../../i18n/language.service';
import {GdataDataInterface, GenericDataObject} from './gdataData.interface';
import {SelfdataAttachmentService} from '../selfdata-attachment.service';
import {MatchingDataView} from '../../../personal-space/components/matching-data-card/matching-data-view';
import {SelfdataRvaService} from '../rva/selfdata/selfdata-rva.service';

@Injectable({
    providedIn: 'root'
})
export class SelfdataDatasetService {

    constructor(
        private readonly selfdataService: SelfdataService,
        private readonly languageService: LanguageService,
        private readonly taskService: TaskService,
        private readonly selfdataAttachmentService: SelfdataAttachmentService,
        private readonly addressMetierService: SelfdataRvaService,
    ) {
    }

    /**
     * Recherche de toutes les dernières demandes de l'utilisateur connecté
     * @param metadata le JDD selfdata concerné par les demandes
     */
    searchAllMyLatestRequests(metadata: Metadata): Observable<SelfdataDatasetLatestRequests> {

        // Si JDD selfdata autorise la suppression alors on va chercher les demandes de suppression
        let deletionObservable: Observable<SelfdataInformationRequest> = of(null);
        if (MetadataUtils.isApplicableForDeletion(metadata)) {
            deletionObservable = this.searchMyLatestRequest(metadata, SelfdataRequestType.DELETION);
        }

        return forkJoin({
            access: this.searchMyLatestRequest(metadata, SelfdataRequestType.ACCESS),
            correction: this.searchMyLatestRequest(metadata, SelfdataRequestType.CORRECTION),
            deletion: deletionObservable
        });
    }

    /**
     * Récupération des selfdata-datasets
     */
    searchSelfdataDatasets(offset?: number, limit?: number, order?: string): Observable<PagedSelfdataDataset> {
        return this.selfdataService.searchSelfdataDatasets(offset, limit, order).pipe(
            map((page: PagedSelfdataDatasetList) => {
                const viewItems: SelfdataDataset[] = page.elements.map((element: BackSelfdataDataset) => {
                    return {
                        title: element.title,
                        process_key: element.processDefinitionKey,
                        updated_date: element.updatedDate,
                        functional_status: element.functionalStatus,
                        dataset_uuid: element.datasetUuid
                    } as SelfdataDataset;
                });
                return {
                    total: page.total,
                    elements: viewItems
                };
            })
        );
    }

    /**
     * Indique si un user ayant fait une demande d'accès peut consulter ses données
     * @param requestContainer Objet contenant les 3 dernières demandes de l'utilisateur connecté
     */
    isDataTabEmpty(requestContainer: SelfdataDatasetLatestRequests): boolean {
        const request: SelfdataInformationRequest = requestContainer?.access;
        if (!request) {
            return true;
        }
        return request.selfdata_information_request_status !== SelfdataInformationRequestStatus.Completed
            || !request.user_present;
    }

    /**
     * Recherche d'une dernière demande effectuée par l'utilisateur connecté sur le JDD fourni
     * @param metadata le JDD selfdata
     * @param type le type de demande qu'on cherche
     */
    private searchMyLatestRequest(metadata: Metadata, type: SelfdataRequestType): Observable<SelfdataInformationRequest> {

        // Erreur métier on cherche une demande sur un JDD pas self data
        if (!MetadataUtils.isSelfdata(metadata)) {
            return EMPTY;
        }

        // Aujourd'hui on ne gère que les demandes d'accès
        if (type !== SelfdataRequestType.ACCESS) {
            return of(null);
        }

        // Recherche d'une demande triée par ordre décroissant de date de MAJ
        const criteria: SelfdataInformationRequestSearchCriteria = {
            datasetUuid: metadata.global_id,
            offset: 0,
            limit: 1,
            order: '-updatedDate'
        };

        // Récupération du 1er résultat ou nul si inexistant
        return this.selfdataService.searchMySelfdataInformationRequests(criteria).pipe(
            map((page: PagedSelfdataInformationRequestList) => {
                if (page == null || page.elements == null || page.elements.length === 0) {
                    return null;
                }

                return page.elements[0];
            })
        );
    }

    getTpbcData(uuid: string): Observable<BarChartData> {
        return this.selfdataService.getTpbcData(uuid);
    }

    getGdataData(uuid: string): Observable<GdataDataInterface> {
        return this.selfdataService.getGdataData(uuid).pipe(
            map((gdata: GenericDataObject) => {
                return {
                    title: this.languageService.getTextForCurrentLanguage(gdata.legend),
                    genericDataObject: gdata
                };
            })
        );
    }

    /**
     * getMySelfdataInformationRequestMatchingData, service qui reccupère les données pivots
     * @param uuid
     */
    getMySelfdataInformationRequestMatchingData(uuid: string): Observable<MatchingData[]> {
        return this.taskService.getMySelfdataInformationRequestMatchingData(uuid).pipe(
            switchMap((matchingData: MatchingData[]) => {
                return from(matchingData.filter((data) => data.type !== FieldType.Attachment)).pipe(
                    mergeMap((data: MatchingData) => {
                        return this.getMyMatchingData(data);
                    }),
                    reduce((accumulator: MatchingDataView[], current: MatchingDataView) => {
                        accumulator.push(current);
                        return accumulator;
                    }, [])
                );
            })
        );
    }

    /**
     * getMyMatchingData, réccupere les matchingData
     * @param data
     */
    getMyMatchingData(data: MatchingData): Observable<MatchingDataView> {
        const matchingData: MatchingDataView = {};
        if (data.type === FieldType.Address) {
            const idRva = parseInt(data.value);
            return this.getAddressById(idRva).pipe(
                switchMap(address => {
                        matchingData.label = data.label;
                        matchingData.value = address.addr3;
                        return of(matchingData);
                    }
                ));
        } else {
            return of(data);
        }
    }

    getAddressById(addressId: number): Observable<Address> {
        return this.addressMetierService.getAddressById(addressId);
    }
}
