import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {AuthGuardService as AuthGuard} from '@core/services/auth-guard.service';
import {OnlyAnonymousGuardService} from '@core/services/only-anonymous-guard.service';
import {AccountValidationComponent} from './account-validation.component';
import {ForgotPasswordComponent} from './pages/forgot-password/forgot-password.component';
import {LoginComponent} from './pages/login/login.component';
import {ResetPasswordComponent} from './pages/reset-password/reset-password.component';
import {SignUpComponent} from './pages/sign-up/sign-up.component';
import {SupportRudiComponent} from './pages/support-rudi/support-rudi.component';

const routes: Routes = [
    {
        path: '',
        component: LoginComponent,
        canActivate: mapToCanActivate([AuthGuard, OnlyAnonymousGuardService])
    },
    {
        // Path authentification
        path: 'login',
        component: LoginComponent,
        canActivate: mapToCanActivate([AuthGuard, OnlyAnonymousGuardService])
    },
    {
        // Path signup
        path: 'sign-up',
        component: SignUpComponent,
        canActivate: mapToCanActivate([AuthGuard, OnlyAnonymousGuardService]),
        data: {
            forcedRoute: '/catalogue'
        }
    },
    {
        // Path forgot-password
        path: 'forgot-password',
        component: ForgotPasswordComponent,
        canActivate: mapToCanActivate([OnlyAnonymousGuardService])
    },
    {
        // Path reset-password
        path: 'reset-password',
        component: ResetPasswordComponent,
        canActivate: mapToCanActivate([AuthGuard])
    },
    {
        // Path support rudi
        path: 'support-rudi',
        component: SupportRudiComponent,
        canActivate: mapToCanActivate([AuthGuard])
    },
    {
        path: 'accountValidation',
        component: AccountValidationComponent,
        canActivate: mapToCanActivate([AuthGuard])
    }
];

@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        RouterModule.forChild(routes)
    ],
    exports: [RouterModule]
})
export class LoginRoutingModule {
}
