import {Media} from '../../api-kaccess';

export function getLayerName(media: Media): string {
    return getConnectorParameter(media, 'layer_name');
}

export function getMatrixSet(media: Media): string {
    return getConnectorParameter(media, 'matrix_set');
}

export function getMatrixIdPrefix(media: Media): string {
    return getConnectorParameter(media, 'matrix_id_prefix');
}

export function getFormat(media: Media): string {
    return getConnectorParameter(media, 'format');
}

export function getMaxZoom(media: Media): string {
    return getConnectorParameter(media, 'max_zoom');
}

/**
 * Extraction d'une valeur de paramètre de connecteur à partir de sa clé dans un média
 * @param media le média testé
 * @param key la clé d'entrée
 */
function getConnectorParameter(media: Media, key: string): string {
    const parameter = media.connector.connector_parameters
        .filter((entry) => entry.key === key)[0];
    return parameter.value;
}

