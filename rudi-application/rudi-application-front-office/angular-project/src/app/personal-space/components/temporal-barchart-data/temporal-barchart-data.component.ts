import {Component, Input, OnInit} from '@angular/core';
import {BarChartData, TpbcDataInterface} from '../../../core/services/selfdata-dataset/tpbcData.interface';
import {BarChartType} from '../../../selfdata/selfdata-api';
import {BarchartService} from '../../../core/services/selfdata-dataset/barchart.service';

@Component({
    selector: 'app-temporal-barchart-data',
    templateUrl: './temporal-barchart-data.component.html',
    styleUrls: ['./temporal-barchart-data.component.scss']
})
export class TemporalBarchartDataComponent implements OnInit {
    @Input() isLoading: boolean;
    @Input() barChartData: BarChartData;
    graphData: TpbcDataInterface;

    constructor(private readonly barchartService: BarchartService) {
    }

    get isLineOrPoint(): boolean {
        return this.barChartData.type === BarChartType.Line || this.barChartData.type === BarChartType.Point;
    }

    get isBar(): boolean {
        return this.barChartData.type === BarChartType.Bar;
    }


    ngOnInit(): void {
        this.graphData = this.barchartService.getDataGraph(this.barChartData);
    }

}
