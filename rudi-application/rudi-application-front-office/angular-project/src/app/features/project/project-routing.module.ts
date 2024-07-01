import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {AuthGuardService} from '@core/services/auth-guard.service';
import {UserGuardService} from '@core/services/user-guard.service';
import {DetailComponent} from './pages/detail/detail.component';
import {ListComponent} from './pages/list/list.component';
import {SubmissionProjectComponent} from './pages/submission-project/submission-project.component';

const routes: Routes = [
    {
        path: '',
        component: ListComponent,
    },
    {
        path: 'soumettre-un-projet',
        component: SubmissionProjectComponent,
        canActivate: mapToCanActivate([AuthGuardService, UserGuardService])
    },
    {
        path: 'detail/:uuid',
        component: DetailComponent,
        canActivate: mapToCanActivate([AuthGuardService])
    },
    {
        path: 'detail/:uuid/:name',
        component: DetailComponent,
        canActivate: mapToCanActivate([AuthGuardService])
    }
];


@NgModule({
    declarations: [],
    imports: [
        CommonModule,
        [RouterModule.forChild(routes)],
    ],
    exports: [RouterModule]
})
export class ProjectRoutingModule {
}
