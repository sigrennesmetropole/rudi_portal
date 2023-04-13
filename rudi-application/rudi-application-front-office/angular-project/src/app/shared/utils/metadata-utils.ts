import {Metadata, SelfdataContent} from '../../api-kaccess';
import {Filters} from '../models/filters';
import {AccessStatusFiltersType} from '../../core/services/filters/access-status-filters-type';
import {AccessConditionConfidentiality} from './access-condition-confidentiality';
import SelfdataAccessEnum = SelfdataContent.SelfdataAccessEnum;

interface Status {
    restrictedAcces?: boolean;
    gdprSensitive?: boolean;
}

export class MetadataUtils {
    static isRestricted(metadata: Metadata): boolean {
        return metadata.access_condition?.confidentiality?.restricted_access
            &&
            !metadata.access_condition?.confidentiality?.gdpr_sensitive;
    }

    static isSelfdata(metadata: Metadata): boolean {
        return metadata.access_condition?.confidentiality?.gdpr_sensitive && metadata.access_condition?.confidentiality?.restricted_access;
    }

    static isSelfdataAccessApi(metadata: Metadata): boolean {
        return MetadataUtils.isSelfdata(metadata)
            && metadata?.ext_metadata?.ext_selfdata?.ext_selfdata_content?.selfdata_access === SelfdataAccessEnum.Api;
    }

    static getAccessStatus(filters: Filters): Status {
        const status: Status = {};
        if (filters.accessStatus === AccessStatusFiltersType.GdprSensitive) {
            status.gdprSensitive = true;
        } else if (filters.accessStatus === AccessStatusFiltersType.Restricted) {
            status.restrictedAcces = true;
        } else if (filters.accessStatus === AccessStatusFiltersType.Opened) {
            status.restrictedAcces = false;
        }
        return status;
    }

    static isApplicableForDeletion(metadata: Metadata): boolean {
        return MetadataUtils.isSelfdata(metadata) && metadata?.ext_metadata?.ext_selfdata?.ext_selfdata_content?.deletable_data;
    }

    static getAccessConditionConfidentiality(metadata: Metadata): AccessConditionConfidentiality {
        if (this.isSelfdata(metadata)) {
            return AccessConditionConfidentiality.Selfdata;
        }
        if (this.isRestricted(metadata)) {
            return AccessConditionConfidentiality.Restricted;
        } else {
            return AccessConditionConfidentiality.Opened;
        }
    }
}
