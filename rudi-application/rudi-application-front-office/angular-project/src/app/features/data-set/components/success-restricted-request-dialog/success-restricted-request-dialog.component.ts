import {Component} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {Router} from '@angular/router';
import {CloseEvent, DialogClosedData} from '../../models/dialog-closed-data';

@Component({
    selector: 'app-success-restricted-request-dialog',
    templateUrl: './success-restricted-request-dialog.component.html',
    styleUrls: ['./success-restricted-request-dialog.component.scss']
})
export class SuccessRestrictedRequestDialogComponent {

    constructor(private matIconRegistry: MatIconRegistry,
                private domSanitizer: DomSanitizer,
                private router: Router,
                public dialogRef: MatDialogRef<DialogClosedData<void>>,
    ) {
        this.matIconRegistry.addSvgIcon(
            'icon-close',
            this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg')
        );
    }

    goToMyReuses(): Promise<boolean>  {
        this.handleClose();
        return  this.router.navigate(['/personal-space/my-activity']);
    }

    /**
     * Fermeture de la popin
     */
    handleClose(): void {
        this.dialogRef.close({
            data: null,
            closeEvent: CloseEvent.VALIDATION
        });
    }

}
