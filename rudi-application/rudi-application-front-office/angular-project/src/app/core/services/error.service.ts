import {Injectable} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";

@Injectable({
    providedIn: 'root'
})
export class ErrorService {

    constructor(private translateService: TranslateService) {
    }


    public fomatError(error: any, defaultMsg?: string): string {

        let errorText: string;

        // Erreur NULL - Message générique
        if (error == null || error.status == null) {
            errorText = this.translateService.instant("error.internalError");
        }

        switch (error.status) {

            case 500 :
                break;
            case 401:
                errorText = this.translateService.instant("authentification.error.authentificationFailed");
                break;
            case 403:
                errorText = this.translateService.instant("authentification.error.habilitation");
                break;

            default:
                errorText = this.translateService.instant("error.internalError");
        }


        return errorText;

    }
}
