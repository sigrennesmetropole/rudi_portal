import {Injectable} from '@angular/core';
import {MetadataUtils} from '@shared/utils/metadata-utils';
import {Metadata} from 'micro_service_modules/api-kaccess';
import {LinkedDatasetService} from 'micro_service_modules/projekt/projekt-api';
import {Observable, of} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class DataSetAccessService {

    constructor(
        private readonly service: LinkedDatasetService
    ) {

    }

    /**
     * Permets de savoir si l'utilisateur connecté peut accéder aux données pour ce JDD
     *
     * @param metadata le JDD évalué
     */
    hasAccess(metadata: Metadata): Observable<boolean> {
        if (metadata == null) {
            return of(false);
        } else if (MetadataUtils.isSelfdata(metadata)) {
            return of(false);
        } else if (MetadataUtils.isRestricted(metadata)) {
            //TODO foussetteszissi
            return this.service.isMyAccessGratedToDataset(metadata.global_id);
        }

        return of(true);
    }
}
