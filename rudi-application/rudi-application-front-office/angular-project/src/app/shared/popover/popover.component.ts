import {Component, Input, OnInit} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
    selector: 'app-popover',
    templateUrl: './popover.component.html',
    styleUrls: ['./popover.component.scss']
})
export class PopoverComponent implements OnInit {
    @Input()
    public buttonLogo: string;
    @Input()
    public buttonMessageBody: string;
    @Input()
    public buttonMessageFooter: string;
    @Input()
    public buttonMessageTitle: string;

    constructor( private matIconRegistry: MatIconRegistry,
                 private domSanitizer: DomSanitizer) {
        this.matIconRegistry.addSvgIcon(
            'rudi_picto_reutilisations',
            this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/images/rudi_picto_reutilisations.svg')
        );
        this.matIconRegistry.addSvgIcon(
            'rudi_picto_projet',
            this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/images/rudi_picto_projet.svg')
        );
    }

    ngOnInit(): void {
    }

}
