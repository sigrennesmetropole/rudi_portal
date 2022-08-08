import {ValidatorFn} from '@angular/forms';

export class PasswordStrengthCriterion {

    constructor(readonly i18nKey: string, readonly validator: ValidatorFn, readonly strength = 1) {
    }

    /**
     * @param password mot-de-passe testé
     * @return true si le mot-de-passe respecte ce critère
     */
    accepts(password: string): boolean {
        if (!password) {
            return false;
        }
        const thisArg = {
            control: {
                value: password
            }
        };
        const validatorWithControl = this.validator.bind(thisArg);
        const errors = validatorWithControl(thisArg.control);
        return errors === null;
    }
}
