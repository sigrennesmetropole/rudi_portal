import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ListComponent} from '@app/organization/pages/list/list.component';
import {DetailComponent} from './pages/detail/detail.component';
import {AuthGuardService as AuthGuard} from '../core/services/auth-guard.service';


const routes: Routes = [
    {
        // Path Organization
        path: '',
        pathMatch: 'full',
        component: ListComponent,
        canActivate: [AuthGuard]
    },
    {
        // Path details
        path: 'detail/:organizationUuid',
        component: DetailComponent,
        canActivate: [AuthGuard]
    }
];

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(routes)],
    exports: [RouterModule]

})
export class OrganizationRoutingModule {
}
