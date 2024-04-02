import {Pipe, PipeTransform} from '@angular/core';
import {ProcessDefinitionEnum} from '@core/services/tasks/process-definition.enum';
import {TranslateService} from '@ngx-translate/core';

/**
 *
 */
@Pipe({name: 'processDefinitionKeyTranslate'})
export class ProcessDefinitionKeyTranslatePipe implements PipeTransform {

    constructor(
        private readonly translateService: TranslateService,
    ) {
    }

    transform(value: string): string {
        let result: string;
        if (value === ProcessDefinitionEnum.LINKED_DATASET_PROCESS) {
            result = this.translateService.instant('personalSpace.myNotifications.access');
        } else if (value === ProcessDefinitionEnum.NEW_DATASET_REQUEST_PROCESS) {
            result = this.translateService.instant('personalSpace.myNotifications.newRequest');
        } else if (value === ProcessDefinitionEnum.SELFDATA_INFORMATION_REQUEST_Process) {
            result = this.translateService.instant('personalSpace.myNotifications.selfdata');
        } else if (value === ProcessDefinitionEnum.PROJECT_PROCESS) {
            result = this.translateService.instant('personalSpace.myNotifications.project');
        } else {
            result = '-';
        }
        return result;
    }
}
