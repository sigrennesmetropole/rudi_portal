import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {Data, Level, NotificationTemplateComponent} from '../../shared/notification-template/notification-template.component';

@Injectable({
    providedIn: 'root'
})
export class SnackBarService {
    duration = 3000;
    setAutoDuratione = true;

    /**
     *  Constructeur
     * @param snackBar  Instanciation SnackBar Bar Material Design (notification en bas de page)
     */
    constructor(private readonly snackBar: MatSnackBar) {
    }

    private static getPanelClass(data: string | Data): string | undefined {
        const level = (data as Data).level;
        if (level === Level.ERROR) {
            return 'red-snackbar';
        }
        return undefined;
    }

    add(message: string): void {
        const data: Data = {
            message,
            level: Level.ERROR
        };
        this.openSnackBar(data, this.setAutoDuratione ? this.duration : 0);
    }

    /**
     * Permet d'afficher le message quand l'inscription se passe bien
     */
    openSnackBar(data: string | Data, duration?: number): void {
        this.snackBar.openFromComponent(NotificationTemplateComponent, {
            data,
            duration,
            panelClass: SnackBarService.getPanelClass(data)
        });
    }
}
