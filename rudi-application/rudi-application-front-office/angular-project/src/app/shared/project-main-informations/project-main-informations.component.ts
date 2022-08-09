import {Component, Input, OnInit} from '@angular/core';
import * as moment from 'moment';
import {Project} from '../../projekt/projekt-model';
import {TranslateService} from '@ngx-translate/core';

@Component({
    selector: 'app-project-main-informations',
    templateUrl: './project-main-informations.component.html',
    styleUrls: ['./project-main-informations.component.scss']
})
export class ProjectMainInformationsComponent implements OnInit {
    @Input() project: Project;

    /**
     * details d'un projet ou d'une réutilisation ? réutilisation par defaut (false)
     */
    @Input() isProject = false;

    @Input() isInfosProject = false;

    constructor(private translateService: TranslateService) {
    }

    ngOnInit(): void {
    }

    /**
     * permet de récupérer la une date d'un champ de l'objet projet au format MM/YYYY
     * @param fieldName le nom du champ date de l'objet project
     * @private
     */
    private getProjectDate(fieldName: string): string {
        if (this.project && this.project[fieldName] != null) {
            return moment(this.project[fieldName]).format('MM/YYYY');
        }

        return this.translateService.instant('personalSpace.myProjects.unknown');
    }

    /**
     * Récupère la date de début du projet au format MM/YYYY
     */
    public getProjectDateDebut(): string {
        return this.getProjectDate('expected_completion_start_date');
    }

    /**
     * Récupère la date de fin du projet au format MM/YYYY
     */
    public getProjectDateFin(): string {
        return this.getProjectDate('expected_completion_end_date');
    }

    /**
     * Détermine si la période de réalisation est affichée ou pas
     */
    public isPeriodFieldDisplayed(): boolean {
        if (this.project) {
            return !(!this.project.expected_completion_end_date && !this.project.expected_completion_start_date);
        }
        return false;
    }

    /**
     * Récupération d'une chaîne de caractère représentant les éléments selectionnées dans une checkbox multiple
     */
    public getStringFromList<T>(listToConcat: T[], fieldName: string): string {
        if (listToConcat.length > 0 && listToConcat[0][fieldName] != null) {
            const labels = listToConcat.map(element => element[fieldName]);
            return labels.join(', ');
        }
        return null;
    }
}
