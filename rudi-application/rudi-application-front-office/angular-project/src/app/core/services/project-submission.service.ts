import {Injectable} from '@angular/core';
import {ProjektMetierService} from './projekt-metier.service';
import {RadioListItem} from '../../shared/radio-list/radio-list-item';
import {forkJoin, iif, Observable, of} from 'rxjs';
import {catchError, mapTo, switchMap, tap} from 'rxjs/operators';
import {
    Confidentiality,
    LinkedDataset,
    NewDatasetRequest,
    OwnerType,
    Project,
    ProjectStatus,
    ProjectType,
    Support,
    TargetAudience,
    TerritorialScale
} from '../../projekt/projekt-model';
import {TranslateService} from '@ngx-translate/core';
import {AbstractControl, FormBuilder, FormGroup, ValidationErrors, Validators} from '@angular/forms';
import {UserService} from './user.service';
import {User} from '../../acl/acl-api';
import {Metadata} from '../../api-kaccess';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {AddDataSetDialogComponent} from '../../project/components/add-data-set-dialog/add-data-set-dialog.component';
import {Level} from '../../shared/notification-template/notification-template.component';
import {SnackBarService} from './snack-bar.service';
import {KonsultMetierService} from './konsult-metier.service';
import {
    SuccessProjectCreationDialogComponent
} from '../../project/components/success-project-creation-dialog/success-project-creation-dialog.component';
import {ProjectDatasetItem} from '../../project/model/project-dataset-item';
import {ProjectDatasetPictoType} from '../../project/model/project-dataset-picto-type';
import {
    EditNewDataSetDialogComponent,
    NewDataSetDialogData
} from '../../project/components/edit-new-data-set-dialog/edit-new-data-set-dialog.component';
import {DataRequestItem} from '../../project/model/data-request-item';
import {AddDataSetDialogData} from '../../project/components/add-data-set-dialog/add-data-set-dialog-data';
import {RequestDetails} from '../../shared/models/request-details';
import {
    RequestDetailsDialogComponent,
    RequestDetailsDialogData
} from '../../project/components/request-details-dialog/request-details-dialog.component';
import {TitleIconType} from '../../shared/models/title-icon-type';
import {UpdateAction} from '../../project/model/upate-action';
import {DefaultMatDialogConfig} from './default-mat-dialog-config';
import {
    SelectProjectDialogComponent,
    SelectProjectDialogData
} from '../../data-set/components/select-project-dialog/select-project-dialog.component';
import {DialogClosedData} from '../../data-set/models/dialog-closed-data';
import {LinkedDatasetFromProject} from '../../data-set/models/linked-dataset-from-project';
import {Organization} from '../../strukture/strukture-model';
import {OrganizationMetierService} from './organization-metier.service';
import {Task} from 'src/app/projekt/projekt-api/model/task';
import {HttpErrorResponse} from '@angular/common/http';
import {TaskMetierService} from './task-metier.service';
import {Moment} from 'moment';
import {consistentPeriodValidator} from '../validators/consistent-period-validator';

/**
 * Liste des codes de niveaux de restriction connus
 */
const KNOWN_CONFIDENTIALITY_CODES = [
    'CONFIDENTIAL', 'OPEN'
];

const DEFAULT_CONFIDENTIALITY_CODE = 'OPEN';

/**
 * Statut d'un projet correspondant à une réutilisation
 */
const REUSE_STATUS: ProjectStatus = 'VALIDATED';

/**
 * Liste des éléments requis pour charger le formulaire de déclaration projet
 */
export interface FormProjectDependencies {
    confidentialities: Confidentiality[];
    projectPublicCible: TargetAudience[];
    territorialScales: TerritorialScale[];
    supports: Support[];
    projectTypes: ProjectType[];
    user: User;
    organizations: Organization[];
}

/**
 * Liste des éléments requis pour charger le formulaire de réutilisation
 */
export interface FormReutilisationDependencies {
    projectTypes: ProjectType[];
    projectPublicCible: TargetAudience[];
    user: User;
    organizations: Organization[];
}

const RESTRICTED_DATASET_ICON: TitleIconType = 'key_icon_88_secondary-color';

@Injectable({
    providedIn: 'root'
})
export class ProjectSubmissionService {

    /**
     * Récupère les nouvelles demandes de projets ajoutées à un projet à enregistrer côté back
     * @param dataRequests l'ensemble des demandes de projets d'un projet
     * @private
     */
    private static getNewDatasetRequestsAdded(dataRequests: DataRequestItem[]): DataRequestItem[] {
        return dataRequests.filter((dataRequest: DataRequestItem) => dataRequest.uuid == null);
    }

