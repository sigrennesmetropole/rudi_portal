import {Component, OnInit} from '@angular/core';
import {ApiAccessService} from '../../../core/services/api-access.service';
import {map} from 'rxjs/operators';
import {ProjectDependenciesService} from '../../../core/services/project-dependencies.service';
import {OwnerType, Project} from '../../../projekt/projekt-model';
import {ActivatedRoute} from '@angular/router';
import {ApiKeys} from '../../../api-konsult';
import {UserService} from '../../../core/services/user.service';
import {Clipboard} from '@angular/cdk/clipboard';
import {TranslateService} from '@ngx-translate/core';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';

@Component({
    selector: 'app-project-api-tab',
    templateUrl: './project-api-tab.component.html',
    styleUrls: ['./project-api-tab.component.scss']
})
export class ProjectApiTabComponent implements OnInit {

    private project: Project;
    public keys: ApiKeys;
    public loading: boolean;
    public hasError: boolean;
    public hidePassword = true;
    public hideIdentificationCard = false;
    public isOwnerTypeUser = false;
    public password: string;
    public rudiDocLink: string;

    constructor(private readonly route: ActivatedRoute,
                private readonly apiAccessService: ApiAccessService,
                private readonly projectDependenciesService: ProjectDependenciesService,
                private readonly propertiesMetierService: PropertiesMetierService,
                private readonly utilisateurService: UserService,
                private readonly clipboard: Clipboard,
                private readonly translateService: TranslateService) {
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            this.projectUuid = params.projectUuid;
        });

        this.propertiesMetierService.get('rudidatarennes.docRudiBzh').subscribe({
            next: (rudiDocLink: string) => {
                this.rudiDocLink = rudiDocLink;
            }
        });
    }

    set projectUuid(uuid: string) {
        this.projectDependenciesService.getProject(uuid).pipe(
            map(({project, dependencies}) => {
                return project;
            }),
        ).subscribe({
            next: (project: Project) => {
                this.project = project;
                this.isOwnerTypeUser = this.project.owner_type === OwnerType.User;
            },
            error: (error) => {
                console.error(error);
            }
        });
    }

    getApiKeys(): void {
        this.keys = null;
        this.loading = true;
        this.hasError = false;
        this.apiAccessService.getConsumerKeys(this.password, this.project).subscribe({
                next: (keys: ApiKeys) => {
                    this.hasError = false;
                    this.loading = false;
                    this.keys = keys;
                    this.hideIdentificationCard = true;
                    this.keys.consumerKey;
                    this.keys.consumerSecret;
                },
                error: (e) => {
                    this.hasError = true;
                    this.loading = false;
                    this.hideIdentificationCard = false;
                    console.error(e);
                }
            }
        );
    }

    /**
     * Méthode qui récupère le mot de passe entré par l'utilisateur
     * @param $event
     */
    handlePasswordChanged($event: string): void {
        this.password = $event;
    }

    /**
     * Affiche le bon label en fonction du Owner du projet
     */
    getLabel(): string {
        if (this.isOwnerTypeUser) {
            return this.translateService.instant('personalSpace.projectApi.textOwner');
        } else {
            return this.translateService.instant('personalSpace.projectApi.textUser');
        }
    }
}
