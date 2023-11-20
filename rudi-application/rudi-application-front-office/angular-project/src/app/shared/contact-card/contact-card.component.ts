import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-contact-card',
    templateUrl: './contact-card.component.html',
    styleUrls: ['./contact-card.component.scss']
})
export class ContactCardComponent {

    /**
     * Variable à copier
     */
    @Input() toCopy: string;

    /**
     * affiche l'oeil de visibilité si true ( input de type paswd )
     */
    @Input() masked: boolean;

    /**
     * l'email à copier
     */
    @Input() email: string;

    /**
     * copied-button ou contact-button ?
     */
    @Input() copiedButton: boolean;
}