    /**
     * Récupère les nouvelles demandes modifiées, mets à jour les objets à envoyer au back dans la liste de retour
     * @param dataRequests la liste des items front qui ont les modifs
     * @param fromBack la liste des éléments back attachés au projet
     * @private
     */
    private static getNewDatasetRequestsEdited(dataRequests: DataRequestItem[], fromBack: NewDatasetRequest[]): NewDatasetRequest[] {
        const modified: NewDatasetRequest[] = [];
        const withUuids: DataRequestItem[] = dataRequests.filter((element: DataRequestItem) => element.uuid != null);
        withUuids.forEach((withUuid: DataRequestItem) => {
            const backItem: NewDatasetRequest = fromBack.find((backElement: NewDatasetRequest) => backElement.uuid === withUuid.uuid);
            if (backItem.title !== withUuid.title || backItem.description !== withUuid.description) {
                backItem.title = withUuid.title;
                backItem.description = withUuid.description;
                modified.push(backItem);
            }
        });

        return modified;
    }

    /**
     * Récupère les nouvelles demandes de projets supprimées d'un projet à supprimer côté back
     * @param dataRequests l'ensemble des demandes de projets d'un projet (côte front)
     * @param newDatasetRequests l'ensemble des demandes de données d'un projet (côté back)
     * @private
     */
    private static getNewDatasetRequestsDeleted(dataRequests: DataRequestItem[], newDatasetRequests: NewDatasetRequest[]): NewDatasetRequest[] {
        // Ceux qui existent côté back mais pas côté front doivent être supprimés du back
        const uuidsFront: string[] = dataRequests.map((dataRequest: DataRequestItem) => dataRequest.uuid);
        return newDatasetRequests.filter((newDatasetRequest: NewDatasetRequest) => {
            return !uuidsFront.includes(newDatasetRequest.uuid);
        });
    }

    /**
     * Récupère les JDDs ajoutés à un projet à enregistrer côté back sous forme de demandes
     * @param metadatasLinked l'ensemble des JDDs liés d'un projet saisi dans l'interface
     * @param linkedDatasets l'ensemble des demandes d'accès a des JDDs venant du back-end
     * @private
     */
    private static getMetadatasAdded(metadatasLinked: Metadata[], linkedDatasets: LinkedDataset[]): Metadata[] {
        const datasetUuidsFromback: string[] = linkedDatasets.map((item: LinkedDataset) => item.dataset_uuid);
        return metadatasLinked.filter((metadataLinked: Metadata) => !datasetUuidsFromback.includes(metadataLinked.global_id));
    }

    /**
     * Récupère les demandes d'accès aux JDDs liés du back qui doivent être supprimés
     * @param metadatasLinked l'ensemble des JDDs liés d'un projet saisi dans l'interface
     * @param linkedDatasets l'ensemble des demandes d'accès a des JDDs venant du back-end
     * @private
     */
    private static getLinkedDatasetDeleted(metadatasLinked: Metadata[], linkedDatasets: LinkedDataset[]): LinkedDataset[] {
        const datasetsUuidsFront: string[] = metadatasLinked.map((metadata: Metadata) => metadata.global_id);
        return linkedDatasets.filter((linkedDataset: LinkedDataset) => {
            return !datasetsUuidsFront.includes(linkedDataset.dataset_uuid);
        });
    }

    /**
     * Récupère les demandes d'accès aux JDDs qui ont été modifies, le tableau retourné représente l'état modifié à persister côté back
     * @param mapUuidDatasetRequest la map : uuid de JDD -> détail de la demande (front)
     * @param fromBack les link venant du back
     * @private
     */
    private static getLinkedDatasetEdited(mapUuidDatasetRequest: Map<string, RequestDetails>, fromBack: LinkedDataset[]): LinkedDataset[] {
        const modified: LinkedDataset[] = [];
        const mapUuidDatasetLink: Map<string, LinkedDataset> = new Map();
        fromBack.forEach((link: LinkedDataset) => mapUuidDatasetLink.set(link.dataset_uuid, link));

        mapUuidDatasetRequest.forEach((requestDetail: RequestDetails, datasetUuid: string) => {
            const link: LinkedDataset = mapUuidDatasetLink.get(datasetUuid);
            if (link && requestDetail && link.comment !== requestDetail.comment) {
                link.comment = requestDetail.comment;
                modified.push(link);
            }
        });

        return modified;
    }

    constructor(private readonly projektMetierService: ProjektMetierService,
                private readonly konsultMetierService: KonsultMetierService,
                private readonly translateService: TranslateService,
                private readonly snackBarService: SnackBarService,
                private readonly userService: UserService,
                private readonly taskService: TaskMetierService,
                private readonly formBuilder: FormBuilder,
                private readonly dialog: MatDialog,
                private readonly organizationMetierService: OrganizationMetierService,
    ) {
    }

