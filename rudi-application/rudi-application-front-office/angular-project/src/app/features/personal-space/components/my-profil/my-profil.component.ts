import {Component, OnInit} from '@angular/core';
import {TranslateService} from '@ngx-translate/core';
import {User} from 'micro_service_modules/acl/acl-model';
import {UserService} from '@core/services/user.service';

@Component({
    selector: 'app-my-profil',
    templateUrl: './my-profil.component.html',
    styleUrls: ['./my-profil.component.scss']
})
export class MyProfilComponent implements OnInit {
    public user: User | undefined;
    email: string;
    isLoading: boolean;

    constructor(
        private readonly translateService: TranslateService,
        private readonly utilisateurService: UserService,
    ) {}

    ngOnInit(): void {
        this.isLoading = true;
        // récupération de l'évènement d'authentification
        this.utilisateurService.getConnectedUser()
            .subscribe(
                {
                    next: (user: User | undefined) => {
                        this.user = user;
                        this.email = this.getEmail();
                        this.isLoading = false;
                    },
                    error: (e) => {
                        console.error(e);
                        this.isLoading = false;
                    }
                }
            );
    }

    getEmail(): string {
        return this.utilisateurService.lookupEMailAddress(this.user);
    }
}
