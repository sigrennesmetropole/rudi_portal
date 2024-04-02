import {Component, Input, ViewChild} from '@angular/core';
import {BreakpointObserverService} from '@core/services/breakpoint-observer.service';
import {AgGridAngular} from 'ag-grid-angular';
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

    constructor(
        private readonly breakpointObserver: BreakpointObserverService,
    ) {
        this.defaultColDef = SpreadsheetComponent.createDefaultColDef();
    }

    private static MAX_COL_SM_SCREEN = 2;
    private static MAX_COL_MD_SCREEN = 3;
    private static MAX_COL_LG_SCREEN = 6;
    private static MAX_COL_XL_SCREEN = 8;
    private static MAX_COL_XXL_SCREEN = 10;

    @ViewChild(AgGridAngular) grid?: AgGridAngular;

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
        if (this.columnDefs.length <= this.mediaSizeGestion()){
            this.grid?.api.sizeColumnsToFit();
        }
    }

    private mediaSizeGestion(): number {
        const mediaSize = this.breakpointObserver.getMediaSize();
        if (mediaSize.isSm){
            return SpreadsheetComponent.MAX_COL_SM_SCREEN;
        }
        if (mediaSize.isMd){
            return SpreadsheetComponent.MAX_COL_MD_SCREEN;
        }
        if (mediaSize.isLg){
            return SpreadsheetComponent.MAX_COL_LG_SCREEN;
        }
        if (mediaSize.isXl) {
            return SpreadsheetComponent.MAX_COL_XL_SCREEN;
        }

        return SpreadsheetComponent.MAX_COL_XXL_SCREEN;
    }


}
