import {Component, Input, ViewChild} from '@angular/core';
import {MatSort, SortDirection} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {ProcessDefinitionsKeyIconRegistryService} from '@core/services/process-definitions-key-icon-registry.service';
import {RequestToStudy} from '@core/services/tasks-aggregator/request-to-study.interface';
import {ProcessDefinitionEnum} from '@core/services/tasks/process-definition.enum';
import {PROCESS_DEFINITION_KEY_TYPES} from '@shared/models/title-icon-type';
import {PaginatorComponent} from '@shared/paginator/paginator.component';
import {ProcessDefinitionKeyTranslatePipe} from '@shared/pipes/process-definition-key-translate.pipe';
import {compareDates, compareIgnoringCase} from '@shared/utils/comparators-utils';

@Component({
    selector: 'app-tasks',
    templateUrl: './tasks.component.html',
    styleUrls: ['./tasks.component.scss']
})
export class TasksComponent {

    requestsToStudyDisplayedColumns: string[] = ['receivedDate', 'processDefinitionKey', 'description', 'status'];
    dataSource: MatTableDataSource<RequestToStudy>;
    restrictedDatasetIcon = 'key_icon_88_blue_definition_key';
    selfdataIcon = 'self_data_icon_definition_key';
    newDatasetRequestIcon = 'nouvelles_donnees_definition_key';
    projectIcon = 'project_definition_key';

    @Input() loading = false;

    @Input()
    set requestsToStudy(requestsToStudy: RequestToStudy[]) {
        this._requestsToStudy = requestsToStudy;
        this.dataSource = new MatTableDataSource<RequestToStudy>(this.requestsToStudy);
        this.dataSource.paginator = this.paginator;
        this.dataSource.sort = this.sort;
        this.dataSource.sortData = (data: RequestToStudy[], sort: MatSort) => {
            return data.sort((a: RequestToStudy, b: RequestToStudy) => {
                if (sort.active === 'processDefinitionKey') {
                    return this.compareProcessDefinitions(a[sort.active], b[sort.active], sort.direction);
                } else if (sort.active === 'receivedDate') {
                    return compareDates(a[sort.active], b[sort.active], sort.direction);
                }
                return compareIgnoringCase(a[sort.active], b[sort.active], sort.direction);
            });
        };
    }

    @ViewChild(PaginatorComponent) paginator: PaginatorComponent;
    @ViewChild(MatSort) sort: MatSort;
    private _requestsToStudy: RequestToStudy[];

    get requestsToStudy(): RequestToStudy[] {
        return this._requestsToStudy;
    }

    constructor(
        processDefinitionsKeyIconRegistryService: ProcessDefinitionsKeyIconRegistryService,
        private readonly processDefinitionKeyTranslatePipe: ProcessDefinitionKeyTranslatePipe
    ) {
        processDefinitionsKeyIconRegistryService.addAllSvgIcons(PROCESS_DEFINITION_KEY_TYPES);
    }

    // tslint:disable-next-line:no-any type de retour any[] obligatoire
    getRouterLinkUrl(request: RequestToStudy): any[] {
        if (request.url) {
            return ['..', request.url, request.taskId];
        }

        return null;
    }

    getIcon(value: RequestToStudy): string {
        let icon: string;
        if (value.processDefinitionKey === ProcessDefinitionEnum.LINKED_DATASET_PROCESS) {
            icon = this.restrictedDatasetIcon;
        } else if (value.processDefinitionKey === ProcessDefinitionEnum.NEW_DATASET_REQUEST_PROCESS) {
            icon = this.newDatasetRequestIcon;
        } else if (value.processDefinitionKey === ProcessDefinitionEnum.SELFDATA_INFORMATION_REQUEST_Process) {
            icon = this.selfdataIcon;
        } else if (value.processDefinitionKey === ProcessDefinitionEnum.PROJECT_PROCESS) {
            icon = this.projectIcon;
        }

        return icon;
    }

    private compareProcessDefinitions(a: string, b: string, isAsc: SortDirection): number {
        const aString = this.processDefinitionKeyTranslatePipe.transform(a);
        const bString = this.processDefinitionKeyTranslatePipe.transform(b);
        return compareIgnoringCase(aString, bString, isAsc);
    }
}
