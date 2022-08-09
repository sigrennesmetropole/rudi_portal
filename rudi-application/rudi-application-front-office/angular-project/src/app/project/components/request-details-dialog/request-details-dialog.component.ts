import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {RequestDetails} from '../../../shared/models/request-details';
import {CloseEvent, DialogClosedData} from '../../../data-set/models/dialog-closed-data';
import {Moment} from 'moment';

/**
 * Les données que peuvent accepter la Dialog
 */
export interface RequestDetailsDialogData {
    data: {
        endDate: Moment;
        requestDetails: RequestDetails;
    };
}
/**
 * taille maximal du commentaire
 */
const COMMENT_MAX_LENGTH = 3000;


@Component({
    selector: 'app-request-details-dialog',
    templateUrl: './request-details-dialog.component.html',
    styleUrls: ['./request-details-dialog.component.scss']
})
export class RequestDetailsDialogComponent implements OnInit {

    /**
     * Formulaire de saisie des infos du détail de la demande d'accès
     */
    formGroup: FormGroup;

    /**
     * La demande d'accès si on est en mode édition
     */
    requestDetails: RequestDetails;

    /**
     * La date de fin de la demande par défaut
     */
    endDateDefault: Moment;

    /**
     * passe plat de la taille maximal du commentaire
     */
    commentMaxLength = COMMENT_MAX_LENGTH;

    constructor(
        public dialogRef: MatDialogRef<RequestDetailsDialogComponent, DialogClosedData<RequestDetails>>,
        @Inject(MAT_DIALOG_DATA) public dialogData: RequestDetailsDialogData,
        private matIconRegistry: MatIconRegistry,
        private domSanitizer: DomSanitizer,
        private readonly formBuilder: FormBuilder,
    ) {
        this.matIconRegistry.addSvgIcon('icon-close', this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg'));

        if (this.dialogData) {
            this.requestDetails = this.dialogData.data.requestDetails;
            this.endDateDefault = this.dialogData.data.endDate;
        }
    }

    ngOnInit(): void {
        this.formGroup = this.formBuilder.group({
            comment: [''],
            date: [null, Validators.required]
        }, {
            updateOn: 'blur'
        });

        if (this.endDateDefault) {
            this.formGroup.get('date').setValue(this.endDateDefault);
        }

        if (this.requestDetails) {
            this.formGroup.get('comment').setValue(this.requestDetails.comment);
            this.formGroup.get('date').setValue(this.requestDetails.endDate);
        }
    }

    /**
     * Quand on ferme la popin et on arrête tout
     */
    handleClose(): void {
        const returned: DialogClosedData<RequestDetails> = {
            data: {},
            closeEvent: CloseEvent.CANCEL
        };
        this.dialogRef.close(returned);
    }

    /**
     * Quand on annule pour revenir en arrière en fermant la popin
     */
    handleCancel(): void {
        const returned: DialogClosedData<RequestDetails> = {
            data: {},
            closeEvent: CloseEvent.PREVIOUS
        };
        this.dialogRef.close(returned);
    }

    /**
     * Quand on valide la popin
     */
    submit(): void {
        const comment: string = this.formGroup.get('comment').value;
        const date: Moment = this.formGroup.get('date').value;
        const returned: DialogClosedData<RequestDetails> = {
            data: {
                comment: comment ? comment : null,
                endDate: date ? date : null
            },
            closeEvent: CloseEvent.VALIDATION
        };
        this.dialogRef.close(
            returned
        );
    }
}
