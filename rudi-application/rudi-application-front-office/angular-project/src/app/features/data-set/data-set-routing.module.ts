import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthGuardService as AuthGuard} from '@core/services/auth-guard.service';
import {UserGuardService as UserGuard} from '@core/services/user-guard.service';
import {DetailComponent} from './pages/detail/detail.component';
import {ListComponent} from './pages/list/list.component';
import {
    SelfdataInformationRequestCreationSuccessComponent
} from './pages/selfdata-information-request-creation-success/selfdata-information-request-creation-success.component';
import {
    SelfdataInformationRequestCreationComponent
} from './pages/selfdata-information-request-creation/selfdata-information-request-creation.component';

const routes: Routes = [
    {
        // Path Data Sets
        path: '',
        component: ListComponent,
        canActivate: [AuthGuard]
    },
    {
        // Path detail catalogue
        path: 'detail/:uuid',
        component: DetailComponent,
        canActivate: [AuthGuard]
    },
    {
        // Path detail catalogue
        path: 'detail/:uuid/:name',
        component: DetailComponent,
        canActivate: [AuthGuard]
    },
    {
        // Path demande d'information
        path: 'detail/:uuid/selfdata-information-request-creation',
        component: SelfdataInformationRequestCreationComponent,
        canActivate: [UserGuard]
    },
    {
        // Path demande d'information
        path: 'detail/:uuid/selfdata-information-request-creation-success',
        component: SelfdataInformationRequestCreationSuccessComponent,
        canActivate: [UserGuard]
    },
    {
        // Path detail
        path: 'details',
        component: DetailComponent,
        canActivate: [AuthGuard]
    },
];

@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        RouterModule.forChild(routes)
    ],
    exports: [RouterModule]
})
export class DataSetRoutingModule {
}
