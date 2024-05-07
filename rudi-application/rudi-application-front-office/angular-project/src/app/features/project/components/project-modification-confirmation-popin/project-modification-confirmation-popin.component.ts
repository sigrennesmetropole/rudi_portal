import { Component } from '@angular/core';
import {MatButton, MatIconButton} from '@angular/material/button';
import {MatDialogActions, MatDialogContent, MatDialogRef} from '@angular/material/dialog';
import {MatIcon} from '@angular/material/icon';
import {CloseEvent} from '@features/data-set/models/dialog-closed-data';
import {TranslateModule} from '@ngx-translate/core';

@Component({
  selector: 'app-project-modification-confirmation-popin',
  standalone: true,
    imports: [
        MatDialogContent,
        MatButton,
        MatDialogActions,
        MatIcon,
        MatIconButton,
        TranslateModule
    ],
  templateUrl: './project-modification-confirmation-popin.component.html',
  styleUrl: './project-modification-confirmation-popin.component.scss'
})
export class ProjectModificationConfirmationPopinComponent {

    constructor(public dialogRef: MatDialogRef<string>) {
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
            closeEvent: CloseEvent.VALIDATION
        });
    }
}
