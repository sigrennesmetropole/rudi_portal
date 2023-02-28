import {FilePreviewModel} from '@sleiss/ngx-awesome-uploader';
import {Observable} from 'rxjs';
import {Injectable} from '@angular/core';
import {HttpEvent} from '@angular/common/http';
import {UploaderAdapter} from '../uploader/uploader.adapter';
import {SelfdataAttachmentService} from '../../core/services/selfdata-attachment.service';

@Injectable({
    providedIn: 'root'
})
export class AttachmentAdapter implements UploaderAdapter<string> {

    constructor(
        private readonly selfdataAttachmentService: SelfdataAttachmentService,
    ) {
    }

    uploadFile(fileItem: FilePreviewModel): Observable<HttpEvent<string>> {
        return this.selfdataAttachmentService.uploadAttachment(fileItem);
    }

    removeFile(fileItem: FilePreviewModel): Observable<void> {
        return this.selfdataAttachmentService.deleteAttachment(fileItem.uploadResponse);
    }

}
