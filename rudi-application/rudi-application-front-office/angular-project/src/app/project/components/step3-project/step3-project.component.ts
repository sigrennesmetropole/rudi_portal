import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {MatDialog} from '@angular/material/dialog';
import {ProjectDatasetItem} from '../../model/project-dataset-item';

@Component({
    selector: 'app-step3-project',
    templateUrl: './step3-project.component.html',
    styleUrls: ['./step3-project.component.scss']
})
export class Step3ProjectComponent implements OnInit {

    @Input()
    public step3FormGroup: FormGroup;

    @Input()
    public linkedDatasetsError: boolean;

    @Input()
    public isPublished: boolean;

    @Input()
    public isSubmitted: boolean;

    @Input()
    public datasetItems: ProjectDatasetItem[];

    @Input()
    public createdProjectLink: string;

    @Output()
    private datasetsDialogOpened: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    private requestDatasetDialogOpened: EventEmitter<void> = new EventEmitter<void>();

    @Output()
    private itemRemoved: EventEmitter<ProjectDatasetItem> = new EventEmitter<ProjectDatasetItem>();

    @Output()
    private itemEdited: EventEmitter<ProjectDatasetItem> = new EventEmitter<ProjectDatasetItem>();

    constructor(public dialog: MatDialog) {
    }

    ngOnInit(): void {
    }
    public openDialogDatasets(): void {
        this.datasetsDialogOpened.emit();
    }

    public openDialogRequestDataset(): void {
        this.requestDatasetDialogOpened.emit();
    }

    public handleRemoveItem(item: ProjectDatasetItem): void {
        this.itemRemoved.emit(item);
    }

    public handleEditItem(item: ProjectDatasetItem): void {
        this.itemEdited.emit(item);
    }
}
