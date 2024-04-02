// Source : https://stackoverflow.com/a/47594193

import {MatPaginatorIntl} from '@angular/material/paginator';
import {TranslateService} from '@ngx-translate/core';
import {TRANSLATE_SERVICE_IS_READY$} from 'src/app/app-initializer-factory';

export class TranslatedMatPaginatorIntl extends MatPaginatorIntl {

    constructor(
        private readonly translateService: TranslateService,
    ) {
        super();
        TRANSLATE_SERVICE_IS_READY$.subscribe(() => {
            this.itemsPerPageLabel = this.translateService.instant('paginator.ITEMS_PER_PAGE_LABEL') + ' :';
            this.nextPageLabel = this.translateService.instant('paginator.NEXT_PAGE_LABEL');
            this.previousPageLabel = this.translateService.instant('paginator.PREVIOUS_PAGE_LABEL');
            this.getRangeLabel = (page: number, pageSize: number, length: number): string => {
                const pageLabel = this.translateService.instant('paginator.page');
                const ofLabel = this.translateService.instant('paginator.of');
                if (length <= 0 || pageSize === 0) {
                    return `-`;
                }

                const lastPage = Math.ceil(length / pageSize);

                return `${pageLabel} ${page + 1} ${ofLabel} ${lastPage}`;
            };
            this.changes.next();
        });

    }

}
