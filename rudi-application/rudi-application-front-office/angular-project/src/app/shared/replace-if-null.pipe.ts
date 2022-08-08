import {Pipe, PipeTransform} from '@angular/core';

/**
 * Pipe permettant de remplacer un string valant NUL ou vide par une chaîne par défaut
 * si la valeur par défaut n'est pas fournie, elle vaut '-'
 */
@Pipe({name: 'replaceIfNullPipe'})
export class ReplaceIfNullPipe implements PipeTransform {
    transform(value: string, replacement?: string): string {
        if (!replacement) {
            replacement = '–';
        }
        return (value == null || value === '') ? replacement : value;
    }
}
