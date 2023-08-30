import {Observable} from 'rxjs';
import {KindOfData} from '../../../api-kmedia';
import {shareReplay, switchMap} from 'rxjs/operators';
import {Injectable} from '@angular/core';
import {Base64EncodedLogo, ImageLogoService} from '../image-logo.service';
import {
    Organization, OrganizationMember, OrganizationMemberType,
    OrganizationSearchCriteria,
    PagedOrganizationList,
    PagedOrganizationUserMembers
} from '../../../strukture/strukture-model';
import {OrganizationService, PasswordUpdate} from '../../../strukture/api-strukture';
import {PageResultUtils} from '../../../shared/utils/page-result-utils';

@Injectable({
    providedIn: 'root'
})
export abstract class OrganizationMetierService {

    protected constructor(
        protected imageLogoService: ImageLogoService,
        protected organizationService: OrganizationService,
    ) {
    }

    private readonly logosByOrganizationId: { [key: string]: Observable<Base64EncodedLogo> } = {};

    getLogo(organizationId: string): Observable<Base64EncodedLogo> {
        if (this.logosByOrganizationId[organizationId]) {
            return this.logosByOrganizationId[organizationId];
        }

        return this.logosByOrganizationId[organizationId] = this.downloadProducerMediaByType(organizationId, KindOfData.Logo).pipe(
            // Source pour la gestion du cache : https://betterprogramming.pub/how-to-create-a-caching-service-for-angular-bfad6cbe82b0
            shareReplay(1),
            switchMap(blob => this.imageLogoService.createImageFromBlob(blob))
        );
    }

    protected abstract downloadProducerMediaByType(organizationId: string, kindOfData: KindOfData): Observable<Blob>;

    searchOrganizations(searchCriteria: OrganizationSearchCriteria): Observable<PagedOrganizationList> {
        return this.organizationService.searchOrganizations(
            searchCriteria.uuid,
            searchCriteria.name,
            searchCriteria.active,
            searchCriteria.user_uuid,
            searchCriteria.offset,
            searchCriteria.limit);
    }

    getMyOrganizations(userUuid: string): Observable<Organization[]> {
        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.searchOrganizations({
                offset,
                user_uuid: userUuid,
            }));
    }

    getOrganizationByUuid(userUuid: string): Observable<Organization> {
        return this.organizationService.getOrganization(userUuid);
    }

    searchOrganizationMembers(
        organizationUuid: string, searchText: string, offset: number, limit: number, order: string
    ): Observable<PagedOrganizationUserMembers> {
        return this.organizationService.searchOrganizationUserMembers(
            organizationUuid, searchText, OrganizationMemberType.Person, offset, limit, order
        );
    }

    isAdministrator(organizationUuid?: string): Observable<boolean> {
        return this.organizationService.isConnectedUserOrganizationAdministrator(organizationUuid);
    }

    addOrganizationMember(organizationUuid: string, organizationMember: OrganizationMember): Observable<OrganizationMember> {
        return this.organizationService.addOrganizationMember(organizationUuid, organizationMember);
    }

    removeOrganizationMember(organizationUuid: string, userUuid: string): Observable<any> {
        return this.organizationService.removeOrganizationMember(organizationUuid, userUuid);
    }

    updateOrganizationMember(organizationUuid: string, userUuid: string, organizationMember: OrganizationMember): Observable<any> {
        return this.organizationService.updateOrganizationMember(organizationUuid, userUuid, organizationMember);
    }

    updateUserOrganizationPassword(organizationUuid: string, passwordUpdate: PasswordUpdate): Observable<OrganizationMember> {
        return this.organizationService.updateUserOrganizationPassword(organizationUuid, passwordUpdate);
    }
}
