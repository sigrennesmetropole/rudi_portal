import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {LiveAnnouncer} from '@angular/cdk/a11y';
import {MatSort} from '@angular/material/sort';
import {NewDatasetRequest} from '../../../../projekt/projekt-api';
import * as moment from 'moment';
import {DomSanitizer} from '@angular/platform-browser';
import {MatIconRegistry} from '@angular/material/icon';

export interface Table2Data {
    date: string;
    titre: string;
}

@Component({
    selector: 'app-acces-details-table2',
    templateUrl: './acces-details-table2.component.html',
    styleUrls: ['./acces-details-table2.component.scss']
})
export class AccesDetailsTable2Component implements OnInit, AfterViewInit {
    jdds: Table2Data[] = [];
    displayedColumns: string[] = ['date', 'titre'];
    dataSource: MatTableDataSource<Table2Data> = new MatTableDataSource(this.jdds);

    @Input()
    set newDatasetRequests(value: NewDatasetRequest[]) {
        if (value && value.length > 0) {
            this.jdds = value.map((request: NewDatasetRequest) => {
                return {
                    titre: request.title,
                    date: moment(request.updated_date).format('DD/MM/YYYY')
                };
            });

            this.dataSource = new MatTableDataSource(this.jdds);
        }
    }

    constructor(private domSanitizer: DomSanitizer,
                private matIconRegistry: MatIconRegistry,
                private _liveAnnouncer: LiveAnnouncer) {
        this.matIconRegistry.addSvgIcon(
            'nouvelles_donnees',
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/pictos/nouvelles_donnees.svg')
        );
    }

    @ViewChild(MatSort) sort: MatSort;

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }

    ngOnInit(): void {}
}
