import {Injectable} from '@angular/core';
import {AuthenticationMethod} from "./authentication-method";
import {HttpParams} from "@angular/common/http";
import {FormGroup} from "@angular/forms";

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
        const identifiant = _form['login'];
        const password = _form['password'];
        return new HttpParams().set('login', identifiant).set('password', password);
    }

    getLogin(formGroup: FormGroup): string {
        const _form = formGroup.value;
        return _form['login'];
    }

}
