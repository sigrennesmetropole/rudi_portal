import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {AccountService} from '../core/services/account.service';
import {SnackBarService} from '../core/services/snack-bar.service';
import {MatSnackBar, MatSnackBarConfig} from '@angular/material/snack-bar';
import {AccountErrorBoxComponent} from '../shared/account-error-box/account-error-box.component';
import {HttpErrorResponse} from '@angular/common/http';

@Component({templateUrl: 'account-validation.component.html'})
export class AccountValidationComponent implements OnInit {
    /**
     * Est-ce que le composant se charge ? (traitement en cours)
     */
    loading = false;

    constructor(
        private route: ActivatedRoute,
        private snackBarService: SnackBarService,
        private router: Router,
        private snackbar: MatSnackBar,
        private accountService: AccountService
    ) {
    }

    ngOnInit(): void {
        // Recuperation du token dans la route
        this.loading = true;
        const token = this.route.snapshot.queryParams.token;
        const badRequestStatus = 400;

        this.accountService.validateAccount(token)
            .pipe(first())
            .subscribe({
                next: () => {
                    this.loading = false;
                },
                error: (err: HttpErrorResponse) => {
                    this.loading = false;
                    // Si l'utilisateur a dépassé le délai de 24 heures ou Si l'utilisateur a déjà cliqué sur le lien d'activation
                    if (err.status === badRequestStatus) {
                        const config = new MatSnackBarConfig();
                        config.panelClass = ['mat-elevation-z3', 'account-error-style'];
                        config.horizontalPosition = 'center';
                        this.snackbar.openFromComponent(AccountErrorBoxComponent, config);
                    }

                },
                complete: () => {
                    this.goToLogin();
                },
            });
    }

    goToLogin(): Promise<boolean> {
        return this.router.navigate(['/login'], {
            queryParams: {
                snackBar: 'snackbarTemplate.successAccountValidation',
                redirectTo: '/',
            }
        });
    }
}
