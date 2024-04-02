import {Task} from 'micro_service_modules/api-bpmn';
import {TaskService as SelfdataTaskService} from 'micro_service_modules/selfdata/selfdata-api';
import {Observable} from 'rxjs';
import {TaskMetierService} from '../task-metier.service';
import {SelfdataTaskSearchCriteria} from './selfdata-task-search-criteria.interface';

export abstract class MicroserviceSelfdataTaskMetierService<T> extends TaskMetierService<T> {

    protected constructor(readonly selfdataTaskService: SelfdataTaskService) {
        super();
    }

    searchMicroserviceTasks(searchCriteria: SelfdataTaskSearchCriteria): Observable<Task[]> {
        return this.selfdataTaskService.searchTasks(
            searchCriteria.title,
            searchCriteria.description,
            searchCriteria.processDefinitionKeys,
            searchCriteria.status,
            searchCriteria.fonctionalStatus,
            searchCriteria.asAdmin
        );
    }

    getTask(taskId): Observable<Task> {
        return this.selfdataTaskService.getTask(taskId);
    }
}
