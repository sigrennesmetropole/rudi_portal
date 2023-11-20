import {CUSTOM_ELEMENTS_SCHEMA, NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PersonalSpaceRoutingModule} from './personal-space-routing.module';
import {MyAccountComponent} from './pages/my-account/my-account.component';
import {TasksComponent} from './components/tasks/tasks.component';
import {MatTableModule} from '@angular/material/table';
import {MyNotificationsComponent} from './pages/my-notifications/my-notifications.component';
import {ProjectDetailComponent} from './components/project-detail/project-detail.component';
import {SharedModule} from '@shared/shared.module';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {CoreModule} from '@core/core.module';
import {AccesDetailsTable3Component} from './components/project-detail/acces-details-table3/acces-details-table3.component';
import {AccesDetailsTable2Component} from './components/project-detail/acces-details-table2/acces-details-table2.component';
import {AccesDetailsTable1Component} from './components/project-detail/acces-details-table1/acces-details-table1.component';
import {DatasetTaskDetailComponent} from './pages/dataset-task-detail/dataset-task-detail.component';
import {TaskDetailComponent} from './components/task-detail/task-detail.component';
import {ProjectOwnerDetailComponent} from './components/project-owner-detail/project-owner-detail.component';
import {ReusesComponent} from './components/reuses/reuses.component';
import {MyProjectDetailsComponent} from './pages/my-project-details/my-project-details.component';
import {ProjectBasicDetailsComponent} from './components/project-basic-details/project-basic-details.component';
import {ProjectApiTabComponent} from './components/project-api-tab/project-api-tab.component';
import {ProjectDatasetsTabComponent} from './components/project-datasets-tab/project-datasets-tab.component';
import {DialogSubscribeDatasetsComponent} from './components/dialog-subscribe-datasets/dialog-subscribe-datasets.component';
import {ProjectTasksComponent} from './components/project-tasks/project-tasks.component';
import {LinkedDatasetHistoryComponent} from './components/linked-dataset-history/linked-dataset-history.component';
import {ProjectInformationComponent} from './components/project-information/project-information.component';
import {
    SelfdataInformationRequestTaskDetailComponent
} from './pages/selfdata-information-request-task-detail/selfdata-information-request-task-detail.component';
import {SelfdataMainInformationComponent} from './components/selfdata-main-information/selfdata-main-information.component';
import {SelfdataDatasetsComponent} from './pages/selfdata-datasets/selfdata-datasets.component';
import {SelfdataDatasetsTableComponent} from './components/selfdata-datasets-table/selfdata-datasets-table.component';
import {SelfdataDatasetDetailsComponent} from './pages/selfdata-dataset-details/selfdata-dataset-details.component';
import {SelfdataDatasetRequestsTabComponent} from './components/selfdata-dataset-requests-tab/selfdata-dataset-requests-tab.component';
import {SelfdataDatasetBasicDetailsComponent} from './components/selfdata-dataset-basic-details/selfdata-dataset-basic-details.component';
import {SelfdataRequestSectionComponent} from './components/selfdata-request-section/selfdata-request-section.component';
import {SelfdataDatasetDataTabComponent} from './components/selfdata-dataset-data-tab/selfdata-dataset-data-tab.component';
import {GenericDataComponent} from './components/generic-data/generic-data.component';
import {TemporalBarchartDataComponent} from './components/temporal-barchart-data/temporal-barchart-data.component';
import {D3LineAndPlotChartComponent} from './components/d3-line-and-plot-chart/d3-line-and-plot-chart.component';
import {D3BarChartComponent} from './components/d3-bar-chart/d3-bar-chart.component';
import {NewRequestTaskDetailComponent} from './pages/new-request-task-detail/new-request-task-detail.component';
import {MatchingDataCardComponent} from './components/matching-data-card/matching-data-card.component';
import {PopoverModule} from 'ngx-smart-popover';
import {MyActivityComponent} from './pages/my-activity/my-activity.component';
import {MyRequestsComponent} from './components/my-requests/my-requests.component';
import {MyNewDatasetRequestsComponent} from './components/my-requests-tables/my-new-dataset-requests/my-new-dataset-requests.component';
import {MySelfdataRequestsComponent} from './components/my-requests-tables/my-selfdata-requests/my-selfdata-requests.component';
import {MyLinkedDatasetsComponent} from './components/my-requests-tables/my-linked-datasets/my-linked-datasets.component';
import {MyProfilComponent} from './components/my-profil/my-profil.component';
import {OrganizationTabComponent} from './components/organization-tab/organization-tab.component';
import {ProjectTaskDetailComponent} from './pages/project-task-detail/project-task-detail.component';
import {ProjectModule} from '../project/project.module';

@NgModule({
    declarations: [
        MyAccountComponent,
        MyNotificationsComponent,
        TasksComponent,
        DatasetTaskDetailComponent,
        ProjectDetailComponent,
        AccesDetailsTable1Component,
        AccesDetailsTable2Component,
        AccesDetailsTable3Component,
        TaskDetailComponent,
        ProjectOwnerDetailComponent,
        ReusesComponent,
        ProjectOwnerDetailComponent,
        MyProjectDetailsComponent,
        ProjectBasicDetailsComponent,
        ProjectApiTabComponent,
        ProjectDatasetsTabComponent,
        DialogSubscribeDatasetsComponent,
        ProjectTasksComponent,
        LinkedDatasetHistoryComponent,
        ProjectInformationComponent,
        SelfdataInformationRequestTaskDetailComponent,
        SelfdataMainInformationComponent,
        SelfdataDatasetsComponent,
        SelfdataDatasetsTableComponent,
        SelfdataDatasetDetailsComponent,
        SelfdataDatasetRequestsTabComponent,
        SelfdataDatasetBasicDetailsComponent,
        SelfdataRequestSectionComponent,
        SelfdataDatasetDataTabComponent,
        GenericDataComponent,
        TemporalBarchartDataComponent,
        D3BarChartComponent,
        D3LineAndPlotChartComponent,
        NewRequestTaskDetailComponent,
        MatchingDataCardComponent,
        MyActivityComponent,
        MyRequestsComponent,
        MyLinkedDatasetsComponent,
        MyNewDatasetRequestsComponent,
        MySelfdataRequestsComponent,
        MyProfilComponent ,
        OrganizationTabComponent,
        ProjectTaskDetailComponent,
    ],
    imports: [
        CommonModule,
        SharedModule,
        CoreModule,
        PersonalSpaceRoutingModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        PopoverModule,
        ProjectModule,
    ]
    ,
    providers:
        [
            {provide: 'DEFAULT_LANGUAGE', useValue: 'fr'}
        ],
    entryComponents: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class PersonalSpaceModule {
}
