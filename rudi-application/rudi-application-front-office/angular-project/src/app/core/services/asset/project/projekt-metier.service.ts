import {Injectable} from '@angular/core';
import {forkJoin, Observable, of, zip} from 'rxjs';
import {
    Confidentiality,
    FrontOfficeProperties,
    LinkedDataset,
    LinkedDatasetStatus,
    NewDatasetRequest,
    OwnerType,
    PagedProjectList,
    Project,
    ProjectSearchCriteria,
    ProjectType,
    Support,
    TargetAudience,
    TerritorialScale
} from '../../../../projekt/projekt-model';
import {DatasetConfidentiality, OwnerInfo, ProjektService} from '../../../../projekt/projekt-api';
import {KindOfData} from '../../../../api-kmedia';
import {Base64EncodedLogo, ImageLogoService} from '../../image-logo.service';
import {catchError, map, mapTo, switchMap} from 'rxjs/operators';
import {PageResultUtils} from '../../../../shared/utils/page-result-utils';
import {Metadata} from '../../../../api-kaccess';
import {KonsultMetierService} from '../../konsult-metier.service';
import {DataRequestItem} from '../../../../project/model/data-request-item';
import {RequestDetails} from '../../../../shared/models/request-details';
import {DateTimeUtils} from '../../../../shared/utils/date-time-utils';
import {ErrorWithCause} from '../../../../shared/models/error-with-cause';
import {DataSize} from '../../../../shared/models/data-size';
import {PropertiesAdapter} from '../../properties-adapter';
import {LinkedDatasetMetadatas} from './project-dependencies.service';

const {firstElementOrThrow} = PageResultUtils;

export type Order = 'title' | '-title' | 'updatedDate' | '-updatedDate' | 'code' | '-code' | 'order_';
export const ORDERS: Order[] = ['title', '-title', 'updatedDate', '-updatedDate'];
export const DEFAULT_PROJECT_ORDER: Order = 'title';

const DEFAULT_LINKED_DATASET_STATUS: LinkedDatasetStatus = 'VALIDATED';
const RESTRICTED_LINKED_DATASET_STATUS: LinkedDatasetStatus = 'DRAFT';
export const DEFAULT_ORDER: Order = 'order_';

@Injectable({
    providedIn: 'root'
})
export class ProjektMetierService {

    /**
     * Chaîne de caractère utilisée pour un utilisateur inconnu dans RUDI
     */
    static UNKOWN_USER_INFO_NAME = '[Utilisateur inconnu]';

    private readonly propertiesAdapter: PropertiesAdapter<FrontOfficeProperties>;

    constructor(
        private readonly projektService: ProjektService,
        private readonly imageLogoService: ImageLogoService,
        private readonly konsultMetierService: KonsultMetierService
    ) {
        this.propertiesAdapter = new class extends PropertiesAdapter<FrontOfficeProperties> {
            protected fetchBackendProperties(): Observable<FrontOfficeProperties> {
                return projektService.getFrontOfficeProperties();
            }
        }();
    }

    /**
     * Récupération de la liste des projets depuis le serveur
     */
    searchProjects(criteria: ProjectSearchCriteria, order = DEFAULT_PROJECT_ORDER): Observable<PagedProjectList> {
        return this.projektService.searchProjects(
            criteria.dataset_uuids,
            criteria.linked_dataset_uuids,
            criteria.owner_uuids,
            criteria.status,
            criteria.offset,
            criteria.limit,
            order
        );
    }

