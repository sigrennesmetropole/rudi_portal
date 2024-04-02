import {Component, Input, OnInit} from '@angular/core';
import {Base64EncodedLogo, ImageLogoService} from '@core/services/image-logo.service';
import {KeyFiguresDescription, KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {switchMap} from 'rxjs/operators';

@Component({
    selector: 'app-key-figures-section',
    templateUrl: 'key-figures-section.component.html',
    styleUrls: ['./key-figures-section.component.scss']
})
export class KeyFiguresSectionComponent implements OnInit {

    @Input() keyFiguresDescription: KeyFiguresDescription;
    logoSrc: Base64EncodedLogo;
    loadingLogo: boolean;

    constructor(
        private readonly konsultService: KonsultService,
        private readonly imageLogoService: ImageLogoService
    ) {
        this.loadingLogo = true;
        this.logoSrc = '/assets/images/default_logo_key_figures.svg';
    }

    ngOnInit(): void {
        this.initImage();
    }

    private initImage() {
        if (this.keyFiguresDescription?.keyFiguresLogo) {
            this.konsultService.downloadCustomizationResource(this.keyFiguresDescription.keyFiguresLogo)
                .pipe(switchMap((blob: Blob) => this.imageLogoService.createImageFromBlob(blob)))
                .subscribe((base64: Base64EncodedLogo) => {
                    this.logoSrc = base64;
                    this.loadingLogo = false;
                });
        } else {
            this.loadingLogo = false;
        }
    }
}
