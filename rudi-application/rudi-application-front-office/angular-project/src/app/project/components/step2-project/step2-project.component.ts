import {Component, Input} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {OrganizationItem} from '../../model/organization-item';
import {MatRadioChange} from '@angular/material/radio';
import {OwnerType} from '../../../projekt/projekt-model';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {Level} from '../../../shared/notification-template/notification-template.component';
import {forkJoin} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';

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
                messageBeforeLink: this.translateService.get('project.stepper.common.step2.ownerType.organization.userHasNoOrganization.messageBeforeLink'),
                linkHref: this.propertiesMetierService.get('rudidatarennes.contact'),
                linkLabel: this.translateService.get('project.stepper.common.step2.ownerType.organization.userHasNoOrganization.linkLabel'),
                messageAfterLink: this.translateService.get('project.stepper.common.step2.ownerType.organization.userHasNoOrganization.messageAfterLink'),
            }).subscribe(({messageBeforeLink, linkHref, linkLabel, messageAfterLink}) => {
                this.snackBarService.openSnackBar({
                    message: `${messageBeforeLink}<a href="${linkHref}">${linkLabel}</a>${messageAfterLink}`,
                    level: Level.ERROR,
                });
            });
        }
    }
}
