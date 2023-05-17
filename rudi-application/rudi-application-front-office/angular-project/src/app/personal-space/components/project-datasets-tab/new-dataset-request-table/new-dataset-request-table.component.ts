import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {LiveAnnouncer} from '@angular/cdk/a11y';
import {NewDatasetRequest, NewDatasetRequestStatus} from '../../../../projekt/projekt-api';
import * as moment from 'moment';
import {ProjectSubmissionService} from '../../../../core/services/asset/project/project-submission.service';
import {DataRequestItem} from '../../../../project/model/data-request-item';
import {SnackBarService} from '../../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {NewDatasetRequestTableData, RowTableData} from '../dataset.interface';
import {DialogSubscribeDatasetsService} from '../../../../core/services/dialog-subscribe-datasets.service';
import {DialogClosedData} from '../../../../data-set/models/dialog-closed-data';
import {ProjectConsultationService} from '../../../../core/services/asset/project/project-consultation.service';


@Component({
    selector: 'app-new-dataset-request-table',
    templateUrl: './new-dataset-request-table.component.html',
    styleUrls: ['./new-dataset-request-table.component.scss']
})
export class NewDatasetRequestTableComponent {
    newDatasetsRequest: NewDatasetRequestTableData[] = [];
    displayedColumns: string[] = ['addedDate', 'title', 'status', 'delete-img'];
    dataSource: MatTableDataSource<NewDatasetRequestTableData> = new MatTableDataSource(this.newDatasetsRequest);
    @Output() requestUuidEmitter: EventEmitter<string> = new EventEmitter<string>();

    /**
     * Boolean indiquant l'état du tableau (chargement en cours)
     */
    @Input()
    tableLoading = true;
    /**
     * Boolen permettant de désactiver le bouton d'ajout (si un autre ajout est déjà en cours dans un tableau)
     */
    @Input()
    disableAddButton = false;
    @Input()
    hasAddButton = false;
    @Output()
    addNewDatasetRequestEvent = new EventEmitter<NewDatasetRequest>();
    @Output()
    addingElementToNewDatasetTable = new EventEmitter<boolean>();

    constructor(
        private _liveAnnouncer: LiveAnnouncer,
        private readonly projectSubmissionService: ProjectSubmissionService,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
        private readonly personalSpaceProjectService: DialogSubscribeDatasetsService,
        private readonly projectConsultationService: ProjectConsultationService,
    ) {
    }

    @Input()
    set newDatasetRequestList(value: NewDatasetRequest[]) {
        if (value) {
            this.newDatasetsRequest = value.map((element: NewDatasetRequest) => {
                return {
                    addedDate: moment(element.creation_date).format('DD/MM/YYYY'),
                    description: element.description,
                    status: element.status,
                    functional_status: element.functional_status,
                    uuid: element.uuid
                };
            });
        }
        this.dataSource = new MatTableDataSource(this.newDatasetsRequest);
    }

    openNewDatasetRequestPopin(): void {
        this.projectSubmissionService.openDialogAskNewDatasetRequest(this.newDatasetsRequest.length).subscribe({
            next: (newRequest: DataRequestItem) => {
                const newDatasetRequestToAdd: NewDatasetRequest = {
                    title: newRequest.title,
                    description: newRequest.description,
                    new_dataset_request_status: NewDatasetRequestStatus.InProgress,
                    object_type: 'NewDatasetRequest'
                };
                this.addNewDatasetRequestEvent.emit(newDatasetRequestToAdd);
            },
            error: err => {
                console.error(err);
            }
        });
    }

    /**
     * Méthode appelée lors du click sur le picto de suppression
     * @param rowTableData
     */
    deleteRequest(rowTableData: RowTableData): void {
        if (!this.projectConsultationService.isRowDelatable(rowTableData)) {
            return;
        } else {
            this.personalSpaceProjectService.openDialogDeletionConfirmation(rowTableData.uuid)
                .subscribe({
                    next: (result: DialogClosedData<string>) => {
                        if (result.data) {
                            this.requestUuidEmitter.emit(result.data);
                        }
                    },
                    error: (e) => {
                        console.error(e);
                    }
                });
        }
    }
}
