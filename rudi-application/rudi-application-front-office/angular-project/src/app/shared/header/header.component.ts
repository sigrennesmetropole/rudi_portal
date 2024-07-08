import {Location} from '@angular/common';
import {Component, Input, OnInit} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {Router} from '@angular/router';
import {AuthenticationService} from '@core/services/authentication.service';
import {AuthenticationState} from '@core/services/authentication/authentication-method';
import {BreakpointObserverService, MediaSize, NgClassObject} from '@core/services/breakpoint-observer.service';
import {CustomizationService} from '@core/services/customization.service';
import {Base64EncodedLogo, ImageLogoService} from '@core/services/image-logo.service';
import {LogService} from '@core/services/log.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {CustomizationDescription, KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {switchMap} from 'rxjs';
import {Level} from '../notification-template/notification-template.component';


const DEFAULT_PICTO: Base64EncodedLogo = '/assets/images/logo_bleu_orange.svg';

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

    /**
     * Permet de récupérer les dimensions de la page
     */
    @Input()
    mediaSize: MediaSize;

    /**
     * Est-ce que le collapsible est collapsé ?
     */
    isCollapsed = true;

    /**
     * Est-ce qu'on est "connecté" CAD user non anonymous
     */
    isConnectedAsUser = false;

    /**
     * Url d'accès à la documentation depuis le header
     */
    urlToDoc: string;

    logoIsLoading: boolean;
    logo: Base64EncodedLogo;

    constructor(
        private location: Location,
        public dialog: MatDialog,
        public router: Router,
        private readonly authenticationService: AuthenticationService,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly iconRegistry: MatIconRegistry,
        private readonly sanitizer: DomSanitizer,
        private readonly propertiesMetierService: PropertiesMetierService,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
        private readonly konsultService: KonsultService,
        private readonly logger: LogService,
        private readonly imageLogoService: ImageLogoService,
        private readonly customizationService: CustomizationService,
    ) {
        this.logoIsLoading = false;
        // Quand l'utilisateur connecté change (on se connecte ou autre)
        this.authenticationService.authenticationChanged$.subscribe((state) => {
            // On est connecté si on est pas anonymous
            this.isConnectedAsUser = state === AuthenticationState.USER;
        });
        iconRegistry.addSvgIcon(
            'logo-bleu-orange',
            sanitizer.bypassSecurityTrustResourceUrl('assets/images/logo_bleu_orange.svg'));
        this.getUrlToDoc();
    }

    ngOnInit(): void {
        this.initCustomizationDescription();
    }

    /**
     * Récupérer les classes CSS pour mobile/desktop
     */
    get ngClassNavLink(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('nav-link');
    }

    /**
     * Evenet : sur clic du burger
     */
    handleClickBurger(): void {
        if (this.mediaSize.isDeviceMobile) {
            this.isCollapsed = !this.isCollapsed;
        }
    }

    handleClickGoLogin(): void {
        this.router.navigate(['/login']);
    }

    /**
     * Event click sur le bouton : Mon Compte
     */
    handleClickGoMonCompte(): void {
        // Si on est co : go /account
        if (this.isConnectedAsUser) {
            this.router.navigate(['/personal-space/my-account']);
        }
        // pas co ? go login
        else {
            this.router.navigate(['/login']);
        }
    }

    handleClickGoReceivedLinkedDatasets(): Promise<boolean> {
        return this.router.navigate(['/personal-space/my-notifications']);
    }

    handleClickGoToReuse(): Promise<boolean> {
        return this.router.navigate(['/personal-space/my-activity']);
    }

    getUrlToDoc(): void {
        this.propertiesMetierService.get('rudidatarennes.docRudiBzh').subscribe({
            next: (link: string) => {
                this.urlToDoc = link;
            }
        });
    }

    handleClickGoToMySelfdata(): Promise<boolean> {
        return this.router.navigate(['/personal-space/selfdata-datasets']);
    }

    handleClickLogout(): void {
        this.authenticationService.logout().subscribe({
            next: () => {
                AuthenticationService.clearTokens();
                this.snackBarService.openSnackBar({
                    message: this.translateService.instant('header.logOutSuccess'),
                    level: Level.INFO,
                    keepBeforeSecondRouteChange: true
                });
                this.goToCatalogues();
            },
            error: (err) => {
                console.error(err);
                this.snackBarService.openSnackBar({
                    message: this.translateService.instant('header.logOutError'),
                    level: Level.ERROR
                });
            }
        });
    }

    private goToCatalogues(): void {
        this.router.navigate(['/catalogue']);
    }


    private initCustomizationDescription(): void {
        this.logoIsLoading = true;
        this.customizationService.getCustomizationDescription()
            .pipe(
                switchMap((customizationDescription: CustomizationDescription) => {
                    return this.konsultService.downloadCustomizationResource(customizationDescription.main_logo);
                }),
                switchMap((blob: Blob) => {
                    return this.imageLogoService.createImageFromBlob(blob);
                })
            ).subscribe({
            next: (logo: Base64EncodedLogo) => {
                this.logo = logo;
                this.logoIsLoading = false;
            },
            error: (error) => {
                this.logo = DEFAULT_PICTO;
                this.logger.error(error);
                this.logoIsLoading = false;
            }
        });
    }


}
