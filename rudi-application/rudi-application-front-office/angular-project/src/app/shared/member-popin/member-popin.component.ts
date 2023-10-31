import {Component, Inject, Input} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {CloseEvent, DialogClosedData} from '../../data-set/models/dialog-closed-data';
import {OrganizationRole} from '../../strukture/api-strukture';
import {TranslateService} from '@ngx-translate/core';
import {
    OrganizationMemberDialogData
} from '../../organization/components/administration-tab/organization-members-table/organization-member-dialog-data';
import {OrganizationMember} from '../../strukture/strukture-model';

@Component({
    selector: 'app-member-popin',
    templateUrl: './member-popin.component.html',
    styleUrls: ['./member-popin.component.scss']
})
export class MemberPopinComponent {

    roles: OrganizationRole[] = [OrganizationRole.Editor, OrganizationRole.Administrator];
    role: OrganizationRole;

    disableLoginField: boolean;
    loginValue: string;
    confirmationPopinText: string;

    constructor(private readonly matIconRegistry: MatIconRegistry,
                private readonly domSanitizer: DomSanitizer,
                public dialogRef: MatDialogRef<OrganizationMember, DialogClosedData<OrganizationMember>>,
                @Inject(MAT_DIALOG_DATA) public organizationMemberDialogData: OrganizationMemberDialogData,
                private readonly translateService: TranslateService) {
        this.matIconRegistry.addSvgIcon('icon-close', this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg'));
        this.role = this.organizationMemberDialogData.organizationUserMember?.role ?? this.roles[0];
        this.loginValue = organizationMemberDialogData.organizationUserMember?.login ?? '';
        // Si on n'a aucun membre passé à la popin => On est en ajout et non en édition ou suppression
        this.disableLoginField = Boolean(organizationMemberDialogData.organizationUserMember);
        this.confirmationPopinText = this.disableLoginField ? this.translateService.instant('personalSpace.project.tabs.deletion.confirmationPopup.update')
            : this.translateService.instant('personalSpace.project.tabs.deletion.confirmationPopup.addMember');
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
            user_uuid: this.organizationMemberDialogData.organizationUserMember?.user_uuid,
            uuid: this.organizationMemberDialogData.organizationUuid,
            role: this.role,
            added_date: this.organizationMemberDialogData.organizationUserMember?.added_date,
            login: this.loginValue
        };
        this.dialogRef.close({
            data: organizationMember,
            closeEvent: CloseEvent.VALIDATION
        });
    }

    /**
     * Traduction de l'énum rôle
     * @param role
     */
    computeRoleLabel(role: OrganizationRole): string {
        if (role === OrganizationRole.Editor) {
            return this.translateService.instant('metaData.administrationTab.membersTable.editor') + ' ';
        } else if (role === OrganizationRole.Administrator) {
            return this.translateService.instant('metaData.administrationTab.membersTable.administrator') + ' ';
        } else {
            return '';
        }
    }
}
