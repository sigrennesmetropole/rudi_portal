import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'parseInt'
})
export class ParseIntPipe implements PipeTransform {

    transform(stringValue: unknown, defaultValue = 0): number {
        try {
            if (typeof stringValue === 'string') {
                return +stringValue;
            }
        } catch (err) {
            console.error('Failed to parse integer from string', stringValue, err);
        }
        return defaultValue;
    }

}
