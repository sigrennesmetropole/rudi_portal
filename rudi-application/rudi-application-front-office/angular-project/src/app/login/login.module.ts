import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {LoginComponent} from './pages/login/login.component';
import {NotAuthorizedComponent} from './pages/not-authorized/not-authorized.component';
import {CoreModule} from '../core/core.module';
import {SharedModule} from '../shared/shared.module';
import {CommonModule} from '@angular/common';
import {SignUpComponent} from './pages/sign-up/sign-up.component';
import {AccountValidationComponent} from './account-validation.component';
import {SupportRudiComponent} from './pages/support-rudi/support-rudi.component';
import {LoginRoutingModule} from './login-routing.module';
import {ForgotPasswordComponent} from './pages/forgot-password/forgot-password.component';
import {ResetPasswordComponent} from './pages/reset-password/reset-password.component';
import {AccountInfoComponent} from './pages/account-info/account-info.component';
@NgModule({
    declarations:
        [
            LoginComponent,
            NotAuthorizedComponent,
            SignUpComponent,
            AccountValidationComponent,
            SupportRudiComponent,
            ForgotPasswordComponent,
            ResetPasswordComponent,
            AccountInfoComponent
        ],
    imports: [
        CommonModule,
        CoreModule,
        SharedModule,
        LoginRoutingModule
    ],
    exports: [],
    entryComponents: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
    ]
})
export class LoginModule {
}
