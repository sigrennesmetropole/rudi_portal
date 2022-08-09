import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import {ListComponent} from './pages/list/list.component';
import {ReuseDeclarationComponent} from './pages/reuse-declaration/reuse-declaration.component';
import {AuthGuardService} from '../core/services/auth-guard.service';
import {DetailComponent} from './pages/detail/detail.component';
import {SubmissionProjectComponent} from './pages/submission-project/submission-project.component';
import {UserGuardService} from '../core/services/user-guard.service';

const routes: Routes = [
    {
        path: '',
        component: ListComponent,
    },
    {
        path: 'declarer-une-reutilisation',
        component: ReuseDeclarationComponent,
        canActivate: [AuthGuardService, UserGuardService]
    },
    {
        path: 'soumettre-un-projet',
        component: SubmissionProjectComponent,
        canActivate: [AuthGuardService, UserGuardService]
    },
    {
        path: 'detail/:uuid',
        component: DetailComponent,
        canActivate: [AuthGuardService]
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
