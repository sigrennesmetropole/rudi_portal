import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {CloseEvent, DialogClosedData} from '../../../data-set/models/dialog-closed-data';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {MatTableDataSource} from '@angular/material/table';
import {SelectionModel} from '@angular/cdk/collections';
import {OwnerType, Project} from '../../../projekt/projekt-model';
import {LinkedDatasetMetadatas} from '../../../core/services/project-dependencies.service';
import {TranslateService} from '@ngx-translate/core';
import {Metadata} from '../../../api-kaccess';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';
import {ErrorWithCause} from '../../../shared/models/error-with-cause';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {Level} from '../../../shared/notification-template/notification-template.component';
import {KonsultApiAccessService} from '../../../core/services/api-access/konsult/konsult-api-access.service';
import {SubscriptionRequestReport} from '../../../core/services/api-access/subscription-request-report';
import {SubscriptionRequestResult} from '../../../core/services/api-access/subscription-request-result';

/**
 * Les données que peuvent accepter la Dialog
 */
export interface DialogSubscribeDatasetsData {
    data: {

        /**
         * tous les JDDs des demandes d'accès d'un projet
         */
        linkedDatasetMetadatas: LinkedDatasetMetadatas[];

        /**
         * Le projet
         */
        project: Project;
    };
}

@Component({
    selector: 'app-dialog-subscribe-datasets',
    templateUrl: './dialog-subscribe-datasets.component.html',
    styleUrls: ['./dialog-subscribe-datasets.component.scss']
})
export class DialogSubscribeDatasetsComponent implements OnInit {
    public loading: boolean;
    public hasError: boolean;
    public hidePassword = true;
    public isOwnerTypeUser = false;
    public password: string;
    public project: Project;
    public infoMessage: string;
    public rudiDocLink: string;
    public subscriptionErrorMessage: string = null;
    displayedColumns: string[] = ['checkbox', 'dataset'];
    linkedDatasetMetadatas: LinkedDatasetMetadatas[] = [];
    dataSource: MatTableDataSource<LinkedDatasetMetadatas>;
    selection = new SelectionModel<LinkedDatasetMetadatas>(true, []);

    constructor(
        private readonly matIconRegistry: MatIconRegistry,
        private readonly domSanitizer: DomSanitizer,
        private readonly propertiesMetierService: PropertiesMetierService,
        private readonly translateService: TranslateService,
        private readonly apiAccessService: KonsultApiAccessService,
        private readonly snackBarService: SnackBarService,
        public dialogRef: MatDialogRef<DialogSubscribeDatasetsComponent, DialogClosedData<void>>,
        @Inject(MAT_DIALOG_DATA) public dialogData: DialogSubscribeDatasetsData) {
        this.matIconRegistry.addSvgIcon('icon-close', this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg'));
    }

    ngOnInit(): void {

        this.propertiesMetierService.get('rudidatarennes.docRudiBzh').subscribe({
            next: (rudiDocLink: string) => {
                this.rudiDocLink = rudiDocLink;
            }
        });

        if (this.dialogData.data) {
            this.project = this.dialogData.data.project;
            this.linkedDatasetMetadatas = this.dialogData.data.linkedDatasetMetadatas;
            this.isOwnerTypeUser = this.dialogData.data.project.owner_type === OwnerType.User;
            this.dataSource = new MatTableDataSource<LinkedDatasetMetadatas>(this.linkedDatasetMetadatas);
        }
    }

    /**
     * Fermeture de la popin
     */
    handleClose(): void {
        this.dialogRef.close({
            data: null,
            closeEvent: CloseEvent.CANCEL
        });
    }

    /**
     * Si le nombre d'éléments sélectionnés correspond au nombre total de lignes.
     */
    isAllRowSelected(): boolean {
        const numSelected = this.selection.selected.length;
        const numRows = this.dataSource.data.length;
        return numSelected === numRows;
    }

    /**
     * Sélectionne toutes les lignes si elles ne sont pas toutes sélectionnées, sinon on efface la sélection.
     */
    selectAll(): void {
        this.isAllRowSelected() ?
            this.selection.clear() :
            this.dataSource.data.forEach(row => this.selection.select(row));
    }

    /**
     * Lancement de la souscription au.x jeu.x de donnée.s séléctionné.s pour un projet
     */
    validate(): void {
        const metadatasSelected: Metadata[] = this.selection.selected.map((select: LinkedDatasetMetadatas) => select.dataset);
        this.loading = true;
        this.infoMessage = null;
        this.subscriptionErrorMessage = null;
        this.apiAccessService.checkPasswordAndDoSubscriptions(metadatasSelected, this.password, this.project.owner_type, this.project.owner_uuid)
            .subscribe({
                next: (report: SubscriptionRequestReport) => {
                    this.loading = false;
                    console.log('Rapport de souscription : ', report);
                    if (report.failed.length > 0) {
                        report.failed.forEach((attempt: SubscriptionRequestResult) => {
                            console.error(attempt.error);
                        });
                    }
                    this.infoMessage = report.subscribed.length + ' souscription(s) réussie(s), '
                        + report.ignored.length + ' souscription(s) ignorée(s), '
                        + report.failed.length + ' souscription(s) échouée(s)';
                },
                error: (error: Error) => {
                    this.loading = false;
                    console.error(error);
                    if (error instanceof ErrorWithCause) {
                        this.subscriptionErrorMessage = error.functionalMessage;
                    } else {
                        this.snackBarService.openSnackBar({
                            message: this.translateService.instant('error.technicalError'),
                            level: Level.ERROR,
                        });
                    }
                }
            });
    }

    /**
     * Grise le button validé quand l'utilisateur n'a pas saisie de mot de passe ou quand aucun traitement est en cours
     */
    isButtonValidateDisabled(): boolean {
        return !(this.password?.length) || this.loading || this.selection.selected?.length === 0;
    }

    /**
     * Méthode qui récupère le mot de passe entré par l'utilisateur
     * @param $event mot de passe saisi
     */
    handlePasswordChanged($event: string): void {
        this.password = $event;
    }

    /**
     * Affiche le bon label en fonction du Owner du projet
     */
    getLabelPassword(): string {
        if (this.isOwnerTypeUser) {
            return this.translateService.instant('personalSpace.dialogDatasets.textOwner');
        } else {
            return this.translateService.instant('personalSpace.dialogDatasets.textUser');
        }
    }
}
