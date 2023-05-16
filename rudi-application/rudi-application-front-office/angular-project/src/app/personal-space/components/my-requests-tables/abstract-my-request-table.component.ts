import {RequestItem} from './request-item';
import {BackPaginationSort} from '../../../shared/back-pagination/back-pagination-sort';
import {SortTableInterface} from '../../../shared/back-pagination/sort-table-interface';
import {Sort} from '@angular/material/sort';
import {Observable} from 'rxjs';
import {MyRequestsService} from '../../../core/services/my-requests/my-requests.service';

/**
 * Composant générique pour les tableaux de "Mes demandes"
 */
import {Component} from '@angular/core';
import {ProcessDefinitionsKeyIconRegistryService} from '../../../core/services/process-definitions-key-icon-registry.service';
import {PROCESS_DEFINITION_KEY_TYPES} from '../../../shared/models/title-icon-type';

@Component({
    template: ''
})
export abstract class AbstractMyRequestTableComponent {

    /**
     * Tri par défaut appliqué aux éléments
     */
    public DEFAULT_SORT_ORDER = '-updatedDate';

    /**
     * Nombre d'éléments par page
     */
    public ITEMS_PER_PAGE = 5;

    /**
     * Nombre total d'éléments
     */
    public total: number;

    /**
     * identifiant à définir pour chaque tableau à instancier pour permettre la présence
     * de plusieurs paginator sur une seule page
     */
    public backPaginatorId: string = null;

    /**
     * éléments à afficher, objets de type : Demandes réalisées (purement visuel)
     */
    public elements: RequestItem[] = [];

    /***
     * Booléen d'état : recherche en cours, pour le loader du tableau
     */
    public searchIsRunning: boolean;

    /**
     * ensemble des colonnes à afficher, toutes les mêmes car tous les composants affichent des demandes
     */
    public displayedColumns: string[] = ['updatedDate', 'description', 'initiator', 'functionalStatus'];

    /**
     * Objet de pagination avec le back-end
     */
    public backPaginationSort = new BackPaginationSort();

    protected constructor(protected readonly myRequestsService: MyRequestsService,
                          processDefinitionsKeyIconRegistryService: ProcessDefinitionsKeyIconRegistryService,
    ) {
        processDefinitionsKeyIconRegistryService.addAllSvgIcons(PROCESS_DEFINITION_KEY_TYPES);
    }

    /**
     * Appel REST vers le back-end pour rechercher des éléments avec un critère de pagination
     * @param sortTableInterface éléments de pagination pour la restitution
     */
    loadContent(sortTableInterface: SortTableInterface): void {
        const wantedPage = sortTableInterface?.page;
        this.searchIsRunning = true;

        this.getMyElements(this.getOffset(wantedPage), this.ITEMS_PER_PAGE, sortTableInterface.order).subscribe({
            next: (elements: RequestItem[]) => {
                this.backPaginationSort.currentPage = wantedPage;
                this.backPaginationSort.currentSort = sortTableInterface.order;
                this.elements = elements;
                this.searchIsRunning = false;
            },
            error: (e) => {
                console.error(e);
                this.searchIsRunning = false;
            }
        });
    }

    /**
     * Action de tri lors du clic sur une colonne du tableau, effectue une appel REST
     * @param sort le tri à appliquer sur la restitution
     */
    sortTable(sort: Sort): void {
        if (!sort.active || sort.direction === '') {
            return;
        } else {
            this.loadContent(this.backPaginationSort.sortTable(sort));
        }
    }

    /**
     * Calcul de l'offset à envoyer à l'API de pagination
     * @param wantedPage la page qu'on veut afficher
     */
    public getOffset(wantedPage: number): number {
        return (wantedPage - 1) >= 0 ? (wantedPage - 1) * this.ITEMS_PER_PAGE : 0;
    }

    /**
     * Appel vers l'API back-end de recherche des éléments
     * @param offset décalage pour afficher les autres pages
     * @param limit nombre d'éléments par page
     * @param order critère de tri
     * @protected
     */
    protected abstract getMyElements(offset: number, limit: number, order: string): Observable<RequestItem[]>;
}
