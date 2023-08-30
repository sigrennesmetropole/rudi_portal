import {Component, Inject, ViewChild} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {OrganizationMember} from '../../../../strukture/strukture-model';
import {CloseEvent, DialogClosedData} from '../../../../data-set/models/dialog-closed-data';
import {AbstractControl, AbstractControlOptions, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {BreakpointObserverService} from '../../../../core/services/breakpoint-observer.service';
import {RouteHistoryService} from '../../../../core/services/route-history.service';
import {AuthenticationService} from '../../../../core/services/authentication.service';
import {SnackBarService} from '../../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {ActivatedRoute} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ConfirmedValidator} from '../../../../login/pages/sign-up/confirmed-validator';
import {OrganizationMetierService} from '../../../../core/services/organization/organization-metier.service';
import {Level} from '../../../../shared/notification-template/notification-template.component';
import {PasswordUpdate} from '../../../../strukture/api-strukture';
import {LogService} from '../../../../core/services/log.service';
import {PASSWORD_REGEX} from '../../../../core/const';
import {RudiCaptchaComponent} from '../../../../shared/rudi-captcha/rudi-captcha.component';
import {CAPTCHA_NOT_VALID_CODE, CaptchaCheckerService} from '../../../../core/services/captcha-checker.service';
import {OrganizationTableDialogData} from '../organization-table/organization-table-dialog-data';
import {ErrorWithCause} from '../../../../shared/models/error-with-cause';

@Component({
    selector: 'app-update-user-password-popin',
    templateUrl: './update-user-password-popin.component.html',
    styleUrls: ['./update-user-password-popin.component.scss']
})
export class UpdateUserPasswordPopinComponent {

    /**
     * Formulaire de saisie
     */
    formGroup: FormGroup;

    /**
     * Le message d'erreur si erreur a lieu lors de la modification
     */
    errorString = '';

    /**
     * Est-ce que le composant se charge ? (authent en cours)
     */
    isLoading = false;

    /**
     * Cache-t-on le mot de passe
     */
    hidePassword = true;

    hidepasswordOld = true;

    hideConfirmPassword = true;

    passwordMinLength = 12;
    passwordMaxLength = 100;
    passwordOldMinLength = 12;
    passwordOldMaxLength = 100;

    /**
     * Indique si le captcha doit s'activer sur cette page
     */
    enableCaptchaOnPage = true;

    @ViewChild(RudiCaptchaComponent) rudiCaptcha: RudiCaptchaComponent;

    get snackBarParam(): string | null {
        return this.route.snapshot.queryParams.snackBar;
    }

    constructor(private readonly matIconRegistry: MatIconRegistry,
                private readonly domSanitizer: DomSanitizer,
                public dialogRef: MatDialogRef<OrganizationMember, DialogClosedData<void>>,
                private formBuilder: FormBuilder,
                private routeHistoryService: RouteHistoryService,
                private authenticationService: AuthenticationService,
                private snackBarService: SnackBarService,
                private translateService: TranslateService,
                private breakpointObserver: BreakpointObserverService,
                private snackbar: MatSnackBar,
                private readonly logService: LogService,
                private readonly route: ActivatedRoute,
                private readonly organizationMetierService: OrganizationMetierService,
                private readonly captchaCheckerService: CaptchaCheckerService,
                @Inject(MAT_DIALOG_DATA) public organizationTableDialogData: OrganizationTableDialogData) {

        this.enableCaptchaOnPage = organizationTableDialogData.enableCaptchaOnPage;

        this.matIconRegistry.addSvgIcon('icon-close', this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg'));
        // Construction du formulaire d'inscription
        this.formGroup = this.formBuilder.group({
                password: ['', [Validators.required, Validators.minLength(this.passwordMinLength),
                    Validators.maxLength(this.passwordMaxLength), Validators.pattern(PASSWORD_REGEX)]],
                confirmPassword: ['', [Validators.required]],
                passwordOld: ['', [Validators.required]],
            },
            {
                validators: ConfirmedValidator('password', 'confirmPassword')
            } as AbstractControlOptions
        );
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
        const passwordUpdate: PasswordUpdate = {
            newPassword: this.password,
            oldPassword: this.passwordOld,
        };
        this.isLoading = true;
        this.captchaCheckerService.validateCaptchaAndDoNextStep(
            this.enableCaptchaOnPage,
            this.rudiCaptcha,
            this.organizationMetierService.updateUserOrganizationPassword(this.organizationTableDialogData.organizationUuid, passwordUpdate)
        )
            .subscribe({
                    next: () => {
                        this.isLoading = false;
                        this.snackBarService.openSnackBar({
                            message: this.translateService.instant('metaData.administrationTab.membersTable.updatePassword.succes'),
                            level: Level.INFO
                        });
                        this.dialogRef.close({
                            data: null,
                            closeEvent: CloseEvent.VALIDATION
                        });
                    },
                    error: err => {
                        this.isLoading = false;
                        this.logService.error(err);

                        let errorLabel = this.translateService.instant('metaData.administrationTab.membersTable.updatePassword.error');

                        if (err instanceof ErrorWithCause && err.code === CAPTCHA_NOT_VALID_CODE) {
                            errorLabel = this.translateService
                                .instant('metaData.administrationTab.membersTable.updatePassword.errorCaptcha');
                        }

                        if (err != null && err.status != null
                            && err.status >= 400 && err.status < 500
                            && err.error != null) {
                            errorLabel = err.error.label;
                        }

                        this.snackBarService.openSnackBar({
                            message: errorLabel,
                            level: Level.ERROR
                        });
                    }
                }
            );
    }


    /**
     * formControls permettant de verifier les validators dans le HTML
     */
    get formControls(): { [key: string]: AbstractControl } {
        return this.formGroup.controls;
    }

    /**
     * Teste si le formulaire est valide
     */
    get isValid(): boolean {
        return this.formGroup.valid && (this.rudiCaptcha?.isFilled() || !this.enableCaptchaOnPage);
    }


    private get password(): string {
        return this.formGroup.get('password').value;
    }

    private get passwordOld(): string {
        return this.formGroup.get('passwordOld').value;
    }
}
