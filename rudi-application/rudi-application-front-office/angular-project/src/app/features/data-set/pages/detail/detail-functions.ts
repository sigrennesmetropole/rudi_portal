import {Injectable} from '@angular/core';
import {LanguageService} from '@core/i18n/language.service';
import {LogService} from '@core/services/log.service';
import {Licence, LicenceCustom, Media, Metadata} from 'micro_service_modules/api-kaccess';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {Observable, of, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import LicenceTypeEnum = Licence.LicenceTypeEnum;
import MediaTypeEnum = Media.MediaTypeEnum;

const DWNL_INTERFACE_CONTRACT = 'dwnl';

@Injectable({
    providedIn: 'root'
})
export class DetailFunctions {
    constructor(
        private readonly languageService: LanguageService,
        private readonly konsulService: KonsultService,
        private readonly logService: LogService
    ) {
    }

    /**
     * Fonction permettant de recuperer custom_licence_label
     */
    getCustomLicenceLabel(item: Licence): string | undefined {
        if (item.licence_type === LicenceTypeEnum.Custom) {
            const licenceCustom = item as LicenceCustom;
            return this.languageService.getTextForCurrentLanguage(licenceCustom.custom_licence_label);
        }
        return undefined;
    }

    /**
     * Fonction permettant de vérifier si un media est téléchargeable
     */
    canDownloadMedia(media: Media, metadata: Metadata): Observable<boolean> {
        if (media?.connector.interface_contract === DWNL_INTERFACE_CONTRACT && media.media_type === MediaTypeEnum.File) {
            if (!metadata.access_condition.confidentiality?.restricted_access) {
                return of(true);
            } else {
                return this.konsulService.hasSubscribeToMetadataMedia(metadata.global_id, media.media_id).pipe(
                    catchError((error) => {
                        if (error.status === 480) {
                            this.logService.error('Accès impossible au média', error);
                            return of(false);
                        }
                        return throwError(error);
                    })
                );
            }
        }
        return of(false);
    }

    isMediaTypeFile(media: Media): boolean {
        return media && media.media_type === MediaTypeEnum.File;
    }

    isMediaTypeSeries(media: Media): boolean {
        return media && media.media_type === MediaTypeEnum.Series;
    }

    isMediaTypeService(media: Media): boolean {
        return media && media.media_type === MediaTypeEnum.Service;
    }

    isLicenceTypeStandard(licence: Licence): boolean {
        return licence && licence.licence_type === LicenceTypeEnum.Standard;
    }

    isLicenceTypeCustom(licence: Licence): boolean {
        return licence && licence.licence_type === LicenceTypeEnum.Custom;
    }

}
