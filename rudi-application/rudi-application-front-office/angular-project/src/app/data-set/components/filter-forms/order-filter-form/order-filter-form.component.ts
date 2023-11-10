import {Component, Input, OnInit} from '@angular/core';
import {FilterFormComponent} from '../filter-form.component';
import {OrderFilter, OrderValue} from '../../../../core/services/filters/order-filter';
import {FiltersService} from '../../../../core/services/filters.service';
import {AbstractControl, FormControl, FormGroup, Validators} from '@angular/forms';
import {Item} from '../array-filter-form.component';
import {TranslateService} from '@ngx-translate/core';
import {switchMap} from 'rxjs/operators';
import {forkJoin, Observable, of} from 'rxjs';

export interface OrderItem extends Item {
    name: string;
    value: OrderValue;
}

const DEFAULT_ORDER: OrderValue = '-dataset_dates.updated';

@Component({
    selector: 'app-order-filter-form',
    templateUrl: './order-filter-form.component.html',
    styleUrls: ['./order-filter-form.component.scss']
})
export class OrderFilterFormComponent extends FilterFormComponent<string, OrderFilter, OrderItem> implements OnInit {
    items?: OrderItem[];

    constructor(
        protected readonly filtersService: FiltersService,
        protected readonly translateService: TranslateService
    ) {
        super(filtersService);
        this.initFormGroup();
    }

    ngOnInit(): void {
        super.ngOnInit();
        this.order = DEFAULT_ORDER;
    }

    @Input() set values(values: OrderValue[] | undefined) {
        if (values) {
            const observableItems: Observable<OrderItem>[] = values.map(value => {
                const i18nKey = OrderFilterFormComponent.i18KeyFor(value);
                return this.translateService.get(i18nKey).pipe(
                    switchMap(translatedOrderName => {
                        return of({
                            name: translatedOrderName,
                            value
                        });
                    })
                );
            });
            forkJoin(observableItems).subscribe(items => {
                this.items = items;
                this.initFormGroup();
            });
        }
    }

    get selectedItems(): OrderItem[] {
        if (this.items) {
            return this.items.filter(item => this.valueIsSelected(item.value));
        } else {
            return [];
        }
    }

    private get control(): AbstractControl {
        return this.formGroup.get('sortFormControl');
    }

    private static i18KeyFor(value: OrderValue): string {
        return `sortBox.${value}`;
    }

    revert(): void {
        if (this.formGroup) {
            this.control.patchValue(this.order);
        }
    }

    get order(): OrderValue {
        return this.filter.value;
    }

    set order(order: OrderValue) {
        this.filter.value = order;
    }

    valueIsSelected(value: OrderValue): boolean {
        return value === this.order;
    }

    isSelected(item: OrderItem): boolean {
        return this.valueIsSelected(item.value);
    }

    protected buildFormGroup(): FormGroup {
        return new FormGroup({
            sortFormControl: new FormControl(null, Validators.required),
        });
    }

    protected count(value: string): number {
        return value ? 1 : 0;
    }

    protected getFilterFrom(filtersService: FiltersService): OrderFilter {
        return filtersService.orderFilter;
    }

    protected getValueFromFormGroup(): string {
        return this.control.value;
    }
}
