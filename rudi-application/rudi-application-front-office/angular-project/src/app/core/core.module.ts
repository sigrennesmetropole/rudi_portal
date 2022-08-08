import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MAT_SNACK_BAR_DATA, MatSnackBarRef} from '@angular/material/snack-bar';
import {ProjectListService} from './services/project-list.service';

@NgModule({
    declarations:
        [],
    imports: [
        CommonModule,
    ],
    exports: [],
    entryComponents: [],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
        {
            provide: MatSnackBarRef,
            useValue: {}
        }, {
            provide: MAT_SNACK_BAR_DATA,
            useValue: {} // Add any data you wish to test if it is passed/used correctly
        },
        ProjectListService
    ]
})

export class CoreModule {
}

// tslint:disable-next-line:no-any
export function initializeApp(ConfigurationService: { load: () => any; }): () => any {
    return () => ConfigurationService.load();
}

