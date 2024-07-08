import {Clipboard} from '@angular/cdk/clipboard';
import {HttpResponse} from '@angular/common/http';
import {Component, Input, OnInit} from '@angular/core';
import {LanguageService} from '@core/i18n/language.service';
import {MediaSize} from '@core/services/breakpoint-observer.service';
import {DEFAULT_VIEW_PROJECTION, DisplayMapService} from '@core/services/data-set/display-map.service';
import {KonsultMetierService} from '@core/services/konsult-metier.service';
import {KosMetierService} from '@core/services/kos-metier.service';
import {LogService} from '@core/services/log.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {Level} from '@shared/notification-template/notification-template.component';
import {GetBackendPropertyPipe} from '@shared/pipes/get-backend-property.pipe';
import {MetadataUtils} from '@shared/utils/metadata-utils';
import saveAs from 'file-saver';
import {
    DictionaryEntry2,
    Licence,
    LicenceCustom,
    LicenceStandard,
    MatchingData,
    Media,
    MediaFile,
    MediaSeries,
    MediaService,
    Metadata,
    MetadataAccessCondition,
    Period,
    SelfdataContent,
} from 'micro_service_modules/api-kaccess';
import {KindOfData} from 'micro_service_modules/api-kmedia';
import {LayerInformation} from 'micro_service_modules/konsult/konsult-model';
import {Feature} from 'ol';
import {Geometry, Polygon} from 'ol/geom';
import {Observable} from 'rxjs';
import {DetailFunctions} from '../../pages/detail/detail-functions';
import LicenceTypeEnum = Licence.LicenceTypeEnum;
import TypeEnum = MatchingData.TypeEnum;
import MediaTypeEnum = Media.MediaTypeEnum;
import UnitEnum = Period.UnitEnum;
import SelfdataAccessEnum = SelfdataContent.SelfdataAccessEnum;
import SelfdataCatagoriesEnum = SelfdataContent.SelfdataCategoriesEnum;

@Component({
    selector: 'app-data-sets-infos',
    templateUrl: './data-set-infos.component.html',
    styleUrls: ['./data-set-infos.component.scss']
})
export class DataSetInfosComponent implements OnInit {

    readonly DWNL_FORMAT = 'DWNL';

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
    panelOpenStateSelfData: boolean;
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

    boundingBox: Feature<Polygon>;
    centeredGeometry: Geometry;
    baseLayers: LayerInformation[] = [];
    isMapLoading = false;

    constructor(private readonly konsultMetierService: KonsultMetierService,
                readonly snacbackService: SnackBarService,
                private readonly translateService: TranslateService,
                private readonly kosMetierService: KosMetierService,
                private readonly languageService: LanguageService,
                readonly metaDataFunctions: DetailFunctions,
                private readonly propertiesMetierService: PropertiesMetierService,
                private readonly getBackendPropertyPipe: GetBackendPropertyPipe,
                private readonly clipboard: Clipboard,
                private readonly displayMapService: DisplayMapService,
                private readonly logService: LogService
    ) {
    }

