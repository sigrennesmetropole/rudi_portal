import {Component, OnInit} from '@angular/core';
import {Level} from '../../../shared/notification-template/notification-template.component';
import {Router} from '@angular/router';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {TasksAggregatorService} from '../../../core/services/tasks-aggregator/tasks-aggregator.service';
import {RequestToStudy} from '../../../core/services/tasks-aggregator/request-to-study.interface';
import {ALL_TYPES} from '../../../shared/models/title-icon-type';
import {IconRegistryService} from '../../../core/services/icon-registry.service';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';

@Component({
    selector: 'app-received-access-requests',
    templateUrl: './received-access-requests.component.html',
    styleUrls: ['./received-access-requests.component.scss']
})
export class ReceivedAccessRequestsComponent implements OnInit {
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
