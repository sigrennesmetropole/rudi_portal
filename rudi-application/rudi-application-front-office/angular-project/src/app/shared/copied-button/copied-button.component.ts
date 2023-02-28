import {Component, Input, OnInit} from '@angular/core';
import {Clipboard} from '@angular/cdk/clipboard';

/**
 * Composant génerique du boutton copy avec/sans icon visibilité
 */
@Component({
    selector: 'app-copied-button',
    templateUrl: './copied-button.component.html',
    styleUrls: ['./copied-button.component.scss']
})
export class CopiedButtonComponent implements OnInit {

    /**
     * Variable à copier
     */
    @Input() toCopy: string;

    /**
     * affiche ou non le password
     */
    hidePassword: boolean;

    /**
     * affiche l'oeil de visibilité si true ( input de type paswd )
     */
    @Input() masked: boolean;


    /**
     * booléen traduisant l'etat de la copie
     */
    copiedSuccess = false;

    constructor(private readonly clipboard: Clipboard) {
    }

    ngOnInit(): void {
        this.hidePassword = this.masked;
    }


    /**
     * Méthode qui copie au click du button
     */
    clickOnButton(): void {
        this.copiedSuccess = true;
        this.clipboard.copy(this.toCopy);
    }

    /**
     * Affiche le contenu de l'input en TITLE uniquement quand la valeur à copier est affichée en clair
     */
    getInputTitle(): string {
        if (!this.hidePassword) {
            return this.toCopy;
        }

        return '';
    }
}
