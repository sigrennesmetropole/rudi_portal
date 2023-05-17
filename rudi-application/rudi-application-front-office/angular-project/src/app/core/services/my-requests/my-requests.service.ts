import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {NewDatasetRequestSearchCriteria, PagedLinkedDatasetList} from '../../../projekt/projekt-model';
import {LinkedDatasetSearchCriteria, ProjektService} from '../../../projekt/projekt-api';
import {SelfdataInformationRequestSearchCriteria, SelfdataService} from '../../../selfdata/selfdata-api';

@Injectable({
    providedIn: 'root'
})
export class MyRequestsService {

    constructor(
        private readonly projektService: ProjektService,
        private readonly selfdataService: SelfdataService) {
    }

    /**
     * Recherche des linked datasets du user connecté qui ont finalisés leurs workflows
     * @param linkedDatasetSearchCriteria
     */
    public searchMyFinishedLinkedDatasets(linkedDatasetSearchCriteria: LinkedDatasetSearchCriteria): Observable<PagedLinkedDatasetList> {
        return this.projektService.searchMyLinkedDatasets(linkedDatasetSearchCriteria);
    }

    /**
     * Recherche des new datasets request du user connecté qui ont finalisés leurs workflows
     * @param newDatasetRequestSearchCriteria
     */
    public searchMyFinishedNewDatasetRequests(newDatasetRequestSearchCriteria: NewDatasetRequestSearchCriteria): Observable<PagedLinkedDatasetList> {
        return this.projektService.searchMyNewDatasetRequests(newDatasetRequestSearchCriteria);
    }

    /**
     * Recherche des demandes effectuées par l'utilisateur connecté qui ont finalisées leurs workflows
     * @param selfdataInformationRequestSearchCriteria
     */
    public searchMyFinishedSelfdataInformationRequests(selfdataInformationRequestSearchCriteria: SelfdataInformationRequestSearchCriteria) {
        return this.selfdataService.searchMySelfdataInformationRequests(selfdataInformationRequestSearchCriteria);
    }
}
