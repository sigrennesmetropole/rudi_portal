/**
 * Indication de l'évènement de fermeture de la popin
 */
export enum CloseEvent {
    VALIDATION,
    CANCEL,
    PREVIOUS
}

/**
 * Objet permettant de savoir l'évènement de cloture d'une dialog + la donnée renvoyée par celle-ci
 */
export interface DialogClosedData<T> {
    closeEvent: CloseEvent;
    data: T;
}
