import {Injectable} from '@angular/core';
import {LanguageService} from '@core/i18n/language.service';
import {DictionaryEntry} from 'micro_service_modules/api-kaccess';
import {BarChartData, BarChartValues, BarModel, GroupedBarModel, LineAndPlot, Serie, TpbcDataInterface} from './tpbcData.interface';

export const DEFAULT_COLOURS = ['#ff8d6d', '#004680', '#498100', '#00D7D2', '#313c53', '#7BD500', '#98abc5', '#8a89a6'];
export const MARGIN = {top: 40, right: 20, bottom: 50, left: 40};
export const WINDOWS_WIDTH = 400;
export const WINDOWS_HEIGHT = 400;

@Injectable({
    providedIn: 'root'
})
export class BarchartService {

    public colours = DEFAULT_COLOURS;

    constructor(
        private readonly languageService: LanguageService
    ) {
    }

    getLabel(dictionaryEntries: DictionaryEntry[]): string {
        return this.languageService.getTextForCurrentLanguage(dictionaryEntries);
    }

    /**
     * méthode qui transforme barchart en une donnée affichable pour les graphes.
     * @param barChartData les données à afficher
     */
    getDataGraph(barChartData: BarChartData): TpbcDataInterface {
        const graph: TpbcDataInterface = {};
        const series: Serie[] = [];
        const dataBars: BarModel[] = [];
        const xLabels: string[] = [];
        const lineAndPlots: LineAndPlot[] = [];
        const colors: string[] = [];
        this.fillXlabels(barChartData, xLabels, dataBars);
        barChartData.values.forEach((v, i) => {
            colors.push(this.getColour(v.colour, i));
            series.push({name: this.getLabel(v.legend), color: this.getColour(v.colour, i)});
            const lineAndPlot: LineAndPlot = {color: this.getColour(v.colour, i), values: []};
            this.fillYlabels(v, dataBars, lineAndPlot, xLabels, series, i);
            lineAndPlots.push(lineAndPlot);
        });

        graph.title = this.getLabel(barChartData.legend);
        graph.type = barChartData.type;
        graph.legendXAxis = this.getLabel(barChartData.legendXAxis);
        graph.legendYAxis = this.getLabel(barChartData.legendYAxis);
        graph.series = series;
        graph.xLabels = xLabels;
        graph.barModels = dataBars;
        graph.groupedModels = this.getDataBarPlot(dataBars);

        graph.lineAndPlot = lineAndPlots;
        graph.colors = colors;
        return graph;
    }

    private fillYlabels(v: BarChartValues, dataBars: BarModel[], lineAndPlot: LineAndPlot, xLabels: string[],
                        series: Serie[], i: number): void {
        let k = 0;
        v.values.forEach((value, j) => {
            dataBars[j].coordonateY.push(value);
            if (value != null) {
                lineAndPlot.values.push({coordonateX: xLabels[k], coordonateY: value, color: series[i].color});
            }
            k = k + 1;
        });

    }

    private fillXlabels(barChartData: BarChartData, xLabels: string[], dataBars: BarModel[]): void {
        barChartData.series.forEach((s) => {
            xLabels.push(this.getLabel(s.legend));
            dataBars.push({coordonateX: this.getLabel(s.legend), coordonateY: []});
        });
    }

    private getDataBarPlot(seriesBar: BarModel[]): GroupedBarModel[] {
        const dataBarPlots = [];
        seriesBar.forEach((field) => {
            const merged = {
                ...{x: field.coordonateX}, ...field.coordonateY.reduce((accumulator, value, index) => {
                    return {...accumulator, ['y' + index]: value};
                }, {})
            };
            dataBarPlots.push(merged);
        });
        return dataBarPlots;
    }

    getColour(colour: string, index: number): string {
        let colourToDisplay: string;
        if (colour !== null && colour !== undefined) {
            colourToDisplay = colour;
        } else {

            colourToDisplay = this.colours[index];
        }
        return colourToDisplay;
    }
}
