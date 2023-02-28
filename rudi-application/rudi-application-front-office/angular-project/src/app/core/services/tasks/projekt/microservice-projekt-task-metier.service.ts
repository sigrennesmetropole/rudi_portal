import {TaskMetierService} from '../task-metier.service';
import {Observable} from 'rxjs';
import {Task} from '../../../../api-bpmn';
import {TaskService as ProjektTaskService} from '../../../../projekt/projekt-api';
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
