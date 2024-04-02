import {HttpResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Filters} from '@shared/models/filters';
import {MetadataUtils} from '@shared/utils/metadata-utils';
import {PageResultUtils} from '@shared/utils/page-result-utils';
import {Media, MediaFile, Metadata, MetadataFacets, MetadataList} from 'micro_service_modules/api-kaccess';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import mime, {Mime} from 'mime';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import customMimeDatabase from 'src/assets/mime-db/custom-mime-db';
import {AccessStatusFiltersType} from './filters/access-status-filters-type';
import {DEFAULT_VALUE as DEFAULT_ORDER_VALUE} from './filters/order-filter';

export const MAX_RESULTS_PER_PAGE = 36;
export const MAX_RESULTS_PER_REQUEST = 100;

const CRYPT_SUFFIX = '+crypt';
const UNKNOWN_EXTENSION = 'Extension inconnue du système';

@Injectable({
    providedIn: 'root'
})
export class KonsultMetierService {
    constructor(
        private readonly konsultService: KonsultService
    ) {
        KonsultMetierService.loadCustomMimeType();
    }

    private static loadCustomMimeType(): void {
        // Le fichier JSON est chargé dans notre objet sous la propriété default et est de type Module
        new Mime().define(customMimeDatabase, true);
    }

    private static getFacetsValues(facets: MetadataFacets): string[] {
        return facets.items.length ? facets.items[0].values.map(facetValue => facetValue.value) : [];
    }

    /**
     * Recuperation de la liste de metadata depuis le server
     */
    searchMetadatas(filters?: Filters, accessStatusHiddenValues?: AccessStatusFiltersType[], offset?: number, limit?: number): Observable<MetadataList> {
        const accessStatus = MetadataUtils.getAccessStatus(filters);
        if (MetadataUtils.isSelfdataHidden(accessStatusHiddenValues)) {
            accessStatus.gdprSensitive = false;
        }
        return this.konsultService.searchMetadatas(
            filters.search,
            filters.themes,
            filters.keywords,
            filters.producerNames,
            filters.dates.debut,
            filters.dates.fin,
            accessStatus.restrictedAcces,
            accessStatus.gdprSensitive,
            filters.globalIds,
            filters.producerUuids,
            offset,
            limit,
            filters.order,
        );
    }

    /**
     * Permet de d'afficher le detail d'un jdd à partir de son uuid
     */
    getMetadataByUuid(uuid: string): Observable<Metadata> {
        return this.konsultService.getMetadataById(uuid);
    }

    /**
     * Permet de d'afficher le detail de plusieurs jdd à partir de leurs globalId
     */
    getMetadatasByUuids(globalIds: string[]): Observable<Metadata[]> {
        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.searchMetadatas({
                search: '',
                themes: [],
                keywords: [],
                producerNames: [],
                dates: {
                    debut: '',
                    fin: ''
                },
                order: DEFAULT_ORDER_VALUE,
                accessStatus: null,
                globalIds,
                producerUuids: [],
            }, null, offset, MAX_RESULTS_PER_REQUEST)
        );
    }

    /**
     * Fonction qui permet de recuperer la methode downloadMetadataMedia du server
     */
    downloadMetadataMedia(globalId: string, mediaId: string): Observable<HttpResponse<Blob>> {
        return this.konsultService.downloadMetadataMedia(globalId, mediaId, 'response');
    }

    private getMetadataProducersFacets(): Observable<MetadataFacets> {
        return this.konsultService.searchMetadataFacets(['producer_organization_name']);
    }

    private getMetadataThemesFacets(): Observable<MetadataFacets> {
        return this.konsultService.searchMetadataFacets(['theme']);
    }

    getProducerNames(): Observable<string[]> {
        return this.getMetadataProducersFacets().pipe(
            map(facets => KonsultMetierService.getFacetsValues(facets))
        );
    }

    getThemeCodes(): Observable<string[]> {
        return this.getMetadataThemesFacets().pipe(
            map(facets => KonsultMetierService.getFacetsValues(facets))
        );
    }

    getMetadatasWithSameTheme(globalId: string, limit: number): Observable<Metadata[]> {
        return this.konsultService.getMetadatasWithSameTheme(globalId, limit);
    }

    getNumberOfDatasetsOnTheSameTheme(globalId: string): Observable<number> {
        return this.konsultService.getNumberOfDatasetsOnTheSameTheme(globalId);
    }

    getMediaFileExtension(media: Media): string {
        const mediaFile = media as MediaFile;
        const originalFileType = mediaFile.file_type.replace(CRYPT_SUFFIX, '');
        return mime.getExtension(originalFileType) ?? UNKNOWN_EXTENSION;
    }
}
