import {Component, OnInit} from '@angular/core';
import {IconRegistryService} from '../../../core/services/icon-registry.service';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';
import {ALL_TYPES} from '../../../shared/models/title-icon-type';

@Component({
    selector: 'app-selfdata-datasets',
    templateUrl: './selfdata-datasets.component.html',
    styleUrls: ['./selfdata-datasets.component.scss']
})
export class SelfdataDatasetsComponent implements OnInit {
    urlToDoc: string;
    searchUrlLoading = false;

    constructor(
        iconRegistryService: IconRegistryService,
        private readonly propertiesMetierService: PropertiesMetierService,
    ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
        this.getUrlToDoc();
    }

    ngOnInit(): void {
    }

    /**
     * on ouvre un nouvel onglet vers la documentation de l'utilisation de l'URL
     */
    getUrlToDoc(): void {
        this.searchUrlLoading = true;
        this.propertiesMetierService.get('rudidatarennes.docRudiBzh').subscribe({
            next: (link: string) => {
                this.urlToDoc = link;
                this.searchUrlLoading = false;
            },
            error: (error) => {
                this.searchUrlLoading = false;
                console.log(error);
            }
        });
    }
}
