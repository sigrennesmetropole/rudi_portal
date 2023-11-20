import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-dataset-button',
    templateUrl: './data-set-button.component.html',
    styleUrls: ['./data-set-button.component.scss']
})
export class DataSetButtonComponent {
    @Input()
    public buttonTitle: string;
}
