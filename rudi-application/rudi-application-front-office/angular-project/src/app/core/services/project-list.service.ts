import {Injectable} from '@angular/core';
import {DEFAULT_ORDER, ProjektMetierService} from './projekt-metier.service';
import {ProjectCatalogItem, ProjectCatalogItemPage} from '../../project/model/project-catalog-item';
import {forkJoin, Observable, of} from 'rxjs';
import {catchError, map, switchMap, tap} from 'rxjs/operators';
import {OwnerType, PagedProjectList, Project, ProjectAllOf, ProjectSearchCriteria} from '../../projekt/projekt-model';
import {UserService} from './user.service';
import {User} from '../../acl/acl-model';
import {Base64EncodedLogo} from './image-logo.service';
import {UserInfo} from '../../project/model/user-info';

/**
 * Service métier des "Projects" dans le module Project de RUDI
 */
@Injectable()
export class ProjectListService {

    constructor(
        private readonly projektMetierService: ProjektMetierService,
        private readonly userService: UserService) {
    }

    /**
     * Recherche les projets pour les afficher dnas le composant Catalogue sous forme de page
     * @param offset la page sur laquelle on est
     * @param limit le nombre de projets pour la page
     */
    public searchProjectsCatalog(linkedDatasetsGlobalIds: string[], offset: number, limit: number, order = DEFAULT_ORDER): Observable<ProjectCatalogItemPage> {

        // La page renvoyée
        const page = new ProjectCatalogItemPage();

        // Tout va partir des projets à récupérer
        const criteria: ProjectSearchCriteria = {
            dataset_uuids: linkedDatasetsGlobalIds,
            offset,
            limit
        };
        return this.projektMetierService.searchProjects(criteria, order).pipe(
            // Une fois qu'on a la page on récupère les éléments et on prépare les infos de la page
            map((pageResult: PagedProjectList) => {
                page.total = pageResult.total;
                return pageResult.elements;
            }),

            // Pour chaque projet on va mapper
            switchMap((projects: Project[]) => {

                if (!projects) {
                    return of({
                        projects: [],
                        users: [],
                        logos: new Map<string, Base64EncodedLogo>(),
                    });
                }

                // Construction d'un set d'uuids d'users unique pour récupérer les infos des users dans ACL
                // Construction de la liste des UUIDs des projets
                const setUserUuuids = new Set<string>();
                const projectUuids = [];
                projects.forEach((projet: Project) => {
                    if (projet.owner_type === OwnerType.User) {
                        setUserUuuids.add(projet.owner_uuid);
                    }
                    projectUuids.push(projet.uuid);
                });

                // On va passer les projets + les informations des managers grâce à ACL + les logos
                return forkJoin({
                    projects: of(projects),
                    users: this.projektMetierService.getUsersInfos(Array.from(setUserUuuids)),
                    logos: this.getLogosFromProjects(projectUuids)
                });
            }),

            // On a récupéré les projets et les infos utilisateur des managers et les logos
            map((projectsAndDependencies: { projects: Project[], users: UserInfo[], logos: Map<string, Base64EncodedLogo> }) => {

                // Construction d'une map <uuid, User>
                const usersMap = new Map<string, UserInfo>();
                projectsAndDependencies.users
                    .filter(user => user)
                    .forEach((user: UserInfo) => usersMap.set(user.uuid, user));

                // pour chaque projet on utilise les maps pour venir peupler l'item du catalogue
                const projectCatalogItems: ProjectCatalogItem[] = [];
                projectsAndDependencies.projects.forEach((projectInfo: Project) => {
                    projectCatalogItems.push(new ProjectCatalogItem({
                        project: projectInfo,
                        manager: projectInfo.owner_type === OwnerType.User ? usersMap.get(projectInfo.owner_uuid) : undefined,
                        logo: projectsAndDependencies.logos.get(projectInfo.uuid)
                    }));
                });

                // On peuple la page
                page.items = projectCatalogItems;
                return page;
            })
        );
    }

    /**
     * Récupération des logos à l'aide des UUIDs de projets fournis
     * On récupère une MAP clé/valeur avec en clé l'UUID du projet et en valeur la string Base64 du logo
     * @param projectUuids la liste des UUIDs des projets
     * @private
     */
    private getLogosFromProjects(projectUuids: string[]): Observable<Map<string, Base64EncodedLogo>> {
        const observables = [];
        const mapLogos = new Map<string, Base64EncodedLogo>();

        // Pour chaque UUID de projet on va
        projectUuids.forEach((uuid: string) => {
            observables.push(
                // Chercher le logo et le placer dans la HashMap avec comme clé l'UUID du projet
                // S'il y a une erreur on bloque pas la chaîne on met NULL dans logo
                this.projektMetierService.getProjectLogo(uuid).pipe(
                    catchError((error) => {
                        console.error('Erreur lors de la récupération du logo du projet ' + uuid + ' : ' + error.message);
                        return of(null);
                    }),
                    tap((base64Logo: Base64EncodedLogo) => {
                        mapLogos.set(uuid, base64Logo);
                    })
                )
            );
        });

        // On renvoie la HashMap remplie par les tap()
        return forkJoin(observables).pipe(
            map(() => mapLogos)
        );
    }
}
