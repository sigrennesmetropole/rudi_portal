import {Component, Input, OnInit} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {BreakpointObserverService, MediaSize, NgClassObject} from '../../../core/services/breakpoint-observer.service';
import {Metadata} from '../../../api-kaccess';
import {LanguageService} from '../../../i18n/language.service';

@Component({
    selector: 'app-data-set-card',
    templateUrl: './data-set-card.component.html',
    styleUrls: ['./data-set-card.component.scss']
})
export class DataSetCardComponent implements OnInit {
    @Input() metadata: Metadata;
    mediaSize: MediaSize;
    @Input() themeLabel: string;

    constructor(
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly languageService: LanguageService,
        private matIconRegistry: MatIconRegistry,
        private domSanitizer: DomSanitizer,
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
        if (this.mediaSize.isLg || this.mediaSize.isSm || this.mediaSize.isXs) {
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

}
