import {Clipboard} from '@angular/cdk/clipboard';
import {Component, Input} from '@angular/core';

@Component({
    selector: 'app-clipboard-field',
    templateUrl: './clipboard-field.component.html',
    styleUrls: ['clipboard-field.component.scss']
})
export class ClipboardFieldComponent {
    @Input()
    content: string;

    constructor(
        private clipboard: Clipboard,
    ) {
        this.content = '';
    }

    copyContentToClipboard(): void {
        this.clipboard.copy(this.content);
    }
}
