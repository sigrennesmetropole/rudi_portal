import {Task} from 'micro_service_modules/api-bpmn';
import {TaskService as ProjektTaskService} from 'micro_service_modules/projekt/projekt-api';
import {Observable} from 'rxjs';
import {TaskMetierService} from '../task-metier.service';
import {ProjektTaskSearchCriteria} from './projekt-task-search-criteria.interface';

export abstract class MicroserviceProjektTaskMetierService<T> extends TaskMetierService<T> {

    protected constructor(readonly projektTaskService: ProjektTaskService) {
        super();
    }

    searchMicroserviceTasks(searchCriteria: ProjektTaskSearchCriteria): Observable<Task[]> {
        return this.projektTaskService.searchTasks(
            searchCriteria.title,
            searchCriteria.description,
            searchCriteria.processDefinitionKeys,
            searchCriteria.status,
            searchCriteria.fonctionalStatus,
            searchCriteria.projectStatus,
            searchCriteria.asAdmin,
            searchCriteria.datasetProducerUuid,
            searchCriteria.projectUuid
        );
    }

    getTask(taskId): Observable<Task> {
        return this.projektTaskService.getTask(taskId);
    }
}
