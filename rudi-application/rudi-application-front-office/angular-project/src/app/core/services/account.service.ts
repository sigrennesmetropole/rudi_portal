import {Injectable} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Account, User} from '../../acl/acl-model';
import {Observable, throwError} from 'rxjs';
import {AclService, PasswordChange} from '../../acl/acl-api';
import {HttpErrorResponse} from '@angular/common/http';
import {TranslateService} from '@ngx-translate/core';
import {catchError} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class AccountService {

    /**
     * Constructeur
     */
    constructor(private readonly aclService: AclService,
                private readonly translateService: TranslateService,
    ) {
    }

    /**
     * Conversion d'un formulaire vers un objet métier : compte utilisateur
     * @param formGroup le formulaire à convertir
     */
    private static formGroupToAccount(formGroup: FormGroup): Account {
        return {
            firstname: formGroup.get('prenom').value,
            lastname: formGroup.get('nom').value,
            login: formGroup.get('adresseEmail').value,
            password: formGroup.get('password').value,
            hasSubscribeToNotifications: formGroup.get('subscribeToNotifications').value
        };
    }

    /**
     * Création d'un compte utilisateur à partir des donénes de formulaire de création de compte
     * si la création de compte réussit : authentification automatique
     *
     * En cas d'erreur, une chaîne de caractère est levée dans la callback d'erreur de l'observable
     * elle contient le paramètre : errorString qui est la cause textuelle de l'erreur
     *
     * @param signupForm le formulaire permettant de créer le compte et de s'authentifier après
     */
    createAccount(signupForm: FormGroup): Observable<User> {

        // Conversion du formGroup en objet création de compte
        const account: Account = AccountService.formGroupToAccount(signupForm);

        // Appel REST création de compte
        return this.aclService.requestAccountCreation(account).pipe(
            // Erreur pendant la création de compte on arrête tout et on renvoie une chaîne qui décrit l'erreur
            catchError((error: HttpErrorResponse) => throwError(this.handleErrorCreateAccount(error)))
        );
    }

    /**
     * gestion de la restitution d'un message d'erreur adéquat lors d'une erreur de création de compte
     * @param error l'erreur HTTP de l'appel REST
     */
    private handleErrorCreateAccount(error: HttpErrorResponse): string {

        // Préfixe de l'erreur
        const errorPrefix = this.translateService.instant('signup.errorCreateAccount');

        // Gestion cas par défaut
        if (error == null) {
            return errorPrefix + this.translateService.instant('signup.errorServer');
        }

        // Gestion des codes d'erreurs customs
        if (error.status === 441) {
            return errorPrefix + this.translateService.instant('signup.errorMissingFieldAccount');
        } else if (error.status === 442) {
            return errorPrefix + this.translateService.instant('signup.errorLoginNotMail');
        } else if (error.status === 443) {
            return errorPrefix + this.translateService.instant('signup.errorLoginAlreadyExists');
        } else if (error.status === 444) {
            return errorPrefix + this.translateService.instant('signup.errorPasswordLength');
        }

        // Erreur par défaut
        return errorPrefix + this.translateService.instant('signup.errorServer');
    }

    /**
     * Confirmation d'activation de compte par le token
     */
    validateAccount(token: string): Observable<User> {
        return this.aclService.validateAccount(token);
    }

    /**
     * Verification d'activation de compte
     */
    isAccountCreatedNotValidated(login: string): Observable<boolean> {
        return this.aclService.isCreatedNotValidated(login);
    }

    requestPasswordChange(email: string): Observable<void> {
        return this.aclService.requestPasswordChange(email);
    }

    validatePasswordChange(passwordChange: PasswordChange): Observable<void> {
        return this.aclService.validatePasswordChange(passwordChange);
    }

    /**
     * Check le delai de peremption de token token au click sur le lien contenu dans le email
     * @param token
     */
    checkPasswordChangeToken(token: string): Observable<void> {
        return this.aclService.checkPasswordChangeToken(token);
    }
}
