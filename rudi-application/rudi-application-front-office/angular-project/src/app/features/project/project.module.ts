import {CommonModule} from '@angular/common';
import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {MatStepperModule} from '@angular/material/stepper';
import {CoreModule} from '@core/core.module';
import {SharedModule} from '@shared/shared.module';
import {FilePickerModule} from '@sleiss/ngx-awesome-uploader';
import {DataSetModule} from '@features/data-set/data-set.module';
import {AddDataSetDialogComponent} from './components/add-data-set-dialog/add-data-set-dialog.component';
import {BannerComponent} from './components/banner/banner.component';
import {DataSetButtonComponent} from './components/data-set-button/data-set-button.component';
import {EditNewDataSetDialogComponent} from './components/edit-new-data-set-dialog/edit-new-data-set-dialog.component';
import {OrderComponent} from './components/order/order.component';
import {ProjectDatasetListComponent} from './components/project-dataset-list/project-dataset-list.component';
import {RequestDetailsDialogComponent} from './components/request-details-dialog/request-details-dialog.component';
import {ReuseProjectCommonComponent} from './components/reuse-project-common/reuse-project-common.component';
import {Step1ProjectComponent} from './components/step1-project/step1-project.component';
import {Step2ProjectComponent} from './components/step2-project/step2-project.component';
import {Step3ProjectComponent} from './components/step3-project/step3-project.component';
import {
    SuccessProjectCreationDialogComponent
} from './components/success-project-creation-dialog/success-project-creation-dialog.component';
import {SuccessStep3TemplateComponent} from './components/success-step3-template/success-step3-template.component';
import {DetailComponent} from './pages/detail/detail.component';
import {ListComponent} from './pages/list/list.component';
import {SubmissionProjectComponent} from './pages/submission-project/submission-project.component';
import {ProjectRoutingModule} from './project-routing.module';

@NgModule({
    declarations: [
        ListComponent,
        BannerComponent,
        AddDataSetDialogComponent,
        DetailComponent,
        OrderComponent,
        SubmissionProjectComponent,
        DataSetButtonComponent,
        EditNewDataSetDialogComponent,
        SubmissionProjectComponent,
        Step1ProjectComponent,
        Step2ProjectComponent,
        Step3ProjectComponent,
        ReuseProjectCommonComponent,
        SuccessProjectCreationDialogComponent,
        ProjectDatasetListComponent,
        RequestDetailsDialogComponent,
        SuccessStep3TemplateComponent
    ],
    imports: [
        CommonModule,
        SharedModule,
        CoreModule,
        FilePickerModule,
        ProjectRoutingModule,
        DataSetModule,
        MatStepperModule
    ],
    providers:
        [
            {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'}

        ],
    bootstrap: [AddDataSetDialogComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})

export class ProjectModule {
}
