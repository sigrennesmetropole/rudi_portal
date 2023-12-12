import {AbstractControl, ValidationErrors} from '@angular/forms';

export type Validator = (control: AbstractControl) => ValidationErrors | null;
