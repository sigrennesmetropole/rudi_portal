import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Item} from '@features/data-set/components/filter-forms/array-filter-form.component';
import {TranslateService} from '@ngx-translate/core';

interface OrderItem extends Item {
    libelle: string;
    value: string;
}

@Component({
    selector: 'cms-order',
    templateUrl: './order.component.html',
    styleUrl: './order.component.scss'
})
export class CmsOrderComponent {

    @Input() items: OrderItem[] = [];
    @Output() orderChange = new EventEmitter<string>();
    menuIsOpened = false;

    constructor(
        protected readonly translateService: TranslateService
    ) {
    }

    private _selectedItem: OrderItem;

    get selectedItem(): OrderItem {
        return this._selectedItem;
    }

    set selectedItem(item: OrderItem) {
        this._selectedItem = item;
        this.orderChange.emit(item.value);
    }

    toggleMenu(): void {
        this.menuIsOpened = !this.menuIsOpened;
    }
}
