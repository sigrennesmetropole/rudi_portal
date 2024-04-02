import {HttpErrorResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {ActionFallbackUtils} from '@shared/utils/action-fallback-utils';
import {Task} from 'micro_service_modules//api-bpmn';
import {Metadata} from 'micro_service_modules/api-kaccess';
import {Form, TaskService} from 'micro_service_modules/selfdata/selfdata-api';
import {SelfdataInformationRequest, SelfdataInformationRequestStatus} from 'micro_service_modules/selfdata/selfdata-model';
import {Observable} from 'rxjs';
import {catchError, switchMap} from 'rxjs/operators';
import {ObjectType} from './tasks/object-type.enum';
import {SelfdataInformationRequestTaskMetierService} from './tasks/selfdata/selfdata-information-request-task-metier.service';

const FUNCTIONAL_DRAFT_STATUS = 'DRAFT';

@Injectable({
    providedIn: 'root'
})
export class SelfdataInformationRequestSubmissionService {

    constructor(
        private readonly selfdataInformationRequestTaskMetierService: SelfdataInformationRequestTaskMetierService,
        private readonly selfdataTaskService: TaskService,
        private readonly translater: TranslateService,
    ) {
    }

    /**
     * Récupération du draft-form pour réaliser une demande d'informations personnelles
     * @param metadata le JDD qui possède les données personnelles
     */
    lookupSelfdataInformationRequestDraftForm(metadata: Metadata): Observable<Form> {
        return this.selfdataTaskService.lookupSelfdataInformationRequestDraftForm(metadata.global_id, navigator.language);
    }

    /**
     * Soumission du formulaire de création d'une demande d'information personnelles
     * @param form le formulaire de saisie
     * @param metadata le JDD qui possède les données personnelles
     */
    submitSelfdataForm(form: Form, metadata: Metadata): Observable<Task> {
        const informationRequestToSave: SelfdataInformationRequest = {
            object_type: ObjectType.SELFDATA_INFORMATION_REQUEST,
            form,
            dataset_uuid: metadata.global_id,
            selfdata_information_request_status: SelfdataInformationRequestStatus.Draft,
            description: metadata.resource_title,
            functional_status: FUNCTIONAL_DRAFT_STATUS
        };

        return this.createSelfdataInformationRequestDraft(informationRequestToSave);
    }

    /**
     * Création de la demande d'information avec chaînage et gestion d'erreur
     * @param request la requête à créer
     */
    private createSelfdataInformationRequestDraft(request: SelfdataInformationRequest): Observable<Task> {

        // 1) Création de la demande
        return this.selfdataInformationRequestTaskMetierService.createDraft(request).pipe(
            catchError((selfdataError: HttpErrorResponse) => {
                console.error(selfdataError);
                throw new Error('Une erreur a eu lieu pendant la création de la demande');
            }),

            // 2) demande d'information créée, maintenant on doit démarrer son workflow
            switchMap((createdTask: Task) => {
                const startAction = new ActionFallbackUtils<Task>({
                    action: this.selfdataInformationRequestTaskMetierService.startTask(createdTask),
                    fallback: this.selfdataTaskService.deleteSelfdataInformationRequest(createdTask.asset.uuid),
                    fallbackSuccessMessage: this.translater.instant('metaData.selfdataInformationRequest.creation.worflowError'),
                    fallbackErrorMessage: this.translater.instant('metaData.selfdataInformationRequest.creation.deletionError')
                });
                return startAction.doActionFallbackOnfailure();
            })
        );
    }
}
