import {TitleIconType} from '@shared/models/title-icon-type';
import {ProjectDatasetPictoType} from './project-dataset-picto-type';

/**
 * Représente un item de la liste visuelles des JDDs liés d'un projet
 * un item peut être un vrai JDD lié ou alors une demande de JDD
 */
export interface ProjectDatasetItem {
    /**
     * L'identificateur unique qui sert vers le mapping de l'objet métier lié à cette vue
     */
    identifier: string;

    /**
     * Le sur-titre qui s'affiche en petit au dessus du titre
     */
    overTitle: string;

    /**
     * Le titre de l'élément, libellé principal
     */
    title: string;

    /**
     * Le type de picto de cet item
     */
    pictoType: ProjectDatasetPictoType;

    /**
     * La valeur du picto de cet item, exemple ID organisation ou nom de picto SVG
     */
    pictoValue: string;

    /**
     * L'élément est-il éditable ?
     */
    editable: boolean;

    /**
     * Icône affichée avant le {@link title titre de l'élément}
     */
    titleIcon?: TitleIconType;
}
