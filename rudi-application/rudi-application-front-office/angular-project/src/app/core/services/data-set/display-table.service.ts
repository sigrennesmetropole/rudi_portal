import {Injectable} from '@angular/core';
import {KonsultMetierService} from '../konsult-metier.service';
import {Observable, Observer, of} from 'rxjs';
import {map, switchMap} from 'rxjs/operators';
import {HttpResponse} from '@angular/common/http';
import {read, utils, WorkBook} from 'xlsx';
import {GetBackendPropertyPipe} from '../../../shared/pipes/get-backend-property.pipe';
import {ColDef} from 'ag-grid-community';
import {DisplayTableDataInterface} from './display-table-data.interface';
import {Media, Metadata} from '../../../api-kaccess';
import {MetadataUtils} from '../../../shared/utils/metadata-utils';
import {KonsultService} from '../../../api-konsult';
import {SPREADSHEET_COLDEF_INDEX} from '../../../data-set/components/spreadsheet/spreadsheet.component';
import {ErrorWithCause} from '../../../shared/models/error-with-cause';
import {TranslateService} from '@ngx-translate/core';

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
     * Permets de savoir si l'utilisateur connecté peut accéder à la fonction d'affichage tabulaire pour ce JDD
     * @param metadata le JDD évalué
     * @param media le média à afficher de manière tabulaire
     */
    hasAccess(metadata: Metadata, media: Media): Observable<boolean> {
        if (metadata == null) {
            return of(false);
        } else if (MetadataUtils.isSelfdata(metadata)) {
            return of(false);
        } else if (MetadataUtils.isRestricted(metadata)) {
            return this.konsultService.hasSubscribeToMetadataMedia(metadata.global_id, media.media_id);
        }

        return of(true);
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
                        return this.readFile(blob);
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
     * Effectue le téléchargement du Blob pour récupérer son contenu en mémoire côté front
     * @param blob le blob a télécharger
     * @private
     */
    private readFile(blob: Blob): Observable<ArrayBuffer> {
        return new Observable<ArrayBuffer>((observer: Observer<ArrayBuffer>) => {
            const reader = new FileReader();

            reader.onload = (event: ProgressEvent<FileReader>) => {
                observer.next(event.target.result as ArrayBuffer);
                observer.complete();
            };

            reader.onerror = (event: ProgressEvent<FileReader>) => {
                observer.error(event);
            };

            reader.readAsArrayBuffer(blob);
        });
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
