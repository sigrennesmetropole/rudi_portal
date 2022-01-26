import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {DetailComponent} from './pages/detail/detail.component';
import {DataSetInfosComponent} from './components/data-set-infos/data-set-infos.component';
import {SharedModule} from '../shared/shared.module';
import {MatMenuModule} from '@angular/material/menu';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatDividerModule} from '@angular/material/divider';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatRadioModule} from '@angular/material/radio';
import {MatTooltipModule} from '@angular/material/tooltip';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {FlexLayoutModule} from '@angular/flex-layout';
import {RouterModule} from '@angular/router';
import {MapComponent} from './components/map/map.component';

@NgModule({
    declarations:
        [
            DataSetInfosComponent,
            DetailComponent,
            MapComponent
        ],
    imports: [
        CommonModule,
        MatIconModule,
        MatRadioModule,
        MatMenuModule,
        MatToolbarModule,
        MatButtonModule,
        MatCardModule,
        MatExpansionModule,
        FormsModule,
        ReactiveFormsModule,
        MatDividerModule,
        MatTooltipModule,
        MatSidenavModule,
        SharedModule,
        FlexLayoutModule,
        RouterModule
    ],
    exports: [],
    entryComponents: [],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
    ]
})
export class DetailModule {
}
