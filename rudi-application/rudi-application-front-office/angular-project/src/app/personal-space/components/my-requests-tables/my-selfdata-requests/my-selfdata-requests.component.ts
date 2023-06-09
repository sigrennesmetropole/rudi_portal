import {Component, OnInit} from '@angular/core';
import {AbstractMyRequestTableComponent} from '../abstract-my-request-table.component';
import {SortTableInterface} from '../../../../shared/back-pagination/sort-table-interface';
import {Observable} from 'rxjs';
import {RequestItem} from '../request-item';
import {map} from 'rxjs/operators';
import {PagedLinkedDatasetList} from '../../../../projekt/projekt-model';
import {MyRequestsService} from '../../../../core/services/my-requests/my-requests.service';
import {SelfdataInformationRequest} from '../../../../selfdata/selfdata-model';
import {ProcessDefinitionsKeyIconRegistryService} from '../../../../core/services/process-definitions-key-icon-registry.service';
import {SelfdataInformationRequestSearchCriteria, SelfdataInformationRequestStatus} from '../../../../selfdata/selfdata-api';

@Component({
    selector: 'app-my-selfdata-requests',
    templateUrl: './my-selfdata-requests.component.html',
    styleUrls: ['./my-selfdata-requests.component.scss']
})
export class MySelfdataRequestsComponent extends AbstractMyRequestTableComponent implements OnInit {

    static SELFDATA_INFORMATION_REQUEST_PAGINATOR_ID = 'selfdataInformationRequestPaginator';

    constructor(myRequestsService: MyRequestsService,
                processDefinitionsKeyIconRegistryService: ProcessDefinitionsKeyIconRegistryService) {
        super(myRequestsService, processDefinitionsKeyIconRegistryService);
        this.backPaginatorId = MySelfdataRequestsComponent.SELFDATA_INFORMATION_REQUEST_PAGINATOR_ID;
    }

    ngOnInit(): void {
        const defaultSortTable: SortTableInterface = {order: super.DEFAULT_SORT_ORDER, page: 1};
        super.loadContent(defaultSortTable);
    }

    protected getMyElements(offset: number, limit: number, order: string): Observable<RequestItem[]> {
        const selfdataInformationRequestSearchCriteria: SelfdataInformationRequestSearchCriteria = {};
        selfdataInformationRequestSearchCriteria.offset = offset;
        selfdataInformationRequestSearchCriteria.limit = limit;
        selfdataInformationRequestSearchCriteria.order = order;
        selfdataInformationRequestSearchCriteria.status = [
            SelfdataInformationRequestStatus.Completed,
            SelfdataInformationRequestStatus.Cancelled,
            SelfdataInformationRequestStatus.Rejected
        ];
        return this.myRequestsService.searchMyFinishedSelfdataInformationRequests(selfdataInformationRequestSearchCriteria).pipe(
            map((page: PagedLinkedDatasetList) => {
                this.total = page.total;
                return page.elements.map((selfdataRequest: SelfdataInformationRequest) => {
                    return {
                        updatedDate: new Date(selfdataRequest.updated_date),
                        initiator: selfdataRequest.initiator,
                        title: selfdataRequest.description,
                        functionalStatus: selfdataRequest.functional_status
                    } as RequestItem;
                });
            })
        );
    }
}
