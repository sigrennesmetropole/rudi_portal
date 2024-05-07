import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {DomSanitizer, SafeHtml} from '@angular/platform-browser';
import {Base64EncodedLogo, ImageLogoService} from '@core/services/image-logo.service';
import {LogService} from '@core/services/log.service';
import {KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {FooterDescription} from 'micro_service_modules/konsult/konsult-model';
import {Observable, of, switchMap} from 'rxjs';

@Component({
    selector: 'app-social-media-section',
    templateUrl: './social-media-section.component.html',
    styleUrl: './social-media-section.component.scss',
    encapsulation: ViewEncapsulation.None,
})
export class SocialMediaSectionComponent implements OnInit {

    @Input() footerDescription: FooterDescription;
    socialsNetworks: Array<{
        label: string,
        url: string,
        icon: SafeHtml
    }>;
    isError: boolean;
    isLoading: number;

    constructor(
        private konsultService: KonsultService,
        private imageLogoService: ImageLogoService,
        private logger: LogService,
        private domSanitizer: DomSanitizer
    ) {
    }

    private initIcon(uuid: string): Observable<SafeHtml> {
        return this.konsultService.downloadCustomizationResource(uuid).pipe(
            switchMap((blob: Blob) => {
                return this.imageLogoService.createImageFromBlob(blob);
            }),
            switchMap((base64: Base64EncodedLogo) => {
                return this.decodeBase64(base64);
            })
        );
    }

    decodeBase64(str: Base64EncodedLogo): Observable<SafeHtml> {
        const data = 'data:image/svg+xml;base64,';
        if (str.startsWith(data)) {
            return of(this.domSanitizer.bypassSecurityTrustHtml(atob(str.substring(data.length))));
        }
        return of(this.domSanitizer.bypassSecurityTrustHtml(''));
    }

    ngOnInit(): void {
        this.isError = false;
        this.isLoading = 0;
        this.socialsNetworks = [];
        for (const socialNetwork of this.footerDescription?.socialNetworks) {
            if (socialNetwork.label && socialNetwork.url && socialNetwork.icon) {
                this.isLoading++;
                this.initIcon(socialNetwork.icon).subscribe({
                    next: (base64: SafeHtml) => {
                        this.socialsNetworks.push({label: socialNetwork.label, url: socialNetwork.url, icon: base64});
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
