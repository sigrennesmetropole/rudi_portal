import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {FormBuilder} from '@angular/forms';
import {ProjektMetierService} from '../../../core/services/projekt-metier.service';
import {OwnerType, Project, TargetAudience} from '../../../projekt/projekt-model';
import {UserService} from '../../../core/services/user.service';
import {KonsultMetierService} from '../../../core/services/konsult-metier.service';
import {Metadata} from '../../../api-kaccess';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {MatHorizontalStepper} from '@angular/material/stepper';
import {ActivatedRoute, Router} from '@angular/router';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {MatDialog} from '@angular/material/dialog';
import {FormReutilisationDependencies, ProjectSubmissionService} from '../../../core/services/project-submission.service';
import {ReuseProjectCommonComponent} from '../../components/reuse-project-common/reuse-project-common.component';
import {FiltersService} from '../../../core/services/filters.service';
import {User} from '../../../acl/acl-model';
import {AccessStatusFiltersType} from '../../../core/services/filters/access-status-filters-type';

@Component({
    selector: 'app-reuse-declaration',
    templateUrl: './reuse-declaration.component.html',
    styleUrls: ['./reuse-declaration.component.scss']
})
export class ReuseDeclarationComponent extends ReuseProjectCommonComponent implements OnInit, OnDestroy {

    @ViewChild(MatHorizontalStepper)
    public stepper: MatHorizontalStepper;

    public publicCibe: TargetAudience[];
    user: User;

    constructor(
        readonly projektMetierService: ProjektMetierService,
        readonly filtersService: FiltersService,
        readonly projectSubmissionService: ProjectSubmissionService,
        private matIconRegistry: MatIconRegistry,
        private domSanitizer: DomSanitizer,
        private readonly formBuilder: FormBuilder,
        private readonly userService: UserService,
        private readonly konsultMetierService: KonsultMetierService,
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
        private readonly route: ActivatedRoute,
        private dialog: MatDialog,
        private readonly router: Router
    ) {
        super(projektMetierService, filtersService, projectSubmissionService);
        this.matIconRegistry.addSvgIcon(
            'icon-add',
            this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-add.svg')
        );
    }

    get createdProjectLink(): string {
        return this.createdProject ? `/projets/detail/${this.createdProject.uuid}` : undefined;
    }

    ngOnInit(): void {

        this.step1FormGroup = this.projectSubmissionService.initStep1ReutilisationFormGroup();
        this.step2FormGroup = this.projectSubmissionService.initStep2FormGroup();
        this.step3FormGroup = this.formBuilder.group({
            uuid: [''],
        });

        this.isLoading = true;
        this.projectSubmissionService.loadDependenciesReutilisation().subscribe(
            (dependencies: FormReutilisationDependencies) => {
                this.isLoading = false;
                this.projectType = dependencies.projectTypes;
                this.publicCibe = dependencies.projectPublicCible;
                this.user = dependencies.user;
                if (this.user) {
                    this.step2FormGroup.setValue({
                        ownerType: OwnerType.User,
                        lastname: this.user.lastname,
                        firstname: this.user.firstname,
                        organizationUuid: null,
                        contactEmail: this.user.login
                    });
                }
                if (dependencies.organizations?.length) {
                    this.organizationItems = dependencies.organizations.map(organization => ({
                        name: organization.name,
                        uuid: organization.uuid
                    }));
                } else {
                    this.organizationItems = [];
                }
            }
        );

        const linkedDatasetParam: string = this.route.snapshot.queryParams.linkedDataset;
        if (linkedDatasetParam && !this.projectSubmissionService.isMetadataPresent(linkedDatasetParam, this.linkedDatasets)) {
            this.konsultMetierService.getMetadataByUuid(linkedDatasetParam).subscribe((metadata: Metadata) => {
                this.selectMetadata(metadata);
            });
        }
    }

    openDialog(): void {
        super.openDialogSelectMetadata(AccessStatusFiltersType.Opened);
    }

    publish(): void {

        this.linkedDatasetsError = this.linkedDatasets.length < 1;
        if (this.linkedDatasetsError) {
            return;
        }

        const project: Project = this.projectSubmissionService.reutilisationFormGroupToProject(
            this.step1FormGroup,
            this.step2FormGroup,
            this.user,
            this.projectSubmissionService.searchProjectType(
                this.step1FormGroup.get('type').value,
                this.projectType
            )
        );

        let image: Blob;
        if (this.step1FormGroup.get('image').value != null) {
            image = this.step1FormGroup.get('image').value.file;
        }

        this.isLoading = true;
        this.projectSubmissionService.createAndSubmitReutilisation(project, this.linkedDatasets, this.datasetRequests,
            image, this.mapRequestDetailsByDatasetUuid).subscribe({
                next: (created: Project) => {
                    this.updateUuidDatasetItems();
                    this.stepper.selected.completed = true;
                    this.stepper.selected.editable = false;
                    this.isLoading = false;
                    this.createdProject = created;
                    this.isPublished = true;
                },
                error: (e) => {
                    console.error(e);
                    this.snackBarService.add(this.translateService.instant('project.stepper.reuse.publish.error'));
                    this.isLoading = false;
                    this.isPublished = false;
                }
            }
        );
    }

    clickCancel(): Promise<boolean> {
        return this.router.navigate(['/projets']);
    }

    ngOnDestroy(): void {
        if (this.dialogWasOpened) {
            this.filtersService.restoreFilters();
        }
    }
}
