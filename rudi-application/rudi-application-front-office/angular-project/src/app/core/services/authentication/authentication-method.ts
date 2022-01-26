import {FormGroup} from "@angular/forms";

export abstract class AuthenticationMethod {

    /**
     * Constructeur
     */
    protected constructor() {
    }

    abstract getBaseUrl(): string

    abstract getContentType(): string

    abstract getPayload(formGroup: FormGroup): any | undefined

    abstract getLogin(formGroup: FormGroup): string

}
