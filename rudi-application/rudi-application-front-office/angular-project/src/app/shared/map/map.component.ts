import {AfterViewInit, Component, Input} from '@angular/core';
import {Style} from 'ol/style';
import VectorSource from 'ol/source/Vector';
import {Geometry, LineString, MultiLineString, MultiPoint, MultiPolygon, Point, Polygon} from 'ol/geom';
import VectorLayer from 'ol/layer/Vector';
import olMap from 'ol/Map';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {Feature, Overlay} from 'ol';
import TileLayer from 'ol/layer/Tile';
import {OSM} from 'ol/source';
import View from 'ol/View';
import {Attribution, ScaleLine} from 'ol/control';
import BaseLayer from 'ol/layer/Base';
import {DEFAULT_VIEW_PROJECTION, DisplayMapService, GPS_PROJECTION} from '../../core/services/data-set/display-map.service';
import {SearchAutocompleteItem} from '../search-autocomplete/search-autocomplete-item.interface';
import {Address} from '../../api-rva';
import {LogService} from '../../core/services/log.service';
import {boundingExtent, Extent, getCenter} from 'ol/extent';
import {ADDRESS_STYLE, getHoveredStyle, LINE_STYLE, POINT_STYLE, POLYGON_STYLE} from './map.style.function';
import {createWmtsBaseLayer, MapLayerFunction} from './map.layer.function';
import {Media, MediaFile, MediaType, Metadata} from '../../api-kaccess';
import {MAP_PROTOCOLS} from '../../core/services/map/map-protocols';
import {LayerInformation} from '../../konsult/konsult-model';
import {get, Projection} from 'ol/proj';
import {tap} from 'rxjs/operators';
import MediaTypeEnum = Media.MediaTypeEnum;
import {getDefaultCrs} from './map.media.layer.function';
import {Observable, of} from 'rxjs';
import proj4 from 'proj4';

/**
 * Le nombre de pixels de "padding" autour d'une extent de géoémtrie sur laquelle la vue se centre
 */
const PADDING_EXTENT = 40;

/**
 * Niveau de zoom maximal quand on centre sur des géométries pour pas que ce soit trop zoomé
 */
const MAX_ZOOM_EXTENT = 11;

/**
 * Zoom par défaut sur arrivage de la carte
 */
const DEFAULT_ZOOM = 13;

/**
 * Coordonnées du point de centrage de la carte en WGS84 (ici Rennes)
 */
const MAP_CENTER = [-1.662712, 48.114767];
const MAP_CENTER_TOP_LEFT = [-1.751289, 48.168261];
const MAP_CENTER_BOTTOM_RIGHT = [-1.534996, 48.062135];

