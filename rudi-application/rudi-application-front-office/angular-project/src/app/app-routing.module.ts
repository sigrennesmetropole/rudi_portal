import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {AuthGuardService as AuthGuard} from './core/services/auth-guard.service';
import {NotAuthorizedComponent} from './login/pages/not-authorized/not-authorized.component';
import {UserGuardService} from './core/services/user-guard.service';
import {AclConfigurationResolver} from './shared/resolver/acl-configuration-resolver';

const routes: Routes = [

    {
        path: 'catalogue',
        loadChildren: () => import('./data-set/data-set.module')
            .then(m => m.DataSetModule),
        canActivate: [AuthGuard],
        resolve: {
            aclAppInfo: AclConfigurationResolver
        }
    },
    {
        path: 'projets',
        loadChildren: () => import('./project/project.module')
            .then(m => m.ProjectModule),
        canActivate: [AuthGuard]
    },
    {
        path: 'login',
        loadChildren: () => import('./login/login.module')
            .then(m => m.LoginModule),
        canActivate: [AuthGuard],
        resolve: {
            aclAppInfo: AclConfigurationResolver
        }
    },
    {
        path: 'personal-space',
        loadChildren: () => import('./personal-space/personal-space.module')
            .then(m => m.PersonalSpaceModule),
        canActivate: [UserGuardService]
    },
    {
        path: 'organization',
        loadChildren: () => import('./organization/organization.module')
            .then(m => m.OrganizationModule),
        canActivate: [AuthGuard],
        resolve: {
            aclAppInfo: AclConfigurationResolver
        }
    },
    {
        // Path vide
        path: '',
        redirectTo: 'catalogue',
        pathMatch: 'full',
        canActivate: [AuthGuard]
    },

    {
        path: 'not-authorized',
        component: NotAuthorizedComponent,
    },
    {
        // Path Home
        path: 'home',
        component: HomeComponent,
        canActivate: [AuthGuard]
    }

];

@NgModule({
    imports: [RouterModule.forRoot(routes, {
        initialNavigation: 'enabled',
        scrollPositionRestoration: 'enabled'
    })],
    exports: [RouterModule],
    providers: [AclConfigurationResolver]
})
export class AppRoutingModule {
}
