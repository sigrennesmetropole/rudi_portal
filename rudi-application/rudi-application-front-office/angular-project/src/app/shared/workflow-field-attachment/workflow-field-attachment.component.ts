import {Component, OnInit} from '@angular/core';
import {WorkflowFieldComponent} from '../workflow-field/workflow-field.component';
import {AttachmentAdapter} from './attachment.adapter';
import {DocumentMetadata} from '../../selfdata/selfdata-api';
import {ALL_TYPES} from '../models/title-icon-type';
import {IconRegistryService} from '../../core/services/icon-registry.service';
import {MatDialog} from '@angular/material/dialog';
import {SelfdataAttachmentService} from '../../core/services/selfdata-attachment.service';
import {Observable} from 'rxjs';
import {DefaultMatDialogConfig} from '../../core/services/default-mat-dialog-config';
import {AttachmentPopinData} from '../workflow-field-attachment-popin/attachment-popin-data';
import {WorkflowFieldAttachmentPopinComponent} from '../workflow-field-attachment-popin/workflow-field-attachment-popin.component';

@Component({
    selector: 'app-workflow-field-attachment',
    templateUrl: './workflow-field-attachment.component.html',
    styleUrls: ['./workflow-field-attachment.component.scss']
})
export class WorkflowFieldAttachmentComponent extends WorkflowFieldComponent implements OnInit {
    attachmentLoading: boolean;
    attachment: DocumentMetadata;

    constructor(
        protected readonly dialog: MatDialog,
        readonly attachmentAdapter: AttachmentAdapter,
        private readonly selfdataAttachmentService: SelfdataAttachmentService,
        private readonly iconRegistryService: IconRegistryService,
    ) {
        super();
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    ngOnInit(): void {
        if (this.readonly) {
            this.lookupAttachment();
        }
    }

    get attachmentNotFound(): boolean {
        return !this.attachment;
    }

    lookupAttachment(): void {
        this.attachmentLoading = true;
        this.selfdataAttachmentService.getAttachmentMetadata(this.formControl.value).subscribe({
            next: (result: DocumentMetadata) => {
                this.attachment = result;
                this.attachmentLoading = false;
            },
            complete: () => {
                this.attachmentLoading = false;
            },
            error: (e) => {
                this.attachmentLoading = false;
                console.error(e);
            }
        });
    }


    public handleClickAttachment(): void {
        this.openDialogWorkflowFieldAttachment(this.formControl.value).subscribe();
    }

    private openDialogWorkflowFieldAttachment(attachmentUuid: string): Observable<void> {
        const dialogConfig = new DefaultMatDialogConfig<AttachmentPopinData>();
        dialogConfig.data = {attachmentUuid};
        const dialogRef = this.dialog.open(WorkflowFieldAttachmentPopinComponent, dialogConfig);
        return dialogRef.afterClosed();
    }
}
