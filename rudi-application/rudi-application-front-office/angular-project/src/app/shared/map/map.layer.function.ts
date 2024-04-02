import {Injectable} from '@angular/core';
import {DEFAULT_VIEW_PROJECTION, DisplayMapService} from '@core/services/data-set/display-map.service';
import {LogService} from '@core/services/log.service';
import {Media} from 'micro_service_modules/api-kaccess';
import {LayerInformation} from 'micro_service_modules/konsult/konsult-model';
import {getTopLeft, getWidth} from 'ol/extent';
import {GeoJSON} from 'ol/format';
import {Geometry} from 'ol/geom';
import BaseLayer from 'ol/layer/Base';
import TileLayer from 'ol/layer/Tile';
import VectorLayer from 'ol/layer/Vector';
import {bbox as bboxStrategy} from 'ol/loadingstrategy';
import {get, Projection} from 'ol/proj';
import {TileWMS, WMTS} from 'ol/source';
import VectorSource from 'ol/source/Vector';
import {Style} from 'ol/style';
import WMTSTileGrid from 'ol/tilegrid/WMTS';
import {Observable} from 'rxjs';
import {map, tap} from 'rxjs/operators';
import {
    getDefaultCrs,
    getFormat,
    getLayerName,
    getMatrixIdPrefix,
    getMatrixSet,
    getMaxZoom,
    getStyles,
    getVersion
} from './map.media.layer.function';
import {LINE_STYLE, POINT_STYLE, POLYGON_STYLE} from './map.style.function';

/**
 * Construction d'un TileGrid pour afficher un layer WMTS dans une projection donnée
 * @param projection la projection utilisée par le layer
 * @param maxZoom le zoom maximal permit pour ce layer
 * @param matrixIdPrefix préfixe pour les identifiants de matrixSet (peut être nul ou vide)
 */
function getWmtsTileGrid(projection: string, maxZoom: number, matrixIdPrefix: string): WMTSTileGrid {

    const projectionOl = get(projection);
    if (projectionOl == null) {
        return null;
    }

    const projectionExtent = projectionOl.getExtent();
    const size = getWidth(projectionExtent) / 256;
    const resolutions = new Array(maxZoom);
    const matrixIds = new Array(maxZoom);
    for (let z = 0; z < maxZoom; ++z) {
        resolutions[z] = size / Math.pow(2, z);
        matrixIds[z] = matrixIdPrefix ? matrixIdPrefix + ':' + z : z;
    }

    return new WMTSTileGrid({
        origin: getTopLeft(projectionExtent),
        resolutions,
        matrixIds,
    });
}

/**
 * Conversion layer RUDI -> Layer OL en WMTS pour affichage
 * @param baseLayer l'objet RUDI à convertir en objet OpenLayers
 * @private
 */
export function createWmtsBaseLayer(baseLayer: LayerInformation): BaseLayer {

    const options = {
        style: 'default',
        url: baseLayer.url,
        layer: baseLayer.layerName,
        matrixSet: baseLayer.matrixSet,
        projection: DEFAULT_VIEW_PROJECTION,
        attributions: baseLayer.attributions,
        attributionsCollapsible: false,
        format: baseLayer.format,
        tileGrid: getWmtsTileGrid(baseLayer.srs, baseLayer.maxZoom, baseLayer.matrixIdPrefix),
    };

    return new TileLayer({
        source: new WMTS(options)
    });
}

/**
 * Création layer WMS à afficher sur la caete
 * @param wmsUrl URL du service WMS
 * @param layerName le nom du layer à récupérer
 * @private
 */
export function createWmsLayer(wmsUrl: string, layerName: string): BaseLayer {

    const options = {
        url: wmsUrl,
        layer: layerName,
        projection: DEFAULT_VIEW_PROJECTION,
        params: {LAYERS: layerName}
    };

    return new TileLayer({
        source: new TileWMS(options),
    });
}

/**
 * Création d'un layer à partir d'un contenu GeoJSON
 * @param geojsonObject le contenu pour créer le layer
 */
export function createGeoJsonLayer(geojsonObject: JSON): BaseLayer {
    return new VectorLayer<VectorSource<Geometry>>({
        source: new VectorSource({
            features: new GeoJSON().readFeatures(geojsonObject),
        }),
        style: styleFunction
    });
}

/**
 * Fonction de style pour une feature donnée
 * @param feature la feature donnée
 */
