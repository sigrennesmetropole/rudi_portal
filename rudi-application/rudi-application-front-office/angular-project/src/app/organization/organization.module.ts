import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DataSetModule} from '@app/data-set/data-set.module';
import {ListContainerComponent} from '@app/organization/components/list-container/list-container.component';
import {OrderComponent} from '@app/organization/components/order/order.component';
import {ListComponent} from '@app/organization/pages/list/list.component';
import {DetailComponent} from './pages/detail/detail.component';
import {OrganizationRoutingModule} from './organization-routing.module';
import {SharedModule} from '@shared/shared.module'
import {CoreModule} from '@core/core.module'
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {OrganizationInformationsComponent} from './components/organization-informations/organization-informations.component';
import {AdministrationTabComponent} from './components/administration-tab/administration-tab.component';
import {
    OrganizationMembersTableComponent
} from './components/administration-tab/organization-members-table/organization-members-table.component';
import {OrganizationTableComponent} from './components/administration-tab/organization-table/organization-table.component';
import {
    DeletionMemberConfirmationPopinComponent
} from './components/administration-tab/deletion-member-confirmation-popin/deletion-member-confirmation-popin.component';
import { UpdateUserPasswordPopinComponent } from './components/administration-tab/update-user-password-popin/update-user-password-popin.component';



@NgModule({
    declarations: [
        OrderComponent,
        ListContainerComponent,
        DetailComponent,
        ListComponent,
        OrganizationInformationsComponent,
        AdministrationTabComponent,
        OrganizationMembersTableComponent,
        OrganizationTableComponent,
        DeletionMemberConfirmationPopinComponent,
        DeletionMemberConfirmationPopinComponent,
        UpdateUserPasswordPopinComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        CoreModule,
        OrganizationRoutingModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        DataSetModule,
    ],
    providers: [
            {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'}
    ],
    entryComponents: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OrganizationModule {
}
