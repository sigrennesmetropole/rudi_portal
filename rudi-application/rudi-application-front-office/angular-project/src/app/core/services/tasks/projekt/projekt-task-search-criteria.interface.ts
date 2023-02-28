import {TaskSearchCriteria} from '../task-search-criteria.interface';
import {ProjectStatus} from '../../../../projekt/projekt-model';

export interface ProjektTaskSearchCriteria extends TaskSearchCriteria {
    title?: string;
    projectStatus?: ProjectStatus;
    datasetProducerUuid?: string;
    projectUuid?: string;
}
