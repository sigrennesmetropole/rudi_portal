import {Observable} from 'rxjs';
import {DialogClosedData} from '../../../data-set/models/dialog-closed-data';
import {DefaultMatDialogConfig} from '../default-mat-dialog-config';
import {MatDialog} from '@angular/material/dialog';
import {Injectable} from '@angular/core';
import {MemberPopinComponent} from '../../../shared/member-popin/member-popin.component';
import {
    OrganizationMemberDialogData
} from '../../../organization/components/administration-tab/organization-members-table/organization-member-dialog-data';
import {OrganizationMember} from '../../../strukture/strukture-model';
import {
    DeletionMemberConfirmationPopinComponent
} from '../../../organization/components/administration-tab/deletion-member-confirmation-popin/deletion-member-confirmation-popin.component';
import {
    UpdateUserPasswordPopinComponent
} from '../../../organization/components/administration-tab/update-user-password-popin/update-user-password-popin.component';
import {OrganizationTableDialogData} from '../../../organization/components/administration-tab/organization-table/organization-table-dialog-data';

@Injectable({
    providedIn: 'root'
})
export class DialogMemberOrganizationService {

    constructor(private readonly dialog: MatDialog) {
    }

    /**
     * Ouverture d'une dialog permettant d'administrer les membres d'une organisation
     */
    public openDialogAddMember(organizationMemberDialogData: OrganizationMemberDialogData): Observable<DialogClosedData<OrganizationMember>> {
        const dialogConfig = new DefaultMatDialogConfig<OrganizationMemberDialogData>();
        dialogConfig.data = organizationMemberDialogData;
        const dialogRef = this.dialog.open(MemberPopinComponent, dialogConfig);
        return dialogRef.afterClosed();
    }

    /**
     * Ouverture d'une dialog permettant d'administrer les membres d'une organisation
     */
    public openDialogUpdateMember(organizationMemberDialogData: OrganizationMemberDialogData): Observable<DialogClosedData<OrganizationMember>> {
        const dialogConfig = new DefaultMatDialogConfig<OrganizationMemberDialogData>();
        dialogConfig.data = organizationMemberDialogData;
        const dialogRef = this.dialog.open(MemberPopinComponent, dialogConfig);
        return dialogRef.afterClosed();
    }

    /**
     * Ouverture d'une dialog permettant de detacher un membre d'une organisation
     */
    public openDialogDeletionConfirmation(organizationMemberDialogData: OrganizationMemberDialogData): Observable<DialogClosedData<OrganizationMember>> {
        const dialogConfig = new DefaultMatDialogConfig<OrganizationMemberDialogData>();
        dialogConfig.data = organizationMemberDialogData;
        const dialogRef = this.dialog.open(DeletionMemberConfirmationPopinComponent, dialogConfig);
        return dialogRef.afterClosed();
    }

    /**
     * Ouverture d'une dialog permettant de modifier le mot de passe d'une organisation
     */
    public openDialogUpdatepassword(organizationTableDialogData: OrganizationTableDialogData): Observable<DialogClosedData<void>> {
        const dialogConfig = new DefaultMatDialogConfig<OrganizationTableDialogData>();
        dialogConfig.data = organizationTableDialogData;
        const dialogRef = this.dialog.open(UpdateUserPasswordPopinComponent, dialogConfig);
        return dialogRef.afterClosed();
    }
}
