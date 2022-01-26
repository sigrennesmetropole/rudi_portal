import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {ThemesFilterFormComponent} from './components/filter-forms/themes-filter-form/themes-filter-form.component';
import {ProducerNamesFilterFormComponent} from './components/filter-forms/producer-names-filter-form/producer-names-filter-form.component';
import {DatesFilterFormComponent} from './components/filter-forms/dates-filter-form/dates-filter-form.component';
import {OrderFilterFormComponent} from './components/filter-forms/order-filter-form/order-filter-form.component';
import {RestrictedAccessFilterFormComponent} from './components/filter-forms/restricted-access-filter-form/restricted-access-filter-form.component';
import {RouterModule} from '@angular/router';
import {MatDividerModule} from '@angular/material/divider';
import {FlexLayoutModule} from '@angular/flex-layout';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatOptionModule} from '@angular/material/core';
import {MatSelectModule} from '@angular/material/select';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {LineTruncationLibModule} from 'ngx-line-truncation';
import {MatSidenavModule} from '@angular/material/sidenav';
import {SharedModule} from '../shared/shared.module';
import {MatRadioModule} from '@angular/material/radio';
import {MatIconModule} from '@angular/material/icon';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatBadgeModule} from '@angular/material/badge';
import {MatMenuModule} from '@angular/material/menu';
import {MatCardModule} from '@angular/material/card';
import {NgxPaginationModule} from 'ngx-pagination';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatToolbarModule} from '@angular/material/toolbar';
import {OrderComponent} from './components/order/order.component';
import {SearchBoxComponent} from './components/search-box/search-box.component';
import {FiltersComponent} from './components/filters/filters.component';
import {FilterSidenavContainerComponent} from './components/filter-sidenav-container/filter-sidenav-container.component';
import {FiltersItemsListComponent} from './components/filters-items-list/filters-items-list.component';
import {FilterMenuComponent} from './components/filter-menu/filter-menu.component';
import {DataSetCardComponent} from './components/data-set-card/data-set-card.component';
import {ListComponent} from './pages/list/list.component';

@NgModule({
    declarations:
        [
            DataSetCardComponent,
            DatesFilterFormComponent,
            FiltersComponent,
            FilterMenuComponent,
            FilterSidenavContainerComponent,
            FiltersItemsListComponent,
            ListComponent,
            OrderComponent,
            OrderFilterFormComponent,
            ProducerNamesFilterFormComponent,
            RestrictedAccessFilterFormComponent,
            SearchBoxComponent,
            ThemesFilterFormComponent
        ],
    imports: [
        CommonModule,
        MatIconModule,
        MatRadioModule,
        MatMenuModule,
        MatToolbarModule,
        MatButtonModule,
        MatCardModule,
        MatCheckboxModule,
        FormsModule,
        MatInputModule,
        MatBadgeModule,
        MatDatepickerModule,
        NgxPaginationModule,
        RouterModule,
        MatDividerModule,
        FlexLayoutModule,
        MatTooltipModule,
        MatOptionModule,
        MatSelectModule,
        ReactiveFormsModule,
        LineTruncationLibModule,
        MatSidenavModule,
        SharedModule,
    ],
    exports: [
    ],
    entryComponents: [],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
    ]
})
export class ListModule {
}
