import {Component, Input, OnInit} from '@angular/core';
import {Order, ProjektMetierService} from '../../../core/services/projekt-metier.service';
import {UserService} from '../../../core/services/user.service';
import {BreakpointObserverService, MediaSize, NgClassObject} from '../../../core/services/breakpoint-observer.service';
import {Sort} from '@angular/material/sort';
import {ProjectDependenciesFetchers, ProjectDependenciesService} from '../../../core/services/project-dependencies.service';
import {injectDependenciesEach} from '../../../shared/utils/dependencies-utils';
import {tap} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {PagedProjectList} from '../../../projekt/projekt-model';
import {mapEach} from '../../../shared/utils/ObservableUtils';

export interface ProjectSummary {
    uuid: string;
    updatedDate: Date;
    projectTitle: string;
    confidentiality: string;
    status: string;
    numberOfDatasets: number;
}

const FIRST_PAGE = 1;
const DEFAULT_SORT_ORDER: Order = '-updatedDate';

@Component({
    selector: 'app-reuses',
    templateUrl: './reuses.component.html',
    styleUrls: ['./reuses.component.scss']
})
export class ReusesComponent implements OnInit {

    displayedColumns: string[] = ['updatedDate', 'title', 'confidentiality', 'functionalStatus', 'numberOfDataset'];

    private _projectList: ProjectSummary[];

    private currentPage = FIRST_PAGE;

    searchIsRunning = false;

    triIsRunning = false;

    total = 0;

    currentSort: string;
    currentSortAsc: boolean;

    @Input() ITEMS_PER_PAGE;

    mediaSize: MediaSize;

    readonly maxPageDesktop = 9;

    /** minimum = 5 */
    readonly maxPageMobile = 5;

    constructor(private readonly projectMetierService: ProjektMetierService,
                private readonly userService: UserService,
                private readonly breakpointObserver: BreakpointObserverService,
                private readonly projectDependencyFetcher: ProjectDependenciesFetchers,
                private readonly projectDependencyService: ProjectDependenciesService) {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    ngOnInit(): void {
        // Permet de trier par défaut les projets par ordre décroissant
        this.loadProjects(1, DEFAULT_SORT_ORDER);
    }

    get projectList(): ProjectSummary[] {
        return this._projectList;
    }

    set projectList(list: ProjectSummary[]) {
        this._projectList = list;
    }

    /**
     * charge une page de ITEMS_PER_PAGE éléments max
     * @param numeroPage
     * @param ordreTri
     * @private
     */
    private loadProjects(numeroPage: number, ordreTri?: Order): void {
        if(!this.triIsRunning ) {
            this.searchIsRunning = true;
        }
        // Observable de récupération des projets pipé sur la récupération du total d'éléments
        const observableMyProjects: Observable<PagedProjectList> = this.projectMetierService
            .getMyAndOrganizationsProjects((numeroPage - 1) >= 0 ? (numeroPage - 1) * this.ITEMS_PER_PAGE : 0, this.ITEMS_PER_PAGE, ordreTri)
            .pipe(
                tap(({total, elements}) => {
                    this.total = total;
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
                    this.triIsRunning = false;
                },
                error: err => {
                    console.error(err);
                    this.searchIsRunning = false;
                    this.triIsRunning = false;
                }
            });
    }

    /**
     * Fonction permettant la gestion la pagination
     */
    handlePageChange(page: number): void {
        this.page = page;
        this.load(this.currentSort, this.currentSortAsc, page);
        window.scroll(0, 0);
    }

    get page(): number {
        return this.currentPage;
    }

    set page(value: number) {
        if (value < FIRST_PAGE) {
            console.warn('Page number cannot be less than ' + FIRST_PAGE);
            value = FIRST_PAGE;
        }
        this.currentPage = value;
    }

    get paginationControlsNgClass(): NgClassObject {
        return this.breakpointObserver.getNgClassFromMediaSize('pagination-spacing');
    }

    /**
     * Fonctions de tris
     */
    sortTable(sort: Sort): void {
        if (!sort.active || sort.direction === '') {
            this.currentSortAsc = null;
            this.currentSort = null;
            return;
        }

        const isAsc = sort.direction === 'asc';
        this.currentSortAsc = isAsc;
        this.currentSort = sort.active;
        this.triIsRunning = true;
        this.load(this.currentSort, isAsc, this.currentPage);
    }

    private load(column: string, isAsc: boolean, page: number): void {
        if (isAsc && column) {
            this.loadProjects(page, column as Order);
        } else if (!isAsc && column) {
            this.loadProjects(page, ('-' + column) as Order);
        } else {
            this.loadProjects(page);
        }
    }
}
