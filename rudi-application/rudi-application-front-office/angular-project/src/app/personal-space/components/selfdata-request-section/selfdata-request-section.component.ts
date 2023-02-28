import {Component, EventEmitter, Input, Output} from '@angular/core';


@Component({
    selector: 'app-selfdata-request-section',
    templateUrl: './selfdata-request-section.component.html',
    styleUrls: ['./selfdata-request-section.component.scss']
})
export class SelfdataRequestSectionComponent {
    @Input() title: string;
    @Input() subTitle: string;
    @Input() description: string;
    @Input() creationLabel: string;

    @Input() hasRequest: boolean;
    @Input() functionalStatus: string;

    @Input() isNotApplicable = false;
    @Input() nonApplicableReasons: string[];
    @Input() disableButton = false;
    @Output() requestDetailsClicked: EventEmitter<void> = new EventEmitter<void>();
    @Output() createRequestClicked: EventEmitter<void> = new EventEmitter<void>();

    constructor() {
    }

    goToRequestDetails(): void {
        this.requestDetailsClicked.emit();
    }

    handleClickButton(): void {
        this.createRequestClicked.emit();
    }
}
