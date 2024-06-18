import {Component, Input, OnInit} from '@angular/core';
import {DataSetAccessService} from '@core/services/data-set/data-set-access.service';
import {DisplayTableDataInterface} from '@core/services/data-set/display-table-data.interface';
import {DisplayTableService} from '@core/services/data-set/display-table.service';
import {IconRegistryService} from '@core/services/icon-registry.service';
import {LogService} from '@core/services/log.service';
import {ErrorWithCause} from '@shared/models/error-with-cause';
import {ALL_TYPES} from '@shared/models/title-icon-type';
import {Media, Metadata} from 'micro_service_modules/api-kaccess';
import {catchError, switchMap} from 'rxjs/operators';
import {WorkBook} from 'xlsx';

const EMPTY_SEARCH = '';

@Component({
    selector: 'app-spreadsheet-tab',
    templateUrl: './spreadsheet-tab.component.html',
    styleUrls: ['./spreadsheet-tab.component.scss']
})
export class SpreadsheetTabComponent implements OnInit {

    @Input()
    metadata: Metadata;

    @Input()
    mediaToDisplay: Media;
    searchTerms = '';
    displayTableLoading = false;
    displayTable = false;
    usesHeader = false;
    displayTableData: DisplayTableDataInterface;
    workbook: WorkBook;
    errorAccess = false;
    errorDownloading = false;
    businessErrorMessage: string;
    unFilteredRowData: unknown[] = [];
    displayResults = false;

    constructor(
        private readonly displayTableService: DisplayTableService,
        private readonly iconRegistryService: IconRegistryService,
        private readonly logService: LogService,
        private readonly datasetAccessService: DataSetAccessService
    ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    switchHeader(): void {
        this.usesHeader = !this.usesHeader;
        this.displayTableData = this.displayTableService.convertToDisplayableData(this.workbook, this.usesHeader);
        this.unFilteredRowData = this.displayTableData.rowData;
        this.onReset();
    }

    ngOnInit(): void {
        if (this.metadata && this.mediaToDisplay) {
            this.displayTableLoading = true;
            this.datasetAccessService.hasAccess(this.metadata).pipe(
                switchMap((hasAccess: boolean) => {
                    if (hasAccess) {
                        this.errorAccess = false;
                        return this.displayTableService.downloadTableFile(this.mediaToDisplay.connector.url).pipe(
                            catchError((error) => {
                                // Cas erreur avec un message à afficher côté front
                                if (error instanceof ErrorWithCause && error.code != null) {
                                    this.businessErrorMessage = error.functionalMessage;
                                    throw error;
                                }

                                // Cas erreur générique => message générique
                                this.errorDownloading = true;
                                throw new ErrorWithCause('Erreur lors du téléchargement des données', error);
                            })
                        );
                    } else {
                        this.errorAccess = true;
                        throw new Error('Accès à la fonctionnalité d\'affichage tabulaire interdit dans ce contexte');
                    }
                })
            ).subscribe({
                next: (workbook: WorkBook) => {
                    this.errorDownloading = false;
                    this.displayTableLoading = false;
                    this.displayTable = true;
                    this.workbook = workbook;
                    this.displayTableData = this.displayTableService.convertToDisplayableData(this.workbook, this.usesHeader);
                    this.unFilteredRowData = this.displayTableData.rowData;
                },
                error: (e) => {
                    this.logService.error(e);
                    this.displayTableLoading = false;
                    this.displayTable = false;
                }
            });
        }
    }

    /**
     * Fonction permettant de vider le champ input de la recherche et l'initialisation de la liste
     */
    onReset(): void {
        this.searchTerms = EMPTY_SEARCH;
        this.displayTableData.rowData = this.unFilteredRowData;
        this.onChanges();
    }

    /**
     * Méthode liée au déclenchement de l'event "key.enter" du champ de recherche
     */
    onChanges(): void {
        if (this.searchTerms) {
            this.displayTableData.rowData = this.filteredRowData;
            this.displayResults = true;
        } else {
            this.displayTableData.rowData = this.unFilteredRowData;
            this.displayResults = false;
        }

    }

    get filteredRowData(): unknown[] {
        const filterText = this.searchTerms.toLowerCase();
        return this.displayTableData.rowData.filter((data: unknown) => {
            return Object.values(data).some((value: unknown) => {
                return String(value).toLowerCase().includes(filterText);
            });
        });
    }
}
