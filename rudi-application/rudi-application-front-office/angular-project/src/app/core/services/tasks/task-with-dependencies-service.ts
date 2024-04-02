import {HttpErrorResponse} from '@angular/common/http';
import {DependencyFetcher} from '@shared/utils/dependencies-utils';
import {mapEach} from '@shared/utils/ObservableUtils';
import {TaskWithDependencies} from '@shared/utils/task-utils';
import {AclService, User} from 'micro_service_modules/acl/acl-api';
import {AssetDescription, Task} from 'micro_service_modules/api-bpmn';
import {OrganizationService} from 'micro_service_modules/strukture/api-strukture';
import {Organization} from 'micro_service_modules/strukture/strukture-model';
import {Observable} from 'rxjs';
import {catchError, map} from 'rxjs/operators';
import {validate as validateUuid} from 'uuid';
import {TaskMetierService} from './task-metier.service';
import {TaskSearchCriteria} from './task-search-criteria.interface';

/**
 * Service définissant le comportement permettant de charger une Tâche RUDI et ses dépendances (éléments calculables à partir d'infos dans
 * la task ou dans l'asset)
 */
export abstract class TaskWithDependenciesService<T, C extends TaskSearchCriteria, A extends AssetDescription> {

    protected constructor(protected readonly taskMetierService: TaskMetierService<A>) {
    }

    /**
     * Récupère la tâche wrappée avec un objet de dépendances prêt à charger celles-ci
     * @param taskId l'id de la tâche
     */
    getTaskWithDependencies(taskId: string): Observable<T> {
        return this.taskMetierService.getTask(taskId).pipe(
            map(task => this.newTaskWithDependencies(task))
        );
    }

    /**
     * Récupère plusieurs tâches wrappées avec les dépendances prêtes à charger
     * @param searchCriteria critère de recherche des tâches
     */
    searchTasksWithDependencies(searchCriteria: C = this.defaultSearchCriteria()): Observable<T[]> {
        return this.taskMetierService.searchTasks(searchCriteria).pipe(
            mapEach(task => this.newTaskWithDependencies(task))
        );
    }

    /**
     * Construction d'un objet { Tâche -> dépendances }
     * @param task la tâche
     */
    abstract newTaskWithDependencies(task: Task): T;

    /**
     * Le critère par défaut pour récupérer les tâches, fourni si aucun critère fourni
     */
    abstract defaultSearchCriteria(): C;
}

/**
 * Service de définition du comportement de récupération des dépendances d'une tâche
 * Définit les dépendances récupérables et les méthodes de récupération
 */
export abstract class TaskDependencyFetchers<T extends TaskWithDependencies<A, D>, A, D> {

    protected constructor(
        protected readonly organizationService: OrganizationService,
        protected readonly aclService: AclService
    ) {
    }

    get initiatorInfo(): DependencyFetcher<T, string> {
        return {
            hasPrerequisites: (input: T) => input != null && input.dependencies != null,
            getKey: taskWithDependencies => taskWithDependencies.task.initiator,
            getValue: initiatorString => {
                if (validateUuid(initiatorString)) {
                    return this.organizationService.getOrganization(initiatorString).pipe(
                        catchError((error: HttpErrorResponse) => {
                            console.error('Erreur lors de la récupération de l\'organisation avec uuid : ' + initiatorString, error);
                            return '-';
                        }),
                        map((organization: Organization) => {
                            if (organization == null) {
                                console.error('Erreur aucune organisation avec uuid : ' + initiatorString);
                                return '-';
                            }
                            return organization.name;
                        })
                    );
                }
                return this.aclService.getUserInfoByLogin(initiatorString).pipe(
                    catchError((error: HttpErrorResponse) => {
                        console.error('Erreur lors de la récupération d\'informations utilisateur avec le login' + initiatorString, error);
                        return '-';
                    }),
                    map((user: User) => {
                        if (user == null) {
                            return '-';
                        }
                        return user.firstname + ' ' + user.lastname;
                    })
                );
            }
        };
    }
}
