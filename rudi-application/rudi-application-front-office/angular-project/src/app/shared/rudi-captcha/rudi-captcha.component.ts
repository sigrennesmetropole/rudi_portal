import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {CaptchaComponent} from '@shared/angular-captcha/captcha.component';
import {CaptchaModel, CaptchaService} from 'micro_service_modules/acl/acl-api';
import {Observable} from 'rxjs';

const ACL_SERVICE_BASEPATH = '/acl/v1';
const CAPTCHA_NAMESPACE = '/kaptcha';

@Component({
    selector: 'app-rudi-captcha',
    templateUrl: './rudi-captcha.component.html',
    styleUrls: ['./rudi-captcha.component.scss']
})
export class RudiCaptchaComponent implements OnInit {

    /**
     * Type du captcha qu'on veut
     */
    @Input()
    nomCaptcha: string;

    @ViewChild(CaptchaComponent, {static: true}) captchaComponent: CaptchaComponent;

    constructor(
        private readonly captchaService: CaptchaService,
    ) {
    }

    ngOnInit(): void {
        this.captchaComponent.captchaEndpoint = ACL_SERVICE_BASEPATH + CAPTCHA_NAMESPACE;
    }

    /**
     * Indique si le champ de captcha a été rempli par l'utilisateur
     */
    isFilled(): boolean {
        return this.captchaComponent?.captchaCode !== '';
    }

    /**
     * Envoie pour validation la saisie utilisateur du captcha auprès du back
     */
    validateInput(): Observable<boolean> {
        const captcha = {id: this.captchaComponent.captchaId, code: this.captchaComponent.captchaCode} as CaptchaModel;
        return this.captchaService.validateCaptcha(captcha);
    }
}
