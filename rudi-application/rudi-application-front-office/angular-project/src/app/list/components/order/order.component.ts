import {Component} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {FiltersService} from '../../../core/services/filters.service';
import {OrderFilterFormComponent} from '../filter-forms/order-filter-form/order-filter-form.component';

@Component({
    selector: 'app-order',
    templateUrl: './order.component.html',
    styleUrls: ['./order.component.scss']
})
export class OrderComponent extends OrderFilterFormComponent {

    menuIsOpened = false;

    constructor(
        protected readonly filtersService: FiltersService,
        protected readonly translateService: TranslateService
    ) {
        super(filtersService, translateService);
    }

    toggleMenu(): void {
        this.menuIsOpened = !this.menuIsOpened;
    }
}
