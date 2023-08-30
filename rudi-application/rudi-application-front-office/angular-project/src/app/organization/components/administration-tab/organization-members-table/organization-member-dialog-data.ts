import {OrganizationUserMember} from '../../../../strukture/api-strukture';

export interface OrganizationMemberDialogData {
    organizationUserMember?: OrganizationUserMember;
    title?: string;
    subTitle?: string;
    organizationUuid?: string;
    fieldLoginDescription?: string;
    fieldRoleDescription?: string;
}
