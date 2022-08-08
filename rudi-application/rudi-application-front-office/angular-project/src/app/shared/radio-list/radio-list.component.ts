import {Component, Input, OnInit} from '@angular/core';
import {RadioListItem} from './radio-list-item';
import {FormGroup} from '@angular/forms';

/**
 * Composant générique de listes de suggestions radio bindées sur FormControl
 */
@Component({
    selector: 'app-radio-list',
    templateUrl: './radio-list.component.html',
    styleUrls: ['./radio-list.component.scss']
})
export class RadioListComponent implements OnInit {

    /**
     * La liste des suggestions du groupe de boutons radio
     */
    @Input()
    public suggestions: RadioListItem[];

    /**
     * FormGroup contenant le contrôle de la valeur choisie
     */
    @Input()
    public formGroup: FormGroup;

    /**
     * Le nom du contrôle du formgroup
     */
    @Input()
    public controlName: string;

    constructor() {
    }

    ngOnInit(): void {
    }

}
