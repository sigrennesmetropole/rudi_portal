import {Component, ElementRef, Input, OnInit} from '@angular/core';
import * as d3 from 'd3';
import {IconRegistryService} from '@core/services/icon-registry.service';
import {MARGIN, WINDOWS_HEIGHT, WINDOWS_WIDTH} from '@core/services/selfdata-dataset/barchart.service';
import {
    BarChartType,
    DEFAULT_HEIGHT,
    LineAndPlot,
    LineAndPlotValue,
    TpbcDataInterface
} from '@core/services/selfdata-dataset/tpbcData.interface';
import {ALL_TYPES} from '@shared/models/title-icon-type';

@Component({
    selector: 'app-d3-line-and-plot-chart',
    templateUrl: './d3-line-and-plot-chart.component.html',
    styleUrls: ['./d3-line-and-plot-chart.component.scss']
})
export class D3LineAndPlotChartComponent implements OnInit {
    @Input() graphBar: TpbcDataInterface;

    private margin = MARGIN;
    private w = WINDOWS_WIDTH;
    private h = WINDOWS_HEIGHT;
    private width: number;
    private height = this.h - this.margin.top - this.margin.bottom;

    private xScale: any;
    private yScale: any;
    private y_axis: any;
    private x_axis: any;
    private svg: any;
    private g: any;
    private chart: any;


    constructor(iconRegistryService: IconRegistryService,
                private container: ElementRef) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    ngOnInit() {
        this.width = (DEFAULT_HEIGHT - this.margin.left - this.margin.right) * this.graphBar.xLabels.length / 11;
        this.initScales();
        this.initSvg();
        this.createAxis();
        this.drawUpdate(this.graphBar.lineAndPlot, this.graphBar.xLabels, this.initAxis(this.graphBar.lineAndPlot));
    }

    get withLine(): boolean {
        return this.graphBar.type === BarChartType.Line;
    }

    private initScales(): void {
        this.xScale = d3.scaleBand()
            .range([0, this.width])
            .padding(.99);

        this.yScale = d3.scaleLinear()
            .rangeRound([this.height, 0]);

    }

    private initSvg(): void {
        this.svg = d3.select(this.container.nativeElement)
            .select('.chart-container')
            .append('svg')
            .attr(
                'viewBox',
                `0 0 ${this.width + this.margin.left + this.margin.right} ${this.height +
                this.margin.top +
                this.margin.bottom}`
            )
            .attr('width', this.width);

        this.chart = this.svg
            .append('g')
            .attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')');

    }

    private createAxis(): void {
        this.y_axis = d3.axisLeft(this.yScale)
            .tickPadding(10)
            .ticks(10);

        this.x_axis = d3.axisBottom(this.xScale)
            .scale(this.xScale)
            .tickPadding(10);

        this.chart.append('g').classed('y-axis', true);
        this.chart.append('g').classed('x-axis', true);

    }

    private drawUpdate(data: LineAndPlot[], axisX: string[], axisY: LineAndPlotValue[]): void {

        this.xScale.domain(axisX);

        this.yScale.domain([0, d3.max(axisY, (d: LineAndPlotValue) => d.coordonateY) + 1]);

        // -- line

        const valueline = d3.line()
            .x((d: any) => this.xScale(d.coordonateX))
            .y((d: any) => this.yScale(d.coordonateY));

        // lignes
        if (this.withLine) {
            this.chart.append('g')
                .attr('class', 'lines').selectAll('.line-group')
                .data(data).enter()
                .append('g')
                .attr('class', 'line-group')
                .append('path')
                .attr('class', 'line')
                .attr('d', d => valueline(d.values))
                .attr('stroke', d => d.color)
                .style('stroke-width', 2)
                .style('fill', 'none');
        }
        // -- point
        this.chart.append('g')
            .attr('class', 'lines').selectAll('circle-group')
            .data(data).enter()
            .append('g')
            .attr('class', 'circle')
            .selectAll('circle')
            .data(d => d.values).enter()
            .append('g')
            .append('circle')
            .style('fill', d => d.color)
            .attr('cx', d => this.xScale(d.coordonateX))
            .attr('cy', d => this.yScale(d.coordonateY))
            .attr('r', 5);

        // -- axis

        d3.select('.y-axis')
            .transition()
            .call(this.y_axis)
            .selectAll('.tick text');

        d3.select('.x-axis')
            .call(this.x_axis)
            .attr('transform', 'translate(0,' + this.height + ')')
            .selectAll('.tick text');
    }

    private initAxis(lineAndPlot: LineAndPlot[]): LineAndPlotValue[] {
        let mergedArray = [];
        for (const element of lineAndPlot) {
            mergedArray = mergedArray.concat(element.values);
        }
        return mergedArray;
    }
}
