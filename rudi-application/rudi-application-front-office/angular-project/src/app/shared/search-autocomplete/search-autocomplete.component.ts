import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormControl} from '@angular/forms';
import {SearchAutocompleteItem} from './search-autocomplete-item.interface';
import {debounceTime, filter, map, tap} from 'rxjs/operators';

@Component({
    selector: 'app-search-autocomplete',
    templateUrl: './search-autocomplete.component.html',
    styleUrls: ['./search-autocomplete.component.scss']
})
export class SearchAutocompleteComponent<T> implements OnInit {

    formControl: FormControl;
    selected: SearchAutocompleteItem<T>;

    @Output()
    searchTriggered: EventEmitter<string> = new EventEmitter<string>();

    @Output()
    itemSelected: EventEmitter<T> = new EventEmitter<T>();

    @Input()
    placeholder: string;

    @Input()
    autocompleteItems: SearchAutocompleteItem<T>[];

    @Input()
    loading = false;

    @Input()
    minimumAutocompleteLength = 3;

    constructor(
        private readonly formBuilder: FormBuilder,
    ) {
    }

    ngOnInit(): void {
        this.formControl = this.formBuilder.control('', {updateOn: 'change'});
        this.setAutocompleteEvent();
    }

    setSelectedItem(selected: SearchAutocompleteItem<T> | undefined): void {
        if (this.selected === selected) {
            return;
        }

        this.selected = selected;
        if (this.selected != null) {
            this.formControl.setValue(this.selected.label);
            this.itemSelected.emit(this.selected.value);
        }
    }

    private setAutocompleteEvent(): void {
        this.formControl.valueChanges.pipe(
            // quelque chose de tapé dans la recherche ? il n'y a plus de suggestions
            tap(() => {
                this.autocompleteItems = [];
            }),
            // Récupération de la valeur sous forme de chaîne de caractère > 3
            filter(inputValue => typeof inputValue === 'string'),
            map(userInput => userInput as string),
            filter(userInput => userInput != null && userInput.length > this.minimumAutocompleteLength),
            // Ce qui est dans le champ est déjà ce qui a été choisi ? on ne fait rien
            filter(userInput => this.selected != null ? userInput !== this.selected.label : true),
            // On ne déclenche pas le traitement de recherche instantanément
            debounceTime(500)
        ).subscribe({
            next: (userInput: string) => {
                this.searchTriggered.emit(userInput);
            }
        });
    }
}
