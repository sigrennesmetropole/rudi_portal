import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {AuthGuardService as AuthGuard} from '@core/services/auth-guard.service';
import {DetailComponent} from '@features/cms/pages/detail/detail.component';


const routes: Routes = [
    {
        path: 'detail/:type/:uuid/:template/:titre',
        pathMatch: 'full',
        component: DetailComponent,
        canActivate: mapToCanActivate([AuthGuard])
    },
    {
        path: 'detail/:type/:uuid/:template',
        pathMatch: 'full',
        component: DetailComponent,
        canActivate: mapToCanActivate([AuthGuard])
    }
];

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(routes)],
    exports: [RouterModule]

})
export class CmsRoutingModule {
}
