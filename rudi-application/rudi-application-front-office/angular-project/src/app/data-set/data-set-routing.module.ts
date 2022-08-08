import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import {ListComponent} from './pages/list/list.component';
import {AuthGuardService as AuthGuard} from '../core/services/auth-guard.service';
import {DetailComponent} from './pages/detail/detail.component';

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
