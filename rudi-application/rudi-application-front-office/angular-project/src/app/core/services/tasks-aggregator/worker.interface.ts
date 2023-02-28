import {Observable} from 'rxjs';
import {RequestToStudy} from './request-to-study.interface';

/**
 * Service capble de récupérer des données et de les renvoyer au format : Tâche de travail RUDI
 */
export interface Worker {

    /**
     * Récupération de Tâches RUDI
     */
    loadTasks(): Observable<RequestToStudy[]>;
}
