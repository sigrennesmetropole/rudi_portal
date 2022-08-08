import {AbstractControl} from '@angular/forms';

export function ConfirmedValidator(controlName: string, matchingControlName: string) {
    return (controls: AbstractControl) => {
        const control = controls.get(controlName);
        const matchingControl = controls.get(matchingControlName);
        if (matchingControl.errors && !matchingControl.errors.confirmedValidator) {
            return;
        }
        if (control.value !== matchingControl.value) {
            return matchingControl.setErrors({confirmedValidator: true});
        } else {
            return  matchingControl.setErrors(null);
        }
    };
}
