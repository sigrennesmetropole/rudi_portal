import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-project-basic-details',
    templateUrl: './project-basic-details.component.html',
    styleUrls: ['./project-basic-details.component.scss']
})
export class ProjectBasicDetailsComponent {

    /**
     * Chaîne décrivant le type de projet dont il s'agit
     */
    @Input()
    projectType: string;

    /**
     * La chaîne décrivant qui est le porteur, utilisateur ou organisation
     */
    @Input()
    ownerDescription: string;

    /**
     * email de contact du porteur de projet
     */
    @Input()
    ownerEmail: string;

    /**
     * Chaîne au format DD/MM/YYYY de la date de création du projet
     */
    @Input()
    creationDate: string;

    /**
     * Chaîne au format DD/MM/YYYY de la date de MAJ du projet
     */
    @Input()
    updatedDate: string;
}
