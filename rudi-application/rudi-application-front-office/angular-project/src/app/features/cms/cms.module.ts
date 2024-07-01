import {CommonModule} from '@angular/common';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CoreModule} from '@core/core.module';
import {CmsRoutingModule} from '@features/cms/cms-routing.module';
import {NewsListComponent} from '@features/cms/components/news-list/news-list.component';
import {CmsOrderComponent} from '@features/cms/components/order/order.component';
import {DetailComponent} from '@features/cms/pages/detail/detail.component';
import {ListComponent} from '@features/cms/pages/list/list.component';
import {OrganizationModule} from '@features/organization/organization.module';
import {ProjectModule} from '@features/project/project.module';
import {SharedModule} from '@shared/shared.module';

@NgModule({
    declarations: [DetailComponent, ListComponent, NewsListComponent, CmsOrderComponent],
    imports: [
        CommonModule,
        SharedModule,
        CoreModule,
        CmsRoutingModule,
        ProjectModule,
        OrganizationModule
    ],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'}
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CmsModule {
}
