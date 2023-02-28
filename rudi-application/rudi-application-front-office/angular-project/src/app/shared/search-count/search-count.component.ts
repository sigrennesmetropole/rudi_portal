import {Component, Input, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

const DEFAULT_TEXT_RESULT = 'searchbox.results';

@Component({
    selector: 'app-search-count',
    templateUrl: './search-count.component.html',
    styleUrls: ['./search-count.component.scss']
})
export class SearchCountComponent implements OnInit {

    @Input()
    searchIsRunning = true;

    @Input()
    count = 0;

    @Input()
    noResultMessage: string;

    @Input()
    hasLink = false;

    @Input()
    routerLink: string;

    @Input()
    hyperLink: string;

    @Input()
    resultMessage = DEFAULT_TEXT_RESULT;

    constructor(
        private readonly translateService: TranslateService,
    ) {
    }

    ngOnInit(): void {
        if (!this.noResultMessage) {
            this.translateService.get('searchbox.noResult').subscribe(noResultMessage => this.noResultMessage = noResultMessage);
        }
    }

    get isDefaultText(): boolean {
        return this.resultMessage === DEFAULT_TEXT_RESULT;
    }

}
