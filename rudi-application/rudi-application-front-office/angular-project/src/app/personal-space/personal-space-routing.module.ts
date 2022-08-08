import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {AuthGuardService, AuthGuardService as AuthGuard} from '../core/services/auth-guard.service';
import {MyAccountComponent} from './pages/my-account/my-account.component';
import {ReceivedAccessRequestsComponent} from './pages/received-access-requests/received-access-requests.component';
import {RequestDetailComponent} from './pages/request-detail/request-detail.component';
import {UserGuardService} from '../core/services/user-guard.service';
import {ProjectDetailComponent} from './components/project-detail/project-detail.component';
import {CommonModule} from '@angular/common';

const routes: Routes = [
    {
        path: '',
        component: ProjectDetailComponent,
    },
    {
        // Path my-account
        path: 'my-account',
        component: MyAccountComponent,
        canActivate: [AuthGuardService]
    },
    {
        // Path received-access-requests
        path: 'received-access-requests',
        component: ReceivedAccessRequestsComponent,
        canActivate: [AuthGuardService]
    },
    {
        // Path to see a request detail
        path: 'request-detail/:taskId',
        component: RequestDetailComponent,
        canActivate: [AuthGuard, UserGuardService]
    }
];

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class PersonalSpaceRoutingModule {
}
