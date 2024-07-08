import {HttpClient, HttpHeaders, HttpParameterCodec, HttpParams} from '@angular/common/http';
import {Inject, Injectable, Optional} from '@angular/core';
import {Filters} from '@shared/models/filters';
import {MetadataUtils} from '@shared/utils/metadata-utils';
import {PageResultUtils} from '@shared/utils/page-result-utils';
import {Media, MediaFile, Metadata, MetadataFacets, MetadataList} from 'micro_service_modules/api-kaccess';
import {BASE_PATH, Configuration, KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {CustomHttpParameterCodec} from 'micro_service_modules/konsult/konsult-api/encoder';
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

export type Order = 'title' | '-title' | 'updatedDate' | '-updatedDate' | 'code' | '-code' | 'order_';
export const ORDERS: Order[] = ['title', '-title', 'updatedDate', '-updatedDate'];
export const DEFAULT_PROJECT_ORDER: Order = '-updatedDate';

@Injectable({
    providedIn: 'root'
})
export class KonsultMetierService {

    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();
    public encoder: HttpParameterCodec;

    constructor(
        private readonly konsultService: KonsultService,
        protected httpClient: HttpClient,
        @Optional()@Inject(BASE_PATH) basePath: string,
        @Optional() configuration: Configuration
    ) {
        KonsultMetierService.loadCustomMimeType();
        if (configuration) {
            this.configuration = configuration;
        }
        this.encoder = this.configuration.encoder || new CustomHttpParameterCodec();
    }

    private addToHttpParamsRecursive(httpParams: HttpParams, value?: any, key?: string): HttpParams {
        if (value == null) {
            return httpParams;
        }

        if (typeof value === "object") {
            if (Array.isArray(value)) {
                (value as any[]).forEach( elem => httpParams = this.addToHttpParamsRecursive(httpParams, elem, key));
            } else if (value instanceof Date) {
                    httpParams = httpParams.append(key,
                        (value as Date).toISOString().substr(0, 10));
            } else {
                Object.keys(value).forEach( k => httpParams = this.addToHttpParamsRecursive(
                    httpParams, value[k], key != null ? `${key}.${k}` : k));
            }
        } else if (key != null) {
            httpParams = httpParams.append(key, value);
        } else {
            throw Error("key may not be null if value is not object or array");
        }
        return httpParams;
    }

    private addToHttpParams(httpParams: HttpParams, value: any, key?: string): HttpParams {
        if (typeof value === "object" && value instanceof Date === false) {
            httpParams = this.addToHttpParamsRecursive(httpParams, value);
        } else {
            httpParams = this.addToHttpParamsRecursive(httpParams, value, key);
        }
        return httpParams;
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
    downloadMetadataMedia(mediaUrl: string, options?: {httpHeaderAccept?: 'application/octet-stream' | 'application/json'}): Observable<any> {
        let headers = this.defaultHeaders;
        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            // to determine the Accept header
            const httpHeaderAccepts: string[] = [
                'application/octet-stream',
                'application/json'
            ];

            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }
        console.log(headers);
        return this.httpClient.get(`${String(mediaUrl)}`,
            {
                responseType: 'blob',
                withCredentials: this.configuration.withCredentials,
                headers,
            }
        );
    }

/**
 *  Fonction qui permet de récupérer des métadonnés de type WMS/WFS d\&#39;un jeu de données.
 *  Récupère le flux WMS/WFS d\&#39;un média de type SERVICE
 */
    callServiceMetadataMedia(mediaUrl: string, parameters?: { [key: string]: string; }, options?: {httpHeaderAccept?: 'application/octet-stream' | 'application/json'}): Observable<any> {
    let queryParameters = new HttpParams({encoder: this.encoder});
    if (parameters !== undefined && parameters !== null) {
        queryParameters = this.addToHttpParams(queryParameters,
            <any>parameters, 'parameters');
    }

        let headers: HttpHeaders = this.defaultHeaders;
        let httpHeaderAcceptSelected: string | undefined = options && options.httpHeaderAccept;
        if (httpHeaderAcceptSelected === undefined) {
            const httpHeaderAccepts: string[] = [
                'application/octet-stream',
                'application/json'
            ];

            httpHeaderAcceptSelected = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        }
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }
        console.log(headers);
        return this.httpClient.get(`${String(mediaUrl)}`,
            {
                params: queryParameters,
                responseType: 'blob',
                withCredentials: this.configuration.withCredentials,
                headers,
            }
        );
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
