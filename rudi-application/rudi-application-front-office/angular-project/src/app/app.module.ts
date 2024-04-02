import {APP_BASE_HREF, LocationStrategy, PathLocationStrategy, registerLocaleData} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClient} from '@angular/common/http';

import localeFr from '@angular/common/locales/fr';
import {APP_INITIALIZER, CUSTOM_ELEMENTS_SCHEMA, Injector, LOCALE_ID, NgModule} from '@angular/core';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import {MatPaginatorIntl} from '@angular/material/paginator';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CoreModule} from '@core/core.module';
import {HttpTokenInterceptor} from '@core/interceptors/http.token.interceptor';
import {ResponseTokenInterceptor} from '@core/interceptors/response.token.interceptor';
import {LogService} from '@core/services/log.service';
import {HomeModule} from '@features/home/home.module';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {TranslateCompiler, TranslateLoader, TranslateModule, TranslateService} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {BotDetectCaptchaModule} from '@shared/angular-captcha/botdetect-captcha.module';
import {SharedModule} from '@shared/shared.module';
import {MESSAGE_FORMAT_CONFIG, TranslateMessageFormatCompiler} from 'ngx-translate-messageformat-compiler';
import {appInitializerFactory} from './app-initializer-factory';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {TranslatedMatPaginatorIntl} from '@core/i18n/translated-mat-paginator-intl';

registerLocaleData(localeFr);
@NgModule({
    declarations: [
        AppComponent,
    ],
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        CoreModule,
        SharedModule,
        HomeModule,
        AppRoutingModule,
        BotDetectCaptchaModule,
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
        NgbModule,
    ],
    exports: [],
    providers: [
        LogService,
        { provide: LOCALE_ID, useValue: 'fr-FR'},
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
    bootstrap: [AppComponent]
})
export class AppModule {
}

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, 'assets/i18n/', '.json');
}
