import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';
import {IconRegistryService} from '@core/services/icon-registry.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {RequestToStudy} from '@core/services/tasks-aggregator/request-to-study.interface';
import {TasksAggregatorService} from '@core/services/tasks-aggregator/tasks-aggregator.service';
import {ALL_TYPES} from '@shared/models/title-icon-type';
import {Level} from '@shared/notification-template/notification-template.component';

@Component({
    selector: 'app-my-notifications',
    templateUrl: './my-notifications.component.html',
    styleUrls: ['./my-notifications.component.scss']
})
export class MyNotificationsComponent implements OnInit {
    searchIsRunning = false;
    requestsToStudy: RequestToStudy[];
    urlToDoc: string;
    searchUrlLoading = false;

    constructor(
        iconRegistryService: IconRegistryService,
        private readonly router: Router,
        private readonly tasksAggregratorService: TasksAggregatorService,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
        private readonly propertiesMetierService: PropertiesMetierService,
    ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
        this.getUrlToDoc();
    }

    ngOnInit(): void {
        this.searchIsRunning = true;
        this.tasksAggregratorService.loadTasks().subscribe(requestsToStudy => {
            this.requestsToStudy = requestsToStudy;
        }, (e) => {
            console.error('Cannot retrieve requests to study', e);
            this.snackBarService.openSnackBar({
                message: this.translateService.instant('error.technicalError'),
                level: Level.ERROR
            });
            this.searchIsRunning = false;
        }, () => {
            this.searchIsRunning = false;
        });
    }

    /**
     * on ouvre un nouvel onglet vers la documentation de l'utilisation de l'URL
     */
    getUrlToDoc(): void {
        this.searchUrlLoading = true;
        this.propertiesMetierService.get('rudidatarennes.docRudiBzh').subscribe({
            next: (link: string) => {
                this.urlToDoc = link;
                this.searchUrlLoading = false;
            },
            error: (error) => {
                this.searchUrlLoading = false;
                console.log(error);
            }
        });
    }
}
