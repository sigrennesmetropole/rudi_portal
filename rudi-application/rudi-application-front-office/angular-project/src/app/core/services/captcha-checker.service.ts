import {Injectable} from '@angular/core';
import {RudiCaptchaComponent} from '../../shared/rudi-captcha/rudi-captcha.component';
import {Observable} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {ErrorWithCause} from '../../shared/models/error-with-cause';

export const CAPTCHA_NOT_VALID_CODE = 423; // Erreur 400 (pour erreur client)

@Injectable({
    providedIn: 'root'
})
export class CaptchaCheckerService {
    constructor() {
    }

    validateCaptchaAndDoNextStep(isEnabled: boolean, rudiCaptcha: RudiCaptchaComponent, nextStep: Observable<unknown>): Observable<unknown> {
        // Si le service de captcha n'est pas activé, on ignore cette étape et passe au next step
        return !isEnabled ? nextStep : rudiCaptcha.validateInput().pipe(
            switchMap((correctCaptcha: boolean) => {
                if (!correctCaptcha) {
                    throw new ErrorWithCause('Le captcha saisi n\'est pas correct', null, CAPTCHA_NOT_VALID_CODE);
                }
                return nextStep;
            })
        );
    }
}
