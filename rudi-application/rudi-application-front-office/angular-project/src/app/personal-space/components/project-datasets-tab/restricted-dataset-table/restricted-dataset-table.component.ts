import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {LiveAnnouncer} from '@angular/cdk/a11y';
import {LinkedDatasetMetadatas} from '../../../../core/services/asset/project/project-dependencies.service';
import * as moment from 'moment';
import {AccessStatusFiltersType} from '../../../../core/services/filters/access-status-filters-type';
import {Metadata} from '../../../../api-kaccess';
import {ProjectSubmissionService} from '../../../../core/services/asset/project/project-submission.service';
import {LinkedDatasetFromProject} from '../../../../data-set/models/linked-dataset-from-project';
import {SnackBarService} from '../../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {DatasetsTableData, RowTableData} from '../dataset.interface';
import {DialogSubscribeDatasetsService} from '../../../../core/services/dialog-subscribe-datasets.service';
import {DialogClosedData} from '../../../../data-set/models/dialog-closed-data';
import {ProjectConsultationService} from '../../../../core/services/asset/project/project-consultation.service';
import {ProjektMetierService} from '../../../../core/services/asset/project/projekt-metier.service';

@Component({
    selector: 'app-restricted-dataset-table',
    templateUrl: './restricted-dataset-table.component.html',
    styleUrls: ['./restricted-dataset-table.component.scss']
})
export class RestrictedDatasetTableComponent {
    restrictedDatasets: DatasetsTableData[] = [];
    displayedColumns: string[] = ['addedDate', 'title', 'status', 'delete-img'];
    dataSource: MatTableDataSource<DatasetsTableData> = new MatTableDataSource(this.restrictedDatasets);
    @Output() requestUuidEmitter: EventEmitter<string> = new EventEmitter<string>();

    /**
     * Boolean indiquant l'état du tableau (chargement des données fini ou pas)
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
    associatedMetadatas: Metadata[] = [];
    @Output()
    addRestrictedLinkedDatasetEvent = new EventEmitter<LinkedDatasetFromProject>();
    @Output()
    addingElementToRestrictedTable = new EventEmitter<boolean>();


    constructor(
        private _liveAnnouncer: LiveAnnouncer,
        private readonly projectSubmissionService: ProjectSubmissionService,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
        private readonly personalSpaceProjectService: DialogSubscribeDatasetsService,
        private readonly projectConsultationService: ProjectConsultationService,
        private readonly projektMetierService: ProjektMetierService,
    ) {
    }

    @Input()
    set restrictedDatasetsList(value: LinkedDatasetMetadatas[]) {
        this.associatedMetadatas = [];
        if (value) {
            this.restrictedDatasets = this.projektMetierService.getDatasetsByUpdatedDate(value).map((element: LinkedDatasetMetadatas) => {
                const dataset = element?.dataset;
                // Extraction des JDDs dans une variable dédiée
                this.associatedMetadatas.push(dataset);
                return {
                    addedDate: moment(element.linkedDataset.creation_date).format('DD/MM/YYYY'),
                    datasetOrganizationId: dataset?.producer?.organization_id,
                    datasetTitle: dataset?.resource_title.toString(),
                    status: element.linkedDataset.status,
                    organization_name: dataset?.producer.organization_name,
                    functional_status: element.linkedDataset.functional_status,
                    uuid: element.linkedDataset.uuid
                };
            });
        }
        this.dataSource = new MatTableDataSource(this.restrictedDatasets);
    }

    /**
     * Ouverture de la popin de selection d'un JDD restreint pour ajouter à notre projet
     */
    openRestrictedDatasetsPopin(): void {
        this.addingElementToRestrictedTable.emit(true);
        const selectedMetadata$ = this.projectSubmissionService
            .openDialogMetadata(AccessStatusFiltersType.Restricted);
        this.projectSubmissionService
            .checkLinkExistsOrCreateLinkObject(selectedMetadata$, this.associatedMetadatas)
            .subscribe({
                next: (linkToAdd: LinkedDatasetFromProject) => {
                    this.addRestrictedLinkedDatasetEvent.emit(linkToAdd);
                },
                error: err => {
                    // S'il y a eu une erreur, reactiver les boutons d'ajout
                    this.addingElementToRestrictedTable.emit(false);
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
