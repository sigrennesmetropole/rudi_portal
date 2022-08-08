import {Component, Input, OnInit} from '@angular/core';

@Component({
    selector: 'app-dataset-button',
    templateUrl: './data-set-button.component.html',
    styleUrls: ['./data-set-button.component.scss']
})
export class DataSetButtonComponent implements OnInit {
    @Input()
    public buttonTitle: string;

    constructor() {
    }

    ngOnInit(): void {
    }

}
