import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpResponse} from '@angular/common/http';
import {KonsultService} from '../../api-konsult';
import {Metadata, MetadataFacets, MetadataList} from '../../api-kaccess';
import {Filters} from '../../shared/models/filters';
import {map} from 'rxjs/operators';

export const MAX_RESULTS_PER_PAGE = 36;

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
    searchMetadatas(limit?: number, offset?: number, filters?: Filters): Observable<MetadataList> {
        return this.konsultService.searchMetadatas(
            MAX_RESULTS_PER_PAGE,
            offset,
            filters.search,
            filters.themes,
            filters.keywords,
            filters.producerNames,
            filters.dates.debut,
            filters.dates.fin,
            filters.order,
            filters.restrictedAccess
        );
    }

    /**
     * Permet de d'afficher le detail d'un jdd à partir de son uuid
     */
    getMetadataByUuid(uuid: string): Observable<Metadata> {
        return this.konsultService.getMetadataById(uuid);
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
}