    /**
     * Récupération des projets de l'utilisateur fourni
     * @param myUserUuid uuid de l'utilisateur
     */
    getMyProjects(userUuid: string): Observable<Project[]> {
        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.searchProjects({
                offset,
                owner_uuids: [userUuid],
            }));
    }

    /**
     * Récupération des projets de l'utilisateur fourni
     * @param myUserUuid uuid de l'utilisateur
     */
    getMyAndOrganizationsProjects(offset?: number, limit?: number, order?: string): Observable<PagedProjectList> {
        return this.projektService.getMyProjects(offset, limit, order);
    }

    /**
     * Récupération des projets de l'utilisateur connecté et ceux de son(ses) organisation(s) sans pagination
     */
    getMyAndOrganizationsProjectsWithoutPagination(): Observable<Project[]> {
        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.getMyAndOrganizationsProjects(offset));
    }

    /**
     * Récupération du logo d'un projet au format base64string
     * @param projectUuid l'UUID du projet dont on veut le logo
     */
    getProjectLogo(projectUuid: string): Observable<Base64EncodedLogo> {
        return this.projektService.downloadProjectMediaByType(projectUuid, KindOfData.Logo).pipe(
            switchMap((blob: Blob) => {
                return this.imageLogoService.createImageFromBlob(blob);
            }),
        );
    }

    /**
     * Get d'un projet par UUID
     * @param uuid l'uuid du projet
     */
    getProject(uuid: string): Observable<Project> {
        return this.projektService.getProject(uuid);
    }

    /**
     * Créatin d'un projet côté back
     * @param project l'objet front à persister
     */
    createProject(project: Project): Observable<Project> {
        return this.projektService.createProject(project);
    }

    /**
     * Suppression d'un objet project (réutilisation ou projet) via appel REST
     * @param project le projet à supprimer
     */
    deleteProject(project: Project): Observable<boolean> {
        if (project != null) {
            return this.projektService.deleteProject(project.uuid).pipe(mapTo(true));
        }

        return of(false);
    }

    /**
     * Mise à jour d'un projet côté back
     * @param project l'objet front à persister
     */
    updateProject(project: Project): Observable<void> {
        return this.projektService.updateProject(project.uuid, project);
    }

    /**
     * Appel API : récupération de tous les types de projets (on récupère tout via plusieurs appels si besoin)
     */
    searchProjectTypes(): Observable<ProjectType[]> {
        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.projektService.searchProjectTypes(null, offset, DEFAULT_ORDER));
    }

    /**
     * Appel API : récupération de tous les types d'assistance (on récupère tout via plusieurs appels si besoin)
     */
    searchSupports(): Observable<Support[]> {
        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.projektService.searchSupports(null, offset, DEFAULT_ORDER));
    }

    /**
     * Appel API : récupération de toutes les échelles (on récupère tout via plusieurs appels si besoin)
     */
    searchTerritorialScales(): Observable<TerritorialScale[]> {
        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.projektService.searchTerritorialScales(null, offset, DEFAULT_ORDER));
    }

    /**
     * Appel API : récupération de tous les niveaux de confidentialité (on récupère tout via plusieurs appels si besoin)
     */
    searchProjectConfidentialities(): Observable<Confidentiality[]> {
        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.projektService.searchConfidentialities(null, offset, DEFAULT_ORDER));
    }

    /**
     * Appel API : récupération de tous les publics cible (plusieurs appels back au besoin)
     */
    searchProjectPublicCible(): Observable<TargetAudience[]> {
        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.projektService.searchTargetAudiences(null, offset, DEFAULT_ORDER)
        );
    }

    /**
     * Appel API upload d'un logo pour un projet
     * @param projectUuid UUID du projet affecté
     * @param image image au format binaire
     */
    uploadLogo(projectUuid: string, image: Blob): Observable<void> {
        return this.projektService.uploadProjectMediaByType(projectUuid, KindOfData.Logo, image);
    }

    /**
     * Appel API retrait du logo d'un projet
     * @param projectUuid l'uuid du projet modifié
     */
    removeLogo(projectUuid: string): Observable<void> {
        return this.projektService.deleteProjectMediaByType(projectUuid, KindOfData.Logo);
    }

    /**
     * Appel d'API ajout des JDDs liés à un projet
     * @param projectUuid UUID du projet impacté
     * @param datasetsGlobalIds les global ids des JDDs liés
     * @param mapRequestDetailsByDatasetUuid associations JDD → détails de la demande, si nécessaire
     */
    linkProjectToDatasets(projectUuid: string, datasetsGlobalIds: string[],
                          mapRequestDetailsByDatasetUuid: Map<string, RequestDetails>): Observable<LinkedDataset[]> {

        if (datasetsGlobalIds.length < 1) {
            return of(null);
        }
        const link$: Observable<LinkedDataset>[] = datasetsGlobalIds.map(datasetUuid => {
            const requestDetails: RequestDetails | undefined = mapRequestDetailsByDatasetUuid.get(datasetUuid);
            const linkedDataset: LinkedDataset = {
                dataset_uuid: datasetUuid,
                comment: requestDetails?.comment,
                linked_dataset_status: requestDetails ? RESTRICTED_LINKED_DATASET_STATUS : DEFAULT_LINKED_DATASET_STATUS,
                object_type: 'LinkedDataset',
                end_date: DateTimeUtils.extractLocalDateTimeToISOString(requestDetails?.endDate)
            };
            return this.projektService.linkProjectToDataset(projectUuid, linkedDataset);
        });

        // Forkjoin pour récupérer les valeurs de retour dans un observable
        return forkJoin(link$);
    }

    /**
     * Appel d'API retrait des demandes d'accès aux JDDs pour un projet
     * @param projectUuid UUID du projet impacté
     * @param linkedDatasetUuids les UUIDs des demandes d'accès à supprimer
     */
    unlinkDatasetsToProject(projectUuid: string, linkedDatasetUuids: string[]): Observable<void> {

        if (linkedDatasetUuids.length < 1) {
            return of(null);
        }

        const link$: Observable<void>[] = linkedDatasetUuids.map(
            datasetUuid => this.projektService.unlinkProjectToDataset(projectUuid, datasetUuid)
        );
        return zip(...link$).pipe(
            mapTo(void 0)
        );
    }

    /**
     * Mets à jour toutes les demandes d'accès à des JDDs fournis
     * @param project le projet concerné
     * @param linkedDatasets les demandes d'accès à MAJ
     */
    updateLinkedDatasets(project: Project, linkedDatasets: LinkedDataset[]): Observable<boolean> {
        if (linkedDatasets.length < 1) {
            return of(true);
        }

        const observables = linkedDatasets
            .map((linkedDataset: LinkedDataset) => this.projektService.updateLinkedDataset(project.uuid, linkedDataset));
        return forkJoin(observables).pipe(mapTo(true));
    }

    /**
     * Récupération des JDDs liés d'un projet
     * @param uuid l'UUID d'un projet JDD qui contient les liens
     */
    getMetadataLinked(uuid: string): Observable<Metadata[]> {
        return this.projektService.getLinkedDatasets(uuid).pipe(
            switchMap((linkedDatasets: LinkedDataset[]) => {
                if (linkedDatasets.length) {
                    const linkedDatasetsUuids = linkedDatasets.map(linkedDataset => linkedDataset.dataset_uuid);
                    return this.konsultMetierService.getMetadatasByUuids(linkedDatasetsUuids);
                } else {
                    return of([]);
                }
            })
        );
    }

    /**
     * Récupération des demandes d'accès aux JDDs ouverts ou restreints pour un projet
     * @param uuid l'UUID d'un projet JDD qui contient les liens
     */
    getLinkedDatasets(uuid: string): Observable<LinkedDataset[]> {
        return this.projektService.getLinkedDatasets(uuid);
    }

    /**
     * Récupération des demandes d'accès aux JDDs ouverts ou restreints validés pour un projet
     * @param uuid l'UUID d'un projet JDD qui contient les liens
     */
    getValidatedLinkedDatasets(uuid: string): Observable<LinkedDataset[]> {
        return this.projektService.getLinkedDatasets(uuid, 'VALIDATED');
    }

    /**
     * Récupération du projet lié par un LinkedDataset
     * @param linkedDatasetUuid UUID du lien (ce n'est ni l'UUID du projet, ni l'UUID du JDD)
     */
    getLinkedProject(linkedDatasetUuid: string): Observable<Project> {
        return this.searchProjects({
            linked_dataset_uuids: [linkedDatasetUuid]
        }).pipe(
            firstElementOrThrow(new Error(`Project referenced by linkedDatasetUuid ${linkedDatasetUuid} not found.`))
        );
    }

    /**
     * Récupération des demandes de nouveaux JDDs dans le back pour un projet
     * @param projectUuid uuid du projet
     */
    getNewDatasetRequests(projectUuid: string): Observable<NewDatasetRequest[]> {
        return this.projektService.getNewDatasetRequests(projectUuid);
    }

    /**
     * Obtention d'un objet métier : NewDatasetRequest à partir d'un objet métier vue
     * @param dataRequest la demande de données (front pas back)
     */
    public dataRequestToNewDatasetRequest(dataRequest: DataRequestItem): NewDatasetRequest {
        return {
            title: dataRequest.title,
            description: dataRequest.description,
            uuid: dataRequest.uuid,
            object_type: 'NewDatasetRequest'
        };
    }

    /**
     * Ajout de plusieurs demandes de nouveaux JDDs à un projet, mets à jour les UUID des vues front après la création
     * @param project le projet concerné
     * @param dataRequests l'ensemble des demandes (front)
     */
    addNewDatasetRequests(project: Project, dataRequests: DataRequestItem[]): Observable<boolean> {

        if (dataRequests.length < 1) {
            return of(true);
        }

        const mapBackFront: Map<NewDatasetRequest, DataRequestItem> = new Map();
        const newDatasetRequests: NewDatasetRequest[] = [];

        // Pour chaque demande front on va créer un objet de type back et on va mapper les 2 pour pouvair MAJ l'objet
        // front quand l'ajout est fait et l'uuid est généré
        dataRequests.forEach((add: DataRequestItem) => {
            const addedBack: NewDatasetRequest = this.dataRequestToNewDatasetRequest(add);
            newDatasetRequests.push(addedBack);
            mapBackFront.set(addedBack, add);
        });

        const observables = [];

        // On fait chaque ajout
        newDatasetRequests.forEach((newDatasetRequest: NewDatasetRequest) => {
            observables.push(this.projektService.createNewDatasetRequest(project.uuid, newDatasetRequest).pipe(
                map((createdDatasetRequest: NewDatasetRequest) => {
                    // Quand l'ajout est fait on utilise la map d'avant pour récupérer l'objet front
                    // Set de son uuid avec l'uuid de l'objet venant du back
                    mapBackFront.get(newDatasetRequest).uuid = createdDatasetRequest.uuid;
                })
            ));
        });

        return forkJoin(observables).pipe(
            mapTo(true)
        );
    }

    /**
     * Suppression de demandes de nouvelles données dans le back pour un projet
     * @param project le projet modifié
     * @param newDatasetRequests les demandes à supprimer
     */
    deleteDatasetRequests(project: Project, newDatasetRequests: NewDatasetRequest[]): Observable<boolean> {

        if (newDatasetRequests.length < 1) {
            return of(true);
        }

        const observables = [];

        newDatasetRequests.forEach((toDelete: NewDatasetRequest) => {
            observables.push(this.projektService.deleteNewDatasetRequest(project.uuid, toDelete.uuid));
        });

        return forkJoin(observables).pipe(mapTo(true));
    }

    /**
     * édition des demandes de nouvelles données dans le back pour un projet
     * @param project le projet modifié
     * @param newDatasetRequests les demandes à modifier
     */
    upddateDatasetRequests(project: Project, newDatasetRequests: NewDatasetRequest[]): Observable<boolean> {

        if (newDatasetRequests.length < 1) {
            return of(true);
        }

        const observables = [];

        newDatasetRequests.forEach((toEdit: NewDatasetRequest) => {
            observables.push(this.projektService.updateNewDatasetRequest(project.uuid, toEdit));
        });

        return forkJoin(observables).pipe(mapTo(true));
    }

    /**
     * Récupère le nombre de total de jeu de données attachés au projet
     * @param projectUuid l'uuid du projet
     */
    getNumberOfRequests(projectUuid: string): Observable<number> {
        return this.projektService.getNumberOfRequests(projectUuid);
    }

    /**
     * Récupère les informations sur le porteur de projet :
     * - Si projet utilisateur : renvoi du nom utilisateur
     * - Si projet organisation : renvoi du nom de l'organisation
     *
     * Si on ne trouve pas d'informations on renvoie un OwnerInfo sans nom
     *
     * @param ownerType quel est le type du porteur de projet
     * @param ownerUuid l'uuid du porteur de projet, soit un UUID d'organisation soit un UUID d'utilisateur
     */
    getOwnerInfo(ownerType: OwnerType, ownerUuid: string): Observable<OwnerInfo> {
        return this.projektService.getOwnerInfo(ownerType, ownerUuid).pipe(
            catchError((error) => {
                const errorString = 'Le porteur de projet de type : ' + ownerType +
                    ' et d\'uuid : ' + ownerUuid + ' n\'existe pas ou plus dans RUDI';
                const errorWithCause = new ErrorWithCause(errorString, error);
                console.error(errorWithCause);
                // si exception renvoyée par le back, on injecte [Utilisateur inconnu] dans la dépendance
                return of({name: ProjektMetierService.UNKOWN_USER_INFO_NAME} as OwnerInfo);
            })
        );
    }

    getDataSizeProperty(key: string): Observable<DataSize> {
        return this.propertiesAdapter.getDataSize(key);
    }

    getStrings(key: string): Observable<string[]> {
        return this.propertiesAdapter.getStrings(key);
    }

    /**
     * Get d'un projet par UUID d'une nouvelle demande de jdd
     * @param uuid l'uuid d'une nouvelle demande de jdd
     */
    findProjectByNewDatasetRequest(uuid: string): Observable<Project> {
        return this.projektService.findProjectByNewDatasetRequest(uuid);
    }

    /**
     * Récupération des demandes d'accès aux JDDs restreints pour un projet
     * @param uuid l'UUID d'un projet JDD qui contient les liens
     */
    getRestrictedLinkedDatasets(uuid: string): Observable<LinkedDataset[]> {
        return this.projektService.getLinkedDatasets(uuid).pipe(
            // on ne récupère que les demande aux jdd restreints
            map((linkedDatasets: LinkedDataset[]) => linkedDatasets.filter(
                (linkedDataset: LinkedDataset) => linkedDataset.dataset_confidentiality === DatasetConfidentiality.Restricted)
            )
        );
    }

    /**
     * Récupération des demandes d'accès aux JDDs ouverts pour un projet
     *
     * @param uuid l'UUID d'un projet JDD qui contient les liens
     */
    getOpenedLinkedDatasets(uuid: string): Observable<LinkedDataset[]> {
        return this.projektService.getLinkedDatasets(uuid).pipe(
            // on ne récupère que les demande aux jdd ouverts
            map((linkedDatasets: LinkedDataset[]) => linkedDatasets.filter(
                (linkedDataset: LinkedDataset) => linkedDataset.dataset_confidentiality === DatasetConfidentiality.Opened)
            )
        );
    }

    /**
     * Appelle le service back pour créer une nouvelle demande de JDD pour le projet donné
     * @param projectUuid uuid du projet
     * @param requestToAdd demande à ajouter
     */
    addNewDatasetRequest(projectUuid: string, requestToAdd: NewDatasetRequest): Observable<NewDatasetRequest> {
        return this.projektService.createNewDatasetRequest(projectUuid, requestToAdd);
    }

    /**
     * Appelle le service back pour supprimer une demande de JDD pour le projet donné
     * @param projectUuid uuid du projet
     * @param requestUuid uuid de la demande à supprimer
     */
    deleteNewDatasetRequest(projectUuid: string, requestUuid: string): Observable<any> {
        return this.projektService.deleteNewDatasetRequest(projectUuid, requestUuid);
    }

    /**
     * Suppression des linkedDatasetRequest
     * @param linkedDatasetRequestUuid
     * @param projectUuid
     */
    deleteLinkedDatasetRequest(projectUuid: string, linkedDatasetRequestUuid: string): Observable<any> {
        return this.projektService.unlinkProjectToDataset(projectUuid, linkedDatasetRequestUuid);
    }

    getDatasetsByUpdatedDate(data: LinkedDatasetMetadatas[]): LinkedDatasetMetadatas[] {
        return data.sort((a: LinkedDatasetMetadatas, b: LinkedDatasetMetadatas) => {
            return new Date(b.linkedDataset?.updated_date).getTime() - new Date(a.linkedDataset?.updated_date).getTime();
        });
    }

    getRequestsByUpdatedDate(data: NewDatasetRequest[]): NewDatasetRequest[] {
        return data.sort((a: NewDatasetRequest, b: NewDatasetRequest) => {
            return new Date(b.updated_date).getTime() - new Date(a.updated_date).getTime();
        });
    }
}
