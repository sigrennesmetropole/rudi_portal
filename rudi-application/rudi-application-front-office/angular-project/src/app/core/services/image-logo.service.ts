import {Injectable} from '@angular/core';
import {Observable, Observer} from 'rxjs';

export type Base64EncodedLogo = string;

export const DEFAULT_LOGO: Base64EncodedLogo = '/assets/images/logo_rennes_metropole.svg';

@Injectable({
    providedIn: 'root'
})
export class ImageLogoService {

    /**
     * Conversion asynchrone d'un Blob en image64
     * @param image le blob contenant l'image
     */
    public createImageFromBlob(image: Blob): Observable<Base64EncodedLogo> {
        return new Observable((observer: Observer<Base64EncodedLogo>) => {
            const reader = new FileReader();
            let logo;
            reader.addEventListener('load', () => {
                logo = reader.result;
                observer.next(logo);
                observer.complete();
            }, false);

            if (image) {
                reader.readAsDataURL(image);
            }
            else {
                observer.error('Erreur tentative de conversion avec un blob nul');
            }
        });
    }
}
