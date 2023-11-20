import {Component, Input, OnInit} from '@angular/core';
import {Order, ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {UserService} from '@core/services/user.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {Sort} from '@angular/material/sort';
import {ProjectDependenciesFetchers, ProjectDependenciesService} from '@core/services/asset/project/project-dependencies.service';
import {injectDependenciesEach} from '@shared/utils/dependencies-utils';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {PagedProjectList} from '@app/projekt/projekt-model';
import {mapEach} from '@shared/utils/ObservableUtils';
import {BackPaginationSort} from '@shared/back-pagination/back-pagination-sort';
import {SortTableInterface} from '@shared/back-pagination/sort-table-interface';

export interface ProjectSummary {
    uuid: string;
    updatedDate: Date;
    projectTitle: string;
    confidentiality: string;
    status: string;
    numberOfDatasets: number;
}

const DEFAULT_SORT_ORDER: Order = '-updatedDate';

@Component({
    selector: 'app-reuses',
    templateUrl: './reuses.component.html',
    styleUrls: ['./reuses.component.scss']
})
export class ReusesComponent implements OnInit {

    displayedColumns: string[] = ['updatedDate', 'title', 'confidentiality', 'functionalStatus', 'numberOfDataset'];

    private _projectList: ProjectSummary[];

    searchIsRunning = false;

    sortIsRunning = false;

    total = 0;

    @Input() ITEMS_PER_PAGE;

    mediaSize: MediaSize;

    backPaginationSort = new BackPaginationSort();
    page: number;

    constructor(private readonly projectMetierService: ProjektMetierService,
                private readonly userService: UserService,
                private readonly breakpointObserver: BreakpointObserverService,
                private readonly projectDependencyFetcher: ProjectDependenciesFetchers,
                private readonly projectDependencyService: ProjectDependenciesService) {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    ngOnInit(): void {
        // Permet de trier par défaut les projets par ordre décroissant
        const defaultSortTable: SortTableInterface = {order: DEFAULT_SORT_ORDER, page: 1};
        this.loadProjects(defaultSortTable);
    }

    get projectList(): ProjectSummary[] {
        return this._projectList;
    }

    set projectList(list: ProjectSummary[]) {
        this._projectList = list;
    }

    /**
     * charge une page de ITEMS_PER_PAGE éléments max
     * @private
     */
    loadProjects(sortTableInterface: SortTableInterface): void {
        this.page = sortTableInterface?.page;
        if (!this.sortIsRunning) {
            this.searchIsRunning = true;
        }
        // Observable de récupération des projets pipé sur la récupération du total d'éléments
        const observableMyProjects: Observable<PagedProjectList> = this.projectMetierService
            .getMyAndOrganizationsProjects((this.page - 1) >= 0 ?
                (this.page - 1) * this.ITEMS_PER_PAGE :
                0, this.ITEMS_PER_PAGE, sortTableInterface.order)
            .pipe(
                tap((result) => {
                    this.total = result.total;
                })
            );

        // Déclenchement de la recherche des projets mappé en objets avec dépendances
        this.projectDependencyService.searchMyProjects(observableMyProjects)
            .pipe(
                injectDependenciesEach({
                    numberOfRequests: this.projectDependencyFetcher.numberOfRequests
                }),
                mapEach(({project, dependencies}) => ({
                    uuid: project.uuid,
                    updatedDate: new Date(project.updated_date),
                    projectTitle: project.title,
                    confidentiality: project.confidentiality.label,
                    status: project.functional_status,
                    numberOfDatasets: dependencies.numberOfRequests
                } as ProjectSummary)),
            )
            .subscribe({
                next: projectResume => {
                    this.projectList = projectResume;
                    this.searchIsRunning = false;
                    this.sortIsRunning = false;
                },
                error: err => {
                    console.error(err);
                    this.searchIsRunning = false;
                    this.sortIsRunning = false;
                }
            });
    }

    /**
     * Fonctions de tris
     */
    sortTable(sort: Sort): void {
        if (!sort.active || sort.direction === '') {
            return;
        } else {
            this.sortIsRunning = true;
            this.backPaginationSort.currentPage = this.page;
            this.loadProjects(this.backPaginationSort.sortTable(sort));
        }
    }
}
