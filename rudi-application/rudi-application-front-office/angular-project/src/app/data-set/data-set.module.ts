import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {SharedModule} from '../shared/shared.module';
import {MapComponent} from './components/map/map.component';
import {ProducerNamesFilterFormComponent} from './components/filter-forms/producer-names-filter-form/producer-names-filter-form.component';
import {DataSetInfosComponent} from './components/data-set-infos/data-set-infos.component';
import {OrderFilterFormComponent} from './components/filter-forms/order-filter-form/order-filter-form.component';
import {DatesFilterFormComponent} from './components/filter-forms/dates-filter-form/dates-filter-form.component';
import {DetailComponent} from './pages/detail/detail.component';
import {OrderComponent} from './components/order/order.component';
import {ThemesFilterFormComponent} from './components/filter-forms/themes-filter-form/themes-filter-form.component';
import {SearchBoxComponent} from './components/search-box/search-box.component';
import {FilterSidenavContainerComponent} from './components/filter-sidenav-container/filter-sidenav-container.component';
import {DataSetCardComponent} from './components/data-set-card/data-set-card.component';
import {BannerComponent} from './components/banner/banner.component';
import {FiltersItemsListComponent} from './components/filters-items-list/filters-items-list.component';
import {FilterMenuComponent} from './components/filter-menu/filter-menu.component';
import {ListComponent} from './pages/list/list.component';
import {DataSetRoutingModule} from './data-set-routing.module';
import {CoreModule} from '../core/core.module';
import {ListContainerComponent} from './components/list-container/list-container.component';
import {GetBackendPropertyPipe} from '../shared/pipes/get-backend-property.pipe';
import {SelectProjectDialogComponent} from './components/select-project-dialog/select-project-dialog.component';
import {
    SuccessRestrictedRequestDialogComponent
} from './components/success-restricted-request-dialog/success-restricted-request-dialog.component';
import {AccessStatusFilterFormComponent} from './components/filter-forms/access-status-filter-form/access-status-filter-form.component';
import {
    SelfdataInformationRequestCreationComponent
} from './pages/selfdata-information-request-creation/selfdata-information-request-creation.component';
import {
    SelfdataInformationRequestCreationSuccessComponent
} from './pages/selfdata-information-request-creation-success/selfdata-information-request-creation-success.component';
import {SpreadsheetComponent} from './components/spreadsheet/spreadsheet.component';
import {AgGridModule} from 'ag-grid-angular';
import {SpreadsheetTabComponent} from './components/spreadsheet-tab/spreadsheet-tab.component';
import {DatasetInformationsComponent} from './components/dataset-informations/dataset-informations.component';


@NgModule({
    declarations:
        [
            DetailComponent,
            DataSetInfosComponent,
            MapComponent,
            DataSetCardComponent,
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
            SearchBoxComponent,
            ThemesFilterFormComponent,
            ListContainerComponent,
            SelectProjectDialogComponent,
            SuccessRestrictedRequestDialogComponent,
            SelfdataInformationRequestCreationComponent,
            SelfdataInformationRequestCreationSuccessComponent,
            SpreadsheetComponent,
            DatasetInformationsComponent,
            SpreadsheetTabComponent
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
    entryComponents: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
        GetBackendPropertyPipe
    ]
})
export class DataSetModule {
}
