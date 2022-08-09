import {Component, EventEmitter, Input, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {RadioListItem} from '../../../shared/radio-list/radio-list-item';
import {ProjectType, Support, TargetAudience, TerritorialScale} from '../../../projekt/projekt-model';

@Component({
    selector: 'app-step1-project',
    templateUrl: './step1-project.component.html',
    styleUrls: ['./step1-project.component.scss']
})
export class Step1ProjectComponent {

    @Input()
    public isPublished: boolean;

    @Input()
    public step1FormGroup: FormGroup;

    @Input()
    public suggestions: RadioListItem[];

    @Input()
    public territoireScale: TerritorialScale[];

    @Input()
    public supports: Support[];

    @Input()
    public projectType: ProjectType[];

    @Input()
    public publicCible: TargetAudience[];

    @Output()
    public imageModified: EventEmitter<Blob> = new EventEmitter<Blob>();

    /**
     * On gère l'événement de changement de l'image
     */
    public handleImageChanged(): void {
        const container = this.step1FormGroup.get('image').value;
        if (container) {
            this.imageModified.emit(container.file);
        } else {
            this.imageModified.emit(null);
        }
    }
}
