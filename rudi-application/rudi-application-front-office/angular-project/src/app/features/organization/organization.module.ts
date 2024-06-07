import {CommonModule} from '@angular/common';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatTableModule} from '@angular/material/table';
import {DataSetModule} from '@features/data-set/data-set.module';
import {ListContainerComponent} from '@features/organization/components/list-container/list-container.component';
import {OrderComponent} from '@features/organization/components/order/order.component';
import {ListComponent} from '@features/organization/pages/list/list.component';
import {CoreModule} from '@core/core.module';
import {SharedModule} from '@shared/shared.module';
import {AdministrationTabComponent} from './components/administration-tab/administration-tab.component';
import {
    DeletionMemberConfirmationPopinComponent
} from './components/administration-tab/deletion-member-confirmation-popin/deletion-member-confirmation-popin.component';
import {
    OrganizationMembersTableComponent
} from './components/administration-tab/organization-members-table/organization-members-table.component';
import {OrganizationTableComponent} from './components/administration-tab/organization-table/organization-table.component';
import {
    UpdateUserPasswordPopinComponent
} from './components/administration-tab/update-user-password-popin/update-user-password-popin.component';
import {OrganizationInformationsComponent} from './components/organization-informations/organization-informations.component';
import {OrganizationRoutingModule} from './organization-routing.module';
import {DetailComponent} from './pages/detail/detail.component';


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
    exports: [
        OrderComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OrganizationModule {
}
