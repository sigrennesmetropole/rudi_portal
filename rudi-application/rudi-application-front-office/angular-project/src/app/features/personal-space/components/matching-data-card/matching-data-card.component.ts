import {Component, Input} from '@angular/core';
import {SelfdataAttachmentService} from '@core/services/selfdata-attachment.service';
import {MatchingData} from 'micro_service_modules/selfdata/selfdata-api';

@Component({
    selector: 'app-matching-data-card',
    templateUrl: './matching-data-card.component.html',
    styleUrls: ['./matching-data-card.component.scss']
})
export class MatchingDataCardComponent {
    @Input() isDataTabEmpty: boolean;
    @Input() subscriptionSucced: boolean;
    @Input() data: MatchingData[];
    @Input() matchingDataLoading = false;
    title: string;

    get showMatchingDataCard(): boolean {
        return this.subscriptionSucced && !this.isDataTabEmpty;
    }

    constructor(
        private readonly selfdataAttachmentService: SelfdataAttachmentService) {
    }
}
