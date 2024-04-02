import {Component, OnInit} from '@angular/core';
import {LogService} from '@core/services/log.service';
import {PropertiesMetierService} from '@core/services/properties-metier.service';

@Component({
  selector: 'app-my-account',
  templateUrl: './my-account.component.html',
  styleUrls: ['./my-account.component.scss']
})
export class MyAccountComponent implements OnInit {
    isLoading: boolean;
    urlToDoc: string;

    constructor(
        private readonly propertiesMetierService: PropertiesMetierService,
        private readonly logService: LogService
    ) {}

    ngOnInit(): void {
        this.isLoading = true;
        this.propertiesMetierService.get('rudidatarennes.docRudiBzh').subscribe({
            next: (rudiDocLink: string) => {
                this.urlToDoc = rudiDocLink;
                this.isLoading = false;
            },
            error: (err) => {
                this.logService.error(err);
                this.isLoading = false;
            }
        });
    }
}
