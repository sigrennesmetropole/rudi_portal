/**
 * énumération des protocoles cartographiques
 */
export enum MAP_PROTOCOLS {
    WMS= 'wms', WMTS = 'wmts', WFS = 'wfs'
}

/**
 * Liste des protocoles cartos supportés
 */
export const MAP_PROTOCOLS_SUPPORTED: string[] = [
    MAP_PROTOCOLS.WMS, MAP_PROTOCOLS.WMTS, MAP_PROTOCOLS.WFS
];
