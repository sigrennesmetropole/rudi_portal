import {Component, EventEmitter, Input, Output} from '@angular/core';

/**
 * Le error-box est une div générique capable d'afficher un message d'erreur et collé un message cliquable qui fait quelque-chose
 */
@Component({
    selector: 'app-error-box',
    templateUrl: './error-box.component.html',
    styleUrls: ['./error-box.component.scss']
})
export class ErrorBoxComponent {

    /**
     * le texte principal de l'erreur, juste visuel
     */
    @Input()
    public text: string;

    /**
     * Le texte "cliquable" qui fait quelque-chose si on clique dessus
     */
    @Input()
    public clickableText: string;

    /**
     * Evenement : j'ai cliqué sur le texte cliquable donc je fais quelque chose
     */
    @Output()
    clickableTextClicked = new EventEmitter<void>();

    /**
     * ai-je un texte de fourni ?
     */
    hasText(): boolean {
        return this.text != null;
    }

    /**
     * ai-je un texte cliquable de fourni ?
     */
    hasClickableText(): boolean {
        return this.clickableText != null;
    }

    /**
     * Emission event quand on clique sur le texte cliquable
     */
    handleClickClickableText(): void {
        this.clickableTextClicked.emit();
    }
}
