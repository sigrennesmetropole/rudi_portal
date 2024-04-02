import {HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {URIComponentCodec} from '../codecs/uri-component-codec';
import {AuthenticationMethod, AuthenticationState} from './authentication-method';

@Injectable({
    providedIn: 'root'
})
export class LoginAuthentication extends AuthenticationMethod {

    constructor() {
        super();
    }

    getBaseUrl(): string {
        return '/authenticate';
    }

    getContentType(): string {
        return 'application/x-www-form-urlencoded';
    }

    getPayload(formGroup: FormGroup): any {
        const _form = formGroup.value;
        const identifiant = _form.login;
        const password = _form.password;
        return new HttpParams({
            encoder: new URIComponentCodec()
        })
            .set('login', identifiant)
            .set('password', password);
    }

    getLogin(formGroup: FormGroup): string {
        const _form = formGroup.value;
        return _form.login;
    }

    getTargetState(): AuthenticationState {
        return AuthenticationState.USER;
    }
}
