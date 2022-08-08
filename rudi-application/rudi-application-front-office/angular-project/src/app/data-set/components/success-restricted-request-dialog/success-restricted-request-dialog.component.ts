import { Component, OnInit } from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-success-restricted-request-dialog',
  templateUrl: './success-restricted-request-dialog.component.html',
  styleUrls: ['./success-restricted-request-dialog.component.scss']
})
export class SuccessRestrictedRequestDialogComponent implements OnInit {

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
