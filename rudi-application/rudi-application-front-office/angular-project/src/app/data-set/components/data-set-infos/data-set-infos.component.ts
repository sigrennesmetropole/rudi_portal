import {Component, Input, OnInit} from '@angular/core';
import {
    Licence,
    LicenceCustom,
    LicenceStandard,
    Media,
    MediaFile,
    MediaSeries,
    MediaService,
    Metadata,
    MetadataAccessCondition
} from '../../../api-kaccess';
import {HttpResponse} from '@angular/common/http';
import {KonsultMetierService} from '../../../core/services/konsult-metier.service';
import saveAs from 'file-saver';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import * as mime from 'mime';
import {KindOfData} from '../../../api-kmedia';
import {KosMetierService} from '../../../core/services/kos-metier.service';
import {LanguageService} from '../../../i18n/language.service';
import {Observable} from 'rxjs';
import {DetailFunctions} from '../../pages/detail/detail-functions';
import {MediaSize} from '../../../core/services/breakpoint-observer.service';
import {Level} from '../../../shared/notification-template/notification-template.component';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';
import {GetBackendPropertyPipe} from '../../../shared/get-backend-property.pipe';
import {Clipboard} from '@angular/cdk/clipboard';
import LicenceTypeEnum = Licence.LicenceTypeEnum;
import MediaTypeEnum = Media.MediaTypeEnum;

@Component({
    selector: 'app-data-sets-infos',
    templateUrl: './data-set-infos.component.html',
    styleUrls: ['./data-set-infos.component.scss']
})
export class DataSetInfosComponent implements OnInit {

    readonly DWNL_FORMAT = "DWNL";

    @Input() metadata: Metadata;
    @Input() mediaSize: MediaSize;
    @Input() downloadableMedias: Media[];

    totalMediaTypeFile: number;
    totalMediaTypeService: number;
    totalMediaTypeSeries: number;
    licenceLabel;
    conceptUri;
    // Indique si on affiche le loader pendant le téléchargement du Media
    public isLoading = false;
    panelOpenStateCond: boolean;
    panelOpenStateDate: boolean;
    panelOpenStateStockage = false;
    panelOpenStateLoc: boolean;
    panelOpenStateProvider: boolean;
    expanded: boolean;
    kindOfData: KindOfData;
    mapMediaIndexSeries: Map<number, number> = new Map<number, number>();
    /**
     * La map d'association : Média -> est-ce que j'ai copié son URL dans le presse papier ?
     */
    mapMediaUrlCopied: Map<Media, boolean> = new Map();

    constructor(private readonly konsultMetierService: KonsultMetierService,
                readonly snacbackService: SnackBarService,
                private readonly translateService: TranslateService,
                private readonly kosMetierService: KosMetierService,
                private readonly languageService: LanguageService,
                readonly metaDataFunctions: DetailFunctions,
                private readonly propertiesMetierService: PropertiesMetierService,
                private readonly getBackendPropertyPipe: GetBackendPropertyPipe,
                private readonly clipboard: Clipboard
    ) {
    }

    ngOnInit(): void {
        this.licenceLabel = this.getLicenceLabel();
        this.conceptUri = this.getConceptUri();

        this.metadata.available_formats.sort(this.mediasSortedFunction);

        this.buildMapMediaIndex(this.mapMediaIndexSeries, this.metaDataFunctions.isMediaTypeSeries);
    }