@Component({
    selector: 'app-map',
    templateUrl: './map.component.html',
    styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit {

    constructor(
        private matIconRegistry: MatIconRegistry,
        private domSanitizer: DomSanitizer,
        private readonly displayMapService: DisplayMapService,
        private readonly logService: LogService,
        private readonly mapLayerFunction: MapLayerFunction
    ) {
        this.matIconRegistry.addSvgIcon(
            'icon_centrage',
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/icons/icon_centrage.svg')
        );
    }

    @Input()
    mapId: string;

    @Input()
    defaultZoom = DEFAULT_ZOOM;

    @Input()
    boundingBox: Feature<Polygon>;

    @Input()
    centeredGeometry: Geometry;

    @Input()
    baseLayers: LayerInformation[];

    @Input()
    hasSearchAddress = false;

    @Input()
    metadata: Metadata;

    @Input()
    media: Media;

    /**
     * Les sources pour les différents layers
     */
    polygonSource: VectorSource<Polygon>;
    multiPolygonSource: VectorSource<MultiPolygon>;
    pointSource: VectorSource<Point>;
    multiPointSource: VectorSource<MultiPoint>;
    lineSource: VectorSource<LineString>;
    multiLineSource: VectorSource<MultiLineString>;
    addressSource: VectorSource<Point>;

    /**
     * Les layers pour la map
     */
    polygonLayer: VectorLayer<VectorSource<Polygon>>;
    multiPolygonLayer: VectorLayer<VectorSource<MultiPolygon>>;
    pointLayer: VectorLayer<VectorSource<Point>>;
    multiPointLayer: VectorLayer<VectorSource<MultiPoint>>;
    lineLayer: VectorLayer<VectorSource<LineString>>;
    multiLineLayer: VectorLayer<VectorSource<MultiLineString>>;
    addressLayer: VectorLayer<VectorSource<Point>>;

    /**
     * Map Openlayer
     */
    map: olMap;

    /**
     * Popup sur clic de feature
     */
    popup: Overlay;

    /**
     * la feature de la popup
     */
    popupFeature: Feature<Geometry>;

    /**
     * Le fond de carte OL actuel
     */
    currentBaseLayer: BaseLayer;

    /**
     * L'extent de la géométrie du JDD (bounding box ou geometric distribution)
     */
    centeredExtent;

    /**
     * Tableau des fonds de plans affichables issus de la propriété baseLayersRudi
     */
    baseLayersMap: Map<LayerInformation, BaseLayer> = new Map();

    /**
     * feature en hover sur la map
     */
    hoveredFeature: Feature<Geometry>;

    isAutocompleteSearching = false;
    autocompleteItems: SearchAutocompleteItem<Address>[] = [];
    autocompletePin: Feature<Point>;

    currentHoverStyle: Style[];

    centeredPoint: number[];
    initExtent: Extent;
    viewProjectionString: string;

    ngAfterViewInit(): void {
        if (this.map == null) {
            let projection: Observable<Projection>;

            // Affichage de données cartographiques d'un JDD récupération de la projection et register avec proj4
            if (this.media != null) {
                const projectionString = getDefaultCrs(this.media);
                this.viewProjectionString = projectionString;
                projection = this.displayMapService.registerAndGetProjection(projectionString).pipe(
                    tap(() => {
                        this.centeredPoint = proj4(GPS_PROJECTION, projectionString, MAP_CENTER);
                        const topLeft = proj4(GPS_PROJECTION, projectionString, MAP_CENTER_TOP_LEFT);
                        const bottomRight = proj4(GPS_PROJECTION, projectionString, MAP_CENTER_BOTTOM_RIGHT);
                        this.initExtent = boundingExtent([topLeft, bottomRight]);
                    })
                );
            }
            // Affichage d'une carte quelconque : EPSG:3857
            else {
                projection = of(get(DEFAULT_VIEW_PROJECTION)).pipe(
                    tap(() => {
                        this.centeredPoint = proj4(GPS_PROJECTION, DEFAULT_VIEW_PROJECTION, MAP_CENTER);
                        const topLeft = proj4(GPS_PROJECTION, DEFAULT_VIEW_PROJECTION, MAP_CENTER_TOP_LEFT);
                        const bottomRight = proj4(GPS_PROJECTION, DEFAULT_VIEW_PROJECTION, MAP_CENTER_BOTTOM_RIGHT);
                        this.initExtent = boundingExtent([topLeft, bottomRight]);
                    })
                );
            }

            projection.pipe(
                tap((usedProjection: Projection) => {
                    this.initMap(usedProjection);
                    this.initPopup();
                    this.map.updateSize();
                })
            ).subscribe({
                error: (err) => {
                    console.error(err);
                }
            });
        }
    }

    /**
     * Initialisation des layers avec leurs sources
     */
    initLayers(): void {

        // Création des layers qui accueilleront les features
        this.polygonSource = new VectorSource<Polygon>();
        this.polygonLayer = new VectorLayer<VectorSource<Polygon>>({
            source: this.polygonSource,
            style: POLYGON_STYLE
        });

        this.multiPolygonSource = new VectorSource<MultiPolygon>();
        this.multiPolygonLayer = new VectorLayer<VectorSource<MultiPolygon>>({
            source: this.multiPolygonSource,
            style: POLYGON_STYLE
        });

        this.pointSource = new VectorSource<Point>();
        this.pointLayer = new VectorLayer<VectorSource<Point>>({
            source: this.pointSource,
            style: POINT_STYLE
        });

        this.multiPointSource = new VectorSource<MultiPoint>();
        this.multiPointLayer = new VectorLayer<VectorSource<MultiPoint>>({
            source: this.multiPointSource,
            style: POINT_STYLE
        });

        this.lineSource = new VectorSource<LineString>();
        this.lineLayer = new VectorLayer<VectorSource<LineString>>({
            source: this.lineSource,
            style: LINE_STYLE
        });

        this.multiLineSource = new VectorSource<MultiLineString>();
        this.multiLineLayer = new VectorLayer<VectorSource<MultiLineString>>({
            source: this.multiLineSource,
            style: LINE_STYLE
        });

        this.addressSource = new VectorSource<Point>();
        this.addressLayer = new VectorLayer<VectorSource<Point>>({
            source: this.addressSource
        });
        this.addressLayer.setZIndex(1001);
    }

    /**
     * Ajoue la géométrie reprojetée dans la source correspondante en fonction de son type
     * @param geometry la géométrie parsée et reprojetée mais on sait pas son type
     */
    addGeometryToCorrespondingSource(geometry: Geometry): void {
        // On va placer la feature dans la bonne source selon son type
        if (geometry.getType() === 'MultiPolygon') {
            this.multiPolygonSource.addFeature(
                new Feature<MultiPolygon>({
                    geometry: geometry as MultiPolygon
                })
            );
        } else if (geometry.getType() === 'MultiPoint') {
            this.multiPointSource.addFeature(
                new Feature<MultiPoint>({
                    geometry: geometry as MultiPoint
                })
            );
        } else if (geometry.getType() === 'MultiLineString') {
            this.multiLineSource.addFeature(
                new Feature<MultiLineString>({
                    geometry: geometry as MultiLineString
                })
            );
        } else if (geometry.getType() === 'Polygon') {
            this.polygonSource.addFeature(
                new Feature<Polygon>({
                    geometry: geometry as Polygon
                })
            );
        } else if (geometry.getType() === 'Point') {
            this.pointSource.addFeature(
                new Feature<Point>({
                    geometry: geometry as Point
                })
            );
        } else if (geometry.getType() === 'LineString') {
            this.lineSource.addFeature(
                new Feature<LineString>({
                    geometry: geometry as LineString
                })
            );
        }
    }

    /**
     *  Création de la map Openlayers
     *  @param usedProjection projection utilisée pour l'affichage
     */
    initMap(usedProjection: Projection): void {

        // On initialise les layers
        this.initLayers();

        // Layer par défaut si rien ne va
        this.currentBaseLayer = new TileLayer({
            source: new OSM()
        });

        // Normalement on regarde dans la conf le fond de carte à afficher par défaut (1er trouvé)
        if (this.baseLayers != null && this.baseLayers.length > 0) {
            this.baseLayers.forEach((layerInformation: LayerInformation) => {
                const baseLayer: BaseLayer = createWmtsBaseLayer(layerInformation);
                this.baseLayersMap.set(layerInformation, baseLayer);
            });

            this.currentBaseLayer = this.baseLayersMap.entries().next().value[1];
        }

        // Création de la map
        this.map = new olMap({
            target: this.mapId,
            layers: [
                // Fond de carte
                this.currentBaseLayer,
                // 1 layer par type de feature sur la carte
                this.polygonLayer, this.multiPolygonLayer,
                this.pointLayer, this.multiPointLayer,
                this.lineLayer, this.multiLineLayer,
                // layer addresse en dernier pour le pin au dessus de tout
                this.addressLayer
            ],
            view: new View({
                center: this.centeredPoint,
                projection: usedProjection,
                extent: usedProjection.getExtent(),
                zoom: 0
            }),
            controls: [
                new ScaleLine(),
                new Attribution()
            ]
        });

        // Si on a donné une feature à afficher alors on l'affiche
        if (this.centeredGeometry != null) {
            this.centeredExtent = this.centeredGeometry.getExtent();
            this.addGeometryToCorrespondingSource(this.centeredGeometry);
        }
        // Si on a donné une bounding box alors on se centre sur elle
        else if (this.boundingBox != null) {
            this.centeredExtent = this.boundingBox.getGeometry();
            this.polygonSource.addFeature(this.boundingBox);
        }

        // Centrage si succès des données fournies
        if (this.centeredExtent) {
            this.handleClickCentrage();
        }
        // Sinon on se centre pas

        // Chargement des dépendances
        this.handleLoadLayers();
        this.handleMapEvents();

        if (this.initExtent != null) {
            this.map.getView().fit(this.initExtent);
        }
    }

    private handleLoadLayers(): void {
        // Gestion chargement des données du JDD
        if (this.metadata != null && this.media != null) {
            let layer;
            if (this.media.connector.interface_contract === MAP_PROTOCOLS.WMS) {
                layer = this.mapLayerFunction.createWmsDataLayer(this.metadata.global_id, this.media);
            } else if (this.media.connector.interface_contract === MAP_PROTOCOLS.WMTS) {
                layer = this.mapLayerFunction.createWmtsDataLayer(this.metadata.global_id, this.media);
            } else if (this.media.connector.interface_contract === MAP_PROTOCOLS.WFS) {
                layer = this.mapLayerFunction.createWfsDataLayer(this.metadata.global_id, this.media);
                this.addFeatureInteraction(layer);
            } else if (this.media.media_type === MediaTypeEnum.File) {
                const mediaFile: MediaFile = this.media as MediaFile;
                if (mediaFile.file_type === MediaType.ApplicationGeojson) {
                    this.mapLayerFunction.createGeojsonDataLayer(this.metadata.global_id, this.media).subscribe({
                        next: (baseLayer: BaseLayer) => {
                            this.map.getLayers().push(baseLayer);
                            this.addFeatureInteraction(baseLayer);
                        }
                    });
                }
            }

            if (layer != null) {
                this.map.getLayers().push(layer);
            }
        }

    }

    private handleMapEvents(): void {
        // Gestion de l'evenement de mouvement de pointeur
        this.map.on('pointermove', (e) => {
            if (this.hoveredFeature != null) {
                this.hoveredFeature.setStyle(null);
                this.hoveredFeature = null;
            }

            if (this.popupFeature) {
                this.popupFeature.setStyle(this.currentHoverStyle);
            }

            this.map.forEachFeatureAtPixel(e.pixel, (f, l) => {
                if (l !== this.addressLayer) {
                    this.hoveredFeature = f as Feature<Geometry>;
                    this.hoveredFeature.setStyle(getHoveredStyle(f.getGeometry().getType()));
                    return true;
                }
                return false;
            });
        });
    }

    /**
     * Initialisation de l'objet popup Ol
     */
    initPopup(): void {
        this.popup = new Overlay({
            element: document.getElementById('map-popup'),
            positioning: 'bottom-center',
            stopEvent: true,
            offset: [0, -10]
        });
    }

    /**
     * Action zoom +
     */
    handleClickZoomIn(): void {
        this.map.getView().animate({
            zoom: this.map.getView().getZoom() + 1,
            duration: 250
        });
    }

    /**
     * Action zoom -
     */
    handleClickZoomOut(): void {
        this.map.getView().animate({
            zoom: this.map.getView().getZoom() - 1,
            duration: 250
        });
    }

    /**
     * Intéraction clic du bouton de centrage sur la géométrie du JDD
     */
    handleClickCentrage(): void {
        let centeredElement = this.centeredExtent;
        if (centeredElement == null) {
            centeredElement = this.initExtent;
        }
        this.map.getView().fit(
            centeredElement,
            {
                padding: [PADDING_EXTENT, PADDING_EXTENT, PADDING_EXTENT, PADDING_EXTENT],
                maxZoom: MAX_ZOOM_EXTENT,
                duration: 1000
            }
        );
    }

    /**
     * Changement du fond de plan affiché
     * @param baseLayer le fond de plan souhaité à partir de sa clé métier
     */
    switchLayer(baseLayer: LayerInformation): void {
        this.map.removeLayer(this.currentBaseLayer);
        this.currentBaseLayer = this.baseLayersMap.get(baseLayer);
        this.map.getLayers().insertAt(0, this.currentBaseLayer);
    }

    /**
     * Recherche d'addresse sur lesquelles se centrer
     * @param input texte de recherche
     */
    searchAddresses(input: string): void {
        this.isAutocompleteSearching = true;
        this.displayMapService.searchAddresses(input).subscribe({
            next: (items: SearchAutocompleteItem<Address>[]) => {
                this.autocompleteItems = items;
                this.isAutocompleteSearching = false;
            },
            error: (e) => {
                this.logService.error(e);
                this.isAutocompleteSearching = false;
            }
        });
    }

    /**
     * Centrage sur l'adresse RVA trouvée
     * @param item l'adresse trouvée
     */
    centerOnAddress(item: Address): void {
        if (this.autocompletePin != null) {
            this.addressSource.removeFeature(this.autocompletePin);
        }

        const destination = this.viewProjectionString != null ? this.viewProjectionString : DEFAULT_VIEW_PROJECTION;

        const point = new Point([Number(item.x), Number(item.y)]).transform(GPS_PROJECTION, destination) as Point;
        this.autocompletePin = new Feature<Point>(point);
        this.autocompletePin.setStyle(ADDRESS_STYLE);
        this.map.getView().fit(
            point,
            {
                size: this.map.getSize(),
                minResolution: 1,
                duration: 1000
            }
        );
        this.addressSource.addFeature(this.autocompletePin);
    }

    /**
     * Fermeture de la popup ouverte des features
     */
    handleClosePopup(): void {
        if (this.popupFeature) {
            this.popupFeature.setStyle();
            this.popupFeature = null;
            this.map.removeOverlay(this.popup);
        }
    }

    /**
     * Ajout des interaction au clic sur une feature pour ajouter une popup
     * @param vectorLayer le layer concerné par l'interaction
     */
    addFeatureInteraction(vectorLayer: BaseLayer): void {
        this.map.on('click', (event) => {
            if (this.popupFeature) {
                this.popupFeature.setStyle();
            }

            const feature = this.map.forEachFeatureAtPixel(event.pixel, (clickedFeature, clickedLayer) => {
                    return clickedLayer === vectorLayer ? clickedFeature : null;
                }
            );

            if (feature) {
                const hoveredStyle = getHoveredStyle(feature.getGeometry().getType());
                this.currentHoverStyle = hoveredStyle;
                this.popupFeature = feature as Feature<Geometry>;
                this.popupFeature.setStyle(hoveredStyle);
                const coordinates = feature.getGeometry().getExtent();
                const center = getCenter(coordinates);
                this.popup.setPosition(center);
                this.map.addOverlay(this.popup);
            } else {
                this.popupFeature = null;
                this.map.removeOverlay(this.popup);
            }
        });
    }
}

