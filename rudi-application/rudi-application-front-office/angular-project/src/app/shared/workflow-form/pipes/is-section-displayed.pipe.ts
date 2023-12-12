import {Pipe, PipeTransform} from '@angular/core';
import {Section} from '@app/projekt/projekt-api';

@Pipe({
    name: 'isSectionDisplayed'
})
export class IsSectionDisplayedPipe implements PipeTransform {
    transform(section: Section): boolean {
        return !(section == null || section.label == null || section.label === '');
    }
}
