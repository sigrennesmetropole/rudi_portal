import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import View from 'ol/View';
import Map from 'ol/Map';
import {defaults as defaultControls, ScaleLine} from 'ol/control';
import TileLayer from 'ol/layer/Tile';
import {TileWMS} from 'ol/source';
import {GeoJsonObject, Metadata, MetadataGeographyBoundingBox} from '../../../api-kaccess';
import {getTransform} from 'ol/proj';
import {Geometry, LineString, MultiLineString, MultiPoint, MultiPolygon, Point, Polygon} from 'ol/geom';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import {Feature} from 'ol';
import GeoJSON from 'ol/format/GeoJSON';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {Fill, Stroke, Style} from 'ol/style';
import CircleStyle from 'ol/style/Circle';

/**
 * URL du flux de données du fond de plan de Rennes Métropole : noir et blanc
 */
const WMS_URL = 'https://public.sig.rennesmetropole.fr/geoserver/ref_fonds/wms';

/**
 * Projection WGS84 pour la bounding box
 */
const GPS_PROJECTION = 'EPSG:4326';

/**
 * Code EPSG de la projection utilisée par le fond de plan
 */
const WMS_PROJECTION = 'EPSG:3958';

/**
 * Identificateur du layer (dans le geoserver RM) correspondant au fond de plan RUDI
 */
const LAYER_PLAN_IDENTIFIER = 'ref_fonds:pvci_nb';

/**
 * Code EPSG de la projection utilisée par la vue OpenLayers
 */
const VIEW_PROJECTION = 'EPSG:3857';

/**
 * Objet de parsing des géométries au format GeoJSON
 */
const geoJSON = new GeoJSON();

/**
 * Le nombre de pixels de "padding" autour d'une extent de géoémtrie sur laquelle la vue se centre
 */
const PADDING_EXTENT = 40;

/**
 * Niveau de zoom maximal quand on centre sur des géométries pour pas que ce soit trop zoomé
 */
const MAX_ZOOM_EXTENT = 11;

const white = [255, 255, 255, 2];
const blue = [2, 112, 231, 0.40];
const darkBlue = [0, 70, 128];
const redLight = [255, 0, 0, 0.2];
const red = [255, 0, 0, 0.6];
const darkOrange = [243, 107, 67];
const width = 3;


// Style sur les POLYGONS
const POLYGON_STYLE = new Style({
    fill: new Fill({
        color: blue
    }),
    stroke: new Stroke({
        width,
        color: darkBlue
    })
});

// Style pour un MULTIPOLYGON
const MULTIPOLYGON_STYLE = new Style({
    fill: new Fill({
        color: blue
    }),
    stroke: new Stroke({
        width,
        color: darkBlue,
    }),
});

// Style pour un POINT
const POINT_STYLE = new Style({
    image: new CircleStyle({
        radius: 5,
        fill: new Fill({
            color: redLight
        }),
        stroke: new Stroke({
            color: red,
            width
        }),
    }),
});

// Style pour un MULTIPOINT
const MULTIPOINT_STYLE = new Style({
    image: new CircleStyle({
        radius: 5,
        fill: new Fill({
            color: white
        }),
        stroke: new Stroke({
            color: darkOrange,
            width
        }),
    }),
});

// Style pour un LINE
const LINE_STYLE = new Style({
    fill: new Fill({
        color: white
    }),
    stroke: new Stroke({
        color: darkOrange,
        width
    }),
});

// Style pour un MULTILINE
const MULTILINE_STYLE = new Style({
    fill: new Fill({
        color: white
    }),
    stroke: new Stroke({
        color: darkOrange,
        width
    })
});


