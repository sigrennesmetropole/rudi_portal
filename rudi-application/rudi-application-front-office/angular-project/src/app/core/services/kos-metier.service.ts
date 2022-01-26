import {Injectable} from '@angular/core';
import {SimpleSkosConcept, SkosConceptsService} from '../../kos/kos-api';
import {LanguageService} from '../../i18n/language.service';
import {EMPTY, Observable, of} from 'rxjs';
import {defaultIfEmpty, switchMap} from 'rxjs/operators';
import {PageResultUtils} from '../../shared/utils/page-result-utils';

const THEMES_AND_KEYWORDS_SCHEME_CODE = 'scheme-keyword';

const CONCEPT_ICON_REGEX = /^assets\/pictos\/(\w+\.svg)$/;

@Injectable({
    providedIn: 'root'
})
export class KosMetierService {

    constructor(private skosConceptsService: SkosConceptsService,
                private languageService: LanguageService) {
    }

    static getAssetNameFromConceptIcon(conceptIcon: string, namePrefix = ''): string {
        const matches: RegExpExecArray = CONCEPT_ICON_REGEX.exec(conceptIcon);
        if (!matches) {
            throw new Error('conceptIcon does not match expected regex : ' + CONCEPT_ICON_REGEX);
        }
        const filename = matches[1];
        return namePrefix + filename;
    }

    static getMiniAssetNameFromConceptIcon(conceptIcon: string): string {
        return this.getAssetNameFromConceptIcon(conceptIcon, 'mini_');
    }

    /**
     * Fonction permettant de retourner le text en francais du licence Code
     */
    getLicenceLabelFromCode(licenceCode: string): Observable<string> {
        return this.getLicence(licenceCode).pipe(
            switchMap(skosConcept => of(skosConcept.text)),
            defaultIfEmpty(licenceCode)
        );
    }

    getConceptUriFromCode(licenceCode: string): Observable<string> {
        return this.getLicence(licenceCode).pipe(
            switchMap(skosConcept => of(skosConcept.concept_uri))
        );
    }

    getThemes(codes: string[]): Observable<SimpleSkosConcept[]> {
        const lang = this.languageService.getCurrentLanguage();
        const schemeCode = THEMES_AND_KEYWORDS_SCHEME_CODE;
        const role = 'theme';

        return PageResultUtils.fetchAllElementsUsing(offset =>
            this.skosConceptsService
                .searchSkosConcepts(null, offset, null, lang, null, null, [role], codes, [schemeCode], null));
    }

    getTheme(themeCode: string): Observable<SimpleSkosConcept> {
        return this.getConcept(THEMES_AND_KEYWORDS_SCHEME_CODE, themeCode, ['theme']);
    }

    private getLicence(licenceCode: string): Observable<SimpleSkosConcept> {
        return this.getConcept('scheme-licence', licenceCode);
    }

    private getConcept(schemeCode: string, conceptCode: string, roles?: string[]): Observable<SimpleSkosConcept> {
        const limit = 1;
        const lang = this.languageService.getCurrentLanguage();
        return this.skosConceptsService
            .searchSkosConcepts(limit, null, null, lang, null, null, roles, [conceptCode], [schemeCode], null)
            .pipe(
                switchMap(skosResults => skosResults.elements.length > 0 ? of(skosResults.elements[0]) : EMPTY)
            );
    }

}
