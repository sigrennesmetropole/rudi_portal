import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HeaderComponent} from './header/header.component';
import {MatIconModule} from '@angular/material/icon';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatButtonModule} from '@angular/material/button';
import {TranslateModule} from '@ngx-translate/core';
import {MatDialogModule} from '@angular/material/dialog';
import {MatRadioModule} from '@angular/material/radio';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {AngularSvgIconModule} from 'angular-svg-icon';
import {FilterPipeModule} from 'ngx-filter-pipe';
import {MatCardModule} from '@angular/material/card';
import {MatMenuModule} from '@angular/material/menu';
import {AccountInfoComponent} from './account-info/account-info.component';
import {RouterModule} from '@angular/router';
import {LayoutModule} from '@angular/cdk/layout';
import {FooterComponent} from './footer/footer.component';
import {MatDividerModule} from '@angular/material/divider';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatNativeDateModule, MatOptionModule} from '@angular/material/core';
import {MatSelectModule} from '@angular/material/select';
import {FlexLayoutModule} from '@angular/flex-layout';
import {MatBadgeModule} from '@angular/material/badge';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {NgxPaginationModule} from 'ngx-pagination';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {LineTruncationLibModule} from 'ngx-line-truncation';
import {NgxNavbarModule} from 'ngx-bootstrap-navbar';

@NgModule({
    declarations:
        [
            AccountInfoComponent,
            FooterComponent,
            HeaderComponent,
        ],
    imports: [
        CommonModule,
        LayoutModule,
        MatIconModule,
        MatRadioModule,
        MatMenuModule,
        MatToolbarModule,
        MatButtonModule,
        MatCardModule,
        TranslateModule,
        MatDialogModule,
        MatExpansionModule,
        MatCheckboxModule,
        FormsModule,
        MatFormFieldModule,
        MatInputModule,
        ReactiveFormsModule,
        HttpClientModule,
        BrowserAnimationsModule,
        AngularSvgIconModule.forRoot(),
        FilterPipeModule,
        MatBadgeModule,
        MatNativeDateModule,
        MatDatepickerModule,
        NgxPaginationModule,
        MatSnackBarModule,
        RouterModule,
        MatDividerModule,
        FlexLayoutModule,
        MatTooltipModule,
        MatOptionModule,
        MatSelectModule,
        LineTruncationLibModule,
        NgxNavbarModule,
    ],
    exports: [
        FooterComponent,
        HeaderComponent],
    entryComponents: [],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
    ]
})
export class CoreModule {
}

// tslint:disable-next-line:no-any
export function initializeApp(ConfigurationService: { load: () => any; }): () => any {
    return () => ConfigurationService.load();
}

