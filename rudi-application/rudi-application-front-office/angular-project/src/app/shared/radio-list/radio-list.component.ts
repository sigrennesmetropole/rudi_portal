import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {RadioListItem} from './radio-list-item';

/**
 * Composant générique de listes de suggestions radio bindées sur FormControl
 */
@Component({
    selector: 'app-radio-list',
    templateUrl: './radio-list.component.html',
    styleUrls: ['./radio-list.component.scss']
})
export class RadioListComponent {

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
}
