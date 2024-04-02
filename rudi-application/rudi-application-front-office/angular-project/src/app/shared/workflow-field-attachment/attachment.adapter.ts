import {HttpEvent} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {SelfdataAttachmentService} from '@core/services/selfdata-attachment.service';
import {FilePreviewModel} from '@sleiss/ngx-awesome-uploader';
import {Observable} from 'rxjs';
import {UploaderAdapter} from '../uploader/uploader.adapter';

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
