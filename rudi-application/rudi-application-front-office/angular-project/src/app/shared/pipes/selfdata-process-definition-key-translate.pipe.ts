import {Pipe, PipeTransform} from '@angular/core';
import {ProcessDefinitionEnum} from '../../core/services/tasks/process-definition.enum';
import {TranslateService} from '@ngx-translate/core';

/**
 *
 */
@Pipe({name: 'selfdataProcessDefinitionKeyTranslate'})
export class SelfdataProcessDefinitionKeyTranslatePipe implements PipeTransform {

    constructor(
        private readonly translateService: TranslateService,
    ) {
    }

    // TODO rajouter Information et Effacement au fur et à mesure de l'avancement des dev des differents workflows
    transform(value: string): string {
        let result: string;
        if (value === ProcessDefinitionEnum.SELFDATA_INFORMATION_REQUEST_Process) {
            result = this.translateService.instant('personalSpace.myNotifications.access');
        } else {
            result = '-';
        }
        return result;
    }
}