    ngOnInit(): void {
        this.licenceLabel = this.getLicenceLabel();
        this.conceptUri = this.getConceptUri();

        this.metadata.available_formats.sort(this.mediasSortedFunction);

        this.buildMapMediaIndex(this.mapMediaIndexSeries, this.metaDataFunctions.isMediaTypeSeries);

        this.boundingBox = this.displayMapService.getMetadataBoundingBox(this.metadata, DEFAULT_VIEW_PROJECTION);
        if (this.metadata.geography != null) {
            this.centeredGeometry = this.displayMapService.getMetadataGeolocation(this.metadata, DEFAULT_VIEW_PROJECTION);
        }

        this.isMapLoading = true;
        this.displayMapService.getLocalisationBaseLayers().subscribe({
            next: (baseLayers: LayerInformation[]) => {
                this.isMapLoading = false;
                this.baseLayers = baseLayers;
            },
            error: (e) => {
                this.isMapLoading = false;
                this.logService.error(e);
            }
        });
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
    getMediaFileExtension(media: Media): string {
        return this.konsultMetierService.getMediaFileExtension(media);
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
        this.konsultMetierService.downloadMetadataMedia(item.connector.url)
            .subscribe({
                next: (response) => {
                    this.isLoading = false;
                    this.downLoadFile(response);
                },
                error: () => {
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
            });
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
        if (MetadataUtils.isSelfdata(this.metadata)) {
            return false;
        } else {
            return this.downloadableMedias
                && this.downloadableMedias.some(downloadableMedia => downloadableMedia.media_id === media.media_id);
        }
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
        const interfaceContract = media?.connector?.interface_contract;
        if (!interfaceContract) {
            return undefined;
        }
        return window.location.host + media.connector.url;
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
    public isDwnlFormat(media: Media): boolean {
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

    /**
     * Construction et récupération de la chaîne de caractères de la liste des données pivots d'un jeu de données
     * @param metadata les métadonnées contenant les infos sur le self data
     */
    getSelfDataPivot(metadata: Metadata): string {

        if (metadata.ext_metadata == null || metadata.ext_metadata.ext_selfdata == null
            || metadata.ext_metadata.ext_selfdata.ext_selfdata_content == null) {
            return null;
        }

        const currentLanguage = this.translateService.currentLang;
        let pivotLabels: string[] = [];
        const matchingDatas = metadata.ext_metadata.ext_selfdata.ext_selfdata_content.matching_data;
        if (matchingDatas && matchingDatas.length > 0) {
            matchingDatas.forEach((matchingData: MatchingData) => {
                const currentLangLabels = matchingData.label.filter((label: DictionaryEntry2) => label.lang === currentLanguage);
                let labelsTxt = currentLangLabels.map((label: DictionaryEntry2) => label.text);

                // Pièce justificative nécessaire si données d'appariement de type : pièce-jointe
                if (matchingData.type === TypeEnum.Attachment) {
                    labelsTxt = labelsTxt.map((labelTxt: string) => labelTxt + ' '
                        + this.translateService.instant('donneesPersonnellesBox.needsJustificatif'));
                }

                pivotLabels = pivotLabels.concat(labelsTxt);
            });
        }

        return pivotLabels.length === 0 ? null : pivotLabels.join(', ');
    }

    /**
     * Construction et récupération de la chaîne de caractère décrivant le mode d'accès aux données self data
     * @param metadata les métadonnées contenant les self data
     */
    getSelfDataAccessMode(metadata: Metadata): string {

        if (metadata.ext_metadata == null || metadata.ext_metadata.ext_selfdata == null
            || metadata.ext_metadata.ext_selfdata.ext_selfdata_content == null) {
            return null;
        }

        const access = metadata.ext_metadata.ext_selfdata.ext_selfdata_content.selfdata_access;
        if (access === SelfdataAccessEnum.Api) {
            return this.translateService.instant('donneesPersonnellesBox.apiAccessMode');
        } else if (access === SelfdataAccessEnum.Out) {
            return this.translateService.instant('donneesPersonnellesBox.outAccessMode');
        }

        return null;
    }

    /**
     * Traduction de l'objet Period en une chaîne de caractère lisible
     * @param period élément à traduire et afficher
     */
    getSelfDataPeriod(period: Period): string {
        if (period && period.unit && period.value) {
            let translateKey;
            switch (period.unit) {
                case UnitEnum.Days:
                    translateKey = (period.value > 1) ? 'donneesPersonnellesBox.days' : 'donneesPersonnellesBox.day';
                    break;
                case UnitEnum.Months:
                    translateKey = (period.value > 1) ? 'donneesPersonnellesBox.months' : 'donneesPersonnellesBox.month';
                    break;
                case UnitEnum.Years:
                    translateKey = (period.value > 1) ? 'donneesPersonnellesBox.years' : 'donneesPersonnellesBox.year';
                    break;
            }

            return `${period.value} ${this.translateService.instant(translateKey)}`;
        }

        return null;
    }

    getSelfDataCategory(category: SelfdataCatagoriesEnum): string {
        return this.translateService.instant('donneesPersonnellesBox.' + category);
    }

    getSelfDataCategories(metadata: Metadata): string {
        if (metadata.ext_metadata == null || metadata.ext_metadata.ext_selfdata == null
            || metadata.ext_metadata.ext_selfdata.ext_selfdata_content == null) {
            return null;
        }

        const categories = metadata.ext_metadata.ext_selfdata.ext_selfdata_content.selfdata_categories;

        let value = '';
        if (categories && categories.length > 0) {
            let index = 0;
            categories.forEach(category => {
                if (index > 0) {
                    value += ', ';
                }
                value += this.getSelfDataCategory(category);
                index++;
            });
        }
        return value;
    }
}
