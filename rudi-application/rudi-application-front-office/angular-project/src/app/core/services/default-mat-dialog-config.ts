import {MatDialogConfig} from '@angular/material/dialog';

/**
 * Configuration par défaut des dialog material (taille etc.)
 */
export class DefaultMatDialogConfig<T> extends MatDialogConfig<T> {
    constructor() {
        super();
        this.disableClose = true;
        this.autoFocus = false;
        this.panelClass = 'my-custom-dialog-class';
        this.width = '768px';
    }
}
