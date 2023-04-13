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
import {RudiCaptchaComponent} from '../../../shared/rudi-captcha/rudi-captcha.component';
import {CAPTCHA_NOT_VALID_CODE, CaptchaCheckerService} from '../../../core/services/captcha-checker.service';
import {ErrorWithCause} from '../../../shared/models/error-with-cause';

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
     * Indique si on active le captcha sur la page ou non (false par défaut)
     *
     */
    enableCaptchaOnPage: true;

    /**
     * Error on captcha input
     */
    errorCaptchaInput = false;

    @ViewChild(RudiCaptchaComponent) rudiCaptcha: RudiCaptchaComponent;
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
        private readonly captchaCheckerService: CaptchaCheckerService,
    ) {
    }

    ngOnInit(): void {
        if (this.activatedRoute.snapshot.data?.aclAppInfo) {
            this.enableCaptchaOnPage = this.activatedRoute.snapshot.data.aclAppInfo.captchaEnabled;
        }

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

    get isValid(): boolean {
        return this.rudiCaptcha?.isFilled() || !this.enableCaptchaOnPage;
    }

    handleClickSubmit(): void {
        if (!this.workflowFormComponent.submit()) {
            return;
        }
        // Validation du captcha avant tout
        this.captchaCheckerService.validateCaptchaAndDoNextStep(this.enableCaptchaOnPage, this.rudiCaptcha,
            this.selfdataInformationRequestSubmissionService.submitSelfdataForm(this.form, this.metadata))
            .subscribe({
                next: () => {
                    this.submitLoading = false;
                    this.router.navigate(['../selfdata-information-request-creation-success'], {relativeTo: this.activatedRoute});
                },
                error: err => {
                    this.submitLoading = false;
                    console.error(err);
                    if (err instanceof ErrorWithCause && err.code === CAPTCHA_NOT_VALID_CODE) {
                        this.errorCaptchaInput = true;
                    } else {
                        // Erreur de lancement de tous les process, informer l'utilisateur
                        this.snackbar.showError('metaData.selfdataInformationRequest.creation.errorToUser', ERROR_DURATION);
                    }
                }
            });
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
