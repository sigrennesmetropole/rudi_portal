import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, CUSTOM_ELEMENTS_SCHEMA, Injector, NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {HTTP_INTERCEPTORS, HttpClient} from '@angular/common/http';
import {TranslateCompiler, TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {CoreModule} from './core/core.module';
import {APP_BASE_HREF, LocationStrategy, PathLocationStrategy} from '@angular/common';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import {LogService} from './core/services/log.service';
import {HttpTokenInterceptor} from './core/interceptors/http.token.interceptor';
import {ResponseTokenInterceptor} from './core/interceptors/response.token.interceptor';
import {SharedModule} from './shared/shared.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MESSAGE_FORMAT_CONFIG, TranslateMessageFormatCompiler} from 'ngx-translate-messageformat-compiler';
import {PopoverModule} from 'ngx-smart-popover';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {TranslatedMatPaginatorIntl} from './i18n/translated-mat-paginator-intl';
import {appInitializerFactory} from './app-initializer-factory';

@NgModule({
    declarations: [
        AppComponent,
        HomeComponent
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        CoreModule,
        SharedModule,
        AppRoutingModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            },
            compiler: {
                provide: TranslateCompiler,
                useClass: TranslateMessageFormatCompiler
            }
        }),
        PopoverModule,
    ],
    exports: [],
    providers: [
        LogService,
        {provide: LocationStrategy, useClass: PathLocationStrategy},
        {provide: APP_BASE_HREF, useValue: '/'},
        {provide: HTTP_INTERCEPTORS, useClass: HttpTokenInterceptor, multi: true},
        {
            provide: HTTP_INTERCEPTORS,
            useClass: ResponseTokenInterceptor,
            multi: true
        },
        {provide: MAT_DATE_LOCALE, useValue: 'fr-FR'},
        {provide: MESSAGE_FORMAT_CONFIG, useValue: {locales: ['fr']}},
        {
            provide: APP_INITIALIZER,
            useFactory: appInitializerFactory,
            deps: [TranslateService, Injector],
            multi: true
        },
        {
            provide: MatPaginatorIntl,
            deps: [TranslateService, Injector],
            useFactory: (translateService: TranslateService) => new TranslatedMatPaginatorIntl(translateService)
        },
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    entryComponents: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, 'assets/i18n/', '.json');
}
