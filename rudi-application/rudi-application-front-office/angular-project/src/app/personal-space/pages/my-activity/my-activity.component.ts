import {Component} from '@angular/core';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';

@Component({
  selector: 'app-my-activity',
  templateUrl: './my-activity.component.html'
})
export class MyActivityComponent {
    urlToDoc: string;
    searchUrlLoading = false;

  constructor(private readonly propertiesMetierService: PropertiesMetierService,) {
      this.getUrlToDoc();
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
