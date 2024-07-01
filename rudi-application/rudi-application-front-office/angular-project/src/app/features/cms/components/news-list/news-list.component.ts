import {Component, Input, OnInit} from '@angular/core';
import {SafeHtml} from '@angular/platform-browser';
import {DEFAULT_PROJECT_ORDER} from '@core/services/asset/project/projekt-metier.service';
import {BreakpointObserverService, MediaSize} from '@core/services/breakpoint-observer.service';


const FIRST_PAGE = 1;

@Component({
  selector: 'cms-news-list',
  templateUrl: './news-list.component.html',
  styleUrl: './news-list.component.scss'
})
export class NewsListComponent implements OnInit {

    mediaSize: MediaSize;
    searchIsRunning = true;
    order = DEFAULT_PROJECT_ORDER;

    @Input() disableScrollOnPageChange = false;
    @Input() displayComponent = false;
    @Input() newsList: SafeHtml[] = [];
    @Input() maxResultsPerPage = 4;
    @Input() title1 = '';
    @Input() title2 = '';
    @Input() newsListTotal = 0;

    allPage = true;
    isTransparent = false;

    @Input() currentPage = FIRST_PAGE;
    public isLoading = false;
    constructor(private readonly breakpointObserver: BreakpointObserverService) {
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
        this.page = FIRST_PAGE;
    }

    get page(): number {
        return this.currentPage;
    }

    set page(value: number) {
        if (value < FIRST_PAGE) {
            value = FIRST_PAGE;
        }
        this.currentPage = value;
    }

}
