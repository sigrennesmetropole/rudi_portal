import {Component, OnInit} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
    selector: 'app-success-project-creation-dialog',
    templateUrl: './success-project-creation-dialog.component.html',
    styleUrls: ['./success-project-creation-dialog.component.scss']
})
export class SuccessProjectCreationDialogComponent implements OnInit {

    constructor(private matIconRegistry: MatIconRegistry,
                private domSanitizer: DomSanitizer
    ) {
        this.matIconRegistry.addSvgIcon(
            'icon-close',
            this.domSanitizer.bypassSecurityTrustResourceUrl('assets/icons/icon-close.svg')
        );
    }

    ngOnInit(): void {
    }
}
