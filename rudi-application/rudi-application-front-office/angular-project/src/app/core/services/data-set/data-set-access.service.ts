import {Injectable} from '@angular/core';
import {MetadataUtils} from '@shared/utils/metadata-utils';
import {Media, Metadata} from 'micro_service_modules/api-kaccess';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {Observable, of} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class DataSetAccessService {

    constructor(
        private readonly konsultService: KonsultService,
    ) {

    }

    /**
     * Permets de savoir si l'utilisateur connecté peut accéder aux données pour ce JDD
     * @param metadata le JDD évalué
     * @param media le média à afficher
     */
    hasAccess(metadata: Metadata, media: Media): Observable<boolean> {
        if (metadata == null) {
            return of(false);
        } else if (MetadataUtils.isSelfdata(metadata)) {
            return of(false);
        } else if (MetadataUtils.isRestricted(metadata)) {
            return this.konsultService.hasSubscribeToMetadataMedia(metadata.global_id, media.media_id);
        }

        return of(true);
    }
}
