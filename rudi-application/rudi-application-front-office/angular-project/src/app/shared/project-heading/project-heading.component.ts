import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-project-heading',
    templateUrl: './project-heading.component.html',
    styleUrls: ['./project-heading.component.scss']
})
export class ProjectHeadingComponent {

    /**
     * CHaîne base 64 du logo du projet
     */
    @Input()
    logo: string;

    /**
     * Prénom NOM du porteur de projet ou nom de l'organisation porteuse du projet
     */
    @Input()
    ownerDescription: string;

    /**
     * Titre du projet
     */
    @Input()
    projectTitle: string;

    /**
     * Chaîne décrivant le statut du projet
     */
    @Input()
    status: string;
}
