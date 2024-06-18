import {Injectable} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {CustomizationDescription, KonsultService} from 'micro_service_modules/konsult/konsult-api';
import {Observable} from 'rxjs';
import {shareReplay} from 'rxjs/operators';


@Injectable({
    providedIn: 'root'
})
export class CustomizationService {
    private customizationDescription$: Observable<CustomizationDescription>;

    constructor(
        private readonly konsultService: KonsultService,
        private readonly translateService: TranslateService
    ) {
    }

    getCustomizationDescription() {
        if (!this.customizationDescription$) {
            this.customizationDescription$ = this.konsultService.getCustomizationDescription(this.translateService.currentLang).pipe(
                shareReplay(1)
            );
        }
        return this.customizationDescription$;
    }
}
