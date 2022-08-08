import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpResponse} from '@angular/common/http';
import {KonsultService} from '../../api-konsult';
import {Metadata, MetadataFacets, MetadataList} from '../../api-kaccess';
import {Filters} from '../../shared/models/filters';
import {map} from 'rxjs/operators';
import {PageResultUtils} from '../../shared/utils/page-result-utils';
import {DEFAULT_VALUE as DEFAULT_ORDER_VALUE} from './filters/order-filter';

export const MAX_RESULTS_PER_PAGE = 36;
export const MAX_RESULTS_PER_REQUEST = 100;

@Injectable({
    providedIn: 'root'
})
export class KonsultMetierService {
    constructor(
        private readonly konsultService: KonsultService
    ) {
    }

    private static getFacetsValues(facets: MetadataFacets): string[] {
        return facets.items.length ? facets.items[0].values.map(facetValue => facetValue.value) : [];
    }

    /**
     * Recuperation de la liste de metadata depuis le server
     */
    searchMetadatas(filters?: Filters, offset?: number, limit?: number): Observable<MetadataList> {
        return this.konsultService.searchMetadatas(
            filters.search,
            filters.themes,
            filters.keywords,
            filters.producerNames,
            filters.dates.debut,
            filters.dates.fin,
            filters.restrictedAccess,
            filters.globalIds,
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
                restrictedAccess: null,
                globalIds
            }, offset, MAX_RESULTS_PER_REQUEST)
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
}
