import {RequestDetails} from '../../shared/models/request-details';
import {Project} from '../../projekt/projekt-model';

/**
 * Infos saisies quand on veut faire une demande d'accès à un JDD à partir d'un projet choisi
 */
export interface LinkedDatasetFromProject {
    datasetUuid: string;
    project: Project;
    requestDetail: RequestDetails;
}
