import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Item} from '@features/data-set/components/filter-forms/array-filter-form.component';
import {Order, ORDERS} from '@core/services/asset/project/projekt-metier.service';
import {TranslateService} from '@ngx-translate/core';
import {forkJoin, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

interface OrderItem extends Item {
    translatedLabel: string;
    value: Order;
}

@Component({
    selector: 'app-order',
    templateUrl: './order.component.html',
    styleUrls: ['./order.component.scss'],
})
export class OrderComponent implements OnInit {

    @Input() orders: Order[] = ORDERS;
    @Input() translatePrefix = 'project.orders.';

    items: OrderItem[] = [];
    menuIsOpened = false;

    @Input() order!: Order;
    @Output() orderChange = new EventEmitter<Order>();

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
        this.loadOrderItems();
    }

    toggleMenu(): void {
        this.menuIsOpened = !this.menuIsOpened;
    }

    private loadOrderItems(): void {
        const item$: Observable<OrderItem>[] = this.orders.map(order => {
            const translatedName$ = this.translateService.get(this.translatePrefix + order);
            return translatedName$.pipe(
                map(translatedName => ({
                    translatedLabel: translatedName,
                    value: order
                } as OrderItem))
            );
        });
        forkJoin(item$).subscribe(items => {
            this.items = items;
            this.selectedItem = this.getItemWithValue(this.order);
        });
    }

    private getItemWithValue(value: Order): OrderItem {
        return this.items.find(item => item.value === value);
    }
}
