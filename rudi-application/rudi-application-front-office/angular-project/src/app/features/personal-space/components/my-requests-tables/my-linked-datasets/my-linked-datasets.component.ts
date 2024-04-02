import {Component, OnInit} from '@angular/core';
import {Observable} from 'rxjs';
import {map} from 'rxjs/operators';
import {MyRequestsService} from '@core/services/my-requests/my-requests.service';
import {ProcessDefinitionsKeyIconRegistryService} from '@core/services/process-definitions-key-icon-registry.service';
import {LinkedDataset, LinkedDatasetSearchCriteria, LinkedDatasetStatus} from 'micro_service_modules/projekt/projekt-api';
import {DatasetConfidentiality, PagedLinkedDatasetList} from 'micro_service_modules/projekt/projekt-model';
import {SortTableInterface} from '@shared/back-pagination/sort-table-interface';
import {AbstractMyRequestTableComponent} from '../abstract-my-request-table.component';
import {RequestItem} from '../request-item';

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
        const defaultSortTable: SortTableInterface = {order: this.DEFAULT_SORT_ORDER, page: 1};
        super.loadContent(defaultSortTable);
    }

    protected getMyElements(offset: number, limit: number, order: string): Observable<RequestItem[]> {
        const linkedDatasetSearchCriteria: LinkedDatasetSearchCriteria = {};
        linkedDatasetSearchCriteria.offset = offset;
        linkedDatasetSearchCriteria.limit = limit;
        linkedDatasetSearchCriteria.order = order;
        linkedDatasetSearchCriteria.datasetConfidentiality = DatasetConfidentiality.Restricted;
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
