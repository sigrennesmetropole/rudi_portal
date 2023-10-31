import {LinkedDatasetMetadatas} from './asset/project/project-dependencies.service';
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
import {
    DeletionConfirmationPopinComponent
} from '../../shared/project-datasets-tables/deletion-confirmation-popin/deletion-confirmation-popin.component';


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

    /**
     * Ouverture d'une dialog permettant de supprimer un jdd de notre projet
     * @param requestUuid
     */
    public openDialogDeletionConfirmation(requestUuid: string): Observable<DialogClosedData<string>> {
        const dialogConfig = new DefaultMatDialogConfig<string>();
        dialogConfig.data = requestUuid;
        const dialogRef = this.dialog.open(DeletionConfirmationPopinComponent, dialogConfig);
        return dialogRef.afterClosed();
    }
}
