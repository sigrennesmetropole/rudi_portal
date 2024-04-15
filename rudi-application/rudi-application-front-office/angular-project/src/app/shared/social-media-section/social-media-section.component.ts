import {Component, Input, OnInit} from '@angular/core';
import {Base64EncodedLogo, ImageLogoService} from '@core/services/image-logo.service';
import {LogService} from '@core/services/log.service';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {FooterDescription} from 'micro_service_modules/konsult/konsult-model';
import {Observable, switchMap} from 'rxjs';

@Component({
    selector: 'app-social-media-section',
    templateUrl: './social-media-section.component.html',
    styleUrl: './social-media-section.component.scss'
})
export class SocialMediaSectionComponent implements OnInit {

    @Input() footerDescription: FooterDescription;
    isError: boolean;
    isLoading: number;

    constructor(
        private konsultService: KonsultService,
        private imageLogoService: ImageLogoService,
        private logger: LogService,
    ) {
    }

    private initIcon(uuid: string): Observable<Base64EncodedLogo> {
        return this.konsultService.downloadCustomizationResource(uuid).pipe(
            switchMap((blob: Blob) => {
                return this.imageLogoService.createImageFromBlob(blob);
            })
        );
    }


    ngOnInit(): void {
        this.isError = false;
        this.isLoading = 0;
        for (const socialNetwork of this.footerDescription?.socialNetworks) {
            if (socialNetwork.label && socialNetwork.url && socialNetwork.icon) {
                this.isLoading++;
                this.initIcon(socialNetwork.icon).subscribe({
                    next: base64 => {
                        socialNetwork.icon = base64;
                    },
                    error: err => {
                        this.isError = true;
                        this.logger.error(err);
                    },
                    complete: () => {
                        this.isLoading--;
                    }
                });
            }
        }
    }


}
