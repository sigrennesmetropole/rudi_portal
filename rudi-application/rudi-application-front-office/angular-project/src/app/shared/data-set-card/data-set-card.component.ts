import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {Router} from '@angular/router';
import {LanguageService} from '@core/i18n/language.service';
import {BreakpointObserverService, MediaSize, NgClassObject} from '@core/services/breakpoint-observer.service';
import {URIComponentCodec} from '@core/services/codecs/uri-component-codec';
import {ThemeCacheService} from '@core/services/theme-cache.service';
import {Metadata} from 'micro_service_modules/api-kaccess';
import {MetadataUtils} from '../utils/metadata-utils';

@Component({
    selector: 'app-data-set-card',
    templateUrl: './data-set-card.component.html',
    styleUrls: ['./data-set-card.component.scss']
})
export class DataSetCardComponent implements OnInit {
    @Input() metadata: Metadata;
    mediaSize: MediaSize;
    @Input() isSelectable = false;
    isSelected = false;
    isSingleClick = true;
    @Output() selectMetadata = new EventEmitter<Metadata>();
    @Output() dbSelectMetadata = new EventEmitter<Metadata>();

    constructor(
        private readonly themeCacheService: ThemeCacheService,
        private readonly breakpointObserver: BreakpointObserverService,
        private readonly languageService: LanguageService,
        private readonly uriComponentCodec: URIComponentCodec,
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
        this.matIconRegistry.addSvgIcon(
            'top-right-triangle-self-data',
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/icons/top-right-triangle-self-data.svg')
        );
        this.matIconRegistry.addSvgIcon(
            'self-data-icon',
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/icons/self-data-icon.svg')
        );

        themeCacheService.init();
    }

    get themePicto(): string {
        return this.metadata.theme;
    }

    get themeLabel(): string {
        return this.themeCacheService.getThemeLabelFor(this.metadata);
    }

    get ngClass(): NgClassObject {
        const ngClassFromMediaSize: NgClassObject = this.breakpointObserver.getNgClassFromMediaSize('data-set-card');
        return {
            ...ngClassFromMediaSize,
            restricted: this.isRestricted,
            selfdata: this.isSelfdata
        };
    }

    get titleMaxLength(): number {
        return 60;
    }

    get descriptionMaxLength(): number {
        return 200;
    }

    get isRestricted(): boolean {
        return MetadataUtils.isRestricted(this.metadata);
    }

    get isSelfdata(): boolean {
        return MetadataUtils.isSelfdata(this.metadata);
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
        if (!this.isSelectable && this.metadata.global_id && this.metadata.resource_title) {
            this.router.navigate(['/catalogue/detail/' + this.metadata.global_id + '/' + this.uriComponentCodec.normalizeString(this.metadata.resource_title)]);
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
