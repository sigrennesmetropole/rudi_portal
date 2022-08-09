import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import {AuthGuardService as AuthGuard} from '../core/services/auth-guard.service';
import {UserGuardService} from '../core/services/user-guard.service';
import {LoginComponent} from './pages/login/login.component';
import {SignUpComponent} from './pages/sign-up/sign-up.component';
import {SupportRudiComponent} from './pages/support-rudi/support-rudi.component';
import {AccountValidationComponent} from './account-validation.component';
import {AccountInfoComponent} from './pages/account-info/account-info.component';
import {ForgotPasswordComponent} from './pages/forgot-password/forgot-password.component';
import {ResetPasswordComponent} from './pages/reset-password/reset-password.component';
import {OnlyAnonymousGuardService} from '../core/services/only-anonymous-guard.service';

const routes: Routes = [
    {
        path: '',
        component: LoginComponent,
        canActivate: [AuthGuard]
    },
    {
        // Path Account info
        path: 'account',
        component: AccountInfoComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
    {
        // Path authentification
        path: 'login',
        component: LoginComponent,
        canActivate: [AuthGuard]
    },
    {
        // Path signup
        path: 'sign-up',
        component: SignUpComponent,
        canActivate: [AuthGuard]
    },
    {
        // Path forgot-password
        path: 'forgot-password',
        component: ForgotPasswordComponent,
        canActivate: [OnlyAnonymousGuardService]
    },
    {
        // Path reset-password
        path: 'reset-password',
        component: ResetPasswordComponent,
        canActivate: [AuthGuard]
    },
    {
        // Path support rudi
        path: 'support-rudi',
        component: SupportRudiComponent,
        canActivate: [AuthGuard]
    },
    {
        path: 'accountValidation',
        component: AccountValidationComponent,
        canActivate: [AuthGuard]
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
