import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {MatRadioChange} from '@angular/material/radio';
import {PropertiesMetierService} from '@core/services/properties-metier.service';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {Level} from '@shared/notification-template/notification-template.component';
import {OwnerType} from 'micro_service_modules/projekt/projekt-model';
import {forkJoin} from 'rxjs';
import {OrganizationItem} from '../../model/organization-item';

@Component({
    selector: 'app-step2-project',
    templateUrl: './step2-project.component.html',
    styleUrls: ['./step2-project.component.scss']
})
export class Step2ProjectComponent {

    @Input()
    public step2FormGroup: FormGroup;

    @Input()
    public organizationItems: OrganizationItem[];

    constructor(
        private readonly snackBarService: SnackBarService,
        private readonly translateService: TranslateService,
        private readonly propertiesMetierService: PropertiesMetierService,
    ) {
    }

    onChangeOwnerType($event: MatRadioChange): void {
        const ownerType = $event.value as OwnerType;
        if (ownerType === OwnerType.Organization && !this.organizationItems?.length) {
            forkJoin({
                messageBeforeLink: this.translateService.get('project.stepper.submission.step2.ownerType.organization.userHasNoOrganization.messageBeforeLink'),
                linkHref: this.propertiesMetierService.get('rudidatarennes.contact'),
                linkLabel: this.translateService.get('project.stepper.submission.step2.ownerType.organization.userHasNoOrganization.linkLabel'),
                messageAfterLink: this.translateService.get('project.stepper.submission.step2.ownerType.organization.userHasNoOrganization.messageAfterLink'),
            }).subscribe(({messageBeforeLink, linkHref, linkLabel, messageAfterLink}) => {
                this.snackBarService.openSnackBar({
                    message: `${messageBeforeLink}<a href="${linkHref}">${linkLabel}</a>${messageAfterLink}`,
                    level: Level.ERROR,
                });
            });
        }
    }
}
