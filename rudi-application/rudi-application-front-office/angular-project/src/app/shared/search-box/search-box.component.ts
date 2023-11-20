import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {MediaSize} from '@core/services/breakpoint-observer.service';

const EMPTY_SEARCH = '';

@Component({
    selector: 'app-search-box',
    templateUrl: './search-box.component.html',
    styleUrls: ['./search-box.component.scss']
})
export class SearchBoxComponent {
    @Input() mediaSize: MediaSize;
    @Input() libelle: string;

    @Output() searchTermsEmitter: EventEmitter<string>;
    searchTerms = EMPTY_SEARCH;
    constructor(
        iconRegistry: MatIconRegistry,
        sanitizer: DomSanitizer
    ) {
        iconRegistry.addSvgIcon('filter-icon', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/filter-icon.svg'));
        iconRegistry.addSvgIcon('search', sanitizer.bypassSecurityTrustResourceUrl('assets/icons/search.svg'));
        this.searchTermsEmitter = new EventEmitter<string>();
    }

    onChanges(): void {
        this.searchTermsEmitter.emit(this.searchTerms);
    }
    onReset(): void {
        this.searchTerms = EMPTY_SEARCH;
        this.onChanges();
    }
}
