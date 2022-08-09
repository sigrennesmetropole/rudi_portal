import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {OrganizationLogoComponent} from './organization-logo/organization-logo.component';
import {SplitPipe} from './split.pipe';
import {TruncateTextPipe} from './truncate-text.pipe';
import {ErrorBoxComponent} from './error-box/error-box.component';
import {LabelSeparatorComponent} from './label-separator/label-separator.component';
import {AccountErrorBoxComponent} from './account-error-box/account-error-box.component';
import {CoreModule} from '../core/core.module';
import {FooterComponent} from './footer/footer.component';
import {HeaderComponent} from './header/header.component';
import {NotificationTemplateComponent} from './notification-template/notification-template.component';
import {PasswordStrengthComponent} from './password-strength/password-strength.component';
import {MaterialModules} from './shared.constant';
import {PopoverComponent} from './popover/popover.component';
import {PopoverModule} from 'ngx-smart-popover';
import {PageTitleComponent} from './page-title/page-title.component';
import {ProjectListComponent} from './project-list/project-list.component';
import {ProjectCardComponent} from './project-card/project-card.component';
import {ContactButtonComponent} from './contact-button/contact-button.component';
import {ClipboardModule} from '@angular/cdk/clipboard';
import {LoaderComponent} from './loader/loader.component';
import {GetBackendPropertyPipe} from './get-backend-property.pipe';
import {MonthYearDatepickerComponent} from './month-year-datepicker/month-year-datepicker.component';
import {RadioListComponent} from './radio-list/radio-list.component';
import {ResetPasswordErrorBoxComponent} from './reset-password-error-box/reset-password-error-box.component';
import {PageComponent} from './page/page.component';
import {BannerComponent} from './banner/banner.component';
import {TabComponent} from './tab/tab.component';
import {SearchCountComponent} from './search-count/search-count.component';
import {TabsComponent} from './tabs/tabs.component';
import {ReplaceIfNullPipe} from './replace-if-null.pipe';
import {TabsLayoutDirective} from './tabs-layout.directive';
import {TabContentDirective} from './tab-content.directive';
import {BannerButtonComponent} from './banner-button/banner-button.component';
import {WorkflowFormComponent} from './workflow-form/workflow-form.component';
import {WorkflowFieldTextComponent} from './workflow-field-text/workflow-field-text.component';
import {WorkflowFieldComponent} from './workflow-field/workflow-field.component';
import {WorkflowFieldTemplateComponent} from './workflow-field-template/workflow-field-template.component';
import {WorkflowPopinComponent} from './workflow-popin/workflow-popin.component';
import {CopiedButtonComponent} from './copied-button/copied-button.component';
import {PasswordComponent} from './password/password.component';
import {ProjectMainInformationsComponent} from './project-main-informations/project-main-informations.component';
import {ProjectHeadingComponent} from './project-heading/project-heading.component';
import {DatasetsInfosComponent} from './dataset-infos/dataset-infos.component';
import {WorkInProgressComponent} from './work-in-progress/work-in-progress.component';
import {PaginatorComponent} from './paginator/paginator.component';

@NgModule({
    declarations:
        [
            LabelSeparatorComponent,
            ErrorBoxComponent,
            OrganizationLogoComponent,
            SplitPipe,
            TruncateTextPipe,
            ReplaceIfNullPipe,
            AccountErrorBoxComponent,
            FooterComponent,
            HeaderComponent,
            NotificationTemplateComponent,
            PasswordStrengthComponent,
            PopoverComponent,
            ProjectListComponent,
            ProjectCardComponent,
            ContactButtonComponent,
            ProjectCardComponent,
            PopoverComponent,
            PageTitleComponent,
            LoaderComponent,
            MonthYearDatepickerComponent,
            GetBackendPropertyPipe,
            RadioListComponent,
            ResetPasswordErrorBoxComponent,
            PageComponent,
            BannerComponent,
            TabComponent,
            SearchCountComponent,
            TabsComponent,
            TabsLayoutDirective,
            TabContentDirective,
            BannerButtonComponent,
            WorkflowFormComponent,
            WorkflowFieldTemplateComponent,
            WorkflowFieldComponent,
            WorkflowFieldTextComponent,
            WorkflowPopinComponent,
            CopiedButtonComponent,
            PasswordComponent,
            ProjectMainInformationsComponent,
            ProjectHeadingComponent,
            DatasetsInfosComponent,
            WorkInProgressComponent,
            PaginatorComponent,
        ],
    imports: [
        CommonModule,
        ...MaterialModules,
        CoreModule,
        PopoverModule,
        ClipboardModule
    ],
    exports: [
        MaterialModules,
        LabelSeparatorComponent,
        OrganizationLogoComponent,
        AccountErrorBoxComponent,
        SplitPipe,
        TruncateTextPipe,
        ReplaceIfNullPipe,
        ErrorBoxComponent,
        FooterComponent,
        HeaderComponent,
        NotificationTemplateComponent,
        PasswordStrengthComponent,
        CoreModule,
        PopoverComponent,
        ProjectListComponent,
        ContactButtonComponent,
        ProjectListComponent,
        PopoverComponent,
        PageTitleComponent,
        LoaderComponent,
        GetBackendPropertyPipe,
        MonthYearDatepickerComponent,
        RadioListComponent,
        GetBackendPropertyPipe,
        ResetPasswordErrorBoxComponent,
        PageComponent,
        BannerComponent,
        TabComponent,
        SearchCountComponent,
        TabsComponent,
        TabsLayoutDirective,
        TabContentDirective,
        BannerButtonComponent,
        WorkflowFormComponent,
        CopiedButtonComponent,
        PasswordComponent,
        ProjectMainInformationsComponent,
        ProjectHeadingComponent,
        DatasetsInfosComponent,
        WorkInProgressComponent,
        PaginatorComponent,
    ],
    entryComponents: [],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
    ]
})
export class SharedModule {
}
