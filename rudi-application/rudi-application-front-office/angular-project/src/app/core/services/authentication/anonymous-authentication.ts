import {Injectable} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {AuthenticationMethod, AuthenticationState} from './authentication-method';

export const ANONYMOUS_USERNAME = 'anonymous';

@Injectable({
    providedIn: 'root'
})
export class AnonymousAuthentication extends AuthenticationMethod {

    constructor() {
        super();
    }

    getBaseUrl(): string {
        return '/anonymous';
    }

    getContentType(): string {
        return 'application/json';
    }

    getPayload(formGroup: FormGroup): undefined {
        return undefined;
    }

    getLogin(formGroup: FormGroup): string {
        return ANONYMOUS_USERNAME;
    }

    getTargetState(): AuthenticationState {
        return AuthenticationState.ANONYMOUS;
    }
}