function styleFunction(feature): Style {
    switch (feature.getGeometry().getType()) {
        case 'MultiPolygon':
        case 'Polygon':
            return POLYGON_STYLE;
        case 'MultiLineString':
        case 'LineString':
            return LINE_STYLE;
        case 'Point':
        case 'MultiPoint':
            return POINT_STYLE;
    }
    return null;
}

@Injectable({
    providedIn: 'root'
})
export class MapLayerFunction {

    constructor(
        private readonly displayMapService: DisplayMapService,
        private readonly logService: LogService
    ) {
    }

    /**
     * Création d'un layer de données de JDD au format WMS
     * @param globalId uuid du JDD
     * @param media ID du média qui est du WMS
     */
    createWmsDataLayer(globalId: string, media: Media): BaseLayer {
        const url = this.displayMapService.getServiceUrl(globalId, media.media_id);
        const layerName = getLayerName(media);
        const projection = getDefaultCrs(media);
        const version = getVersion(media);

        const options = {
            url,
            tileLoadFunction: (tile, src) => this.loadTile(globalId, media, tile, src),
            layer: layerName,
            projection,
            params: {LAYERS: layerName, VERSION: version}
        };

        return new TileLayer({
            source: new TileWMS(options),
        });
    }

    /**
     * Création d'un layer de données de JDD au format WMTS
     * @param globalId UUID du JDD
     * @param media média à afficher
     */
    createWmtsDataLayer(globalId: string, media: Media): BaseLayer {

        const url = this.displayMapService.getServiceUrl(globalId, media.media_id);
        const maxZoom = Number(getMaxZoom(media));

        const options = {
            style: getStyles(media),
            url,
            tileLoadFunction: (tile, src) => this.loadTile(globalId, media, tile, src),
            layer: getLayerName(media),
            matrixSet: getMatrixSet(media),
            projection: getDefaultCrs(media),
            format: getFormat(media),
            tileGrid: getWmtsTileGrid(DEFAULT_VIEW_PROJECTION, maxZoom, getMatrixIdPrefix(media))
        };

        return new TileLayer({
            source: new WMTS(options)
        });
    }

    /**
     * Création d'un layer de données de JDD au format WFS
     * @param globalId uuid du JDD
     * @param media ID du média qui est du WFS
     */
    createWfsDataLayer(globalId: string, media: Media): BaseLayer {
        const layerName = getLayerName(media);
        const format = getFormat(media);
        const version = getVersion(media);
        const vectorSource = new VectorSource({
            format: new GeoJSON(),
            loader: (extent: number[], resolution: number, projection: Projection) => {
                const src = '?service=WFS&version=' + version + '&request=GetFeature&typename=' + layerName + '&' +
                    'outputFormat=' + format + '&srsname=' + projection.getCode() + '&bbox=' + extent.join(',') +
                    ',' + projection.getCode();
                this.displayMapService.getFeatureLayerContent(globalId, media.media_id, src).pipe(
                    tap((geojsonObject: JSON) => {
                        const features = new GeoJSON().readFeatures(geojsonObject);
                        vectorSource.addFeatures(features);
                    })
                ).subscribe({
                    error: (e) => {
                        this.logService.error(e);
                    }
                });
            },
            strategy: bboxStrategy,
        });

        return new VectorLayer<VectorSource<Geometry>>({
            source: vectorSource,
            style: styleFunction
        });
    }

    /**
     * Création d'un layer de données de JDD au format GeoJSON
     * @param globalId l'UUId du jdd
     * @param media le média geojson
     */
    createGeojsonDataLayer(globalId: string, media: Media): Observable<BaseLayer> {
        return this.displayMapService.downloadGeojson(globalId, media.media_id).pipe(
            map((geojson: JSON) => createGeoJsonLayer(geojson))
        );
    }

    /**
     * Fonction de chargement de tile pour un layer
     * @param globalId UUID du JDD
     * @param media média cartographique
     * @param tile le tile à charger
     * @param src les arguments queryparam
     * @private
     */
    // tslint:disable-next-line:no-any OpenLayers gèrele type de tile comme ça
    private loadTile(globalId: string, media: Media, tile: any, src: string): void {
        this.displayMapService.getImageLayerContent(globalId, media.media_id, src).pipe(
            tap((arrayBuffer: ArrayBuffer) => {
                const base64String = btoa(String.fromCharCode.apply(null, new Uint8Array(arrayBuffer)));
                tile.getImage().src = 'data:image/png;base64,' + base64String;
            })
        ).subscribe({
            error: (e) => {
                this.logService.error(e);
            }
        });
    }
}
