import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {CloseEvent, DialogClosedData} from '@features/data-set/models/dialog-closed-data';
import {OrganizationMember} from 'micro_service_modules/strukture/strukture-model';
import {OrganizationMemberDialogData} from '../organization-members-table/organization-member-dialog-data';

@Component({
    selector: 'app-deletion-member-confirmation-popin',
    templateUrl: './deletion-member-confirmation-popin.component.html',
    styleUrls: ['./deletion-member-confirmation-popin.component.scss']
})
export class DeletionMemberConfirmationPopinComponent {

    constructor(private readonly matIconRegistry: MatIconRegistry,
                private readonly domSanitizer: DomSanitizer,
                public dialogRef: MatDialogRef<OrganizationMember, DialogClosedData<OrganizationMember>>,
                @Inject(MAT_DIALOG_DATA) public organizationMemberDialogData: OrganizationMemberDialogData) {
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
        const organizationMember: OrganizationMember = {
            user_uuid: this.organizationMemberDialogData.organizationUserMember.user_uuid,
            uuid: this.organizationMemberDialogData.organizationUuid,
            role: this.organizationMemberDialogData.organizationUserMember.role,
            added_date: this.organizationMemberDialogData.organizationUserMember.added_date
        };
        this.dialogRef.close({
            data: organizationMember,
            closeEvent: CloseEvent.VALIDATION
        });
    }
}
