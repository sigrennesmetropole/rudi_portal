import {Component, OnInit} from '@angular/core';
import {UserService} from '../../../core/services/user.service';
import {AddressType, EmailAddress, PostalAddress, TelephoneAddress, User} from '../../../acl/acl-model';
import {TranslateService} from '@ngx-translate/core';
import {BreakpointObserverService} from '../../../core/services/breakpoint-observer.service';

@Component({
    selector: 'app-account-info',
    templateUrl: './account-info.component.html',
    styleUrls: ['./account-info.component.scss']
})
export class AccountInfoComponent implements OnInit {
    public user: User | undefined;

    mediaSize: any;

    constructor(
        private readonly translateService: TranslateService,
        private readonly utilisateurService: UserService,
        private readonly breakpointObserver: BreakpointObserverService,
    ) {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    ngOnInit(): void {

        // récupération de l'évènement d'authentification
        this.utilisateurService.getConnectedUser()
            .subscribe(
                (user: User | undefined) => {
                    this.user = user;
                }
            );
    }

    /**
     * Permet de retourner le type d'adresse : TelephoneAddress
     */
    get telephoneAddresses(): TelephoneAddress[] {
        return this.user ? this.user.addresses
            .filter(address => address.type === AddressType.Phone)
            .map(address => address as TelephoneAddress) : [];
    }

    /**
     * Permet de retourner le type d'adresse : EmailAddress
     */
    get emailAddresses(): EmailAddress[] {
        return this.user ? this.user.addresses
            .filter(address => address.type === AddressType.Email)
            .map(address => address as EmailAddress) : [];
    }

    /**
     * Permet de retourner le type d'adresse : PostalAddress
     */
    get postalAddresses(): PostalAddress[] {
        return this.user ? this.user.addresses
            .filter(address => address.type === AddressType.Postal)
            .map(address => address as PostalAddress) : [];
    }
}
