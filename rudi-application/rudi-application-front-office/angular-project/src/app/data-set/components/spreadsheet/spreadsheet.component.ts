import {Component, Input} from '@angular/core';
import {ColDef, GridOptions} from 'ag-grid-community';
import {SPREADSHEET_LOCALE_FR} from './spreadsheet-locale-fr';

export const SPREADSHEET_COLDEF_INDEX: ColDef = {
    field: '',
    width: 75,
    valueGetter: 'node.rowIndex + 1',
    cellClass: 'ag-first-cell-column'
};

@Component({
    selector: 'app-spreadsheet',
    templateUrl: './spreadsheet.component.html',
    styleUrls: ['./spreadsheet.component.scss']
})
export class SpreadsheetComponent {

    constructor() {
        this.defaultColDef = SpreadsheetComponent.createDefaultColDef();
    }

    gridOptions: GridOptions = {
        localeText: SPREADSHEET_LOCALE_FR
    };

    @Input()
    public rowData: unknown[] = [];

    @Input()
    columnDefs: ColDef[];

    public defaultColDef: ColDef;

    /**
     * Méthode qui initialise le tri
     * @private
     */
    private static createDefaultColDef(): ColDef {
        return {
            sortable: true,
            resizable: true,
        };
    }

    fitColumnSize(): void {
        if (this.gridOptions && this.gridOptions.api) {
            this.gridOptions.api.sizeColumnsToFit();
        }
    }
}
