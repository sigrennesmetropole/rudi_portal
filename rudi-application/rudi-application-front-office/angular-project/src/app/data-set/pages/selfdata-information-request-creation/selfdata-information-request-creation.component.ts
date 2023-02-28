import {Component, OnInit, ViewChild} from '@angular/core';
import {Metadata} from '../../../api-kaccess';
import {switchMap, tap} from 'rxjs/operators';
import {ActivatedRoute, Params, Router} from '@angular/router';
import {KonsultMetierService} from '../../../core/services/konsult-metier.service';
import {Form} from '../../../api-bpmn';
import {WorkflowFormComponent} from '../../../shared/workflow-form/workflow-form.component';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {DataSize} from '../../../shared/models/data-size';
import {WorkflowProperties} from '../../../shared/workflow-form/workflow-properties';
import {SelfdataInformationRequestSubmissionService} from '../../../core/services/selfdata-information-request-submission.service';
import {SelfdataAttachmentService} from '../../../core/services/selfdata-attachment.service';

const ERROR_DURATION = 10000;

@Component({
    selector: 'app-selfdata-information-request-creation',
    templateUrl: './selfdata-information-request-creation.component.html',
    styleUrls: ['./selfdata-information-request-creation.component.scss']
})
export class SelfdataInformationRequestCreationComponent implements OnInit {

    metadata: Metadata;
    isLoading = false;
    errorLoading = false;
    submitLoading = false;
    form: Form;
    @ViewChild(WorkflowFormComponent)
    workflowFormComponent: WorkflowFormComponent;

    /**
     * Taille maximale acceptée par le backend, pour l'upload de fichier.
     */
    fileMaxSize: DataSize;

    constructor(
        private readonly selfdataInformationRequestSubmissionService: SelfdataInformationRequestSubmissionService,
        private readonly konsultMetierService: KonsultMetierService,
        private readonly activatedRoute: ActivatedRoute,
        private readonly router: Router,
        private readonly snackbar: SnackBarService,
        private readonly selfdataAttachmentService: SelfdataAttachmentService,
    ) {
    }

    ngOnInit(): void {
        this.activatedRoute.params.pipe(
            tap(() => {
                this.isLoading = true;
                this.errorLoading = false;
                this.metadata = null;
            }),
            switchMap((params: Params) => this.konsultMetierService.getMetadataByUuid(params.uuid)),
            switchMap((metadata: Metadata) => {
                this.metadata = metadata;
                return this.selfdataInformationRequestSubmissionService.lookupSelfdataInformationRequestDraftForm(this.metadata);
            })
        ).subscribe({
            next: (form: Form) => {
                this.form = form;
                this.isLoading = false;
                this.errorLoading = false;
            },
            error: (e) => {
                console.error(e);
                this.isLoading = false;
                this.errorLoading = true;
            }
        });
        this.selfdataAttachmentService.getDataSizeProperty('spring.servlet.multipart.max-file-size')
            .subscribe(value => this.fileMaxSize = value);
    }

    handleClickSubmit(): void {
        if (this.workflowFormComponent.submit()) {
            this.submitLoading = true;
            this.selfdataInformationRequestSubmissionService.submitSelfdataForm(this.form, this.metadata)
                .subscribe({
                    next: () => {
                        this.submitLoading = false;
                        this.router.navigate(['../selfdata-information-request-creation-success'], {relativeTo: this.activatedRoute});
                    },
                    error: err => {
                        this.submitLoading = false;
                        console.error(err);
                        // Erreur de lancement de tous les process, informer l'utilisateur
                        this.snackbar.showError('metaData.selfdataInformationRequest.creation.errorToUser', ERROR_DURATION);
                    }
                });
        }
    }

    cancel(): void {
        this.router.navigate(['..'], {relativeTo: this.activatedRoute});
    }

    get properties(): WorkflowProperties {
        return {
            fileMaxSize: this.fileMaxSize
        };
    }
}
