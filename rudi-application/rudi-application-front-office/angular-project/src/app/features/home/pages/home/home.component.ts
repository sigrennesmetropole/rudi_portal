import {Component, OnDestroy, OnInit} from '@angular/core';
import {Order, ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {CustomizationService} from '@core/services/customization.service';
import {FiltersService} from '@core/services/filters.service';
import {KonsultMetierService} from '@core/services/konsult-metier.service';
import {LogService} from '@core/services/log.service';
import {ThemeCacheService} from '@core/services/theme-cache.service';
import {Theme} from '@features/home/types';
import {ProjectCatalogItem} from '@features/project/model/project-catalog-item';
import {TranslateService} from '@ngx-translate/core';
import {Metadata, MetadataList} from 'micro_service_modules/api-kaccess';
import {CustomizationDescription, KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {SimpleSkosConcept} from 'micro_service_modules/kos/kos-api';
import {PagedProjectList, ProjectStatus, ProjektService} from 'micro_service_modules/projekt/projekt-api';
import {Subject} from 'rxjs';
import {distinctUntilChanged, filter, map, takeUntil} from 'rxjs/operators';

const DEFAULT_PROJECT_ORDER: Order = '-updatedDate';
const PROJECT_STATUS: ProjectStatus[] = [ProjectStatus.Validated];

@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, OnDestroy {
    private destroyed$: Subject<boolean>;

    customizationDescription: CustomizationDescription;
    projects: ProjectCatalogItem[];
    themes: Theme[];
    jdds: Metadata[];

    customizationDescriptionIsLoading: boolean;
    projectsIsLoading: boolean;
    themesIsLoading: boolean;
    jddsIsLoading: boolean;

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly filtersService: FiltersService,
        private readonly konsultService: KonsultService,
        private readonly projektService: ProjektService,
        private readonly projektMetierService: ProjektMetierService,
        private readonly logger: LogService,
        private readonly translateService: TranslateService,
        private readonly themeCacheService: ThemeCacheService,
        private readonly customizationService: CustomizationService,
    ) {
        this.destroyed$ = new Subject<boolean>();

        this.customizationDescriptionIsLoading = false;
        this.projectsIsLoading = false;
        this.themesIsLoading = false;
        this.jddsIsLoading = false;

        this.themes = [];
        this.jdds = [];
        this.initCustomizationDescription();
        this.initProjects();
        this.initThemes();
        this.initJdds();
    }

    private initThemes(): void {
        this.themesIsLoading = true;
        this.themeCacheService.isLoading$
            .pipe(
                takeUntil(this.destroyed$),
                distinctUntilChanged(),
                filter((isLoading: boolean) => !isLoading)
            )
            .subscribe({
                next: (): void => {
                    this.themes = this.themeCacheService.themes.map((concept: SimpleSkosConcept): Theme => ({
                        picto: concept.concept_code,
                        name: concept.text,
                        code: concept.concept_code
                    }));
                    this.themesIsLoading = false;
                },
                error: (error): void => {
                    this.logger.error(error);
                    this.themesIsLoading = false;
                }
            });
    }

    private initCustomizationDescription(): void {
        this.customizationDescriptionIsLoading = true;
        this.customizationService.getCustomizationDescription()
            .subscribe({
                next: (customizationDescription: CustomizationDescription) => {
                    this.customizationDescription = customizationDescription;
                    this.customizationDescriptionIsLoading = false;
                },
                error: (error) => {
                    this.logger.error(error);
                    this.customizationDescriptionIsLoading = false;
                }
            });
    }

    private initProjects(): void {
        this.projectsIsLoading = true;
        this.projektService.searchProjects(
            null,
            null,
            null,
            null,
            PROJECT_STATUS,
            0,
            3,
            DEFAULT_PROJECT_ORDER
        ).pipe(
            map((data: PagedProjectList) => {
                return data.elements.map(elem => new ProjectCatalogItem({project: elem}));
            }),
        ).subscribe({
            next: (projects: ProjectCatalogItem[]) => {
                this.projects = projects;
                this.projectsIsLoading = false;
            },
            error: error => {
                this.logger.error(error);
                this.projectsIsLoading = false;
            }
        });
    }

    private initJdds(): void {
        this.filtersService.deleteAllFilters();
        this.filtersService.orderFilter.value = '-dataset_dates.updated';
        this.jddsIsLoading = true;
        this.konsultMetierService.searchMetadatas(
            this.filtersService.currentFilters,
            [],
            0,
            6
        )
            .pipe(map((metadataList: MetadataList): Metadata[] => metadataList.items ?? []))
            .subscribe((data: Metadata[]): void => {
                this.jdds = data;
                this.jddsIsLoading = false;
            });
    }

    ngOnInit(): void {
        this.themeCacheService.init();
    }

    ngOnDestroy(): void {
        this.destroyed$.next(true);
        this.destroyed$.complete();
    }
}