    /**
     * Tri permettant d'afficher les media file avant les media series
     */
    mediasSortedFunction(media1: Media, media2: Media): number {
        if (media1.media_type === media2.media_type) {
            return 0;
        } else if (media1.media_type === MediaTypeEnum.File) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Fonction permettant de retourn le type MediaFile
     */
    getMediaFile(item: Media): MediaFile {
        return item as MediaFile;
    }

    /**
     * Fonction permettant de retourner l'extension du fichier
     */
    getMediaFileExtension(item: Media): string {
        const media = item as MediaFile;
        return ' (' + mime.getExtension(media.file_type) + ')';
    }

    /**
     * Fonction permettant de retourer le type MediaSeries
     */
    getMediaSerie(item: Media): MediaSeries {
        return item as MediaSeries;
    }
    getMediaService(item: Media): MediaService {
        return item as MediaService;
    }

    /**
     * Function permettant de recuperer le nom du fichier à télécharger
     */
    downLoadFile(response: HttpResponse<Blob>): void {
        const blob = new Blob([response.body], {type: response.body.type});
        const filename = response.headers.get('content-disposition').split(';')[1].split('filename')[1].split('=')[1].trim();
        saveAs(blob, filename);
    }


    /**
     * Fonction permettant de télécharger un fichier suivant son format
     */
    onDownloadFile(item: Media): void {
        this.isLoading = true;
        this.konsultMetierService.downloadMetadataMedia(this.metadata.global_id, item.media_id)
            .subscribe(
                (response) => {
                    this.isLoading = false;
                    this.downLoadFile(response);
                },
                () => {
                    this.isLoading = false;
                    const message = this.translateService.instant('common.echec');
                    const linkLabel = this.translateService.instant('snackbarTemplate.ici');
                    this.propertiesMetierService.get('rudidatarennes.contact').subscribe(link => {
                        this.snacbackService.openSnackBar({
                            message: `${message} <a href="${link}">${linkLabel}</a>.`,
                            level: Level.ERROR
                        });
                    });
                }
            );
    }


    /**
     * Fonction permettant de recuperer bibliographical_reference
     */
    getBibliographicalReference(item: MetadataAccessCondition): string | undefined {
        return this.languageService.getTextForCurrentLanguage(item.bibliographical_reference);
    }

    /**
     * Fonction permettant de recuperer mandatory_mention
     */
    getMandatoryMention(item: MetadataAccessCondition): string | undefined {
        return this.languageService.getTextForCurrentLanguage(item.mandatory_mention);
    }

    /**
     * Fonction permettant de recuperer usage_constraint
     */
    getUsageConstraint(item: MetadataAccessCondition): string | undefined {
        return this.languageService.getTextForCurrentLanguage(item.usage_constraint);
    }

    /**
     * Fonction permettant de recuperer access_constraint
     */
    getAccessConstraint(item: MetadataAccessCondition): string | undefined {
        return this.languageService.getTextForCurrentLanguage(item.access_constraint);
    }

    /**
     * Function permettant de récuperer le other_contraint
     */
    getOtherConstraints(item: MetadataAccessCondition): string | undefined {
        return this.languageService.getTextForCurrentLanguage(item.other_constraints);
    }

    /**
     * Fonction permettant de recuperer custom_licence_label
     */
    getCustomLicenceLabel(item: Licence): string | undefined {
        return this.metaDataFunctions.getCustomLicenceLabel(item);
    }

    /**
     * Fonction permettant de verifier si un jdd est téléchargeable
     * @param media
     */
    canDownloadMedia(media: Media): boolean {
        return this.downloadableMedias && this.downloadableMedias.some(downloadableMedia => downloadableMedia.media_id === media.media_id);
    }

    /**
     * Fonction permettant de recuperer CustomLicenceUri
     */
    getCustomLicenceUri(): string {
        if (this.metadata.access_condition.licence.licence_type === LicenceTypeEnum.Custom) {
            const licenceCustom = this.metadata.access_condition.licence as LicenceCustom;
            return licenceCustom.custom_licence_uri;
        }
        return '';
    }

    /**
     * Fonction permettant de recuperer LicenceLabel
     */
    getLicenceLabel(): Observable<string> {
        if (this.metadata.access_condition.licence.licence_type === LicenceTypeEnum.Standard) {
            const licenceStandard = this.metadata.access_condition.licence as LicenceStandard;
            const licenceCode = licenceStandard.licence_label;
            return this.kosMetierService.getLicenceLabelFromCode(licenceCode);
        }
        return null;
    }

    /**
     * Fonction permettant de recuperer ConceptUri
     */
    getConceptUri(): Observable<string> {
        if (this.metadata.access_condition.licence.licence_type === LicenceTypeEnum.Standard) {
            const licenceStandard = this.metadata.access_condition.licence as LicenceStandard;
            const licenceCode = licenceStandard.licence_label;
            return this.kosMetierService.getConceptUriFromCode(licenceCode);
        }
        return null;
    }

    /**
     * Sur clic du bouton "?" on ouvre un nouvel onglet vers la documentation de l'utilisation de l'URL
     */
    goApiDocumentation(): void {
        this.getBackendPropertyPipe.transform('rudidatarennes.apidocumentation').subscribe(
            (urlApi: string) => {
                window.open(urlApi, '_blank');
            }
        );
    }

    /**
     * Sur clic du bouton "?" pour un média de type service on ouvre un nouvel onglet vers la documentation des media_service
     */
    goApiDocumentationService(): void {
        this.getBackendPropertyPipe.transform('rudidatarennes.apiDocumentationService').subscribe(
            (urlApi: string) => {
                window.open(urlApi, '_blank');
            }
        );
    }

    /**
     * Récupère l'URL d'accès au JDD en dehors de RUDI
     * @param media média concerné par le JDD
     */
    getUrlApiMetadata(media: Media): string {
        return window.location.host + '/medias/' + media.media_id + '/' + media?.connector?.interface_contract + '/1.0.0';
    }

    /**
     * L'URL du média a-t-elle été copiée dans le presse papier ?
     * @param media le média concerné
     */
    isUrlCopiedToClipboard(media: Media): boolean {
        return this.mapMediaUrlCopied.get(media);
    }

    /**
     * Copie du lien vers l'api JDD dans le presse-papier
     * @param media le média concerné par le JDD
     */
    copyUrlApiMetadataToClipboard(media: Media): void {
        this.mapMediaUrlCopied.clear();
        this.mapMediaUrlCopied.set(media, true);
        this.clipboard.copy(this.getUrlApiMetadata(media));
    }

    /**
     * Verifie si un jdd est au format dwnl
     */
    public isDwnlFormat(media: Media) : boolean {
        return this.getMediaFile(media)?.connector.interface_contract.toUpperCase() === this.DWNL_FORMAT;
    }

    /**
     * @return interface_contract with parenthesis
     */
    getInterfaceContract(media: Media): string {
        return ' (' + media.connector?.interface_contract + ')';
    }

    getMediaIndexSeries(index: number): number {
        return this.getMediaIndexByType(index, this.mapMediaIndexSeries);
    }
    /**
     * Get le rang d'un media par rapport aux autres medias du même type
     * @param currentIndex
     */
    getMediaIndexByType(currentIndex: number, mapMediaIndexByType: Map<number, number>): number {
        return mapMediaIndexByType.get(currentIndex);
    }

    buildMapMediaIndex(mapMedia: Map<number, number>, filterFunction: (media: Media) => boolean): void {
        let currentIndexMedia = 1;
        if (this.metadata?.available_formats) {
            this.metadata.available_formats.forEach((media: Media, index: number) => {
                if (filterFunction(media)) {
                    mapMedia.set(index, currentIndexMedia);
                    currentIndexMedia += 1;
                }
            });
        }
    }
}
