import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {UserService} from '../../../core/services/user.service';
import {LinkedDataset, Project} from '../../../projekt/projekt-model';
import {map, switchMap} from 'rxjs/operators';
import {ProjektMetierService} from '../../../core/services/projekt-metier.service';
import {CloseEvent, DialogClosedData} from '../../models/dialog-closed-data';
import {Metadata} from '../../../api-kaccess';
import {MatSelectChange} from '@angular/material/select';

/**
 * Les données que peuvent accepter la Dialog
 */
export interface SelectProjectDialogData {
    data: {
        metadata: Metadata;
    };
}

@Component({
    selector: 'app-restricted-dataset-request-dialog',
    templateUrl: './select-project-dialog.component.html',
    styleUrls: ['./select-project-dialog.component.scss']
})
export class SelectProjectDialogComponent implements OnInit {

    /**
     * Formulaire de saisie d'un projet
     */
    formGroup: FormGroup;

    /**
     * Liste des projets proposés
     */
    myProjects: Project[] = [];

    /**
     * Est-ce que le contenu de la liste des projets se charge ?
     */
    isLoading = false;

    /**
     * Est-ce que le projet choisi est invalide ?
     */
    isProjectInvalid = false;

    /**
     * Est-ce que la validation du projet choisi est en cours ?
     */
    isProjectValidationLoading = false;

    /**
     * Est-ce qu'il y a eu une erreur 5XX lors du check de la validité projet
     */
    errorWhileChecking = false;

    /**
     * Le JDD auquel on veut faire une demande d'accès
     */
    metadata: Metadata;

    constructor(public dialogRef: MatDialogRef<SelectProjectDialogComponent, DialogClosedData<Project>>,
                private matIconRegistry: MatIconRegistry,
                public dialog: MatDialog,
                private domSanitizer: DomSanitizer,
                private readonly formBuilder: FormBuilder,
                private readonly userService: UserService,
                private readonly projektMetierService: ProjektMetierService,
                @Inject(MAT_DIALOG_DATA) public dialogData: SelectProjectDialogData,
    ) {
        this.matIconRegistry.addSvgIcon('icon-close', this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg'));
        this.formGroup = this.formBuilder.group({
            selectProject: [null, Validators.required],
        });
    }

    /**
     * Récupération du projet qui a été choisi dans la liste (nul sinon)
     */
    get projectSelected(): Project {
        if (this.formGroup && this.formGroup.get('selectProject')) {
            return this.formGroup.get('selectProject').value;
        }

        return null;
    }

    ngOnInit(): void {

        // On récupère le JDD fourni en param de la dialog s'il existe
        if (this.dialogData) {
            this.metadata = this.dialogData.data.metadata;
        }

        // On charge pour récupérer les projets de l'user connected
        this.isLoading = true;
        this.userService.getConnectedUserOrEmpty().pipe(
            switchMap((me) => {
                return this.projektMetierService.getMyProjects(me.uuid);
            }),
        ).subscribe(myProjects => {
            this.isLoading = false;
            this.myProjects = myProjects;
        }, e => {
            this.isLoading = false;
            console.error('Cannot retrieve my projects', e);
        });
    }

    /**
     * Si on ferme la popin de quelconque manière on interrompt le workflow
     */
    handleClose(): void {
        this.dialogRef.close({
            data: null,
            closeEvent: CloseEvent.CANCEL
        });
    }

    /**
     * Quand l'utilisateur clique sur valider pour continuer
     * on renvoie le projet choisi
     */
    validate(): void {
        const project: Project = this.formGroup.get('selectProject').value;
        const returned = project ? project : null;
        this.dialogRef.close({
            data: returned,
            closeEvent: CloseEvent.VALIDATION
        });
    }

    /**
     * Vérifie si le projet choisi peut bien faire une demande d'accès vers le JDD concerné
     * @param change le projet choisi
     */
    checkProjectSelected(change: MatSelectChange): void {
        const project = change.value;
        if (project == null) {
            this.isProjectInvalid = true;
            return;
        }

        // L'idée est de charger car on va récupérer toutes les demandes d'accès d'un projet
        this.isProjectValidationLoading = true;
        this.projektMetierService.getLinkedDatasets(project.uuid).pipe(
            map((links: LinkedDataset[]) => {

                // S'il n'y en a pas c'est OK pour créer la demande
                if (links == null || links.length === 0) {
                    return false;
                }

                // S'il y'en a on ne veut pas pouvoir créer une demande d'accès si elle existe déjà, check par uuid
                return links.find((link: LinkedDataset) => link.dataset_uuid === this.metadata.global_id) !== undefined;
            })
        ).subscribe({
            next: (hasAlreadyLinked: boolean) => {
                this.errorWhileChecking = false;
                this.isProjectValidationLoading = false;
                this.isProjectInvalid = hasAlreadyLinked;
            },
            error: (e) => {
                console.error(e);
                this.errorWhileChecking = true;
                this.isProjectValidationLoading = false;
                this.isProjectInvalid = true;
            }
        });
    }
}
