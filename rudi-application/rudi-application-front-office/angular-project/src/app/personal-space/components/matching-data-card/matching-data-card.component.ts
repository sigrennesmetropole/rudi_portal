import {Component, Input, OnInit} from '@angular/core';
import {MatchingData} from '../../../selfdata/selfdata-api';
import {SelfdataAttachmentService} from '../../../core/services/selfdata-attachment.service';

@Component({
    selector: 'app-matching-data-card',
    templateUrl: './matching-data-card.component.html',
    styleUrls: ['./matching-data-card.component.scss']
})
export class MatchingDataCardComponent implements OnInit {
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

    ngOnInit(): void {
    }
}
