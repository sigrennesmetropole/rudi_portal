import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
    providedIn: 'root'
})
export class SnackBarService {

    /**
     *  Constructeur
     * @param snackBar  Instanciation SnackBar Bar Material Design (notification en bas de page)
     */
    constructor(private readonly snackBar: MatSnackBar) {
    }

    add(message: string): void {
        this.snackBar.open(message, null, {
            duration: 3000,
            panelClass: ['style-snackbar']
        });
    }
}
