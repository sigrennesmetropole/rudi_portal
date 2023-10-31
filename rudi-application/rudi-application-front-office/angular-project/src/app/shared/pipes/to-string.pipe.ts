import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
    name: 'toString'
})
export class ToStringPipe implements PipeTransform {
    transform(value?: string | Array<{ label?: string }>): string {
        if (Array.isArray(value)) {
            return value.map((val) => val.label).filter(Boolean).join(' ');
        }
        return value ?? '';
    }
}
