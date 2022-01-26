import {Component, Input, OnInit} from '@angular/core';
import {
    Licence,
    LicenceCustom,
    LicenceStandard,
    Media,
    MediaFile,
    MediaSeries,
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
import {DetailFunctions} from '../../detail-functions';
import {MediaSize} from '../../../core/services/breakpoint-observer.service';
import LicenceTypeEnum = Licence.LicenceTypeEnum;
import MediaTypeEnum = Media.MediaTypeEnum;

@Component({
    selector: 'app-data-sets-infos',
    templateUrl: './data-set-infos.component.html',
    styleUrls: ['./data-set-infos.component.scss']
})
export class DataSetInfosComponent implements OnInit {

    @Input() metadata: Metadata;
    @Input() mediaSize: MediaSize;
    @Input() downloadableMedias: Media[];

    totalMediaTypeFile: number;
    licenceLabel;
    conceptUri;

    panelOpenStateCond: boolean;
    panelOpenStateDate: boolean;
    panelOpenStateStockage = false;
    panelOpenStateLoc: boolean;
    panelOpenStateProvider: boolean;
    expanded: boolean;
    kindOfData: KindOfData;

    constructor(private readonly konsultMetierService: KonsultMetierService,
                readonly snacbackService: SnackBarService,
                private readonly translateService: TranslateService,
                private readonly kosMetierService: KosMetierService,
                private readonly languageService: LanguageService,
                readonly metaDataFunctions: DetailFunctions) {
    }

    ngOnInit(): void {
        this.licenceLabel = this.getLicenceLabel();
        this.conceptUri = this.getConceptUri();

        this.metadata.available_formats.sort(this.mediasSortedFunction);

        // Permet de determiner le nombre total de media de type FILE
        this.totalMediaTypeFile = this.metadata.available_formats.filter(media => this.metaDataFunctions.isMediaTypeFile(media)).length;
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
        this.konsultMetierService.downloadMetadataMedia(this.metadata.global_id, item.media_id)
            .subscribe(
                (response) => {
                    this.downLoadFile(response);
                },
                () => {
                    this.snacbackService.add(this.translateService.instant('common.echec'));
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

}
