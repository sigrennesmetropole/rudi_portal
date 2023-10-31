import {Injectable} from '@angular/core';
import {DEFAULT_PROJECT_ORDER, ProjektMetierService} from './asset/project/projekt-metier.service';
import {ProjectCatalogItem, ProjectCatalogItemPage} from '../../project/model/project-catalog-item';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {PagedProjectList, Project, ProjectSearchCriteria, ProjectStatus} from '../../projekt/projekt-model';
import {ProjectDependenciesFetchers, ProjectWithDependencies} from './asset/project/project-dependencies.service';
import {mapEach} from '../../shared/utils/ObservableUtils';
import {injectDependenciesEach} from '../../shared/utils/dependencies-utils';

/**
 * Service métier des "Projects" dans le module Project de RUDI
 */
@Injectable()
export class ProjectListService {

    constructor(
        private readonly projektMetierService: ProjektMetierService,
        private readonly projectDependenciesFetchers: ProjectDependenciesFetchers) {
    }

    /**
     * Recherche les projets pour les afficher dnas le composant Catalogue sous forme de page
     * @param linkedDatasetsGlobalIds liste des global ids des datasets
     * @param offset la page sur laquelle on est
     * @param limit le nombre de projets pour la page
     * @param order la colonne sur léquelles on trie
     * @param producerUuid UUIDs des organisations ayant déclaré la réutilisation ou soumis le projet
     */
    public searchProjectsCatalog(linkedDatasetsGlobalIds: string[], offset: number, limit: number,
                                 order = DEFAULT_PROJECT_ORDER, producerUuid?: string): Observable<ProjectCatalogItemPage> {

        // La page renvoyée
        const page = new ProjectCatalogItemPage();

        // Tout va partir des projets à récupérer
        const criteria: ProjectSearchCriteria = {
            dataset_uuids: linkedDatasetsGlobalIds,
            owner_uuids: [producerUuid],
            status: [ProjectStatus.Validated],
            offset,
            limit
        };
        const projectCatalogItems: ProjectCatalogItem[] = [];
        return this.projektMetierService.searchProjects(criteria, order).pipe(
            // Une fois qu'on a la page on récupère les éléments et on prépare les infos de la page
            map((pageResult: PagedProjectList) => {
                page.total = pageResult.total;
                return pageResult.elements;
            }),
            // On crée un ProjectWithDependencies pour chaque projet
            mapEach((project: Project) => new ProjectWithDependencies(project, {})),
            // On injecte les infos sur le owner et le logo
            injectDependenciesEach({
                ownerInfo: this.projectDependenciesFetchers.ownerInfo
            }),
            injectDependenciesEach({
                logo: this.projectDependenciesFetchers.logo
            }),
            // On remplit le projectCatalogItems qui permettra d'alimenter les card
            mapEach(({project, dependencies}) => {
                projectCatalogItems.push({
                    project: project,
                    ownerInfo: dependencies.ownerInfo,
                    logo: dependencies.logo
                });
            }),
            map(() => {
                page.items = projectCatalogItems;
                return page;
            })
        );
    }
}
