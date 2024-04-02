import {Injectable} from '@angular/core';
import {Project} from 'micro_service_modules/projekt/projekt-model';

@Injectable({
    providedIn: 'root'
})
export class DataSetActionsAuthorizationService {

    canAddDatasetFromProject(project: Project): boolean {
        return this.handleActionAuthorization(project);
    }

    canDeleteDatasetFromProject(project: Project): boolean {
        return this.handleActionAuthorization(project);
    }

    private handleActionAuthorization(project: Project): boolean {
        switch (project.project_status) {
            case 'DRAFT':
            case 'REJECTED':
                return true;
            case 'VALIDATED' :
                return project.reutilisation_status.dataset_set_modification_allowed;
            case 'IN_PROGRESS':
            case 'CANCELLED':
            case 'DISENGAGED':
            default:
                return false;
        }
    }

}
