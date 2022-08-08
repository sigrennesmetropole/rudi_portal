import {Injectable} from '@angular/core';
import {Metadata} from '../../api-kaccess';
import {switchMap} from 'rxjs/operators';
import {of} from 'rxjs';
import {KosMetierService} from './kos-metier.service';
import {KonsultMetierService} from './konsult-metier.service';
import {SimpleSkosConcept} from '../../kos/kos-model';

@Injectable({
    providedIn: 'root'
})
export class ThemeCacheService {
    private readonly themeLabelsByCode: { [key: string]: string } = {};
    private readonly themePictosByCode: { [key: string]: string } = {};

    constructor(
        private readonly konsultMetierService: KonsultMetierService,
        private readonly kosMetierService: KosMetierService,
    ) {
        this.konsultMetierService.getThemeCodes().pipe(
            switchMap(themeCodes => themeCodes.length > 0 ? this.kosMetierService.getThemes(themeCodes) : of([]))
        ).subscribe(concepts => {
            this._themes = concepts;
            concepts.forEach(concept => {
                this.themeLabelsByCode[concept.concept_code] = concept.text;
                this.themePictosByCode[concept.concept_code] = KosMetierService
                    .getMiniAssetNameFromConceptIcon(concept.concept_icon);
            });
        });
    }

    private _themes: SimpleSkosConcept[];

    get themes(): SimpleSkosConcept[] {
        return this._themes;
    }

    getThemeLabelFor(metadata: Metadata): string {
        return this.themeLabelsByCode[metadata.theme] || `[${metadata.theme}]`;
    }

    getThemePictoFor(metadata: Metadata): string {
        return this.themePictosByCode[metadata.theme];
    }
}
