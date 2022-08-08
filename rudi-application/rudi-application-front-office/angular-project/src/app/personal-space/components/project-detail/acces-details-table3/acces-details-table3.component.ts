import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {LiveAnnouncer} from '@angular/cdk/a11y';
import {MatSort} from '@angular/material/sort';
import {OpenLinkedDatasetAccessRequest} from '../../../../core/services/linked-dataset-task.service';
import * as moment from 'moment';

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
export class AccesDetailsTable3Component implements OnInit, AfterViewInit {
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

    constructor(private _liveAnnouncer: LiveAnnouncer) {
    }

    @ViewChild(MatSort) sort: MatSort;

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }

    ngOnInit(): void {
    }

}
