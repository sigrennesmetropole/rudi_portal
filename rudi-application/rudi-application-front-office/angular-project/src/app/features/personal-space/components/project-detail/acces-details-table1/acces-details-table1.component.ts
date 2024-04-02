import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatTableDataSource} from '@angular/material/table';
import * as moment from 'moment';
import {Task} from 'micro_service_modules/api-bpmn';
import {Indicators} from 'micro_service_modules/projekt/projekt-api';

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
export class AccesDetailsTable1Component implements AfterViewInit {
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

    hasOtherRequests(): boolean {
        return this.jdds.length !== 0;
    }

    isOneToOne(): boolean {
        return this._otherIndicators.x === 1 && this._otherIndicators.n === 1;
    }

    isOneToMany(): boolean {
        return this._otherIndicators.x === 1 && this._otherIndicators.n > 1;
    }

    isManyToOne(): boolean {
        return this._otherIndicators.x > 1 && this._otherIndicators.n === 1;
    }

    isManyToMany(): boolean {
        return this._otherIndicators.x > 1 && this._otherIndicators.n > 1;
    }

    ngAfterViewInit() {
        this.dataSource.sort = this.sort;
    }
}
