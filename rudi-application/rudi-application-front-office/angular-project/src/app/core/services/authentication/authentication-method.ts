import {FormGroup} from '@angular/forms';

/**
 * Les différents d'authentification de l'appli
 */
export enum AuthenticationState {
    ANONYMOUS = 'ANONYMOUS',
    USER = 'USER'
}

export abstract class AuthenticationMethod {

    /**
     * Constructeur
     */
    protected constructor() {
    }

    abstract getBaseUrl(): string;

    abstract getContentType(): string;

    abstract getPayload(formGroup: FormGroup): any | undefined;

    abstract getLogin(formGroup: FormGroup): string;

    abstract getTargetState(): AuthenticationState;
}
