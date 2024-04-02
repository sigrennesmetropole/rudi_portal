import {Media} from 'micro_service_modules/api-kaccess';

export function getLayerName(media: Media): string {
    return getConnectorParameter(media, 'layer');
}

export function getMatrixSet(media: Media): string {
    return getConnectorParameter(media, 'matrix_set');
}

export function getMatrixIdPrefix(media: Media): string {
    return getConnectorParameter(media, 'matrix_id_prefix');
}

export function getFormat(media: Media): string {
    const formats = getConnectorParameter(media, 'formats');
    let format = formats;
    if (formats.includes(',')) {
        format = formats.split(',')[0];
    }

    return format;
}

export function getDefaultCrs(media: Media): string {
    return getConnectorParameter(media, 'default_crs');
}

export function getOtherCRSS(media: Media): string {
    return getConnectorParameter(media, 'other_crss');
}

export function getStyles(media: Media): string {
    return getConnectorParameter(media, 'styles');
}

export function getTransparent(media: Media): string {
    return getConnectorParameter(media, 'transparent');
}

export function getMaxZoom(media: Media): string {
    return getConnectorParameter(media, 'max_zoom');
}

export function getVersion(media: Media): string {
    return getConnectorParameter(media, 'versions');
}

/**
 * Extraction d'une valeur de paramètre de connecteur à partir de sa clé dans un média
 * @param media le média testé
 * @param key la clé d'entrée
 */
function getConnectorParameter(media: Media, key: string): string {
    const parameter = media.connector.connector_parameters
        .filter((entry) => entry.key === key)[0];

    if (parameter == null) {
        return null;
    }

    return parameter.value;
}

