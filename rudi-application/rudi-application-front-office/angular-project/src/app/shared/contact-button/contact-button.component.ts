import {Component, Input} from '@angular/core';
import {Clipboard} from '@angular/cdk/clipboard';


@Component({
    selector: 'app-contact-button',
    templateUrl: './contact-button.component.html',
    styleUrls: ['./contact-button.component.scss']
})
export class ContactButtonComponent {
    @Input() email: string;
    emailIsVisible = false;
    copiedSuccess = false;


    constructor(private readonly clipboard: Clipboard) {
    }

    clickContactButton(): void {
        this.emailIsVisible = true;
    }

    clickEmailButton(): void {
        this.copiedSuccess = true;
        this.clipboard.copy(this.email);
    }
}
