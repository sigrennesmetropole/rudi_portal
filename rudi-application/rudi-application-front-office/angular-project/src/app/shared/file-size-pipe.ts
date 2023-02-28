import {Pipe, PipeTransform} from '@angular/core';

const KILO_PER_10 = 102.4;
const KILO = 1024;
const MEGA = 1024 * 1024;

/**
 * Convert a file size to KB by default
 * We pass through a superior unit when size is greater than 1024*1024 targetUnit
 * If size < 102,4 Byte, any convertion is done
 * Example : 690 Byte = 0,67 Ko
 *           706560 Byte = 690 Ko
 *           1048576 Byte = 1024 Ko
 *           15728640 Byte = 15360 Ko
 *           1073741824 Byte = 1048576 Ko = 1024 Mo
 */
@Pipe({
    name: 'fileSize'
})
export class FileSizePipe implements PipeTransform {
    transform(size: number) {
        let extension: string;
        let orignalSize: number = size;
        if (size < KILO_PER_10) {
            extension = 'o';
            return size + ' ' + extension;
        }
        let i: number = 1;
        while (size >= KILO * MEGA) {
            size = size / KILO;
            i += 1;
        }
        if (i === 1) {
            extension = 'Ko';
        } else if (i === 2) {
            extension = 'Mo';
        } else { // Dans tous les autres cas, on donne l'extension en Go
            extension = 'Go';
        }
        // Formatage du résultat pour insérer des espaces après chaque millier
        return Math.ceil((orignalSize / Math.pow(1024, i))).toLocaleString() + ' ' + extension;
    }
}
