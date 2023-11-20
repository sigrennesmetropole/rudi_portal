import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {Order, OrderItem} from '@app/organization/components/order/type';

const LIST_ORDER: OrderItem[]  = [
    {libelle:'sortBox.producer.organization_name', order:'name'},
    {libelle:'sortBox.-producer.organization_name', order:'-name'},
    {libelle:'sortBox.dataset_dates.updated', order:'openingDate'},
    {libelle:'sortBox.-dataset_dates.updated', order:'-openingDate'},
];

@Component({
    selector: 'app-order',
    templateUrl: './order.component.html',
    styleUrls: ['./order.component.scss'],
})
export class OrderComponent {
    listOrder: OrderItem[];
    menuIsOpened: boolean;
    selectedItem: OrderItem;

    @Output() orderChangeEvent: EventEmitter<Order>;

    constructor() {
        this.orderChangeEvent = new EventEmitter();
        this.menuIsOpened = false;
        this.listOrder = LIST_ORDER;
        this.selectedItem = this.listOrder[3];
    }
    onSelectedItemChange($event:OrderItem){
        this.selectedItem = $event;
        this.orderChangeEvent.emit($event.order);
    }
    toggleMenu(): void {
        this.menuIsOpened = !this.menuIsOpened;
    }
}
