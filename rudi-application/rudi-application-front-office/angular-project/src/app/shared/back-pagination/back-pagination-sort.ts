import {Sort} from '@angular/material/sort';
import {Order} from '../../core/services/asset/project/projekt-metier.service';
import {SortTableInterface} from './sort-table-interface';

export class BackPaginationSort {
    currentSort: string;
    currentSortAsc: boolean;
    currentPage: number;


    sort(column?: string, isAsc?: boolean, page?: number): SortTableInterface {
        let data: SortTableInterface = {page: page, order: null};
        if (isAsc && column) {
            data.order = column as Order;
        } else if (!isAsc && column) {
            data.order = ('-' + column) as Order;
        }
        return data;
    }

    /**
     * Fonctions de tris du tableau
     */
    sortTable(sort: Sort): SortTableInterface {
        this.currentSortAsc = sort.direction === 'asc';
        this.currentSort = sort.active;
        return this.sort(this.currentSort, this.currentSortAsc, this.currentPage);
    }
}
