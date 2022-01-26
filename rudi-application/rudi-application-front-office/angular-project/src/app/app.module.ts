import {BrowserModule} from '@angular/platform-browser';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {HTTP_INTERCEPTORS, HttpClient} from '@angular/common/http';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {TranslateHttpLoader} from '@ngx-translate/http-loader';
import {CoreModule} from './core/core.module';
import {APP_BASE_HREF, LocationStrategy, PathLocationStrategy} from '@angular/common';
import {MAT_DATE_LOCALE} from '@angular/material/core';
import {LoginDialogComponent} from './authent/login-dialog/login-dialog.component';
import {LogService} from './core/services/log.service';
import {HttpTokenInterceptor} from './core/interceptors/http.token.interceptor';
import {ResponseTokenInterceptor} from './core/interceptors/response.token.interceptor';
import {NotAuthorizedComponent} from './authent/not-authorized/not-authorized.component';
import {ListModule} from './list/list.module';
import {DetailModule} from './detail/detail.module';
import {SharedModule} from './shared/shared.module';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatSidenavModule} from '@angular/material/sidenav';

@NgModule({
    declarations: [
        AppComponent,
        HomeComponent,
        LoginDialogComponent,
        NotAuthorizedComponent,
    ],
    imports: [
        BrowserModule,
        CoreModule,
        AppRoutingModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        }),
        SharedModule,
        ListModule,
        DetailModule,
        FormsModule,
        ReactiveFormsModule,
        MatSidenavModule,
    ],
    exports: [
    ],
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
        {provide: MAT_DATE_LOCALE, useValue: 'fr-FR'}
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA],
    entryComponents: [LoginDialogComponent],
    bootstrap: [AppComponent]
})
export class AppModule {
}

export function HttpLoaderFactory(http: HttpClient) {
    return new TranslateHttpLoader(http, 'assets/i18n/', '.json');
}
