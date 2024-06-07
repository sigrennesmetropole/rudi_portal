import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Order, ORDERS} from '@core/services/konsult-metier.service';
import {Item} from '@features/data-set/components/filter-forms/array-filter-form.component';
import {TranslateService} from '@ngx-translate/core';

interface OrderItem extends Item {
    libelle: string;
    value: Order;
}
@Component({
  selector: 'cms-order',
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss'
})
export class CmsOrderComponent implements OnInit {

    @Input() orders: Order[] = ORDERS;

    @Input() items: OrderItem[] = [];
    menuIsOpened = false;

    @Input() order!: Order;
    @Output() orderChange = new EventEmitter<string>();

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

    ngOnInit(): void {
    }

    toggleMenu(): void {
        this.menuIsOpened = !this.menuIsOpened;
    }
}
