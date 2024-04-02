import {CommonModule} from '@angular/common';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CoreModule} from '@core/core.module';
import {GetBackendPropertyPipe} from '@shared/pipes/get-backend-property.pipe';
import {SharedModule} from '@shared/shared.module';
import {AgGridModule} from 'ag-grid-angular';
import {BannerComponent} from './components/banner/banner.component';
import {DataSetInfosComponent} from './components/data-set-infos/data-set-infos.component';
import {DatasetInformationsComponent} from './components/dataset-informations/dataset-informations.component';
import {AccessStatusFilterFormComponent} from './components/filter-forms/access-status-filter-form/access-status-filter-form.component';
import {DatesFilterFormComponent} from './components/filter-forms/dates-filter-form/dates-filter-form.component';
import {OrderFilterFormComponent} from './components/filter-forms/order-filter-form/order-filter-form.component';
import {ProducerNamesFilterFormComponent} from './components/filter-forms/producer-names-filter-form/producer-names-filter-form.component';
import {ThemesFilterFormComponent} from './components/filter-forms/themes-filter-form/themes-filter-form.component';
import {FilterMenuComponent} from './components/filter-menu/filter-menu.component';
import {FilterSidenavContainerComponent} from './components/filter-sidenav-container/filter-sidenav-container.component';
import {FiltersItemsListComponent} from './components/filters-items-list/filters-items-list.component';
import {ListContainerComponent} from './components/list-container/list-container.component';
import {MapTabComponent} from './components/map-tab/map-tab.component';
import {OrderComponent} from './components/order/order.component';
import {SelectProjectDialogComponent} from './components/select-project-dialog/select-project-dialog.component';
import {SpreadsheetTabComponent} from './components/spreadsheet-tab/spreadsheet-tab.component';
import {SpreadsheetComponent} from './components/spreadsheet/spreadsheet.component';
import {
    SuccessRestrictedRequestDialogComponent
} from './components/success-restricted-request-dialog/success-restricted-request-dialog.component';
import {DataSetRoutingModule} from './data-set-routing.module';
import {DetailComponent} from './pages/detail/detail.component';
import {ListComponent} from './pages/list/list.component';
import {
    SelfdataInformationRequestCreationSuccessComponent
} from './pages/selfdata-information-request-creation-success/selfdata-information-request-creation-success.component';
import {
    SelfdataInformationRequestCreationComponent
} from './pages/selfdata-information-request-creation/selfdata-information-request-creation.component';


@NgModule({
    declarations:
        [
            DataSetInfosComponent,
            DetailComponent,
            DatesFilterFormComponent,
            BannerComponent,
            FilterMenuComponent,
            FilterSidenavContainerComponent,
            FiltersItemsListComponent,
            ListComponent,
            OrderComponent,
            OrderFilterFormComponent,
            ProducerNamesFilterFormComponent,
            AccessStatusFilterFormComponent,
            ThemesFilterFormComponent,
            ListContainerComponent,
            SelectProjectDialogComponent,
            SuccessRestrictedRequestDialogComponent,
            SelfdataInformationRequestCreationComponent,
            SelfdataInformationRequestCreationSuccessComponent,
            SpreadsheetComponent,
            DatasetInformationsComponent,
            SpreadsheetTabComponent,
            MapTabComponent
        ],
    imports: [
        CommonModule,
        CoreModule,
        SharedModule,
        DataSetRoutingModule,
        AgGridModule
    ],
    exports: [
        ListContainerComponent,
        SelectProjectDialogComponent
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
        GetBackendPropertyPipe
    ]
})
export class DataSetModule {
}
