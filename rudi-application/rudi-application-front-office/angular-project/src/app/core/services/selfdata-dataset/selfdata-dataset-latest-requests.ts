import {SelfdataInformationRequest} from '../../../selfdata/selfdata-model';

/**
 * L'ensemble des dernières demandes effectuées par un utilisateur sur un JDD selfdata
 */
export interface SelfdataDatasetLatestRequests {
    access: SelfdataInformationRequest;
    correction: SelfdataInformationRequest;
    deletion: SelfdataInformationRequest;
}
