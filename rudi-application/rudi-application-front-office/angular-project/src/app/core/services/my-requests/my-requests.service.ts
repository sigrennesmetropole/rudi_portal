import {Injectable} from '@angular/core';
import {LinkedDatasetSearchCriteria, ProjektService} from 'micro_service_modules/projekt/projekt-api';
import {NewDatasetRequestSearchCriteria, PagedLinkedDatasetList} from 'micro_service_modules/projekt/projekt-model';
import {SelfdataInformationRequestSearchCriteria, SelfdataService} from 'micro_service_modules/selfdata/selfdata-api';
import {Observable} from 'rxjs';

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
