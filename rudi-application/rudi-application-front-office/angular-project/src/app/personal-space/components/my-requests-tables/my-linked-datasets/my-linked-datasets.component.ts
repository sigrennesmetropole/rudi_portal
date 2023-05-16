import {Component, OnInit} from '@angular/core';
import {RequestItem} from '../request-item';
import {Observable} from 'rxjs';
import {AbstractMyRequestTableComponent} from '../abstract-my-request-table.component';
import {SortTableInterface} from '../../../../shared/back-pagination/sort-table-interface';
import {map} from 'rxjs/operators';
import {MyRequestsService} from '../../../../core/services/my-requests/my-requests.service';
import {PagedLinkedDatasetList} from '../../../../projekt/projekt-model';
import {LinkedDataset, LinkedDatasetSearchCriteria, LinkedDatasetStatus} from '../../../../projekt/projekt-api';
import {ProcessDefinitionsKeyIconRegistryService} from '../../../../core/services/process-definitions-key-icon-registry.service';

@Component({
    selector: 'app-my-linked-datasets',
    templateUrl: './my-linked-datasets.component.html',
    styleUrls: ['./my-linked-datasets.component.scss']
})
export class MyLinkedDatasetsComponent extends AbstractMyRequestTableComponent implements OnInit {

    static LINKED_DATASET_PAGINATOR_ID = 'linkedDatasetPaginator';

    constructor(myRequestsService: MyRequestsService,
                processDefinitionsKeyIconRegistryService: ProcessDefinitionsKeyIconRegistryService) {
        super(myRequestsService, processDefinitionsKeyIconRegistryService);
        this.backPaginatorId = MyLinkedDatasetsComponent.LINKED_DATASET_PAGINATOR_ID;
    }

    ngOnInit(): void {
        const defaultSortTable: SortTableInterface = {order: super.DEFAULT_SORT_ORDER, page: 1};
        super.loadContent(defaultSortTable);
    }

    protected getMyElements(offset: number, limit: number, order: string): Observable<RequestItem[]> {
        let linkedDatasetSearchCriteria: LinkedDatasetSearchCriteria = {};
        linkedDatasetSearchCriteria.offset = offset;
        linkedDatasetSearchCriteria.limit = limit;
        linkedDatasetSearchCriteria.order = order;
        linkedDatasetSearchCriteria.status = [LinkedDatasetStatus.Validated, LinkedDatasetStatus.Cancelled];
        return this.myRequestsService.searchMyFinishedLinkedDatasets(linkedDatasetSearchCriteria).pipe(
            map((page: PagedLinkedDatasetList) => {
                this.total = page.total;
                return page.elements.map((link: LinkedDataset) => {
                    return {
                        updatedDate: new Date(link.updated_date),
                        initiator: link.initiator,
                        title: link.description,
                        functionalStatus: link.functional_status
                    } as RequestItem;
                });
            })
        );
    }
}
