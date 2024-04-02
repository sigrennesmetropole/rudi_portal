import {Component} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
    selector: 'app-success-project-creation-dialog',
    templateUrl: './success-project-creation-dialog.component.html'
})
export class SuccessProjectCreationDialogComponent {

    constructor(private matIconRegistry: MatIconRegistry,
                private domSanitizer: DomSanitizer
    ) {
        this.matIconRegistry.addSvgIcon(
            'icon-close',
            this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg')
        );
    }
}