    /**
     * Initialisation des champs du formulaire de l'étape 1 : réutilisation
     */
    public initStep1ReutilisationFormGroup(): FormGroup {
        return this.formBuilder.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            image: [''],
            publicCible: [''],
            type: ['', Validators.required],
            url: ['', Validators.pattern(/^(http|https|ftp):\/\/.*$/)],
        });
    }

    public reutilisationFormGroupToProject(step1FormGroup: FormGroup,
                                           step2FormGroup: FormGroup,
                                           user: User,
                                           projectType: ProjectType): Project {
        const ownerType = step2FormGroup.get('ownerType').value as OwnerType;
        return {
            title: step1FormGroup.get('title').value,
            description: step1FormGroup.get('description').value,
            type: projectType,
            access_url: step1FormGroup.get('url').value,
            owner_uuid: ownerType === OwnerType.Organization ? step2FormGroup.get('organizationUuid').value : user.uuid,
            owner_type: ownerType,
            contact_email: step2FormGroup.get('contactEmail').value,
            project_status: REUSE_STATUS,
            object_type: 'Project'
        };
    }

    /**
     * Initialisation des champs du formulaire de l'étape 1 : projet
     */
    public initStep1ProjectFormGroup(): FormGroup {
        return this.formBuilder.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            image: [''],
            begin_date: [null],
            end_date: [null],
            publicCible: [''],
            echelle: [null],
            territoire: [null],
            accompagnement: ['', Validators.required],
            type: ['', Validators.required],
            confidentiality: this.formBuilder.control(DEFAULT_CONFIDENTIALITY_CODE, Validators.required),
        }, {
            // Contrôle cross champs sur la période
            validators: [consistentPeriodValidator({beginControlName: 'begin_date', endControlName: 'end_date'})],
            updateOn: 'blur'
        });
    }

    /**
     * Conversion de l'état du formulaire saisi en un objet métier Project
     * @param step1FormGroup le form de l'étape 1
     * @param step2FormGroup le form de l'étape 2
     * @param user l'utilisateur connecté
     * @param projectType le type de projet créé
     * @param confidentiality le niveau de confidentialité choisi
     */
    public projectFormGroupToProject(step1FormGroup: FormGroup,
                                     step2FormGroup: FormGroup,
                                     user: User,
                                     projectType: ProjectType,
                                     confidentiality: Confidentiality): Project {
        const ownerType = step2FormGroup.get('ownerType').value as OwnerType;
        return {
            title: step1FormGroup.get('title').value,
            expected_completion_start_date: step1FormGroup.get('begin_date').value,
            expected_completion_end_date: step1FormGroup.get('end_date').value,
            description: step1FormGroup.get('description').value,
            territorial_scale: step1FormGroup.get('echelle').value,
            detailed_territorial_scale: step1FormGroup.get('territoire').value,
            confidentiality,
            desired_supports: step1FormGroup.get('accompagnement').value,
            type: projectType,
            owner_uuid: ownerType === OwnerType.Organization ? step2FormGroup.get('organizationUuid').value : user.uuid,
            contact_email: step2FormGroup.get('contactEmail').value,
            owner_type: ownerType,
            object_type: 'Project',
            target_audiences: step1FormGroup.get('publicCible').value === '' ? null : step1FormGroup.get('publicCible').value
        };
    }

    /**
     * Mets à jour les champs du premier projet avec les valeurs du second (valeurs uniquement modifiables)
     * @param toUpdate le projet modifié
     * @param updated le projet qui contient les valeurs àjour
     */
    public updateProjectField(toUpdate: Project, updated: Project): void {
        toUpdate.title = updated.title;
        toUpdate.expected_completion_start_date = updated.expected_completion_start_date;
        toUpdate.expected_completion_end_date = updated.expected_completion_end_date;
        toUpdate.description = updated.description;
        toUpdate.territorial_scale = updated.territorial_scale;
        toUpdate.detailed_territorial_scale = updated.detailed_territorial_scale;
        toUpdate.confidentiality = updated.confidentiality;
        toUpdate.desired_supports = updated.desired_supports;
        toUpdate.target_audiences = updated.target_audiences;
        toUpdate.type = updated.type;
        toUpdate.contact_email = updated.contact_email;
    }

    /**
     * Initialisation des champs du formulaire de l'étape 2 pour l'instant identiques
     * pour retutilisation et projet
     */
    public initStep2FormGroup(): FormGroup {
        return this.formBuilder.group({
            ownerType: ['', Validators.required],
            lastname: [
                {
                    value: '',
                    disabled: true
                },
                Validators.required
            ],
            firstname: [
                {
                    value: '',
                    disabled: true
                },
                Validators.required
            ],
            contactEmail: ['', Validators.email],
            organizationUuid: [null]
        }, {
            validators: [this.organizationUuidValidator]
        });
    }

    /**
     * Valide le champ organizationUuid en fonction du champ ownerType
     */
    protected organizationUuidValidator(step2FormGroup: FormGroup): ValidationErrors | null {
        const ownerType = step2FormGroup.get('ownerType').value as OwnerType;
        const organizationUuidFormControl: AbstractControl = step2FormGroup.get('organizationUuid');
        const organizationUuid = organizationUuidFormControl.value;
        if (ownerType === OwnerType.Organization && !organizationUuid) {
            const errors: ValidationErrors | null = {organizationUuidValidator: true};
            organizationUuidFormControl.setErrors(errors);
            return errors;
        }
        organizationUuidFormControl.setErrors(null);
        return null;
    }

    /**
     * Chargement des éléments requis pour afficher le formulaire de réutilisation
     */
    public loadDependenciesReutilisation(): Observable<FormReutilisationDependencies> {
        const connectedUser$: Observable<User | undefined> = this.userService.getConnectedUser();
        const dependencies = {
            projectTypes: this.projektMetierService.searchProjectTypes(),
            projectPublicCible: this.projektMetierService.searchProjectPublicCible(),
            user: connectedUser$,
            organizations: connectedUser$.pipe(
                switchMap(connectedUser => this.organizationMetierService.getMyOrganizations(connectedUser.uuid))
            ),
        };

        return forkJoin(dependencies);
    }

    /**
     * Chargement des éléments requis pour afficher le formulaire de projet
     */
    public loadDependenciesProject(): Observable<FormProjectDependencies> {
        const connectedUser$: Observable<User | undefined> = this.userService.getConnectedUser();
        const dependencies = {
            confidentialities: this.projektMetierService.searchProjectConfidentialities(),
            territorialScales: this.projektMetierService.searchTerritorialScales(),
            supports: this.projektMetierService.searchSupports(),
            projectPublicCible: this.projektMetierService.searchProjectPublicCible(),
            projectTypes: this.projektMetierService.searchProjectTypes(),
            user: connectedUser$,
            organizations: connectedUser$.pipe(
                switchMap(connectedUser => this.organizationMetierService.getMyOrganizations(connectedUser.uuid))
            ),
        };

        return forkJoin(dependencies);
    }

    /**
     * Récupère l'ensemble des niveaux de confidentialité sous forme d'items de boutons radio
     * @return Un observable d'un tableau de suggestions
     */
    public getConfidentialitiesRadio(confidentialities: Confidentiality[]): RadioListItem[] {

        // Le tableau qui contiendra les radio items
        const radioConfidentialities = [];

        // On va mapper chaque niveau de confidentialité en item de bouton radio
        confidentialities.forEach((confidentiality) => {
            const radioItem = this.getConfidentialityDetails(confidentiality);
            if (radioItem) {
                radioConfidentialities.push(radioItem);
            }
        });

        return radioConfidentialities;
    }

    /**
     * Obtention d'un objet vue : ProejctDatasetItem à partir d'un JDD
     * @param metadata le JDD pour créer une vue
     */
    public metadataToProjectDatasetItem(metadata: Metadata): ProjectDatasetItem {
        return {
            title: metadata.resource_title,
            overTitle: metadata.producer.organization_name,
            pictoType: ProjectDatasetPictoType.LOGO,
            pictoValue: metadata.producer.organization_id,
            identifier: metadata.local_id,
            editable: metadata.access_condition?.confidentiality?.restricted_access,
            titleIcon: metadata.access_condition?.confidentiality?.restricted_access ? RESTRICTED_DATASET_ICON : undefined
        };
    }

    /**
     * Obtention d'un objet vue : ProejctDatasetItem à partir d'une vue demande de données
     * @param dataRequest la demande de données (front pas back)
     */
    public dataRequestToProjectDatasetItem(dataRequest: DataRequestItem): ProjectDatasetItem {
        return {
            title: dataRequest.title,
            overTitle: null,
            pictoType: ProjectDatasetPictoType.STATIC,
            pictoValue: 'rudi_picto_nouvelle_demande',
            identifier: null,
            editable: true
        };
    }

    /**
     * Ouvre une popin de sélection d'un JDD
     */
    public openDialogMetadata(restrictedAccessFilterValue: boolean): Observable<Metadata> {
        const dialogConfig = new DefaultMatDialogConfig<AddDataSetDialogData>();
        dialogConfig.width = '';
        dialogConfig.data = {
            restrictedAccessForcedValue: restrictedAccessFilterValue
        };

        const dialogRef = this.dialog.open(AddDataSetDialogComponent, dialogConfig);
        return dialogRef.afterClosed();
    }

    /**
     * Ouvre la popin de selection de projet
     * @param metadata le JDD qu'on souhaite lier au projet qui sera choisi
     */
    public selectProjectsDialog(metadata: Metadata): Observable<DialogClosedData<Project>> {
        const dialogConfig = new DefaultMatDialogConfig<SelectProjectDialogData>();
        dialogConfig.data = {
            data: {
                metadata
            }
        };

        const dialogRef = this.dialog.open(SelectProjectDialogComponent, dialogConfig);
        return dialogRef.afterClosed();
    }

    /**
     * Ouverture de la popin de saisie des détails d'une demande d'accès à un JDD
     * @param endDate la date de fin de la demande par défaut
     */
    public openDialogRequestDetails(endDate: Moment): Observable<DialogClosedData<RequestDetails>> {
        const dialogConfig = new DefaultMatDialogConfig<RequestDetailsDialogData>();
        dialogConfig.data = {
            data: {
                endDate,
                requestDetails: null,
            }
        };

        const dialogRef = this.dialog.open(RequestDetailsDialogComponent, dialogConfig);
        return dialogRef.afterClosed();
    }

    /**
     * Ouverture de la popin de saisie des details d'une demande d'accès à un JDD en mode édition
     * @param original les détails originaux qu'on vient modifier
     */
    public openDialogEditRequest(original: RequestDetails): Observable<DialogClosedData<RequestDetails>> {
        const dialogConfig = new DefaultMatDialogConfig<RequestDetailsDialogData>();
        dialogConfig.data = {
            data: {
                endDate: null,
                requestDetails: original
            }
        };

        const dialogRef = this.dialog.open(RequestDetailsDialogComponent, dialogConfig);
        return dialogRef.afterClosed();
    }

    /**
     * Ouvre une popin d'info que l'enregistrement PROJET à bien marché
     */
    public openDialogSuccess(): void {
        const dialogConfig = new MatDialogConfig();

        dialogConfig.autoFocus = false;
        dialogConfig.panelClass = 'my-custom-dialog-class';
        dialogConfig.data = {};
        dialogConfig.width = '768px';

        this.dialog.open(SuccessProjectCreationDialogComponent, dialogConfig);
    }

    /**
     * Ouvre une popin de saisie d'une nouvelle demande de projet
     * @param numberOfRequests le nombre de requêtes présentes dans le contexte actuel
     */
    public openDialogAskNewDatasetRequest(numberOfRequests: number): Observable<DataRequestItem> {
        const dialogConfig = new MatDialogConfig<NewDataSetDialogData>();

        dialogConfig.disableClose = true;
        dialogConfig.autoFocus = false;
        dialogConfig.width = '768px';
        dialogConfig.data = {
            data: {
                dataRequestItem: null,
                counter: numberOfRequests + 1 // On veut "ajouter" cette requête si elle a pas de titre elle est n° au dessus
            }
        };

        const dialogRef = this.dialog.open(EditNewDataSetDialogComponent, dialogConfig);
        return dialogRef.afterClosed();
    }

    /**
     * Ouvre une popin d'édition de nouvelle demande de projet
     * @param dataRequestItem la demande éditée
     * @param demandNumber le n° de la demande si on lui retire son titre pour lui donner son titre par défaut (jamais 0)
     */
    public openDialogEditNewDatasetRequest(dataRequestItem: DataRequestItem, demandNumber: number): Observable<DataRequestItem> {
        const dialogConfig = new DefaultMatDialogConfig();

        dialogConfig.data = {
            data: {
                dataRequestItem,
                counter: demandNumber // On veut "editer" cette requête si elle a pas de titre elle est n° courant
            }
        };

        const dialogRef = this.dialog.open(EditNewDataSetDialogComponent, dialogConfig);
        return dialogRef.afterClosed();
    }

    /**
     * Regarde si un JDD est présent dans une liste grâce à son UUID
     * @param metadataUuid l'UUID du JDD cherché
     * @param linkedDatasets les JDDs parcourus
     */
    public isMetadataPresent(metadataUuid: string, linkedDatasets: Metadata[]): boolean {
        const existingLinkedDataset: Metadata = linkedDatasets.find(linkedDataset => linkedDataset.global_id === metadataUuid);
        if (existingLinkedDataset) {
            this.snackBarService.openSnackBar({
                message: this.translateService.instant('project.stepper.reuse.step3.linkedDatasets.alreadyAdded'),
                level: Level.ERROR
            });
            return true;
        }

        return false;
    }

    /**
     * Action de sauvegarde d'une réutilisation/projet côté back
     * @param project l'objet Projet à persister en entity
     * @param linkedDatasets les JDDs liés
     * @param dataRequests les éventuelles demandes de nouveaux JDD (uniquement pour un projet)
     * @param image l'image du projet
     * @param mapRequestDetailsByDatasetUuid associations JDD -> détails de la demande, si nécessaire
     */
    public createProject(project: Project, linkedDatasets: Metadata[], dataRequests: DataRequestItem[], image: Blob,
                         mapRequestDetailsByDatasetUuid: Map<string, RequestDetails>): Observable<Project> {

        // 1) Créer le projet côté back
        return this.projektMetierService.createProject(project).pipe(
            // 2) S'il y a des demandes de JDD les créer et les lier au projet
            switchMap((createdProject: Project) => {
                return this.manageRequestsToProjects(createdProject, dataRequests).pipe(
                    mapTo(createdProject)
                );
            }),

            // 3) S'il y a des JDDs liés, on les gère (ajout et suppression)
            switchMap(createdProject => {
                return this.manageLinkedDatasetsToProjects(createdProject, linkedDatasets, mapRequestDetailsByDatasetUuid).pipe(
                    mapTo(createdProject)
                );
            }),

            // 4) S'il y a une image, on l'ajoute au projet
            switchMap(createdProject => {
                if (image != null) {
                    return this.uploadImage(createdProject, image).pipe(
                        mapTo(createdProject)
                    );
                } else {
                    return of(createdProject);
                }
            })
        );
    }

    /**
     * Action de mise à jour d'une réutilisation/projet côté back
     * @param project l'objet Projet à persister en entity
     * @param linkedDatasets les JDDs liés
     * @param dataRequests les éventuelles demandes de nouveaux JDD (uniquement pour un projet)
     * @param mapRequestDetailsByDatasetUuid la map d'association : JDD UUID -> détails d'une demande
     * @param image l'image du projet
     * @param imageAction quelle action de MAJ on applique à l'image projet
     */
    public updateProject(project: Project, linkedDatasets: Metadata[], dataRequests: DataRequestItem[],
                         mapRequestDetailsByDatasetUuid: Map<string, RequestDetails>,
                         image: Blob, imageAction: UpdateAction): Observable<Project> {
        return this.projektMetierService.updateProject(project).pipe(
            switchMap(() => {
                return this.manageRequestsToProjects(project, dataRequests).pipe(
                    mapTo(project)
                );
            }),
            switchMap(updatedProject => {
                return this.manageLinkedDatasetsToProjects(updatedProject, linkedDatasets, mapRequestDetailsByDatasetUuid).pipe(
                    mapTo(updatedProject)
                );
            }),
            switchMap(updatedProject => {
                if (imageAction === UpdateAction.AJOUT) {
                    return this.uploadImage(updatedProject, image).pipe(
                        mapTo(updatedProject)
                    );
                } else if (imageAction === UpdateAction.SUPPRESSION) {
                    return this.projektMetierService.removeLogo(updatedProject.uuid).pipe(
                        mapTo(updatedProject)
                    );
                } else if (imageAction === UpdateAction.MISE_A_JOUR) {
                    return this.projektMetierService.removeLogo(updatedProject.uuid).pipe(
                        switchMap(() => this.uploadImage(updatedProject, image)),
                        mapTo(updatedProject)
                    );
                } else {
                    return of(updatedProject);
                }
            })
        );
    }

    /**
     * Créé la réutilisation et la soumets (transmission des demandes + workflow) avec gestion fine des cas d'erreur
     * @param project l'objet réutilisation a créer
     * @param linkedDatasets les JDDs ouverts liés
     * @param dataRequests c'est vide pour une réutilisation
     * @param image l'image de la réutilisation
     * @param mapRequestDetailsByDatasetUuid c'est vide pour une réutilisation
     */
    public createAndSubmitReutilisation(project: Project, linkedDatasets: Metadata[], dataRequests: DataRequestItem[], image: Blob,
                                        mapRequestDetailsByDatasetUuid: Map<string, RequestDetails>): Observable<Project> {

        // 1) on crée le projet
        return this.createProject(project, linkedDatasets, dataRequests, image, mapRequestDetailsByDatasetUuid).pipe(
            // Si erreur a la création du project alors on l'indique techniquement
            catchError((projectError: HttpErrorResponse) => {
                console.error(projectError);
                throw new Error('Une erreur a eu lieu pendant la création du project avant les workflow');
            }),

            // 2) projet créé, maintenant on doit démarrer son workflow
            switchMap((created: Project) => {
                // Démarrage du workflow qui peut échouer
                return this.submitProject(created).pipe(
                    // Si on a une erreur pendant le workflow on doit supprimer la réutilisation qui a été créé
                    catchError((workflowError: HttpErrorResponse) => {
                        console.error(workflowError);
                        return this.projektMetierService.deleteProject(created).pipe(
                            // Si on a une erreur pendant la suppression ben
                            // on ne peut que se plier en 4 et informer la console d'erreur ...
                            catchError((projectSuppressionError: Error) => {
                                console.error(projectSuppressionError);
                                throw Error('Erreur lors de la suppression du project après avoir eu une erreur dans le workflow, ' +
                                    'une incohérence a été créée');
                            }),

                            // Après la suppression du projet on doit quand même arrêter la chaîne en erreur car il y'en a eu une
                            tap(() => {
                                throw Error('Une erreur a eu lieu lors du démarrage du workflow de la réutilisation');
                            })
                        );
                    }),

                    // Tout se passe bien avec les workflow on renvoie le projet créé
                    mapTo(created)
                );
            }),
        );
    }


    /**
     * Créé et démarre le workflow pour les demandes d'accès aux JDDs
     * @param links l'ensemble des demandes d'accès à traiter
     */
    private createAndStartTaskForLinkedDatasets(links: LinkedDataset[]): Observable<Task[]> {
        const link$: Observable<Task>[] = links.map(
            link => this.taskService.createLinkedDatasetDraft(link).pipe(
                switchMap((task: Task) => this.taskService.startLinkedDatasetTask(task))
            )
        );
        return forkJoin(link$);
    }

    /**
     * Soumets un objet "Project" réutilisation ou projet pour démarrer son workflow
     * @param project l'asset qui doit démarrer le workflow
     */
    public submitProject(project: Project): Observable<Task> {
        // 1) On crée le draft à partir du projet
        return this.taskService.createProjectDraft(project).pipe(
            // 2 et maintenant on doit démarrer le workflow de cet élément
            switchMap((task: Task) => {
                return this.taskService.startProjectTask(task);
            })
        );
    }

    /**
     * Recherche d'un type de projet par son code
     * @param codeProject le code
     * @param projectTypes les types
     */
    public searchProjectType(codeProject: string, projectTypes: ProjectType[]): ProjectType {
        return projectTypes.find(projectType => projectType.code === codeProject);
    }

    /**
     * Recherche d'un niveau de confidentialité par code
     * @param codeConfidentiality le code
     * @param confidentialities les niveaux
     */
    public searchConfidentiality(codeConfidentiality: string, confidentialities: Confidentiality[]): Confidentiality {
        return confidentialities.find(confidentiality => confidentiality.code === codeConfidentiality);
    }

    /**
     * Appel de l'API d'upload d'image
     * @param createdProject le projet possédant l'image
     * @param image l'imag en binaire
     * @private
     */
    private uploadImage(createdProject: Project, image: Blob): Observable<void> {
        return this.projektMetierService.uploadLogo(createdProject.uuid, image);
    }

    /**
     * Création d'une demande d'accès (JDD lié) à partir d'un projet
     * @param linkToCreate l'objet à créer côté back
     */
    public createLinkedDatasetFromProject(linkToCreate: LinkedDatasetFromProject): Observable<void> {
        const createdProject: Project = linkToCreate.project;
        const map: Map<string, RequestDetails> = new Map();
        map.set(linkToCreate.datasetUuid, linkToCreate.requestDetail);

        // Lier les demandes (la seule en l'occurence) au projet
        return this.projektMetierService.linkProjectToDatasets(createdProject.uuid, [linkToCreate.datasetUuid], map).pipe(
            // Quand ça se termine on va faire un traitement conditionnel
            switchMap((linksCreated: LinkedDataset[]) => iif(
                // Si le projet est déjà soumis
                () => createdProject.project_status === ProjectStatus.InProgress,
                // Il faut transmettre la demande créé
                this.createAndStartTaskForLinkedDatasets(linksCreated).pipe(
                    // S'il y'a une erreur de workflow on doit supprimer la demande
                    catchError((workflowError: HttpErrorResponse) => {
                        console.error(workflowError);
                        const linksUuids = linksCreated.map(link => link.uuid);
                        return this.projektMetierService.unlinkDatasetsToProject(createdProject.uuid, linksUuids).pipe(
                            // Si on arrive quand même pas à supprimer la demande ... on se plie en 4
                            catchError((deletionError: HttpErrorResponse) => {
                                console.error(deletionError);
                                throw Error('Impossible de supprimer la demande d\'accès alors que son workflow a échoué, ' +
                                    'une incohérence a été créée');
                            }),

                            // Après la suppression du linked dataset on doit quand même arrêter la chaîne en erreur car il y'en a eu une
                            tap(() => {
                                throw Error('Une erreur a eu lieu lors du démarrage du workflow de la demande d\'accès');
                            })
                        );
                    })
                ),
                // Sinon on ne fait rien
                of([])
            )),

            // Observable mappé sur void
            mapTo(null)
        );
    }

    /**
     * Gestion des nouvelles demandes de JDD pour un projet (ajout/suppression)
     * @param createdProject le projet porteur
     * @param dataRequests l'ensemble des demandes côté front
     * @private
     */
    public manageRequestsToProjects(createdProject: Project, dataRequests: DataRequestItem[]): Observable<boolean> {

        // On récupère d'abord les demandes du projet avec le back pour comparer
        return this.projektMetierService.getNewDatasetRequests(createdProject).pipe(
            switchMap((newDatasetRequests: NewDatasetRequest[]) => {

                // Récupération des items créés et supprimés du projets par comparaison
                const added: DataRequestItem[] = ProjectSubmissionService.getNewDatasetRequestsAdded(dataRequests);
                const deleted: NewDatasetRequest[] = ProjectSubmissionService.getNewDatasetRequestsDeleted(dataRequests, newDatasetRequests);
                const edited: NewDatasetRequest[] = ProjectSubmissionService.getNewDatasetRequestsEdited(dataRequests, newDatasetRequests);

                // Si on a rien à ajouter ou suppr on fait rien
                if (added.length === 0 && deleted.length === 0 && edited.length === 0) {
                    return of(true);
                }

                // Ajout des observables et lancement du traitement parallèle d'ajout/suppression
                return forkJoin({
                    add: this.projektMetierService.addNewDatasetRequests(createdProject, added),
                    delete: this.projektMetierService.deleteDatasetRequests(createdProject, deleted),
                    edit: this.projektMetierService.upddateDatasetRequests(createdProject, edited)
                }).pipe(mapTo(true));
            })
        );
    }

    /**
     * Gestion des demandes d'accès aux JDDs pour un projet (ajout/suppression)
     * @param createdProject le projet porteur
     * @param metadatasLinked l'ensemble des JDDs présents côté front
     * @param mapRequestDetailsByDatasetUuid associations JDD → détails de la demande, si nécessaire
     * @private
     */
    public manageLinkedDatasetsToProjects(createdProject: Project, metadatasLinked: Metadata[],
                                          mapRequestDetailsByDatasetUuid: Map<string, RequestDetails>): Observable<boolean> {

        // On récupère les DEMANDES côté back
        return this.projektMetierService.getLinkedDatasets(createdProject.uuid).pipe(
            switchMap((linkedDatasets: LinkedDataset[]) => {

                // Pour l'ajout on doit se débrouiller avec les METADATA côté front pour déterminer les DEMANDES à créer
                const added: string[] = ProjectSubmissionService.getMetadatasAdded(metadatasLinked, linkedDatasets)
                    .map((element: Metadata) => element.global_id);

                // Pour la Suppression on peut directement filtrer les DEMANDES à l'aides des information de METADATA front
                const deleted: string[] = ProjectSubmissionService.getLinkedDatasetDeleted(metadatasLinked, linkedDatasets)
                    .map((element: LinkedDataset) => element.uuid);

                // Pour l'édition on doit manipuler la map d'association : uuid jdd -> détail demande, pour comparer aux DEMANDES côté back
                const modified: LinkedDataset[] = ProjectSubmissionService
                    .getLinkedDatasetEdited(mapRequestDetailsByDatasetUuid, linkedDatasets);

                if (added.length === 0 && deleted.length === 0 && modified.length === 0) {
                    return of(true);
                }

                return forkJoin({
                    add: this.projektMetierService.linkProjectToDatasets(createdProject.uuid, added, mapRequestDetailsByDatasetUuid),
                    delete: this.projektMetierService.unlinkDatasetsToProject(createdProject.uuid, deleted),
                    edit: this.projektMetierService.updateLinkedDatasets(createdProject, modified)
                }).pipe(mapTo(true));
            })
        );
    }

    /**
     * Traduit le niveau de confidentialité en item de Radio Button
     * @param confidentiality le niveau de confidentialité traduit
     * @private
     */
    private getConfidentialityDetails(confidentiality: Confidentiality): RadioListItem | undefined {

        // Si le niveau de confidentialité est inconnu on renvoie undefined
        if (!KNOWN_CONFIDENTIALITY_CODES.includes(confidentiality.code)) {
            return undefined;
        }

        // Récupération de la clé de translate
        const translateKey = 'project.confidentiality.' + confidentiality.code;

        // On construit un RadioItem
        return {
            code: confidentiality.code,
            label: this.translateService.instant(translateKey + '.label'),
            description: this.translateService.instant(translateKey + '.description')
        };
    }
}
