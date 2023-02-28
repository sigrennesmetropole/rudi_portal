import {Component, ElementRef, Input} from '@angular/core';
import * as d3 from 'd3';
import {DEFAULT_HEIGHT, TpbcDataInterface} from '../../../core/services/selfdata-dataset/tpbcData.interface';
import {ALL_TYPES} from '../../../shared/models/title-icon-type';
import {IconRegistryService} from '../../../core/services/icon-registry.service';
import {MARGIN, WINDOWS_HEIGHT, WINDOWS_WIDTH} from '../../../core/services/selfdata-dataset/barchart.service';

@Component({
    selector: 'app-d3-bar-chart',
    templateUrl: './d3-bar-chart.component.html',
    styleUrls: ['./d3-bar-chart.component.scss']
})
export class D3BarChartComponent {
    @Input() graphBar: TpbcDataInterface;

    private margin = MARGIN;
    private w = WINDOWS_WIDTH;
    private h = WINDOWS_HEIGHT;
    private width: number;
    private height = this.h - this.margin.top - this.margin.bottom;

    private x0: any;
    private x1: any;
    private y: any;
    private color;
    private barNames;
    private keys: any;
    private svg: any;
    private chart: any;
    private xAxis: any;
    private yAxis: any;

    constructor(iconRegistryService: IconRegistryService,
                private container: ElementRef) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    ngOnInit() {
        this.width = (DEFAULT_HEIGHT - this.margin.left - this.margin.right) * this.graphBar.xLabels.length / 11;
        this.keys = Object.keys(this.graphBar.groupedModels[0])[0];
        this.color = d3
            .scaleOrdinal()
            .range(this.graphBar.colors);
        this.initScales();
        this.initSvg();
        this.drawChart();
        this.drawAxis();
    }

    private initScales() {
        this.x0 = d3
            .scaleBand()
            .domain(this.graphBar.groupedModels.map((d) => d[this.keys]))
            .rangeRound([0, this.width])
            .padding(0.05);

        this.x1 = d3
            .scaleBand()
            .domain(this.keys)
            .rangeRound([0, this.x0.bandwidth()])
            .padding(0.05);

        this.y = d3
            .scaleLinear()
            .range([this.height, 0])
            .domain([0, 100]).nice();
    }

    private initSvg() {
        this.svg = d3
            .select(this.container.nativeElement)
            .select('.chart-container')
            .append('svg')
            .attr('preserveAspectRatio', 'xMinYMin meet')
            .attr('class', 'chart')
            .attr('height', this.h)
            .attr(
                'viewBox',
                `0 0 ${this.width + this.margin.left + this.margin.right} ${this.height +
                this.margin.top +
                this.margin.bottom}`
            );

        this.chart = this.svg
            .append('g')
            .classed('chart-contents', true)
            .attr(
                'transform',
                'translate(' + this.margin.left + ',' + this.margin.top + ')'
            );
    }

    private drawAxis() {
        this.xAxis = d3.axisBottom(this.x0).ticks(5);
        this.yAxis = d3
            .axisLeft(this.y)
            .ticks(7);

        this.chart
            .append('g')
            .attr('class', 'x axis')
            .attr('transform', 'translate(0,' + this.height + ')')
            .call(this.xAxis);

        this.chart
            .append('g')
            .attr('class', 'y axis')
            .call(this.yAxis)
            .append('text')
            .attr('transform', 'rotate(-90)')
            .attr('y', 6)
            .attr('dy', '.71em');
        let borderRadiusX = 20;

        let borderRadiusY = 20;

        let state = this.chart
            .selectAll('.state')
            .data(this.graphBar.groupedModels)
            .enter()
            .append('g')
            .attr('class', 'state')
            .attr('transform', (d) => {
                return 'translate(' + this.x0(d[this.keys]) + ',0)';
            });

        state
            .selectAll('rect')
            .data((d) => {
                return d.element;
            })
            .enter()
            .append('rect')
            .attr('width', this.x1.bandwidth())
            .attr('x', (d) => {
                return this.x1(d.name);
            })
            .attr('y', (d) => {
                return this.y(d.value || 0);
            })
            .attr('height', (d) => {
                return this.height - this.y(d.value || 0);
            })
            .style('fill', (d) => {
                return this.color(d.name);
            })
            .attr({ry: borderRadiusX, rx: borderRadiusY});
    }

    private drawChart() {
        this.barNames = Object.keys(this.graphBar.groupedModels[0]).filter((key) => {
            return key;
        });

        this.graphBar.groupedModels.forEach((d: any) => {
            d.element = this.barNames.map((name) => {
                return {name: name, value: +d[name]};
            });
        });

        this.x0.domain(
            this.graphBar.groupedModels.map((d: any) => {
                return d[this.keys];
            })
        );

        this.x1.domain(this.barNames).rangeRound([0, this.x0.bandwidth()]);

        this.y.domain([
            0,
            d3.max(this.graphBar.groupedModels, function(d: any) {
                return d3.max(d.element, function(d: any) {
                    return d.value;
                });
            }),
        ]);
    }
}
