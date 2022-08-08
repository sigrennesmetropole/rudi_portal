import {Component, Input, OnChanges, SimpleChange} from '@angular/core';
import {PasswordStrengthCriterion} from './password-strength-criterion';
import {PasswordStrengthCriteria} from './password-strength-criteria';
import {Validators} from '@angular/forms';

@Component({
    selector: 'app-password-strength',
    templateUrl: './password-strength.component.html',
    styleUrls: ['./password-strength.component.scss']
})
export class PasswordStrengthComponent implements OnChanges {
    /** Le mot-de-passe dont on cherche la force */
    @Input() password: string;

    /** Critères à vérifier */
    readonly criteria = new PasswordStrengthCriteria([
        new PasswordStrengthCriterion('lowerLetters', Validators.pattern(/[a-z]+/)),
        new PasswordStrengthCriterion('upperLetters', Validators.pattern(/[A-Z]+/)),
        new PasswordStrengthCriterion('numbers', Validators.pattern(/[0-9]+/)),
        new PasswordStrengthCriterion('symbols', Validators.pattern(/[!-/:-@\[-`{-~¡-ÿĀ-ʯ]+/))
    ]);

    /** De 0 à n. n étant le nombre de critères à vérifier */
    strength: number;

    ngOnChanges(changes: { [propName: string]: SimpleChange }): void {
        this.strength = this.criteria.getStrength(this.password);
    }

}
