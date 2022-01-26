import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Licence, LicenceCustom, LicenceStandard, Media, MediaFile, Metadata} from '../../../api-kaccess';
import {KonsultMetierService} from '../../../core/services/konsult-metier.service';
import {ActivatedRoute} from '@angular/router';
import {BreakpointObserverService, MediaSize} from '../../../core/services/breakpoint-observer.service';
import * as mediaType from '../../../api-kaccess/model/media';
import {FormBuilder, FormGroup} from '@angular/forms';
import {MatMenuTrigger} from '@angular/material/menu';
import saveAs from 'file-saver';
import {HttpResponse} from '@angular/common/http';
import * as mime from 'mime';
import {Observable} from 'rxjs';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {KosMetierService} from '../../../core/services/kos-metier.service';
import {LanguageService} from '../../../i18n/language.service';
import {DetailFunctions} from '../../detail-functions';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {ObservableUtils} from '../../../shared/utils/ObservableUtils';
import MediaTypeEnum = Media.MediaTypeEnum;
import LicenceTypeEnum = Licence.LicenceTypeEnum;


@Component({
    selector: 'app-detail',
    templateUrl: './detail.component.html',
    styleUrls: ['./detail.component.scss']
})
export class DetailComponent implements OnInit {
    @ViewChild('clickMenuFormatTrigger') clickMenuFormatTrigger: MatMenuTrigger;
    @ViewChild('a_export') exportLink: ElementRef;
    form: FormGroup;
    public selection: string;
    metadata: Metadata | undefined;
    uuid?: string | null;
    mediaType = mediaType.Media.MediaTypeEnum;
    mediaSize: MediaSize;
    formatsMenuActive = false;
    mediaDataType = MediaTypeEnum;
    restrictedAccess: boolean;
    licenceLabel;
    conceptUri;
    licenceType = Licence.LicenceTypeEnum;
    themeLabel: string;
    _themePicto: string;
    downloadableMedias: Media[] = [];

    constructor(
        private matIconRegistry: MatIconRegistry,
        private domSanitizer: DomSanitizer,
        private readonly fb: FormBuilder,
        private readonly konsultMetierService: KonsultMetierService,
        private readonly breakpointObserverService: BreakpointObserverService,
        private readonly kosMetierService: KosMetierService,
        private readonly route: ActivatedRoute,
        private readonly languageService: LanguageService,
        private readonly dataSetDetailsFunctions: DetailFunctions,
        private translateService: TranslateService,
        private snackBarService: SnackBarService,
    ) {
        this.mediaSize = this.breakpointObserverService.getMediaSize();
        this.form = this.fb.group({
            options: []
        });
        this.matIconRegistry.addSvgIcon(
            'icon-info',
            this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/icons/icon_info.svg')
        );

    }

    get chosenItem(): Media {
        return this.form.controls.options.value;
    }

    set chosenItem(chosenItem: Media) {
        this.form.setValue({
            options: chosenItem || null
        });
    }

    ngOnInit(): void {
        this.uuid = this.route.snapshot.params.uuid;
        if (this.uuid) {
            this.konsultMetierService.getMetadataByUuid(this.uuid).subscribe(metadata => {
                if (metadata) {
                    this.metadata = metadata;
                    this.restrictedAccess = this.metadata?.access_condition?.confidentiality?.restricted_access;
                    // L'item sélectionné est le premier type FILE de la liste des formats disponibles
                    this.chosenItem = this.metadata.available_formats.filter(f => f.media_type === 'FILE')[0];
                    this.conceptUri = this.getConceptUri();
                    this.licenceLabel = this.getLicenceLabel();

                    this.initDownloadableMedias();
                    const themeCode: string = this.metadata.theme;
                    if (themeCode) {
                        this.kosMetierService.getTheme(themeCode).subscribe({
                            next: themeConcept => {
                                this.themeLabel = themeConcept.text;
                                this.themePicto = KosMetierService.getAssetNameFromConceptIcon(themeConcept.concept_icon);
                            },
                            complete: () => {
                                if (!this.themeLabel) {
                                    this.themeLabel = `[${themeCode}]`;
                                }
                            }
                        });
                    }
                } else {
                    this.uuid = '0';
                }
            });
        }
    }

    /**
     * Fonction permettant de verifier si available_formats n'est pas vide et affiche le media_type
     * @param mediaData
     */
    availableFormat(mediaData: string): boolean {
        const result = this.metadata.available_formats.filter(element =>
            element.media_type === mediaData
        );
        return result.length > 0;

    }

    /**
     * Permet de récupérer le résumé long d'un metadata
     * @param item
     */
    getSummaryDescription(item: Metadata): string {
        return this.languageService.getTextForCurrentLanguage(item.summary);
    }

    /**
     * Fonction permettant de retourner l'extension du fichier
     * @param item
     */
    getMediaFileExtension(item: Media): string {
        const media = item as MediaFile;
        return mime.getExtension(media.file_type);
    }

    /**
     * Function permettant de récupérer le nom du fichier à télécharger
     * @param response
     */
    downLoadFile(response: HttpResponse<Blob>): void {
        const blob = new Blob([response.body], {type: response.body.type});
        const filename = response.headers.get('content-disposition').split(';')[1].split('filename')[1].split('=')[1].trim();
        saveAs(blob, filename);
    }


    /**
     * Fonction permettant de télécharger un fichier suivant son format
     */
    onDownloadFormat(): void {
        const chosenItem = this.chosenItem;
        if (chosenItem) {
            this.konsultMetierService.downloadMetadataMedia(this.metadata.global_id, chosenItem.media_id)
                .subscribe(
                    (response) => {
                        this.downLoadFile(response);
                    },
                    () => {
                        this.snackBarService.add(this.translateService.instant('common.echec'));
                    });
            this.clickMenuFormatTrigger.closeMenu();
        }
    }

    getCustomLicenceLabel(licence: Licence): string {
        return this.dataSetDetailsFunctions.getCustomLicenceLabel(licence);
    }

    getCustomLicenceUri(): string {
        if (this.metadata.access_condition?.licence.licence_type === LicenceTypeEnum.Custom) {
            const licenceCustom = this.metadata.access_condition.licence as LicenceCustom;
            return licenceCustom.custom_licence_uri;
        }
        return '';
    }


    getLicenceLabel(): Observable<string> {
        if (this.metadata.access_condition?.licence.licence_type === LicenceTypeEnum.Standard) {
            const licenceStandard = this.metadata.access_condition.licence as LicenceStandard;
            const licenceCode = licenceStandard.licence_label;
            return this.kosMetierService.getLicenceLabelFromCode(licenceCode);
        }
        return null;
    }


    getConceptUri(): Observable<string> {
        if (this.metadata.access_condition.licence.licence_type === LicenceTypeEnum.Standard) {
            const licenceStandard = this.metadata.access_condition.licence as LicenceStandard;
            const licenceCode = licenceStandard.licence_label;
            return this.kosMetierService.getConceptUriFromCode(licenceCode);
        }
        return null;
    }

    /**
     * Fonction permettant de charger les média téléchargeables
     * @private
     */
    private initDownloadableMedias(): void {
        ObservableUtils
            .filter(this.metadata.available_formats)
            .using(media => this.dataSetDetailsFunctions.canDownloadMedia(media, this.metadata))
            .subscribe(downloadableMedias => this.downloadableMedias = downloadableMedias);
    }


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
}
