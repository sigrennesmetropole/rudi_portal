import {CommonModule} from '@angular/common';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CoreModule} from '@core/core.module';
import {CmsRoutingModule} from '@features/cms/cms-routing.module';
import {DetailComponent} from '@features/cms/pages/detail/detail.component';
import {SharedModule} from '@shared/shared.module';

@NgModule({
    declarations: [DetailComponent],
    imports: [
        CommonModule,
        SharedModule,
        CoreModule,
        CmsRoutingModule
    ],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'}
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class CmsModule {
}
