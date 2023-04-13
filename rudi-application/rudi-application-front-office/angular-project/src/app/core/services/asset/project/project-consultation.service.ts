import {Injectable} from '@angular/core';
import {ProjektMetierService} from './projekt-metier.service';
import {from, Observable} from 'rxjs';
import {LinkedDatasetMetadatas} from './project-dependencies.service';
import {map, mergeMap, reduce, switchMap} from 'rxjs/operators';
import {LinkedDataset} from '../../../../projekt/projekt-model';
import {Metadata} from '../../../../api-kaccess';
import {KonsultMetierService} from '../../konsult-metier.service';
import {NewDatasetRequest} from '../../../../projekt/projekt-api';
import {RowTableData} from '../../../../personal-space/components/project-datasets-tab/dataset.interface';
import {Status} from '../../../../api-bpmn';
import {Level} from '../../../../shared/notification-template/notification-template.component';
import {SnackBarService} from '../../snack-bar.service';
import {TranslateService} from '@ngx-translate/core';

@Injectable({
    providedIn: 'root'
})
export class ProjectConsultationService {
    constructor(
        private readonly projektMetierService: ProjektMetierService,
        private readonly konsultMetierService: KonsultMetierService,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
    ) {
    }

    /**
     * Recupère les JDDs ouverts d'un projet et les mappent vers l'objet cible pour affichage
     * @param projectUuid uuid du projet
     */
    getOpenedLinkedDatasetsMetadata(projectUuid: string): Observable<LinkedDatasetMetadatas[]> {
        return this.projektMetierService.getOpenedLinkedDatasets(projectUuid).pipe(
            switchMap((linkedDatasets: LinkedDataset[]) => {
                return this.getLinkedDatasetMetadata(linkedDatasets);
            })
        );
    }

    /**
     * Recupère les JDDs restreints d'un projet et les mappent vers l'objet cible pour affichage
     * @param projectUuid uuid du projet
     */
    getRestrictedLinkedDatasetsMetadata(projectUuid: string): Observable<LinkedDatasetMetadatas[]> {
        return this.projektMetierService.getRestrictedLinkedDatasets(projectUuid).pipe(
            switchMap((linkedDatasets: LinkedDataset[]) => {
                return this.getLinkedDatasetMetadata(linkedDatasets);
            })
        );
    }

    getNewDatasetsRequest(projectUuid: string): Observable<NewDatasetRequest[]> {
        return this.projektMetierService.getNewDatasetRequests(projectUuid);
    }

    private getLinkedDatasetMetadata(linkedDatasets: LinkedDataset[]): Observable<LinkedDatasetMetadatas[]> {
        return from(linkedDatasets).pipe(
            mergeMap((linkedData: LinkedDataset) => {
                return this.konsultMetierService.getMetadataByUuid(linkedData.dataset_uuid).pipe(
                    map((dataset: Metadata) => {
                        return {linkedDataset: linkedData, dataset};
                    })
                );
            }),
            reduce((accumulator: LinkedDatasetMetadatas[], current: LinkedDatasetMetadatas) => {
                accumulator.push(current);
                return accumulator;
            }, [])
        );
    }

    /**
     * Méthode appelée lors du click sur le picto de suppression
     * @param rowTableData
     */
    isRowDelatable(rowTableData: RowTableData): boolean {
        if (rowTableData.status === Status.Completed) {
            return true;
        }
        this.snackBarService.openSnackBar({
            message: this.translateService.instant('personalSpace.project.tabs.deletion.impossible'),
            level: Level.ERROR,
        });
        return false;
    }
}
