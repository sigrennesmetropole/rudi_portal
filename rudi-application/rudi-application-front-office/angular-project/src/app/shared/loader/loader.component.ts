import {Component, Input, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';

/**
 * Classe représentant le loader de l'application RUDI
 */
@Component({
    selector: 'app-loader',
    templateUrl: './loader.component.html',
    styleUrls: ['./loader.component.scss']
})
export class LoaderComponent implements OnInit {
    /**
     * Active désactive le loader en fonction de la valeur
     */
    @Input() active = false;

    /**
     * Indique si le loader est blanc ou noir
     * défaut : NOIR
     * @type {boolean}
     */
    @Input() isLight = false;

    /**
     * Indique si le background du loader est transparent
     * défaut : NON on choisit une couleur light ou dark
     */
    @Input() isTransparent = false;

    /**
     * Indique si le loader doit prendre toute la page
     * ou non, défaut oui
     * @type {boolean}
     */
    @Input() allPage = true;

    /**
     * On veut pas le texte juste le logo
     */
    @Input() noText = false;

    /**
     * Texte éventuel affiché sous le loader
     */
        // description = 'Chargement...';
    description: string;


    constructor(private translate: TranslateService) {
    }

    ngOnInit(): void {
        this.translate.get('common.loaderDescription').subscribe(
            () => {
                this.description = this.translate.instant('common.loaderDescription');
            }
        );

    }

    /**
     * Permet de savoir si le loader a un texte à afficher
     * @return {boolean}
     */
    public isWithText(): boolean {

        if (this.noText === true) {
            return false;
        }

        return this.description != null && this.description !== '';
    }

    /**
     * Récupère la classe CSS light ou dark en fonction
     * du booléen d'état lightClass
     * @return {boolean}
     */
    public getCssClass(): string {
        if (this.isLight && !this.isTransparent) {
            return 'light';
        } else if (!this.isLight && !this.isTransparent) {
            return 'dark';
        }

        return 'transparent';
    }

    /**
     * Permet de savoir en temps réel s'il faut prendre toute la largeur de l'écran
     * @return {string}
     */
    public getAllPageClass(): string {
        return this.allPage ? 'all-page-loading' : '';
    }
}
