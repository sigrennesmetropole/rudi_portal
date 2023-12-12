import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {LogService} from '@core/services/log.service';
import {ALL_TYPES} from '@shared/models/title-icon-type';
import {IconRegistryService} from '@core/services/icon-registry.service';
import * as moment from 'moment/moment';
import {TaskDetailComponent} from '@shared/task-detail/task-detail.component';

import {MatDialog} from '@angular/material/dialog';
import {TranslateService} from '@ngx-translate/core';
import {SnackBarService} from '@core/services/snack-bar.service';
import {Form, SelfdataInformationRequest} from '@app/selfdata/selfdata-api';
import {Level} from '@shared/notification-template/notification-template.component';
import {SelfdataInformationRequestDetailService} from '@core/services/selfdata-information-request-detail.service';
import {
    SelfdataInformationRequestTaskMetierService
} from '@core/services/tasks/selfdata/selfdata-information-request-task-metier.service';
import {
    SelfdataInformationRequestDependencies,
    SelfdataInformationRequestTask,
    SelfdataInformationRequestTaskDependenciesService,
    SelfdataInformationRequestTaskDependencyFetchers
} from '@core/services/tasks/selfdata/selfdata-information-request-task-dependencies.service';
import {map, tap} from 'rxjs/operators';
import {injectDependencies} from '@shared/utils/dependencies-utils';
import {SelfdataTaskSearchCriteria} from '@core/services/tasks/selfdata/selfdata-task-search-criteria.interface';
import {Period} from '@app/api-kaccess';
import {RequestDetailDependencies} from '@app/personal-space/pages/request-detail-dependencies';
import UnitEnum = Period.UnitEnum;


@Component({
    selector: 'app-selfdata-information-request-task-detail',
    templateUrl: './selfdata-information-request-task-detail.component.html',
    styleUrls: ['./selfdata-information-request-task-detail.component.scss']
})
export class SelfdataInformationRequestTaskDetailComponent
    extends TaskDetailComponent<SelfdataInformationRequest, SelfdataInformationRequestDependencies,
        SelfdataInformationRequestTask, SelfdataTaskSearchCriteria>
    implements OnInit {

    dependencies: RequestDetailDependencies;
    taskLoading: boolean;
    formLoading: boolean;
    formToDisplay: Form;

    constructor(private readonly router: Router,
                private readonly route: ActivatedRoute,
                private readonly selfdataInformationRequestDetailService: SelfdataInformationRequestDetailService,
                private readonly selfdataInformationRequestTaskDependencyFetchers: SelfdataInformationRequestTaskDependencyFetchers,
                protected logger: LogService,
                iconRegistryService: IconRegistryService,
                dialog: MatDialog,
                translateService: TranslateService,
                snackBarService: SnackBarService,
                taskWithDependenciesService: SelfdataInformationRequestTaskDependenciesService,
                taskMetierService: SelfdataInformationRequestTaskMetierService,
    ) {
        super(dialog, translateService, snackBarService, taskWithDependenciesService, taskMetierService, logger);
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.getForm(params.taskId);
            this.taskId = params.taskId;
        });
    }

    set taskId(idTask: string) {
        if (idTask) {
            this.taskLoading = true;
            this.taskWithDependenciesService.getTaskWithDependencies(idTask).pipe(
                tap(taskWithDependencies => this.taskWithDependencies = taskWithDependencies),
                injectDependencies({
                    initiatorInfo: this.selfdataInformationRequestTaskDependencyFetchers.initiatorInfo,
                    dataset: this.selfdataInformationRequestTaskDependencyFetchers.dataset,
                }),
                map(({task, asset, dependencies}) => {
                    return {
                        ownerName: dependencies.initiatorInfo,
                        ownerEmail: task.initiator,
                        receivedDate: moment(task.creationDate),
                        datasetTitle: asset.description,
                        taskStatus: asset.functional_status,
                        processDefinitionKey: asset.process_definition_key,
                        expiredDate: moment(
                            this.getSelfDataEndTreatmentDay(
                                dependencies.dataset.ext_metadata.ext_selfdata.ext_selfdata_content.treatment_period, asset.updated_date)),
                    };
                })
            ).subscribe({
                next: (dependencies: RequestDetailDependencies) => {
                    this.taskLoading = false;
                    this.dependencies = dependencies;
                },
                error: (error) => {
                    this.taskLoading = false;
                    console.error(error);
                }
            });
        }
    }


    getForm(idTask: string): void {
        this.formLoading = true;
        this.selfdataInformationRequestDetailService.lookupFilledMatchingDataForm(idTask).subscribe({
            next: (result: Form) => {
                this.formToDisplay = result;
            },
            complete: () => {
                this.formLoading = false;
            },
            error: (e) => {
                this.formLoading = false;
                console.error(e);
                this.snackBarService.openSnackBar({
                    message: this.translateService.instant('error.technicalError'),
                    level: Level.ERROR
                });
            }
        });
    }

    getSelfDataEndTreatmentDay(period: Period, receivedDate: string): string {
        const date = new Date(receivedDate);
        switch (period.unit) {
            case UnitEnum.Days:
                date.setDate(date.getDate() + period.value);
                break;
            case UnitEnum.Months:
                date.setMonth(date.getMonth() + period.value);
                break;
            case UnitEnum.Years:
                date.setFullYear(date.getFullYear() + period.value);
                break;
        }

        return date.toString();
    }

    protected goBackToList(): Promise<boolean> {
        return this.router.navigate(['/personal-space/my-notifications']);
    }
}
