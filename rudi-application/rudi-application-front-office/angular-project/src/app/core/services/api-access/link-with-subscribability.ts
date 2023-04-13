import {LinkedDatasetMetadatas} from '../asset/project/project-dependencies.service';

/**
 * Wrapper pour savoir si l'utilisateur peut souscrire à un JDD
 */
export class LinkWithSubscribability {

    /**
     * le JDD lié à la demande pour la souscription
     */
    link: LinkedDatasetMetadatas;

    /**
     * Est-ce que l'user peut souscrire à ce JDD
     */
    canSubscribe: boolean;

    /**
     * Constructeur paramétré
     * @param link objet enrichi wrappé
     * @param canSubscribe si on peut souscrire ou pas
     */
    constructor(link: LinkedDatasetMetadatas, canSubscribe: boolean) {
        this.link = link;
        this.canSubscribe = canSubscribe;
    }
}
