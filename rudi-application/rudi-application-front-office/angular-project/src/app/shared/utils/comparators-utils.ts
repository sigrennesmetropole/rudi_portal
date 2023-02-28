import {SortDirection} from '@angular/material/sort/sort-direction';

/**
 * Comparaison entre 2 dates avec renvoi numérique
 * @param a date 1
 * @param b date 2
 * @param isAsc ordre de tri
 */
export function compareDates(a: Date, b: Date, isAsc: SortDirection): number {
    const rankShift = a < b ? -1 : 1;
    if (isAsc === 'asc') {
        return rankShift;
    }

    return rankShift * -1;
}

/**
 * Comparaison entre 2 chaînes en ignorant la casse
 * @param a chaîne 1
 * @param b chaîne 2
 * @param isAsc ordre de tri
 */
export function compareIgnoringCase(a: string, b: string, isAsc: SortDirection): number {
    const rankShift: number = a.toLowerCase().localeCompare(b.toLowerCase());
    if (isAsc === 'asc') {
        return rankShift;
    }

    return rankShift * -1;
}
