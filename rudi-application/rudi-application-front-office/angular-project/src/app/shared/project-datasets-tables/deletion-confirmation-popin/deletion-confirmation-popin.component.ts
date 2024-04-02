import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {CloseEvent, DialogClosedData} from '@features/data-set/models/dialog-closed-data';

@Component({
    selector: 'app-deletion-confirmation-popin',
    templateUrl: './deletion-confirmation-popin.component.html',
    styleUrls: ['./deletion-confirmation-popin.component.scss']
})
export class DeletionConfirmationPopinComponent {

    constructor(private readonly matIconRegistry: MatIconRegistry,
                private readonly domSanitizer: DomSanitizer,
                public dialogRef: MatDialogRef<string, DialogClosedData<string>>,
                @Inject(MAT_DIALOG_DATA) public requestUuid: string) {
        this.matIconRegistry.addSvgIcon('icon-close', this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg'));
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
     * Méthode appelée au clic sur le bouton "Confirmé"
     */
    validate(): void {
        this.dialogRef.close({
            data: this.requestUuid,
            closeEvent: CloseEvent.VALIDATION
        });
    }
}
