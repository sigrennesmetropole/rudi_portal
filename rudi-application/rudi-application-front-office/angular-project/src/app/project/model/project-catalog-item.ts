import {OwnerInfo, Project} from '../../projekt/projekt-model';
import {Base64EncodedLogo} from '../../core/services/image-logo.service';

/**
 * Représente l'objet métier visuel du catalogue des projets
 * avec :
 * - Les infos du projet
 * - le logo du projet
 * - les infos sur le responsable du projet
 */
export class ProjectCatalogItem {

    /**
     * les informations sur le projet en lui-même
     */
    project: Project;

    /**
     * Les informations sur le gestionnaire du projet
     */
    ownerInfo: OwnerInfo;

    /**
     * Une chaîne base64 du logo du projet
     */
    logo: Base64EncodedLogo;

    /**
     * Constructeur parametré du type {project: [value]}
     * @param data les données partielles
     */
    constructor(data: Partial<ProjectCatalogItem>) {
        Object.assign(this, data);
    }
}

/**
 * Objet de pagination des item du catalogue des projets
 */
export class ProjectCatalogItemPage {
    items: ProjectCatalogItem[];
    total: number;
}
