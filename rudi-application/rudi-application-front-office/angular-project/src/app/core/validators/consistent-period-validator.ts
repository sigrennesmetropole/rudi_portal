import {FormGroup, ValidatorFn} from '@angular/forms';
import {Moment} from 'moment';

/**
 * Interface permettant de valider une période saisie
 */
export interface PeriodValidatorParams {

    /**
     * Le nom du formControl attaché au champ de saisie du début de période
     */
    beginControlName: string;

    /**
     * Le nom du formControl attaché au champ de saisie de la fin de la période
     */
    endControlName: string;
}

/**
 * Fonction de validation de la période saisie pour la réalisation du projet
 * @param param un ensemble de plages de période à vérifier
 */
export function consistentPeriodValidator(param: PeriodValidatorParams): ValidatorFn {
    return (formGroup: FormGroup) => {

        // Aucune conf fournie on fait rien
        if (!param) {
            return null;
        }

        // Récupération des 2 contrôles de saisie de la période
        const beginDate = formGroup.get(param.beginControlName);
        const endDate = formGroup.get(param.endControlName);

        // Erreur d'inconsistance valide que si les 2 champs existent
        if (beginDate != null && endDate != null) {

            // Il ne sont pas en erreur par défaut
            beginDate.setErrors(null);
            endDate.setErrors(null);

            // Récupération des valeurs
            const beginValue = beginDate.value as Moment;
            const endValue = endDate.value as Moment;

            // Il y'a les 2 valeurs saisies, check que la date de fin est bien après la date de début
            if (beginValue != null && endValue != null && beginValue.isAfter(endValue)) {

                // KO les 2 contrôles sont en erreur
                beginDate.setErrors({periodInconsistent: true});
                endDate.setErrors({periodInconsistent: true});

                // Le form entier est en erreur
                return {periodInconsistent: true};
            }
        }

        return null;
    };
}
