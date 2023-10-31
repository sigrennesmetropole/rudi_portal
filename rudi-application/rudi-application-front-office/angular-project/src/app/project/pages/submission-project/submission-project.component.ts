import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {RadioListItem} from '../../../shared/radio-list/radio-list-item';
import {FormProjectDependencies, ProjectSubmissionService} from '../../../core/services/asset/project/project-submission.service';
import {Confidentiality, OwnerType, Project, Support, TargetAudience, TerritorialScale} from '../../../projekt/projekt-model';
import {ProjektMetierService} from '../../../core/services/asset/project/projekt-metier.service';
import {FiltersService} from '../../../core/services/filters.service';
import {ReuseProjectCommonComponent} from '../../components/reuse-project-common/reuse-project-common.component';
import {MatHorizontalStepper} from '@angular/material/stepper';
import {User} from '../../../acl/acl-api';
import {ProjectDatasetItem} from '../../model/project-dataset-item';
import {DataRequestItem} from '../../model/data-request-item';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {UpdateAction} from '../../model/upate-action';
import {Router} from '@angular/router';
import {RequestDetails} from '../../../shared/models/request-details';
import {Metadata} from '../../../api-kaccess';
import {Observable} from 'rxjs';
import {catchError, switchMap, tap} from 'rxjs/operators';
import {RedirectService} from '../../../core/services/redirect.service';
import {CloseEvent, DialogClosedData} from '../../../data-set/models/dialog-closed-data';
import {AuthenticationService} from '../../../core/services/authentication.service';
import {AccessStatusFiltersType} from '../../../core/services/filters/access-status-filters-type';
import {ReutilisationStatus} from '../../../projekt/projekt-api';

@Component({
    selector: 'app-submission-reuse',
    templateUrl: './submission-project.component.html',
    styleUrls: ['./submission-project.component.scss']
})
export class SubmissionProjectComponent extends ReuseProjectCommonComponent implements OnInit, OnDestroy {

    @ViewChild(MatHorizontalStepper)
    public stepper: MatHorizontalStepper;

    public publicCible: TargetAudience[];
    public territorialScales: TerritorialScale[];
    public AccessStatusFiltersType = AccessStatusFiltersType;

    public suggestions: RadioListItem[];
    public supports: Support[];
    user: User;

    private confidentialities: Confidentiality[];

    public reuseStatus: ReutilisationStatus[];

    /**
     * L'image du projet qui a été sauvegardée, pour savoir si une mise à jour a eu lieu
     * @private
     */
    private projectImageSaved: Blob;

    /**
     * L'action de mise à jour d'image à apppliquer pour un projet
     * @private
     */
    private updateImageAction: UpdateAction;

    constructor(
        readonly projektMetierService: ProjektMetierService,
        readonly filtersService: FiltersService,
        readonly projectSubmissionService: ProjectSubmissionService,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
        private readonly formBuilder: FormBuilder,
        private readonly router: Router,
        private readonly redirectService: RedirectService,
        private readonly authenticationService: AuthenticationService
    ) {
        super(projektMetierService, filtersService, projectSubmissionService);
    }

    get createdProjectLink(): string {
        return this.createdProject ? `/projets/detail/${this.createdProject.uuid}` : undefined;
    }

    ngOnInit(): void {

        this.suggestions = [];
        this.step1FormGroup = this.projectSubmissionService.initStep1ProjectFormGroup();
        this.step2FormGroup = this.projectSubmissionService.initStep2FormGroup();
        this.step3FormGroup = this.formBuilder.group({
            uuid: [''],
        });

        this.isLoading = true;
        this.projectSubmissionService.loadDependenciesProject().subscribe(
            (dependencies: FormProjectDependencies) => {
                this.isLoading = false;
                this.suggestions = this.projectSubmissionService.getConfidentialitiesRadio(dependencies.confidentialities);
                this.confidentialities = dependencies.confidentialities;
                this.publicCible = dependencies.projectPublicCible;
                this.territorialScales = dependencies.territorialScales;
                this.projectType = dependencies.projectTypes;
                this.supports = dependencies.supports;
                this.user = dependencies.user;
                this.reuseStatus = dependencies.reuseStatus;
                if (this.user) {
                    this.step2FormGroup.setValue({
                        ownerType: OwnerType.User,
                        lastname: this.user.lastname,
                        firstname: this.user.firstname,
                        organizationUuid: null,
                        contactEmail: this.user.login
                    });
                }
                if (dependencies.organizations?.length) {
                    this.organizationItems = dependencies.organizations.map(organization => ({
                        name: organization.name,
                        uuid: organization.uuid
                    }));
                } else {
                    this.organizationItems = [];
                }
            }
        );
    }

