import {Observable, Observer} from 'rxjs';

/**
 * Effectue le téléchargement du Blob pour récupérer son contenu en mémoire côté front
 * @param blob le blob a télécharger
 * @private
 */
export function readFile(blob: Blob): Observable<ArrayBuffer> {
    return new Observable<ArrayBuffer>((observer: Observer<ArrayBuffer>) => {
        const reader = new FileReader();

        reader.onload = (event: ProgressEvent<FileReader>) => {
            observer.next(event.target.result as ArrayBuffer);
            observer.complete();
        };

        reader.onerror = (event: ProgressEvent<FileReader>) => {
            observer.error(event);
        };

        reader.readAsArrayBuffer(blob);
    });
}
