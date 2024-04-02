import {Component, Input} from '@angular/core';
import {Router} from '@angular/router';
import {TranslateService} from '@ngx-translate/core';

@Component({
    selector: 'app-workflow-form-submit-success',
    templateUrl: './workflow-form-submit-success.component.html',
    styleUrls: ['./workflow-form-submit-success.component.scss']
})
export class WorkflowFormSubmitSuccessComponent {
    @Input()
    title1Key: string;
    @Input()
    title2Key: string;
    @Input()
    subTitleKey: string;
    @Input()
    additonalInfoKey: string;
    @Input()
    additonalInfoParams: number;
    @Input()
    descriptionBeforeLinkKey: string;
    @Input()
    descriptionLinkTextKey?: string;
    @Input()
    buttonMessageKey: string;
    @Input()
    descriptionLinkUrl: string;
    @Input()
    descriptionAfterLinkKey: string;
    @Input()
    urlToBack: string;
    @Input()
    cardIsLoading = false;

    constructor(private readonly translater: TranslateService,
                private readonly router: Router) {
    }

    get title1(): string {
        return this.translater.instant(this.title1Key);
    }

    get title2(): string {
        return this.translater.instant(this.title2Key);
    }

    get subTitle(): string {
        return this.translater.instant(this.subTitleKey);
    }

    get additonalInfo(): string {
        return this.additonalInfoKey && this.translater.instant(this.additonalInfoKey, {maximumDelay: this.additonalInfoParams});
    }

    get descriptionBeforeLink(): string {
        return this.translater.instant(this.descriptionBeforeLinkKey);
    }

    get descriptionLinkText(): string {
        return this.translater.instant(this.descriptionLinkTextKey);
    }

    get descriptionAfterLink(): string {
        return this.translater.instant(this.descriptionAfterLinkKey);
    }

    get buttonMessage(): string {
        return this.translater.instant(this.buttonMessageKey);
    }

    handleClickToBack(): void {
        this.router.navigate([this.urlToBack]);
    }
}