    /**
     * Ouverture de la popin d'ajout d'un JDD lié au projet puis ajout dans la liste
     */
    openDialogAddLinkedDataset(): void {
        super.openDialogSelectMetadata(undefined, [AccessStatusFiltersType.GdprSensitive]);
    }

    /**
     * Ouverture de la popin d'ajout de nouvelle demande de JDD pour le projet puis ajout dans la liste
     */
    openDialogAskNewDatasetRequest(): void {
        this.projectSubmissionService.openDialogAskNewDatasetRequest(this.datasetRequests.length)
            .subscribe((dataRequestItem: DataRequestItem) => {
                if (dataRequestItem) {
                    const view = this.projectSubmissionService.dataRequestToProjectDatasetItem(dataRequestItem);
                    this.datasetRequests.unshift(dataRequestItem);
                    this.projectDatasetItems.unshift(view);
                    this.mapDatasetItemViewModel.set(view, dataRequestItem);
                    this.mapDatasetItemModelView.set(dataRequestItem, view);
                    this.linkedDatasetsError = false;
                }
            });
    }

    /**
     * Ouverture de la popin d'édition d'une nouvelle demande de JDD
     * @param originalView la demande de JDD originale
     */
    openDialogEditNewDatasetRequest(originalView: ProjectDatasetItem): void {
        const original: DataRequestItem = this.mapDatasetItemViewModel.get(originalView);
        // On récupère l'index de la demande, obligé de faire reverse() non destructif avec [...] car les demandes sont rangées inversement
        const indexOfOriginal = [...this.datasetRequests].reverse().indexOf(original) + 1;
        this.projectSubmissionService.openDialogEditNewDatasetRequest(original, indexOfOriginal)
            .subscribe((modified: DataRequestItem) => {
                if (modified) {
                    const view = this.mapDatasetItemModelView.get(original);
                    view.title = modified.title;
                    original.title = modified.title;
                    original.description = modified.description;
                    this.linkedDatasetsError = false;
                }
            }, (e) => {
                console.error(e);
            });
    }

    /**
     * Ouverture de la popin d'édition d'une demande d'accès à un JDD
     * @param originalView la demande d'accès originale
     */
    openDialogEditRequestDetails(originalView: ProjectDatasetItem): void {
        const original: Metadata = this.mapDatasetItemViewModel.get(originalView);
        const request: RequestDetails = this.mapRequestDetailsByDatasetUuid.get(original.global_id);
        this.projectSubmissionService.openDialogEditRequest(request)
            .subscribe((modified: DialogClosedData<RequestDetails>) => {
                if (modified && modified.closeEvent === CloseEvent.VALIDATION) {
                    request.comment = modified.data.comment;
                    request.endDate = modified.data.endDate;
                }
            }, (e) => {
                console.error(e);
            });
    }

    /**
     * Gestion de l'édition d'un élément de la liste des éléments liés au projet
     * @param item l'item édité pour savoir si on agit sur une nouvelle demande ou une demande d'accès
     */
    handleItemEdited(item: ProjectDatasetItem): void {
        const value = this.mapDatasetItemViewModel.get(item);
        if (this.datasetRequests.includes(value)) {
            this.openDialogEditNewDatasetRequest(item);
        } else if (this.linkedDatasets.includes(value)) {
            this.openDialogEditRequestDetails(item);
        }
    }

