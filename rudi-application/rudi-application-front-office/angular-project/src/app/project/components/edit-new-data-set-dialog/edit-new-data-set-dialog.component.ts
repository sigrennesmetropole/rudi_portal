import {Component, Inject, Input, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {DataRequestItem} from '../../model/data-request-item';

/**
 * Les données que peuvent accepter la Dialog
 */
export interface NewDataSetDialogData {
    data: {
        dataRequestItem: DataRequestItem;
        counter: number;
    };
}

@Component({
    selector: 'app-edit-new-data-set-dialog',
    templateUrl: './edit-new-data-set-dialog.component.html',
    styleUrls: ['./edit-new-data-set-dialog.component.scss']
})
export class EditNewDataSetDialogComponent implements OnInit {

    /**
     * Les propriétés média (dekstop/mobile)
     */
    @Input()
    mediaSize: MediaSize;

    /**
     * Le numéro de la demande parmi toutes les autres rattachées au même projet.
     * On commence à 1.
     */
    number = 1;

    /**
     * La demande de JDD a modifier si en mode édition
     */
    dataRequestItem: DataRequestItem;

    /**
     * Le formulaire de saisie de la demande
     */
    newDatasetRequestFormGroup: FormGroup;

    /**
     * Titre par défaut d'une demande sans titre
     */
    titleMessage = 'Nouvelle demande';

    constructor(
        public dialogRef: MatDialogRef<EditNewDataSetDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public dialogData: NewDataSetDialogData,
        private matIconRegistry: MatIconRegistry,
        private domSanitizer: DomSanitizer,
        private readonly formBuilder: FormBuilder,
        private readonly breakpointObserver: BreakpointObserverService) {
        if (this.dialogData) {
            this.dataRequestItem = this.dialogData.data.dataRequestItem;
            this.number = this.dialogData.data.counter;
        }
        this.matIconRegistry.addSvgIcon(
            'icon-close',
            this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg')
        );
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();

        this.newDatasetRequestFormGroup = this.formBuilder.group({
            title: [''],
            description: ['', Validators.required]
        });

        if (this.dataRequestItem) {
            this.newDatasetRequestFormGroup.get('title').setValue(this.dataRequestItem.title);
            this.newDatasetRequestFormGroup.get('description').setValue(this.dataRequestItem.description);
        }
    }

    /**
     * Récupération du titre par défaut d'une nouvelle demande à l'aide du compteur
     */
    get defaultTitle(): string {
        return (this.titleMessage + ' (' + this.number + ')');
    }

    /**
     * Création de la demande et fermeture de la popin
     */
    submit(): void {
        const request: DataRequestItem = {
            title: this.newDatasetRequestFormGroup.get('title').value
                ? this.newDatasetRequestFormGroup.get('title').value : this.defaultTitle,
            description: this.newDatasetRequestFormGroup.get('description').value,
            uuid: null
        };

        this.dialogRef.close(request);
    }
}
