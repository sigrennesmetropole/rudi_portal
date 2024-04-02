import {InjectionToken} from '@angular/core';
import {CaptchaSettings} from 'src/app/shared/angular-captcha/captcha-settings.interface';

export let CAPTCHA_SETTINGS = new InjectionToken<CaptchaSettings>('captcha.settings');