    /**
     * Gestion de la suppression d'un élément de la liste des éléments liés au projet
     * @param item l'item supprimé pour savoir si on agit sur un JDD ou une demande par exemple
     */
    handleItemRemoved(item: ProjectDatasetItem): void {
        const value = this.mapDatasetItemViewModel.get(item);
        if (this.linkedDatasets.includes(value)) {
            super.removeDataset(item);
        } else if (this.datasetRequests.includes(value)) {
            this.removeDataRequest(item);
        }
    }

    /**
     * Retrait d'un item : nouvelle demande, de la liste des éléments liés au projet
     * @param item l'item : nouvelle demande retiré de la liste
     */
    public removeDataRequest(item: ProjectDatasetItem): void {
        const demand = this.mapDatasetItemViewModel.get(item);
        const viewIndexToRemove = this.projectDatasetItems.indexOf(item);
        this.mapDatasetItemViewModel.delete(item);
        this.mapDatasetItemModelView.delete(demand);
        const indexToRemove = this.datasetRequests.indexOf(demand);
        if (indexToRemove >= 0) {
            this.datasetRequests.splice(indexToRemove, 1);
        }
        if (viewIndexToRemove >= 0) {
            this.projectDatasetItems.splice(viewIndexToRemove, 1);
        }
    }

    /**
     * On gère le fait que l'image saisie a l'étape 1 change
     * @param image l'image qui a été saisie
     */
    handleImageChanged(image: Blob): void {
        // On détermine l'action de mise à jour à faire si on update le projet
        if (this.projectImageSaved == null && image) {
            this.updateImageAction = UpdateAction.AJOUT;
        } // Si l'utillisateur saisit une image et qu'il y'en avait déjà une avant ET si leurs tailles sont différentes
        // Comparaison de blob nécessite fileReader qui bloque le Thread
        else if (this.projectImageSaved && image && this.projectImageSaved.size !== image.size) {
            this.updateImageAction = UpdateAction.MISE_A_JOUR;
        } else if (this.projectImageSaved && image == null) {
            this.updateImageAction = UpdateAction.SUPPRESSION;
        }
    }

    /**
     * Création de l'objet Project à partir de l'état du formulaire de saisie
     * @private
     */
    private createProjectFromForm(): Project {
        return this.projectSubmissionService.projectFormGroupToProject(
            this.step1FormGroup,
            this.step2FormGroup,
            this.user,
            this.projectSubmissionService.searchProjectType(
                this.step1FormGroup.get('type').value,
                this.projectType
            ),
            this.projectSubmissionService.searchConfidentiality(
                this.step1FormGroup.get('confidentiality').value, this.confidentialities
            ),
            this.projectSubmissionService.findCorrespondingReutilisationStatus(
                this.step1FormGroup.get('reuse_status').value.code, this.reuseStatus
            )
        );
    }

    /**
     * MAJ de l'objet Project à partir de l'état du formulaire de saisie
     * @private
     */
    private updateProjectFromForm(): void {
        const updated = this.createProjectFromForm();
        this.projectSubmissionService.updateProjectField(this.createdProject, updated);
    }

    /**
     * Action au clic de l'enregistrement du projet : persistence du projet + de ses liens
     * Gestion de la mise à jour également car pour un projet on peut mettre à jour les liens
     * en restant sur cet écran
     */
    save(): void {
        // Si le token a expiré on annule tout et le renvoie vers le form de connexion
        if (!this.authenticationService.isAuthenticatedWithToken()) {
            this.redirectToLogin();
            return;
        }

        // Recuperation de l'image projet
        let image: Blob;
        if (this.step1FormGroup.get('image').value != null) {
            image = this.step1FormGroup.get('image').value.file;
        }

        // Si projet existant on met à jour
        if (this.createdProject) {
            this.updateProjectFromForm();
            this.isLoading = true;
            this.projectSubmissionService.updateProject(this.createdProject, this.linkedDatasets, this.datasetRequests,
                this.mapRequestDetailsByDatasetUuid, image, this.updateImageAction).subscribe(
                (created: Project) => {
                    this.onSaveOrCreateSuccess(image, created);
                    this.projectSubmissionService.openDialogSuccess();
                },
                (e) => {
                    console.error(e);
                    this.snackBarService.add(
                        this.translateService.instant('project.stepper.submission.publish.error-update')
                    );
                    this.isLoading = false;
                }
            );
        }
        // Sinon on créé le projet en + des liens
        else {
            const project: Project = this.createProjectFromForm();
            this.isLoading = true;
            this.projectSubmissionService.createProject(project, this.linkedDatasets, this.datasetRequests,
                image, this.mapRequestDetailsByDatasetUuid).subscribe((created: Project) => {
                this.onSaveOrCreateSuccess(image, created);
                this.projectSubmissionService.openDialogSuccess();
            }, e => {
                console.error(e);
                this.snackBarService.add(this.translateService.instant('project.stepper.submission.publish.error'));
                this.isLoading = false;
            });
        }
    }

