import {LinkedDatasetMetadatas} from './project-dependencies.service';
import {Project} from '../../projekt/projekt-api';
import {Observable} from 'rxjs';
import {DialogClosedData} from '../../data-set/models/dialog-closed-data';
import {DefaultMatDialogConfig} from './default-mat-dialog-config';
import {
    DialogSubscribeDatasetsComponent,
    DialogSubscribeDatasetsData
} from '../../personal-space/components/dialog-subscribe-datasets/dialog-subscribe-datasets.component';
import {MatDialog} from '@angular/material/dialog';
import {Injectable} from '@angular/core';

@Injectable({
    providedIn: 'root'
})
export class DialogSubscribeDatasetsService {

    constructor(private readonly dialog: MatDialog) {
    }

    /**
     * Ouverture d'une dialog permettant de saisir les JDDs auxquels souscrire
     * @param linkedDatasetMetadatas tous les JDDs des demandes d'un projet et leurs métadatas
     * @param project le projet
     */
    public openDialogSelectDatasetsToSubscribe(linkedDatasetMetadatas: LinkedDatasetMetadatas[],
                                               project: Project): Observable<DialogClosedData<void>> {
        const dialogConfig = new DefaultMatDialogConfig<DialogSubscribeDatasetsData>();
        dialogConfig.data = {
            data: {
                linkedDatasetMetadatas,
                project
            }
        };

        const dialogRef = this.dialog.open(DialogSubscribeDatasetsComponent, dialogConfig);
        return dialogRef.afterClosed();
    }
}
