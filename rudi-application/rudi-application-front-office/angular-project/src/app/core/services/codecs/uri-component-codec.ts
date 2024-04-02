import {HttpParameterCodec} from '@angular/common/http';


export class URIComponentCodec implements HttpParameterCodec {
    decodeKey(key: string): string {
        return decodeURIComponent(key);
    }

    decodeValue(value: string): string {
        return decodeURIComponent(value);
    }

    encodeKey(key: string): string {
        return encodeURIComponent(key);
    }

    encodeValue(value: string): string {
        return encodeURIComponent(value);
    }

    /**
     *
     * Retire tous les accents, caractères spéciaux et met en lowerCase
     *
     * @param value la string que l'on veut normaliser
     */
    normalizeString(value: string): string {
        return value
            .normalize('NFD')
            .replace(/\p{Diacritic}/gu, '') // Retire les accents et caractères spéciaux
            .replace(/[^a-zA-Z0-9]/gu, '-') // remplace les espaces et les ":" par des "-"
            .replace(/(-)+/gu, '-') // remplace les "-" multiple par un seul "-"
            .toLowerCase(); // met tout en minuscules
    }
}
