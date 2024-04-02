import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import * as moment from 'moment';
import {OpenLinkedDatasetAccessRequest} from '@core/services/tasks/projekt/linked-dataset-task-dependencies.service';

export interface Table3Data {
    titre: string;
    nom: string;
    date: string;
}

@Component({
    selector: 'app-acces-details-table3',
    templateUrl: './acces-details-table3.component.html',
    styleUrls: ['./acces-details-table3.component.scss']
})
export class AccesDetailsTable3Component implements AfterViewInit {
    jdds: Table3Data[] = [];
    displayedColumns: string[] = ['date', 'titre'];
    dataSource: MatTableDataSource<Table3Data> = new MatTableDataSource(this.jdds);

    @Input()
    set openLinkedDatasetAccessRequests(value: OpenLinkedDatasetAccessRequest[]) {
        if (value && value.length > 0) {
            this.jdds = value.map((request: OpenLinkedDatasetAccessRequest) => {
                return {
                    titre: request.dataset.resource_title,
                    nom: request.dataset.producer.organization_name,
                    date: moment(request.linkedDataset.updated_date).format('DD/MM/YYYY')
                };
            });

            this.dataSource = new MatTableDataSource(this.jdds);
        }
    }

    @ViewChild(MatSort) sort: MatSort;

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }
}
