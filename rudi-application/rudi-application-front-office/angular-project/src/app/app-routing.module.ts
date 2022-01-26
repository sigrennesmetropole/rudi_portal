import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {AccountInfoComponent} from './core/account-info/account-info.component';
import {DetailComponent} from './detail/pages/detail/detail.component';
import {AuthGuardService as AuthGuard} from './core/services/auth-guard.service';
import {NotAuthorizedComponent} from './authent/not-authorized/not-authorized.component';
import {ListComponent} from './list/pages/list/list.component';

const routes: Routes = [
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
  },
  {
    // Path Account info
    path: 'account',
    component: AccountInfoComponent,
    canActivate: [AuthGuard]
  },
  {
    // Path Home
    path: 'details',
    component: DetailComponent,
    canActivate: [AuthGuard]
  },
  {
    // Path Data Sets
    path: 'catalogue',
    component: ListComponent,
    canActivate: [AuthGuard]
  },
  {
    // Path metaData
    path: 'detail/:uuid',
    component: DetailComponent,
    canActivate: [AuthGuard]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    initialNavigation: 'enabled',
    scrollPositionRestoration: 'enabled'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
