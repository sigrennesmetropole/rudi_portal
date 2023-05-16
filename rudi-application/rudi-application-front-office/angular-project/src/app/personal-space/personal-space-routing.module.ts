import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MyAccountComponent} from './pages/my-account/my-account.component';
import {MyNotificationsComponent} from './pages/my-notifications/my-notifications.component';
import {RequestDetailComponent} from './pages/request-detail/request-detail.component';
import {ProjectDetailComponent} from './components/project-detail/project-detail.component';
import {CommonModule} from '@angular/common';
import {MyProjectDetailsComponent} from './pages/my-project-details/my-project-details.component';
import {
    SelfdataInformationRequestDetailComponent
} from './pages/selfdata-information-request-detail/selfdata-information-request-detail.component';
import {SelfdataDatasetsComponent} from './pages/selfdata-datasets/selfdata-datasets.component';
import {SelfdataDatasetDetailsComponent} from './pages/selfdata-dataset-details/selfdata-dataset-details.component';
import {UserGuardService} from '../core/services/user-guard.service';
import {AuthGuardService as AuthGuard} from '../core/services/auth-guard.service';
import {NewRequestDetailComponent} from './pages/new-request-detail/new-request-detail.component';
import {MyActivityComponent} from './pages/my-activity/my-activity.component';

const routes: Routes = [
    {
        path: '',
        component: ProjectDetailComponent,
    },
    {
        // Path my-account
        path: 'my-account',
        component: MyAccountComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
    {
        // Path my-notifications
        path: 'my-notifications',
        component: MyNotificationsComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
    {
        // Path my activity
        path: 'my-activity',
        component: MyActivityComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
    {
        // Path to see a request detail
        path: 'request-detail/:taskId',
        component: RequestDetailComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
    {
        // Path to see the details of a specific project
        path: 'my-project-details/:projectUuid',
        component: MyProjectDetailsComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
    {
        // Path to see the details of a specific selfdata
        path: 'selfdata-information-request-detail/:taskId',
        component: SelfdataInformationRequestDetailComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
    {
        // Path to see my selfdata-dataset last demand
        path: 'selfdata-datasets',
        component: SelfdataDatasetsComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
    {
        // Path to see the details of a selfdata dataset
        path: 'selfdata-dataset-details/:datasetUuid',
        component: SelfdataDatasetDetailsComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
    {
        // Path to see a new request detail
        path: 'new-request-detail/:taskId',
        component: NewRequestDetailComponent,
        canActivate: [AuthGuard, UserGuardService]
    },
];

@NgModule({
    imports: [
        CommonModule,
        RouterModule.forChild(routes)],
    exports: [RouterModule]

})
export class PersonalSpaceRoutingModule {
}
