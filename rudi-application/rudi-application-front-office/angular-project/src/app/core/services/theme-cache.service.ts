import {Injectable} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer, SafeResourceUrl} from '@angular/platform-browser';
import {SkosConceptsService} from 'micro_service_modules/kos/kos-api';
import {SimpleSkosConcept} from 'micro_service_modules/kos/kos-model';
import {Base64EncodedLogo, ImageLogoService} from '@core/services/image-logo.service';
import {KonsultMetierService} from '@core/services/konsult-metier.service';
import {KosMetierService} from '@core/services/kos-metier.service';
import {mapEach} from '@shared/utils/ObservableUtils';
import {Metadata} from 'micro_service_modules/api-kaccess';
import {forkJoin, Observable, of, Subject} from 'rxjs';
import {switchMap, tap} from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class ThemeCacheService {
    public isLoading$: Subject<boolean>;

    private _themes: SimpleSkosConcept[] = [];
    private readonly themeLabelsByCode: { [key: string]: string } = {};
    private readonly themePictosByCode: { [key: string]: SafeResourceUrl } = {};

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly kosMetierService: KosMetierService,
        private readonly skosConceptsService: SkosConceptsService,
        private readonly sanitizer: DomSanitizer,
        private readonly imageLogoService: ImageLogoService,
        private readonly matIconRegistry: MatIconRegistry,
    ) {
        this.isLoading$ = new Subject<boolean>();
    }

    init(): void {
        // if themes are already loaded
        if (this._themes.length > 0 && Object.keys(this.themeLabelsByCode).length > 0 && Object.keys(this.themePictosByCode).length > 0) {
            this.isLoading$.next(false);
            return;
        }

        // start loading themes
        this.isLoading$.next(true);
        this.konsultMetierService.getThemeCodes()
            .pipe(
                switchMap((themeCodes: string[]): Observable<SimpleSkosConcept[]> => {
                    return themeCodes.length > 0 ? this.kosMetierService.getThemes(themeCodes) : of([]);
                }),
                mapEach((concept: SimpleSkosConcept) => {
                    return this.skosConceptsService.downloadSkosConceptIcon(concept.concept_icon)
                        .pipe(
                            switchMap((blob: Blob) => this.imageLogoService.createImageFromBlob(blob)),
                            tap((base64: Base64EncodedLogo): void => {
                                this._themes.push(concept);
                                this.themeLabelsByCode[concept.concept_code] = concept.text;
                                this.themePictosByCode[concept.concept_code] = this.sanitizer.bypassSecurityTrustResourceUrl(base64);
                                this.matIconRegistry.addSvgIcon(
                                    concept.concept_code,
                                    this.sanitizer.bypassSecurityTrustResourceUrl(base64)
                                );
                            })
                        );
                }),
                switchMap((concepts: Observable<string>[]) => forkJoin([...concepts]))
            )
            .subscribe((): void => this.isLoading$.next(false));
    }

    get themes(): SimpleSkosConcept[] {
        return this._themes;
    }

    getThemeLabelFor(metadata: Metadata): string {
        return this.themeLabelsByCode[metadata.theme] || `[${metadata.theme}]`;
    }

    getThemePictoFor(metadata: Metadata): SafeResourceUrl {
        return this.themePictosByCode[metadata.theme];
    }
}
