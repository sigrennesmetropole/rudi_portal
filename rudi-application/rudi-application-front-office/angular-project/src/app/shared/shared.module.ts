import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatTableModule} from '@angular/material/table';
import {CoreModule} from '@core/core.module';
import {NgbPopoverModule} from '@ng-bootstrap/ng-bootstrap';
import {BotDetectCaptchaModule} from '@shared/angular-captcha/botdetect-captcha.module';
import {ClipboardFieldComponent} from '@shared/clipboard-field/clipboard-field.component';
import {ErrorPageComponent} from '@shared/error-page/error-page.component';
import {ListOrganizationCardComponent} from '@shared/list-organization-card/list-organization-card.component';
import {OrganizationCardComponent} from '@shared/organization-card/organization-card.component';
import {ToStringPipe} from '@shared/pipes/to-string.pipe';
import {DatasetTableComponent} from '@shared/project-datasets-tables/dataset-table/dataset-table.component';

import {SearchBoxComponent} from '@shared/search-box/search-box.component';
import {SocialMediaSectionComponent} from '@shared/social-media-section/social-media-section.component';
import {WorkflowFieldDateComponent} from '@shared/workflow-field-date/workflow-field-date.component';
import {WorkflowFormDialogComponent} from '@shared/workflow-form-dialog/workflow-form-dialog.component';
import {IsSectionDisplayedPipe} from '@shared/workflow-form/pipes/is-section-displayed.pipe';
import {IsSectionOnlyHelpPipe} from '@shared/workflow-form/pipes/is-section-only-help.pipe';
import {FilePickerModule} from '@sleiss/ngx-awesome-uploader';
import {AccountErrorBoxComponent} from './account-error-box/account-error-box.component';
import {BackPaginationComponent} from './back-pagination/back-pagination.component';
import {BannerButtonComponent} from './banner-button/banner-button.component';
import {BannerComponent} from './banner/banner.component';
import {BooleanDataBlockComponent} from './boolean-data-block/boolean-data-block.component';
import {CardComponent} from './card/card.component';
import {ContactButtonComponent} from './contact-button/contact-button.component';
import {ContactCardComponent} from './contact-card/contact-card.component';
import {CopiedButtonComponent} from './copied-button/copied-button.component';
import {CustomRouterlinkDirective} from './custom-routerlink-directive/custom-routerlink.directive';
import {DataSetCardComponent} from './data-set-card/data-set-card.component';
import {DatasetsInfosComponent} from './dataset-infos/dataset-infos.component';
import {DatasetListComponent} from './dataset-list/dataset-list.component';
import {DocumentationButtonComponent} from './documentation-button/documentation-button.component';
import {ErrorBoxComponent} from './error-box/error-box.component';
import {FileSizePipe} from './file-size-pipe';
import {FooterComponent} from './footer/footer.component';
import {HeaderComponent} from './header/header.component';
import {LabelSeparatorComponent} from './label-separator/label-separator.component';
import {LoaderComponent} from './loader/loader.component';
import {MapPopupComponent} from './map-popup/map-popup.component';
import {MapComponent} from './map/map.component';
import {MemberPopinComponent} from './member-popin/member-popin.component';
import {MonthYearDatepickerComponent} from './month-year-datepicker/month-year-datepicker.component';
import {NotificationTemplateComponent} from './notification-template/notification-template.component';
import {OrganizationLogoComponent} from './organization-logo/organization-logo.component';
import {PageHeadingComponent} from './page-heading/page-heading.component';
import {PageSubtitleComponent} from './page-subtitle/page-subtitle.component';
import {PageTitleComponent} from './page-title/page-title.component';
import {PageComponent} from './page/page.component';
import {PaginatorComponent} from './paginator/paginator.component';
import {ParseIntPipe} from './parse-int.pipe';
import {PasswordStrengthComponent} from './password-strength/password-strength.component';
import {PasswordComponent} from './password/password.component';
import {GetBackendPropertyPipe} from './pipes/get-backend-property.pipe';
import {ProcessDefinitionKeyTranslatePipe} from './pipes/process-definition-key-translate.pipe';
import {ReplaceIfNullPipe} from './pipes/replace-if-null.pipe';
import {SelfdataProcessDefinitionKeyTranslatePipe} from './pipes/selfdata-process-definition-key-translate.pipe';
import {SplitPipe} from './pipes/split.pipe';
import {TruncateTextPipe} from './pipes/truncate-text.pipe';
import {PopoverComponent} from './popover/popover.component';
import {ProjectCardComponent} from './project-card/project-card.component';
import {
    DeletionConfirmationPopinComponent
} from './project-datasets-tables/deletion-confirmation-popin/deletion-confirmation-popin.component';
import {NewDatasetRequestTableComponent} from './project-datasets-tables/new-dataset-request-table/new-dataset-request-table.component';
import {OpenDatasetTableComponent} from './project-datasets-tables/open-dataset-table/open-dataset-table.component';
import {RestrictedDatasetTableComponent} from './project-datasets-tables/restricted-dataset-table/restricted-dataset-table.component';
import {ProjectHeadingComponent} from './project-heading/project-heading.component';
import {ProjectListComponent} from './project-list/project-list.component';
import {RadioListComponent} from './radio-list/radio-list.component';
import {ResetPasswordErrorBoxComponent} from './reset-password-error-box/reset-password-error-box.component';
import {RudiCaptchaComponent} from './rudi-captcha/rudi-captcha.component';
import {RudiSwiperComponent} from './rudi-swiper/rudi-swiper.component';
import {SearchAutocompleteComponent} from './search-autocomplete/search-autocomplete.component';
import {SearchCountComponent} from './search-count/search-count.component';
import {MaterialModules} from './shared.constant';
import {TabContentDirective} from './tab-content.directive';
import {TabComponent} from './tab/tab.component';
import {TabsLayoutDirective} from './tabs-layout.directive';
import {TabsComponent} from './tabs/tabs.component';
import {TaskDetailHeaderComponent} from './task-detail-header/task-detail-header.component';
import {UploaderComponent} from './uploader/uploader.component';
import {WorkInProgressComponent} from './work-in-progress/work-in-progress.component';
import {WorkflowFieldAddressComponent} from './workflow-field-address/workflow-field-address.component';
import {WorkflowFieldAttachmentPopinComponent} from './workflow-field-attachment-popin/workflow-field-attachment-popin.component';
import {WorkflowFieldAttachmentComponent} from './workflow-field-attachment/workflow-field-attachment.component';
import {WorkflowFieldBooleanComponent} from './workflow-field-boolean/workflow-field-boolean.component';
import {WorkflowFieldTemplateComponent} from './workflow-field-template/workflow-field-template.component';
import {WorkflowFieldTextComponent} from './workflow-field-text/workflow-field-text.component';
import {WorkflowFieldComponent} from './workflow-field/workflow-field.component';
import {WorkflowFormSubmitSuccessComponent} from './workflow-form-submit-success/workflow-form-submit-success.component';
import {WorkflowFormComponent} from './workflow-form/workflow-form.component';


