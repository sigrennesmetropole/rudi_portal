import {FilePreviewModel} from '@sleiss/ngx-awesome-uploader';
import {Observable} from 'rxjs';
import {HttpEvent} from '@angular/common/http';

export interface UploaderAdapter<T> {
    uploadFile(fileItem: FilePreviewModel): Observable<HttpEvent<T>>;

    removeFile(fileItem: FilePreviewModel): Observable<void>;
}
