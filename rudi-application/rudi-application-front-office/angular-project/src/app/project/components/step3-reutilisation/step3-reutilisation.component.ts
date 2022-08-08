import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Metadata} from '../../../api-kaccess';
import {ProjectDatasetItem} from '../../model/project-dataset-item';

@Component({
    selector: 'app-step3-reutilisation',
    templateUrl: './step3-reutilisation.component.html',
    styleUrls: ['./step3-reutilisation.component.scss']
})
export class Step3ReutilisationComponent implements OnInit {

    @Input()
    public step3FormGroup: FormGroup;

    @Input()
    public isPublished: boolean;

    @Input()
    public linkedDatasetsError: boolean;

    @Input()
    public linkedDatasets: ProjectDatasetItem[];

    @Input()
    public createdProjectLink: string;

    @Output()
    private dialogOpened: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    private datasetRemoved: EventEmitter<ProjectDatasetItem> = new EventEmitter<ProjectDatasetItem>();

    constructor() {
    }

    ngOnInit(): void {
    }

    public openDialog(): void {
        this.dialogOpened.emit();
    }

    public removeDataset(item: ProjectDatasetItem): void {
        this.datasetRemoved.emit(item);
    }
}
