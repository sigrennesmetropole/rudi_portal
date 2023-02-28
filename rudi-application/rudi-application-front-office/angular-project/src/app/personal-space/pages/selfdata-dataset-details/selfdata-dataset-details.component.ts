import {Component, OnInit} from '@angular/core';
import {ALL_TYPES} from '../../../shared/models/title-icon-type';
import {IconRegistryService} from '../../../core/services/icon-registry.service';
import {ActivatedRoute} from '@angular/router';
import {Metadata} from '../../../api-kaccess';
import {KonsultMetierService} from '../../../core/services/konsult-metier.service';
import {SelfdataDatasetLatestRequests} from '../../../core/services/selfdata-dataset/selfdata-dataset-latest-requests';
import {SelfdataDatasetService} from '../../../core/services/selfdata-dataset/selfdata-dataset.service';
import {switchMap} from 'rxjs/operators';
import {Observable, throwError} from 'rxjs';
import {MetadataUtils} from '../../../shared/utils/metadata-utils';
import {MatchingData} from '../../../selfdata/selfdata-api';
import {MatchingDataView} from '../../components/matching-data-card/matching-data-view';

@Component({
    selector: 'app-selfdata-dataset-details',
    templateUrl: './selfdata-dataset-details.component.html',
    styleUrls: ['./selfdata-dataset-details.component.scss']
})
export class SelfdataDatasetDetailsComponent implements OnInit {

    metadata: Metadata;
    dataLoading = false;
    errorLoading = false;
    lastRequests: SelfdataDatasetLatestRequests;
    datasetUuid: string;
    matchingDataLoading = false;
    matchingData: MatchingData[] = [];
    subscriptionSucced = false;


    constructor(
        private readonly iconRegistryService: IconRegistryService,
        private readonly route: ActivatedRoute,
        private readonly konsultMetierService: KonsultMetierService,
        private readonly selfdataDatasetService: SelfdataDatasetService,
    ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    ngOnInit(): void {
        this.dataLoading = true;
        this.errorLoading = false;
        this.route.params.pipe(
            switchMap(params => this.konsultMetierService.getMetadataByUuid(params.datasetUuid)),
            switchMap(metadata => {
                this.metadata = metadata;
                this.getMySelfdataInformationRequestMatchingData(this.metadata.global_id);
                return this.getLatestRequests(this.metadata.global_id);
            })
        ).subscribe({
            next: (latestRequests: SelfdataDatasetLatestRequests) => {
                this.lastRequests = latestRequests;
                this.dataLoading = false;
                this.errorLoading = false;
            },
            error: (e) => {
                console.error(e);
                this.dataLoading = false;
                this.errorLoading = true;
            }
        });
    }

    get isDataTabEmpty(): boolean {
        return this.selfdataDatasetService.isDataTabEmpty(this.lastRequests);
    }

    /**
     * méthode qui récupère les dernières demandes d'un utilisateur sur un jdd donné
     * @param datasetUuid
     */
    getLatestRequests(datasetUuid: string): Observable<SelfdataDatasetLatestRequests> {
        this.dataLoading = true;
        this.errorLoading = false;
        return this.konsultMetierService.getMetadataByUuid(datasetUuid).pipe(
            switchMap((metadata: Metadata) => {
                if (metadata) {
                    this.metadata = metadata;
                    return this.selfdataDatasetService.searchAllMyLatestRequests(metadata);
                }
                return throwError('Aucun metadata trouvé');
            })
        );
    }

    hasDataTab(): boolean {
        return MetadataUtils.isSelfdataAccessApi(this.metadata);
    }

    /**
     * getMySelfdataInformationRequestMatchingData , méthode qui réccupere les matchingData
     * @param datasetUuid
     */
    getMySelfdataInformationRequestMatchingData(datasetUuid: string): void {
        this.matchingDataLoading = true;
        this.selfdataDatasetService.getMySelfdataInformationRequestMatchingData(datasetUuid).subscribe({
            next: (data: MatchingDataView[]) => {
                this.matchingData = data;
                this.matchingDataLoading = false;
            },
            error: (e) => {
                console.error(e);
                this.matchingDataLoading = false;
            }
        });
    }

    /**
     * Méthode qui récupère le subscriptionSucced
     */
    handleSubscriptionSuccedChanged(subscriptionSucced: boolean): void {
        this.subscriptionSucced = subscriptionSucced;
    }
}
