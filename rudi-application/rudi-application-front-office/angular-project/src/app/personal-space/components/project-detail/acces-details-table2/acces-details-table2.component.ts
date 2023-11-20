import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import {DomSanitizer} from '@angular/platform-browser';
import * as moment from 'moment';
import {NewDatasetRequest} from '../../../../projekt/projekt-api';

export interface Table2Data {
    date: string;
    titre: string;
    statut: string;
}

@Component({
    selector: 'app-acces-details-table2',
    templateUrl: './acces-details-table2.component.html',
    styleUrls: ['./acces-details-table2.component.scss']
})
export class AccesDetailsTable2Component implements AfterViewInit {
    jdds: Table2Data[] = [];
    displayedColumns: string[] = ['date', 'titre', 'statut'];
    dataSource: MatTableDataSource<Table2Data> = new MatTableDataSource(this.jdds);

    @Input()
    set newDatasetRequests(value: NewDatasetRequest[]) {
        if (value && value.length > 0) {
            this.jdds = value.map((request: NewDatasetRequest) => {
                return {
                    titre: request.title,
                    date: moment(request.updated_date).format('DD/MM/YYYY'),
                    statut: request.functional_status,
                };
            });

            this.dataSource = new MatTableDataSource(this.jdds);
        }
    }

    constructor(
        private domSanitizer: DomSanitizer,
        private matIconRegistry: MatIconRegistry,
    ) {
        this.matIconRegistry.addSvgIcon(
            'nouvelles_donnees',
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/pictos/nouvelles_donnees.svg')
        );
    }

    @ViewChild(MatSort) sort: MatSort;

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }
}
