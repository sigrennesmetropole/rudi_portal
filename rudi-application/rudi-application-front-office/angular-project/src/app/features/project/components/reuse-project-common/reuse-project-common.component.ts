import {Component} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {CloseEvent} from '@features/data-set/models/dialog-closed-data';
import {ProjectSubmissionService} from '@core/services/asset/project/project-submission.service';
import {ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {FiltersService} from '@core/services/filters.service';
import {AccessStatusFiltersType} from '@core/services/filters/access-status-filters-type';
import {RequestDetails} from '@shared/models/request-details';
import {MetadataUtils} from '@shared/utils/metadata-utils';
import {Metadata} from 'micro_service_modules/api-kaccess';
import {Project, ProjectType} from 'micro_service_modules/projekt/projekt-model';
import {DataRequestItem} from '../../model/data-request-item';
import {OrganizationItem} from '../../model/organization-item';
import {ProjectDatasetItem} from '../../model/project-dataset-item';

@Component({
    selector: 'app-reuse-project-common',
    templateUrl: './reuse-project-common.component.html'
})
export class ReuseProjectCommonComponent {

    // Le type any est safe car on veut mapper n'importe quoi par une vue
    // tslint:disable-next-line:no-any
    public readonly mapDatasetItemViewModel: Map<ProjectDatasetItem, any> = new Map();
    // tslint:disable-next-line:no-any
    public readonly mapDatasetItemModelView: Map<any, ProjectDatasetItem> = new Map();
    public readonly mapRequestDetailsByDatasetUuid: Map<string, RequestDetails> = new Map();
    public projectDatasetItems: ProjectDatasetItem[] = [];
    public readonly linkedDatasets: Metadata[] = [];
    public readonly datasetRequests: DataRequestItem[] = [];
    public organizationItems: OrganizationItem[];

    public linkedDatasetsError = false;
    public dialogWasOpened = false;

    public step1FormGroup: FormGroup;
    public step2FormGroup: FormGroup;
    public step3FormGroup: FormGroup;

    public projectType: ProjectType[];

    public createdProject: Project;
    public isLoading: boolean;
    public isSubmitted: boolean;
    public isPublished = false;

    constructor(
        readonly projektMetierService: ProjektMetierService,
        readonly filtersService: FiltersService,
        readonly projectSubmissionService: ProjectSubmissionService
    ) {
    }

    /**
     * Retrait d'un JDD lié de la liste
     * @param item l'item visuel retiré de la liste
     */
    public removeDataset(item: ProjectDatasetItem): void {
        const dataset = this.mapDatasetItemViewModel.get(item);
        const viewIndexToRemove = this.projectDatasetItems.indexOf(item);
        this.mapDatasetItemViewModel.delete(item);
        this.mapDatasetItemModelView.delete(dataset);
        const indexToRemove = this.linkedDatasets.indexOf(dataset);
        if (indexToRemove >= 0) {
            this.linkedDatasets.splice(indexToRemove, 1);
            this.linkedDatasetsError = this.linkedDatasets.length === 0;
        }
        if (viewIndexToRemove >= 0) {
            this.projectDatasetItems.splice(viewIndexToRemove, 1);
        }
    }

    /**
     * Ouverture de la popin de saisie d'un JDD lié pour l'ajouter dans la liste
     */
    public openDialogSelectMetadata(restrictedAccessFilterValue?: AccessStatusFiltersType, restrictedAccessHiddenValues?: AccessStatusFiltersType[]): void {
        if (!this.dialogWasOpened) {
            this.filtersService.backupFilters();
            this.dialogWasOpened = true;
        }

        this.projectSubmissionService.openDialogMetadata(restrictedAccessFilterValue, restrictedAccessHiddenValues).subscribe((metadata: Metadata) => {
            if (metadata != null) {
                if (this.projectSubmissionService.isMetadataPresent(metadata.global_id, this.linkedDatasets)) {
                    return;
                }
                if (MetadataUtils.isRestricted(metadata)) {
                    this.openSecondDialogRequestDetails(metadata);
                } else {
                    this.selectMetadata(metadata);
                }
            }
        });
    }

    private openSecondDialogRequestDetails(metadata: Metadata): void {

        // Récupération de la date de fin du projet pour pré-remplir la valeur de la popin
        let endDate;
        const controlEndDate = this.step1FormGroup.get('end_date');
        if (controlEndDate && controlEndDate.value) {
            endDate = controlEndDate.value;
        }

        // Ouverture de la popin de saisie
        this.projectSubmissionService.openDialogRequestDetails(endDate).subscribe(closeData => {
            const requestDetails: RequestDetails = closeData.data;
            const buttonCliked: CloseEvent = closeData.closeEvent;
            // Si un bouton autre que la validation a été cliquée, annulez l'ajout de la demande d'accès
            if (buttonCliked !== CloseEvent.VALIDATION) {
                this.linkedDatasetsError = false;
            } else {
                this.selectMetadata(metadata, requestDetails);
            }
        });
    }

    protected selectMetadata(metadata: Metadata, requestDetails: RequestDetails = null): void {
        if (this.projectSubmissionService.isMetadataPresent(metadata.global_id, this.linkedDatasets)) {
            return;
        }
        this.step3FormGroup.get('uuid').reset();
        this.linkedDatasets.unshift(metadata);
        const view = this.projectSubmissionService.metadataToProjectDatasetItem(metadata);
        this.projectDatasetItems.unshift(view);
        this.mapDatasetItemViewModel.set(view, metadata);
        this.mapDatasetItemModelView.set(metadata, view);

        const datasetUuid = metadata.global_id;
        this.mapRequestDetailsByDatasetUuid.set(datasetUuid, requestDetails);

        this.linkedDatasetsError = false;
    }

    /**
     * Synchronise les UUIDs des éléments vues à partir des UUIDs récupérés lors de l'enregistrement côté back
     * @private
     */
    public updateUuidDatasetItems(): void {
        this.linkedDatasets.forEach((linkedDataset: Metadata) => {
            const view: ProjectDatasetItem = this.mapDatasetItemModelView.get(linkedDataset);
            if (view != null && view.identifier == null) {
                view.identifier = linkedDataset.global_id;
            }
        });

        this.datasetRequests.forEach((dataRequest: DataRequestItem) => {
            const view: ProjectDatasetItem = this.mapDatasetItemModelView.get(dataRequest);
            if (view != null && view.identifier == null) {
                view.identifier = dataRequest.uuid;
            }
        });
    }
}
