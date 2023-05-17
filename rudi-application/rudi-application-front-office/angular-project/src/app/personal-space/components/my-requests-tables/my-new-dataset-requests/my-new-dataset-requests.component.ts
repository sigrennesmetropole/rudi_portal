import {Component, OnInit} from '@angular/core';
import {AbstractMyRequestTableComponent} from '../abstract-my-request-table.component';
import {MyRequestsService} from '../../../../core/services/my-requests/my-requests.service';
import {SortTableInterface} from '../../../../shared/back-pagination/sort-table-interface';
import {Observable} from 'rxjs';
import {RequestItem} from '../request-item';
import {map} from 'rxjs/operators';
import {NewDatasetRequestSearchCriteria, NewDatasetRequestStatus, PagedLinkedDatasetList} from '../../../../projekt/projekt-model';
import {NewDatasetRequest} from '../../../../projekt/projekt-api';
import {ProcessDefinitionsKeyIconRegistryService} from '../../../../core/services/process-definitions-key-icon-registry.service';

@Component({
    selector: 'app-my-new-dataset-requests',
    templateUrl: './my-new-dataset-requests.component.html',
    styleUrls: ['./my-new-dataset-requests.component.scss']
})
export class MyNewDatasetRequestsComponent extends AbstractMyRequestTableComponent implements OnInit {

    static NEW_DATASET_REQUEST_PAGINATOR_ID = 'newDatasetRequestPaginator';

    constructor(myRequestsService: MyRequestsService,
                processDefinitionsKeyIconRegistryService: ProcessDefinitionsKeyIconRegistryService) {
        super(myRequestsService, processDefinitionsKeyIconRegistryService);
        this.backPaginatorId = MyNewDatasetRequestsComponent.NEW_DATASET_REQUEST_PAGINATOR_ID;
    }

    ngOnInit(): void {
        const defaultSortTable: SortTableInterface = {order: super.DEFAULT_SORT_ORDER, page: 1};
        super.loadContent(defaultSortTable);
    }

    protected getMyElements(offset: number, limit: number, order: string): Observable<RequestItem[]> {
        let newDatasetRequestSearchCriteria: NewDatasetRequestSearchCriteria = {};
        newDatasetRequestSearchCriteria.offset = offset;
        newDatasetRequestSearchCriteria.limit = limit;
        newDatasetRequestSearchCriteria.order = order;
        newDatasetRequestSearchCriteria.status = [NewDatasetRequestStatus.Validated, NewDatasetRequestStatus.Refused];
        return this.myRequestsService.searchMyFinishedNewDatasetRequests(newDatasetRequestSearchCriteria).pipe(
            map((page: PagedLinkedDatasetList) => {
                this.total = page.total;
                return page.elements.map((newRequest: NewDatasetRequest) => {
                    return {
                        updatedDate: new Date(newRequest.updated_date),
                        initiator: newRequest.initiator,
                        title: newRequest.description,
                        functionalStatus: newRequest.functional_status
                    } as RequestItem;
                });
            })
        );
    }
}
