import {EMPTY, Observable, Observer} from 'rxjs';
import {KindOfData} from '../../api-kmedia';
import {catchError, shareReplay, switchMap} from 'rxjs/operators';

export const DEFAULT_LOGO: Base64EncodedLogo = '/assets/images/logo_rennes_metropole.svg';

export type Base64EncodedLogo = string;

export abstract class OrganizationMetierService {
    private readonly logosByOrganizationId: { [key: string]: Observable<Base64EncodedLogo> } = {};

    getLogo(organizationId: string): Observable<Base64EncodedLogo> {
        if (this.logosByOrganizationId[organizationId]) {
            return this.logosByOrganizationId[organizationId];
        }

        return this.logosByOrganizationId[organizationId] = this.downloadProducerMediaByType(organizationId, KindOfData.Logo).pipe(
            // Source pour la gestion du cache : https://betterprogramming.pub/how-to-create-a-caching-service-for-angular-bfad6cbe82b0
            shareReplay(1),
            catchError(() => this.logosByOrganizationId[organizationId] = EMPTY),
            switchMap(blob => this.createImageFromBlob(blob, organizationId)),
            catchError(() => EMPTY)
        );
    }

    protected abstract downloadProducerMediaByType(organizationId: string, kindOfData: KindOfData): Observable<Blob>;

    protected createImageFromBlob(image: Blob, organizationId: string): Observable<Base64EncodedLogo> {
        return new Observable((observer: Observer<Base64EncodedLogo>) => {
            const reader = new FileReader();
            let logo;
            reader.addEventListener('load', () => {
                logo = reader.result;
                const logoWithUuid = {
                    uuid: organizationId,
                    logo
                };
                observer.next(logo);
            }, false);

            if (image) {
                reader.readAsDataURL(image);
            }

            // TODO gestion d'erreur
        });
    }

}
