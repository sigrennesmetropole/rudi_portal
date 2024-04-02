import {Component, OnInit} from '@angular/core';
import {Sort} from '@angular/material/sort';
import {Order} from '@core/services/asset/project/projekt-metier.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';
import {ProcessDefinitionsKeyIconRegistryService} from '@core/services/process-definitions-key-icon-registry.service';
import {SelfdataDatasetService} from '@core/services/selfdata-dataset/selfdata-dataset.service';
import {BackPaginationSort} from '@shared/back-pagination/back-pagination-sort';
import {SortTableInterface} from '@shared/back-pagination/sort-table-interface';
import {PROCESS_DEFINITION_KEY_TYPES} from '@shared/models/title-icon-type';
import {SelfdataDataset} from './selfdata-dataset.interface';


const DEFAULT_SORT_ORDER: Order = '-updatedDate';
const ITEMS_PER_PAGE = 10;

@Component({
    selector: 'app-selfdata-datasets-table',
    templateUrl: './selfdata-datasets-table.component.html',
    styleUrls: ['./selfdata-datasets-table.component.scss']
})
export class SelfdataDatasetsTableComponent implements OnInit {
    displayedColumns: string[] = ['title', 'processDefinitionKey', 'updatedDate', 'functionalStatus'];

    private _selfdataDatasetList: SelfdataDataset[] = [];

    searchIsRunning = false;

    sortIsRunning = false;

    total = 0;

    mediaSize: MediaSize;

    itemsperpage = ITEMS_PER_PAGE;
    page: number;
    order: Order;

    backPaginationSort = new BackPaginationSort();

    constructor(processDefinitionsKeyIconRegistryService: ProcessDefinitionsKeyIconRegistryService,
                private readonly selfdataDatasetService: SelfdataDatasetService,
                private readonly breakpointObserver: BreakpointObserverService, ) {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        processDefinitionsKeyIconRegistryService.addAllSvgIcons(PROCESS_DEFINITION_KEY_TYPES);
    }

    ngOnInit(): void {
        const defaultSortTable: SortTableInterface = {order: DEFAULT_SORT_ORDER, page: 1};
        this.loadSelfdataDatasets(defaultSortTable);
    }

    get selfdataDatasetList(): SelfdataDataset[] {
        return this._selfdataDatasetList;
    }

    set selfdataDatasetList(list: SelfdataDataset[]) {
        this._selfdataDatasetList = list;
    }

    /**
     * charge une page de selfdata datasets
     * @private
     */
    loadSelfdataDatasets(sortTableInterface: SortTableInterface): void {
        this.page = sortTableInterface.page;
        if (!this.sortIsRunning) {
            this.searchIsRunning = true;
        }
        this.selfdataDatasetService.searchSelfdataDatasets((this.page - 1) >= 0 ? (this.page - 1) * this.itemsperpage : 0, this.itemsperpage, sortTableInterface.order)
            .subscribe({
                next: result => {
                    this.total = result.total;
                    this.selfdataDatasetList = result.elements;
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
            this.loadSelfdataDatasets(this.backPaginationSort.sortTable(sort));
        }
    }

    isStatusDefined(selfdataDataset: SelfdataDataset): boolean {
        if (!selfdataDataset.functional_status) {
            return false;
        } else {
            return true;
        }
    }
}
