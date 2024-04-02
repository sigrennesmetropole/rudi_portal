import {HttpClientModule} from '@angular/common/http';
import {ModuleWithProviders, NgModule, Provider} from '@angular/core';
import {CaptchaEndpointPipe} from 'src/app/shared/angular-captcha/captcha-endpoint.pipe';
import {CaptchaHelperService} from 'src/app/shared/angular-captcha/captcha-helper.service';
import {CaptchaSettings} from 'src/app/shared/angular-captcha/captcha-settings.interface';

import {CaptchaComponent} from 'src/app/shared/angular-captcha/captcha.component';
import {CaptchaService} from 'src/app/shared/angular-captcha/captcha.service';
import {CAPTCHA_SETTINGS} from 'src/app/shared/angular-captcha/config';
import {CorrectCaptchaDirective} from 'src/app/shared/angular-captcha/correct-captcha.directive';

@NgModule({
  imports: [
    HttpClientModule
  ],
  declarations: [
    CaptchaEndpointPipe,
    CaptchaComponent,
    CorrectCaptchaDirective
  ],
  providers: [
    CaptchaService,
    CaptchaHelperService,
    CaptchaEndpointPipe,
    {
      // we need this provide CAPTCHA_SETTINGS declaration
      // since we have added support for the captchaEndpoint
      // setting in component
      provide: CAPTCHA_SETTINGS,
      useValue: null
    }
  ],
  exports: [
    CaptchaComponent,
    CorrectCaptchaDirective
  ]
})
export class BotDetectCaptchaModule {

  static forRoot(config: CaptchaSettings): ModuleWithProviders<BotDetectCaptchaModule> {
    return {
      ngModule: BotDetectCaptchaModule,
      providers: [provideBotDetectCaptcha(config)]
    };
  }

  static forChild(config: CaptchaSettings): ModuleWithProviders<BotDetectCaptchaModule> {
    return {
      ngModule: BotDetectCaptchaModule,
      providers: [provideBotDetectCaptcha(config)]
    };
  }
}

export function provideBotDetectCaptcha(config: CaptchaSettings): Provider {
  return [
    {
      provide: CAPTCHA_SETTINGS,
      useValue: config
    }
  ];
}
