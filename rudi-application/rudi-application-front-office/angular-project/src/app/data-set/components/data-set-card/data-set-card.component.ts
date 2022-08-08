import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {BreakpointObserverService, MediaSize, NgClassObject} from '../../../core/services/breakpoint-observer.service';
import {Metadata} from '../../../api-kaccess';
import {LanguageService} from '../../../i18n/language.service';
import {Router} from '@angular/router';

@Component({
    selector: 'app-data-set-card',
    templateUrl: './data-set-card.component.html',
    styleUrls: ['./data-set-card.component.scss']
})
export class DataSetCardComponent implements OnInit {
    @Input() metadata: Metadata;
    mediaSize: MediaSize;
    @Input() themeLabel: string;
    @Input() isSelectable = false;
    isSelected = false;
    isSingleClick = true;
    @Output() selectMetadata = new EventEmitter<Metadata>();
    @Output() dbSelectMetadata = new EventEmitter<Metadata>();

    constructor(
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly languageService: LanguageService,
        private matIconRegistry: MatIconRegistry,
        private domSanitizer: DomSanitizer,
        private router: Router,
    ) {
        this.matIconRegistry.addSvgIcon(
            'key_icon_88_blue',
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/icons/key_icon_88_blue.svg')
        );
        this.matIconRegistry.addSvgIcon(
            'top-right-triangle',
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/icons/top-right-triangle.svg')
        );
    }

    private _themePicto: string;

    get themePicto(): string {
        return this._themePicto;
    }

    /** Example 'weather.svg' */
    @Input() set themePicto(themePicto: string) {
        this.matIconRegistry.addSvgIcon(
            themePicto,
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/pictos/' + themePicto)
        );
        this._themePicto = themePicto;
    }

    get ngClass(): NgClassObject {
        const ngClassFromMediaSize: NgClassObject = this.breakpointObserver.getNgClassFromMediaSize('data-set-card');
        return {
            ...ngClassFromMediaSize,
            restricted: this.isRestricted
        };
    }

    get titleMaxLength(): number {
        if (this.mediaSize.isLg || this.mediaSize.isSm) {
            return 60;
        } else if (this.mediaSize.isMd) {
            return 80;
        } else {
            return 70;
        }
    }

    get isRestricted(): boolean {
        return this.metadata.access_condition?.confidentiality?.restricted_access;
    }

    ngOnInit(): void {
        this.mediaSize = this.breakpointObserver.getMediaSize();
    }

    /**
     * Permet de recuperer la description dans metadata
     */
    getSynopsis(item: Metadata): string {
        return this.languageService.getTextForCurrentLanguage(item.synopsis);
    }

    removeAccents(theme: string): string {
        return theme.normalize('NFD').replace(/[\u0300-\u036f]/g, '');
    }

    /**
     * Fonction de permet de naviguer vers le detail d'un jdd
     */
    ifNotSelectableGoToDetail(): void {
        if (!this.isSelectable && this.metadata.global_id) {
            this.router.navigate(['/catalogue/detail/' + this.metadata.global_id]);
        }
    }

    /**
     * Function permettant de verifier si le jdd est selectionnable
     * @param isSelected
     */
    select(isSelected = true): void {
        if (this.isSelectable) {
            this.isSelected = isSelected;
            if (isSelected) {
                this.selectMetadata.emit(this.metadata);
            }
        }
    }

    /**
     * Function SingleClick
     * @param isSelected
     */
    singleClickSelect(isSelected = true): void {
        this.isSingleClick = true;
        setTimeout(() => {
            if (this.isSingleClick) {
                this.select(isSelected);
            }
        }, 250);
    }

    /**
     * Function doubleClick
     * @param isSelected
     */
    doubleClickSelect(isSelected = true): void {
        this.isSingleClick = false;
        this.dbSelectMetadata.emit(this.metadata);
    }


}
