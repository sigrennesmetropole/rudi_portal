import {AfterViewInit, Component, Input, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {LiveAnnouncer} from '@angular/cdk/a11y';
import {MatSort} from '@angular/material/sort';
import {Task} from '../../../../api-bpmn';
import * as moment from 'moment';
import {Indicators} from '../../../../projekt/projekt-api';

export interface Table1Data {
    date: string;
    titre: string;
    statut: string;
}

export interface OtherIndicators {
    x: number;
    n: number;
}

@Component({
    selector: 'app-acces-details-table1',
    templateUrl: './acces-details-table1.component.html',
    styleUrls: ['./acces-details-table1.component.scss']
})
export class AccesDetailsTable1Component implements OnInit, AfterViewInit {
    jdds: Table1Data[] = [];
    displayedColumns: string[] = ['date', 'titre', 'statut'];
    dataSource: MatTableDataSource<Table1Data> = new MatTableDataSource(this.jdds);
    _otherIndicators: OtherIndicators;

    @Input()
    set otherIndicators(value: Indicators) {
        if (value) {
            this._otherIndicators = {
                x: value.numberOfRequest,
                n: value.numberOfProducer
            };
        }
    }

    @Input()
    set otherLinkedDatasets(value: Task[]) {
        if (value && value.length > 0) {
            this.jdds = value.map((task: Task) => {
                return {
                    date: moment(task.updatedDate).format('DD/MM/YYYY'),
                    titre: task.asset.description,
                    statut: task.functionalStatus
                };
            });

            this.dataSource = new MatTableDataSource(this.jdds);
        }
    }

    constructor(private _liveAnnouncer: LiveAnnouncer) {
    }

    @ViewChild(MatSort) sort: MatSort;

    /**
     * Est-ce qu'il y'a des indicateurs sur d'autres demandes d'accès
     * @return vrai ou faux
     */
    hasOtherIndicators(): boolean {

        // Si pas d'objet fourni
        if (!this._otherIndicators) {
            return false;
        }

        // Si 0 autre JDD
        if (this._otherIndicators && this._otherIndicators.x === 0) {
            return false;
        }

        return true;
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }

    ngOnInit(): void {
    }

}
