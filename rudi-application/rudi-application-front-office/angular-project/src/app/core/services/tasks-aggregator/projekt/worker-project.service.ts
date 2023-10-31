import {Injectable} from '@angular/core';
import {Project} from '@app/projekt/projekt-model';
import {Task} from 'src/app/api-bpmn';
import {
    ProjectDependencies,
    ProjectTask,
    ProjectTaskDependenciesService,
    ProjectTaskDependencyFetcher
} from '../../tasks/projekt/project-task-dependencies.service';
import {ProjektTaskSearchCriteria} from '../../tasks/projekt/projekt-task-search-criteria.interface';
import {RequestToStudy} from '../request-to-study.interface';
import {WorkerService} from '../worker.service';


@Injectable({
    providedIn: 'root'
})
export class WorkerProjectService extends WorkerService<ProjectTask, Project, ProjectDependencies, ProjektTaskSearchCriteria> {

    constructor(projectTaskDependenciesService: ProjectTaskDependenciesService,
                projectDependenciyFetcher: ProjectTaskDependencyFetcher) {
        super(projectTaskDependenciesService, projectDependenciyFetcher);
    }


    mapToRequestToStudy(task: Task, assetDescription: Project, dependencies: ProjectDependencies): RequestToStudy {
        const requestToStudy = super.mapToRequestToStudy(task, assetDescription, dependencies);

        //on override le champ pour forcer la banette à afficher le titre de la réutilisation dans la colonne description.
        requestToStudy.description = task.asset.title;
        requestToStudy.url = 'project-task-detail';

        return requestToStudy;
    }
}
