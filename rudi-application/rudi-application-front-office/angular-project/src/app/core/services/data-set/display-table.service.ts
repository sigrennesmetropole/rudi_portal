import {HttpResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {SPREADSHEET_COLDEF_INDEX} from '@features/data-set/components/spreadsheet/spreadsheet.component';
import {TranslateService} from '@ngx-translate/core';
import {ErrorWithCause} from '@shared/models/error-with-cause';
import {GetBackendPropertyPipe} from '@shared/pipes/get-backend-property.pipe';
import {ColDef} from 'ag-grid-community';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {Observable} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {read, utils, WorkBook} from 'xlsx';
import {KonsultMetierService} from '../konsult-metier.service';
import {DisplayTableDataInterface} from './display-table-data.interface';
import {readFile} from './display.function';

@Injectable({
    providedIn: 'root'
})
export class DisplayTableService {

    static FILE_SIZE_ERROR_CODE = 12;

    /**
     * le nom de la première "Feuille" de calcul d'un fichier CSV ou XLS importé dans SheetJS
     * @private
     */
    private static FIRST_SHEET_NAME = 'Sheet1';

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly getBackendPropertyPipe: GetBackendPropertyPipe,
        private readonly konsultService: KonsultService,
        private readonly translateService: TranslateService
    ) {

    }

    /**
     * Téléchargement d'un fichier tabulaire et récupération du contenu en mémoire au format Workbook XLS
     * @param globalId ID du JDD contenant le média
     * @param mediaId ID du média XLS a télécharger
     */
    downloadTableFile(globalId: string, mediaId: string): Observable<WorkBook> {
        return this.konsultMetierService.downloadMetadataMedia(globalId, mediaId).pipe(
            switchMap((responseBlob: HttpResponse<Blob>) => {
                const blob = new Blob([responseBlob.body], {type: responseBlob.body.type});

                // Vérification de la taille max autorisée pour télécharger
                return this.getTableDisplayMaxFileSize().pipe(
                    switchMap((maxFileSize: number) => {
                        if (blob.size > maxFileSize) {
                            const errorMessage = this.translateService.instant('metaData.tabulatedDataTab.errorFileSize');
                            throw new ErrorWithCause(errorMessage, null, DisplayTableService.FILE_SIZE_ERROR_CODE);
                        }

                        // Lecture du flux du blob
                        return readFile(blob);
                    })
                );
            }),
            // parse du blob en XLSX ou CSV
            map((arrayBuffer: ArrayBuffer) => {
                return read(arrayBuffer, {
                    type: 'array'
                });
            })
        );
    }

    /**
     * Conversion d'un Workbook en un objet affichable dans un tableau
     * @param workbook le workbook extrait d'un CSV ou XLS
     * @param withHeaders si on considère que la première ligne est un en-tête de colonnes
     */
    convertToDisplayableData(workbook: WorkBook, withHeaders: boolean): DisplayTableDataInterface {

        // Par défaut la première feuille de calcul s'appelle 'Sheet1'
        // c'est le cas d'un fichier CSV importé qui ne possèdes pas de 'Workbook'
        let sheetName = DisplayTableService.FIRST_SHEET_NAME;
        if (workbook.Workbook != null) {
            // Dans le cadre d'un fichier XLS la propriété Workbook est définie et il faut chercher le nom de la Sheet dedans
            // On ne gère l'affichage que de la première Sheet
            sheetName = workbook.Workbook.Sheets[0].name;
        }

        const worksheet = workbook.Sheets[sheetName];

        // L'option 'header' à la conversion de la worksheet en JSON permet de définir les en-têtes
        // si = null alors le contenu est parsé en supposant que la première ligne définit les entêtes
        // Si = A alors on considère qu'il n'y a que des données, et les entêtes sont générés comme dans excel (A, B ...)
        let header = null;
        if (!withHeaders) {
            header = 'A';
        }
        const sheet = utils.sheet_to_json(worksheet, {header});

        // Les noms de colonnes du tableau sont les noms des 'clés' des entrées d'une ligne
        const columnNames: string[] = Object.keys(sheet[0]);
        const columnDefs: ColDef[] = [];
        columnDefs.push(SPREADSHEET_COLDEF_INDEX);
        columnNames.forEach((columnName: string) => {
            columnDefs.push({field: columnName});
        });

        // Les coldefs sont construits à partir des noms de colonnes déduits et les données tabulaires sont déjà formatées en JSON
        // pour un affichage dans le tableau
        return {
            columnDefs,
            rowData: sheet
        } as DisplayTableDataInterface;
    }

    /**
     * Récupération de la taille max autorisée pour un affichage tabulaire
     * @private
     */
    private getTableDisplayMaxFileSize(): Observable<number> {
        return this.getBackendPropertyPipe.transform('rudidatarennes.tableDisplayMaxFileSize').pipe(
            map((tableDisplayMaxFileSize: string) => {
                return parseInt(tableDisplayMaxFileSize, 10);
            })
        );
    }
}
