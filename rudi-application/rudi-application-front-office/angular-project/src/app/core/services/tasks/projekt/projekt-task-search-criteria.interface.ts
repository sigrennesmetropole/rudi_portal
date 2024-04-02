import {ProjectStatus} from 'micro_service_modules/projekt/projekt-model';
import {TaskSearchCriteria} from '../task-search-criteria.interface';

export interface ProjektTaskSearchCriteria extends TaskSearchCriteria {
    title?: string;
    projectStatus?: ProjectStatus;
    datasetProducerUuid?: string;
    projectUuid?: string;
}