@Component({
    selector: 'app-map',
    templateUrl: './map.component.html',
    styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit, OnInit {

    /**
     * Les métadonnées affichées dans la page de détail
     */
    @Input()
    metadata: Metadata;

    /**
     * Les sources pour les différents layers
     */
    polygonSource: VectorSource<Polygon>;
    multiPolygonSource: VectorSource<MultiPolygon>;
    pointSource: VectorSource<Point>;
    multiPointSource: VectorSource<MultiPoint>;
    lineSource: VectorSource<LineString>;
    multiLineSource: VectorSource<MultiLineString>;

    /**
     * Les layers pour la map
     */
    polygonLayer: VectorLayer<VectorSource<Polygon>>;
    multiPolygonLayer: VectorLayer<VectorSource<MultiPolygon>>;
    pointLayer: VectorLayer<VectorSource<Point>>;
    multiPointLayer: VectorLayer<VectorSource<MultiPoint>>;
    lineLayer: VectorLayer<VectorSource<LineString>>;
    multiLineLayer: VectorLayer<VectorSource<MultiLineString>>;

    /**
     * Map Openlayer
     */
    map: Map;

    /**
     * L'extent de la géométrie du JDD (bounding box ou geometric distribution)
     */
    centeredExtent;

    constructor(private matIconRegistry: MatIconRegistry,
                private domSanitizer: DomSanitizer) {
        this.matIconRegistry.addSvgIcon(
            'icon_centrage',
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/icons/icon_centrage.svg')
        );
    }

    ngOnInit(): void {
    }

    ngAfterViewInit(): void {
        if (this.map == null && this.metadata?.geography) {
            this.initMap();
            this.map.updateSize();
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
            style: MULTIPOLYGON_STYLE
        });

        this.pointSource = new VectorSource<Point>();
        this.pointLayer = new VectorLayer<VectorSource<Point>>({
            source: this.pointSource,
            style: POINT_STYLE
        });

        this.multiPointSource = new VectorSource<MultiPoint>();
        this.multiPointLayer = new VectorLayer<VectorSource<MultiPoint>>({
            source: this.multiPointSource,
            style: MULTIPOINT_STYLE
        });

        this.lineSource = new VectorSource<LineString>();
        this.lineLayer = new VectorLayer<VectorSource<LineString>>({
            source: this.lineSource,
            style: LINE_STYLE
        });

        this.multiLineSource = new VectorSource<MultiLineString>();
        this.multiLineLayer = new VectorLayer<VectorSource<MultiLineString>>({
            source: this.multiLineSource,
            style: MULTILINE_STYLE
        });
    }

    /**
     * Ajoue la feature reprojetée dans la source correspondante en fonction de son type
     * @param geometrie la géométrie d'origine pour savoir le type de géométrie
     * @param reprojected la géométrie parsée et reprojetée mais on sait pas son type
     */
    addFeatureToCorrespondingSource(geometrie: GeoJsonObject, reprojected: Geometry): void {
        // On va placer la feature dans la bonne source selon son type
        if (geometrie.type === 'MultiPolygon') {
            this.multiPolygonSource.addFeature(
                new Feature<MultiPolygon>({
                    geometry: reprojected as MultiPolygon
                })
            );
        } else if (geometrie.type === 'MultiPoint') {
            this.multiPointSource.addFeature(
                new Feature<MultiPoint>({
                    geometry: reprojected as MultiPoint
                })
            );
        } else if (geometrie.type === 'MultiLineString') {
            this.multiLineSource.addFeature(
                new Feature<MultiLineString>({
                    geometry: reprojected as MultiLineString
                })
            );
        } else if (geometrie.type === 'Polygon') {
            this.polygonSource.addFeature(
                new Feature<Polygon>({
                    geometry: reprojected as Polygon
                })
            );
        } else if (geometrie.type === 'Point') {
            this.pointSource.addFeature(
                new Feature<Point>({
                    geometry: reprojected as Point
                })
            );
        } else if (geometrie.type === 'LineString') {
            this.lineSource.addFeature(
                new Feature<LineString>({
                    geometry: reprojected as LineString
                })
            );
        }
    }

    /**
     *  Création de la map Openlayers
     */
    initMap(): void {

        // Récupération des infos des métadonnées
        const boundingBox = this.metadata?.geography?.bounding_box;
        const geometrie = this.metadata?.geography?.geographic_distribution;;


        // On initialise les layers
        this.initLayers();

        // Si on a pas de géométrie dans les métadata
        // on va créer une géométrie à partir de la bounding box et l'afficher sur la carte
        if (geometrie == null && boundingBox != null) {
            // récupérer l'objet transformateur de projection pour créer la feature de bounding box
            const bboxPolygon: Feature<Polygon> = this.createBoundingBoxFeature(boundingBox, getTransform(GPS_PROJECTION, VIEW_PROJECTION));
            this.polygonSource.addFeature(bboxPolygon);
            this.centeredExtent = bboxPolygon.getGeometry().getExtent();
        }
        // Sinon on va ajouter la feature des métadata
        else if (geometrie != null && boundingBox != null) {
            const geometryParsed = geoJSON.readGeometry(geometrie);
            const reprojected = geometryParsed.transform(GPS_PROJECTION, VIEW_PROJECTION);
            this.centeredExtent = geometryParsed.getExtent();
            this.addFeatureToCorrespondingSource(geometrie, reprojected);
        }

        // Création de la map
        this.map = new Map({
            target: 'map-rudi',
            layers: [
                // Fond de carte
                new TileLayer({
                    source: new TileWMS({
                        url: WMS_URL,
                        params: {LAYERS: LAYER_PLAN_IDENTIFIER},
                        projection: WMS_PROJECTION
                    }),
                }),
                // 1 layer par type de feature sur la carte
                this.polygonLayer, this.multiPolygonLayer,
                this.pointLayer, this.multiPointLayer,
                this.lineLayer, this.multiLineLayer
            ],
            view: new View({
                projection: VIEW_PROJECTION
            }),
            controls: defaultControls().extend([
                new ScaleLine()
            ])
        });

        // Si les données sont OK et qu'on peut se centrer sur une géométrie
        if (this.centeredExtent) {
            this.handleClickCentrage();
        }
        // Sinon on se centre pas
    }

    /**
     * Créée une feature géo à partir de la bounding box
     * @param boundingBox la bounding box des métadatas
     * @param projectionTransformer fonction de reprojection
     */
    createBoundingBoxFeature(boundingBox: MetadataGeographyBoundingBox,
                             projectionTransformer: (array: number[]) => number[]): Feature<Polygon> {

        // Calcul des points projetés dans la proj de destination
        const nordOuestSource = [boundingBox.west_longitude, boundingBox.north_latitude];
        const sudEstSource = [boundingBox.east_longitude, boundingBox.south_latitude];
        const nordOuestDestination = projectionTransformer(nordOuestSource);
        const sudEstDestination = projectionTransformer(sudEstSource);

        // Création des coordonnées du polygone :
        // 1 outer ring qui fait le tour de la bounding box dans le sens horaire
        const polygonCordinates: number[][][] = [
            [
                nordOuestDestination,
                [nordOuestDestination[0], sudEstDestination[1]],
                sudEstDestination,
                [sudEstDestination[0], nordOuestDestination[1]],
                nordOuestDestination
            ]
        ];

        const poly = new Polygon(polygonCordinates);

        // On crée une feature avec les coordonnées
        return new Feature<Polygon>({
            geometry: poly
        });
    }

    /**
     * Intéraction clic du bouton de centrage sur la géométrie du JDD
     */
    handleClickCentrage(): void {
        if (this.centeredExtent) {
            this.map.getView().fit(
                this.centeredExtent,
                {
                    padding: [PADDING_EXTENT, PADDING_EXTENT, PADDING_EXTENT, PADDING_EXTENT],
                    maxZoom: MAX_ZOOM_EXTENT
                }
            );
        }
    }
}