@NgModule({
    declarations:
        [
            ErrorPageComponent,
            OrganizationCardComponent,
            ListOrganizationCardComponent,
            SearchBoxComponent,
            LabelSeparatorComponent,
            ErrorBoxComponent,
            OrganizationLogoComponent,
            SplitPipe,
            TruncateTextPipe,
            ReplaceIfNullPipe,
            ToStringPipe,
            AccountErrorBoxComponent,
            FooterComponent,
            HeaderComponent,
            NotificationTemplateComponent,
            PasswordStrengthComponent,
            PopoverComponent,
            ProjectListComponent,
            ProjectCardComponent,
            ContactButtonComponent,
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
            WorkflowFieldDateComponent,
            WorkflowFormDialogComponent,
            CopiedButtonComponent,
            PasswordComponent,
            ProjectHeadingComponent,
            DatasetsInfosComponent,
            WorkInProgressComponent,
            PaginatorComponent,
            BooleanDataBlockComponent,
            PageSubtitleComponent,
            WorkflowFieldBooleanComponent,
            WorkflowFormSubmitSuccessComponent,
            CardComponent,
            WorkflowFieldAddressComponent,
            WorkflowFieldAttachmentComponent,
            UploaderComponent,
            ParseIntPipe,
            WorkflowFieldAddressComponent,
            ProcessDefinitionKeyTranslatePipe,
            CustomRouterlinkDirective,
            ContactCardComponent,
            TaskDetailHeaderComponent,
            WorkflowFieldAttachmentPopinComponent,
            FileSizePipe,
            SelfdataProcessDefinitionKeyTranslatePipe,
            BackPaginationComponent,
            PageHeadingComponent,
            DocumentationButtonComponent,
            RudiCaptchaComponent,
            MapComponent,
            SearchAutocompleteComponent,
            MapPopupComponent,
            DatasetListComponent,
            DataSetCardComponent,
            MemberPopinComponent,
            OpenDatasetTableComponent,
            NewDatasetRequestTableComponent,
            RestrictedDatasetTableComponent,
            DeletionConfirmationPopinComponent,
            DatasetTableComponent,
            ClipboardFieldComponent,
            IsSectionDisplayedPipe,
            IsSectionOnlyHelpPipe,
            RudiSwiperComponent,
            SocialMediaSectionComponent,
        ],
    imports: [
        CommonModule,
        ...MaterialModules,
        CoreModule,
        FilePickerModule,
        MatAutocompleteModule,
        BotDetectCaptchaModule,
        MatTableModule,
        NgbPopoverModule,
    ],
    exports: [
        ErrorPageComponent,
        ProjectCardComponent,
        OrganizationCardComponent,
        ListOrganizationCardComponent,
        SearchBoxComponent,
        MaterialModules,
        LabelSeparatorComponent,
        OrganizationLogoComponent,
        AccountErrorBoxComponent,
        SplitPipe,
        TruncateTextPipe,
        ReplaceIfNullPipe,
        ToStringPipe,
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
        PageSubtitleComponent,
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
        WorkflowFormDialogComponent,
        CopiedButtonComponent,
        PasswordComponent,
        ProjectHeadingComponent,
        DatasetsInfosComponent,
        WorkInProgressComponent,
        PaginatorComponent,
        WorkflowFieldAddressComponent,
        BooleanDataBlockComponent,
        WorkflowFormSubmitSuccessComponent,
        UploaderComponent,
        ProcessDefinitionKeyTranslatePipe,
        ContactCardComponent,
        TaskDetailHeaderComponent,
        CardComponent,
        FileSizePipe,
        SelfdataProcessDefinitionKeyTranslatePipe,
        BackPaginationComponent,
        PageHeadingComponent,
        DocumentationButtonComponent,
        RudiCaptchaComponent,
        MapComponent,
        RudiCaptchaComponent,
        DatasetListComponent,
        DataSetCardComponent,
        MemberPopinComponent,
        OpenDatasetTableComponent,
        NewDatasetRequestTableComponent,
        RestrictedDatasetTableComponent,
        DeletionConfirmationPopinComponent,
        ClipboardFieldComponent,
        RudiSwiperComponent
    ],
    providers: [
        {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'},
        ProcessDefinitionKeyTranslatePipe,
    ]
})
export class SharedModule {
}
