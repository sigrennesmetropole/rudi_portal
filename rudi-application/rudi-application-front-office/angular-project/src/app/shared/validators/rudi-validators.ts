import {AbstractControl, ValidationErrors} from '@angular/forms';

export class RudiValidators {

    /**
     * Stricter version of Angular Validators.email
     * @param control the control to validate
     * @see Validators.email
     * @author https://stackoverflow.com/a/46181
     */
    static email(control: AbstractControl): ValidationErrors | null {
        const validEmail = String(control.value)
            .toLowerCase()
            .match(
                /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
            ) !== null;
        return validEmail ? null : {email: true};
    }

}
