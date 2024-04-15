import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {AuthGuardService as AuthGuard} from '@core/services/auth-guard.service';
import {UserGuardService} from '@core/services/user-guard.service';
import {HomeComponent} from '@features/home/pages/home/home.component';
import {NotAuthorizedComponent} from '@features/login/pages/not-authorized/not-authorized.component';
import {AclConfigurationResolver} from '@shared/resolver/acl-configuration-resolver';

const routes: Routes = [

    {
        path: 'catalogue',
        loadChildren: () => import('./features/data-set/data-set.module')
            .then(m => m.DataSetModule),
        canActivate: mapToCanActivate([AuthGuard]),
        resolve: {
            aclAppInfo: AclConfigurationResolver
        }
    },
    {
        path: 'projets',
        loadChildren: () => import('./features/project/project.module')
            .then(m => m.ProjectModule),
        canActivate: mapToCanActivate([AuthGuard])
    },
    {
        path: 'login',
        loadChildren: () => import('./features/login/login.module')
            .then(m => m.LoginModule),
        canActivate: mapToCanActivate([AuthGuard]),
        resolve: {
            aclAppInfo: AclConfigurationResolver
        }
    },
    {
        path: 'personal-space',
        loadChildren: () => import('./features/personal-space/personal-space.module')
            .then(m => m.PersonalSpaceModule),
        canActivate: mapToCanActivate([UserGuardService])
    },
    {
        path: 'organization',
        loadChildren: () => import('./features/organization/organization.module')
            .then(m => m.OrganizationModule),
        canActivate: mapToCanActivate([AuthGuard]),
        resolve: {
            aclAppInfo: AclConfigurationResolver
        }
    },

    {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full',
    },

    {
        path: 'not-authorized',
        component: NotAuthorizedComponent,
    },
    {
        path: 'home',
        component: HomeComponent,
        canActivate: mapToCanActivate([AuthGuard])
    },
    {
        path: 'cms',
        loadChildren: () => import('./features/cms/cms.module')
            .then(m => m.CmsModule),
        canActivate: mapToCanActivate([AuthGuard])
    }


];

@NgModule({
    imports: [RouterModule.forRoot(routes, {
        initialNavigation: 'enabledBlocking',
        scrollPositionRestoration: 'enabled'
    })],
    exports: [RouterModule],
    providers: [AclConfigurationResolver]
})
export class AppRoutingModule {
}
