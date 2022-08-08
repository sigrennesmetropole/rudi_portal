import {PasswordStrengthCriterion} from './password-strength-criterion';

export class PasswordStrengthCriteria implements Iterable<PasswordStrengthCriterion> {

    constructor(private criterionList: PasswordStrengthCriterion[]) {
    }

    /**
     * Test la complexité du password à travers une regex
     */
    getStrength(password: string): number {
        if (!password) {
            return 0;
        }
        return this.criterionList
            .filter(criterion => criterion.accepts(password))
            .map(criterion => criterion.strength)
            .reduce((sum, currentStrength) => sum + currentStrength, 0);
    }

    [Symbol.iterator](): Iterator<PasswordStrengthCriterion> {
        return this.criterionList[Symbol.iterator]();
    }


}