    /**
     * Callback appelée lors du succès de l'enregistrement/création du projet
     * @param image l'image qui a été créée
     * @param created le projet créé
     * @private
     */
    private onSaveOrCreateSuccess(image: Blob, created: Project): void {
        this.projectImageSaved = image;
        this.updateUuidDatasetItems();
        this.isLoading = false;
        this.createdProject = created;
    }

    ngOnDestroy(): void {
        if (this.dialogWasOpened) {
            this.filtersService.restoreFilters();
        }
    }

    clickCancel(): Promise<boolean> {
        return this.router.navigate(['/projets']);
    }

    /**
     * Soumission si aumoins une demande ajoutée au projet
     */
    public get isSubmit(): boolean {
        return (this.datasetRequests.length > 0 || this.linkedDatasets.length > 0);
    }

    /**
     * Détermine si on doit MAj ou créer le projet
     * @private
     */
    private getUpdateOrCreateAction(): Observable<Project> {

        // Recuperation de l'image projet
        let image: Blob;
        if (this.step1FormGroup.get('image').value != null) {
            image = this.step1FormGroup.get('image').value.file;
        }

        // On va créer ou mettre à jour le projet
        let createOrUpdate: Observable<Project>;

        // Si edition du projet on MAJ que les liens
        if (this.createdProject) {
            createOrUpdate = this.projectSubmissionService.updateProject(this.createdProject, this.linkedDatasets, this.datasetRequests,
                this.mapRequestDetailsByDatasetUuid, image, this.updateImageAction);
        }
        // Sinon on crée le projet en + des liens
        else {
            const project: Project = this.createProjectFromForm();
            createOrUpdate = this.projectSubmissionService.createProject(project, this.linkedDatasets, this.datasetRequests,
                image, this.mapRequestDetailsByDatasetUuid).pipe(
                tap(created => this.createdProject = created)
            );
        }

        return createOrUpdate;
    }

    private redirectToLogin(): void {
        this.router.navigate
        (['/login'],
            {
                queryParams: {
                    snackBar: 'project.buttonPopover.genericUnauthorizedMessage'
                }
            }
        );
    }

    /**
     * Methode permettant de soumettre le projet, géstion création ou mise à jour puis soumission
     */
    submit(): void {
        // Si le token a expiré on annule tout et le renvoie vers le form de connexion
        if (!this.authenticationService.isAuthenticatedWithToken()) {
            this.redirectToLogin();
            return;
        }

        // chargement du loader
        this.isLoading = true;

        // On détermine si on va créer ou mettre à jour le projet
        const createOrUpdate: Observable<Project> = this.getUpdateOrCreateAction();

        // Lancement création ou MAJ du project
        createOrUpdate.pipe(
            // Si erreur project on l'indique techniquement
            catchError((e) => {
                console.error(e);
                throw Error('Erreur lors de la création/mise à jour du Project');
            }),

            // Création/MAJ est OK, on démarre le workflow
            switchMap(project => {
                return this.projectSubmissionService.submitProject(project).pipe(
                    // Si erreur workflow on l'indique techniquement
                    catchError((e) => {
                        console.error(e);
                        throw Error('Erreur lors du lancement du workflow ');
                    })
                );
            })
        ).subscribe({
                next: () => {
                    this.isSubmitted = true;
                    this.isLoading = false;
                },
                error: (e) => {
                    console.error(e);
                    this.snackBarService.add(this.translateService.instant('project.stepper.submission.publish.error'));
                    this.isLoading = false;
                }
            }
        );
    }
}
