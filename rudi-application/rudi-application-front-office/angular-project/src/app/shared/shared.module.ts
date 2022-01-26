import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {OrganizationLogoComponent} from './organization-logo/organization-logo.component';
import {SplitPipe} from './split.pipe';
import {TruncateTextPipe} from './truncate-text.pipe';
import {TranslateModule} from '@ngx-translate/core';

@NgModule({
    declarations:
        [
            OrganizationLogoComponent,
            SplitPipe,
            TruncateTextPipe
        ],
    imports: [
        CommonModule,
        TranslateModule,
    ],
    exports: [
        TranslateModule,
        OrganizationLogoComponent,
        SplitPipe,
        TruncateTextPipe
    ],
    entryComponents: [],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
    ]
})
export class SharedModule {
}
