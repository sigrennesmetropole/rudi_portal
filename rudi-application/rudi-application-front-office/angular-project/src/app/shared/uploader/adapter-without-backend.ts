import {FilePreviewModel} from '@sleiss/ngx-awesome-uploader';
import {Observable, of} from 'rxjs';
import {UploaderAdapter} from './uploader.adapter';
import {HttpEvent, HttpResponse} from '@angular/common/http';

export class AdapterWithoutBackend implements UploaderAdapter<FilePreviewModel> {

    uploadFile(fileItem: FilePreviewModel): Observable<HttpEvent<FilePreviewModel>> {
        return of(new HttpResponse({
            body: fileItem
        }));
    }

    // tslint:disable-next-line:no-any : Dépend de la librairie ngx-awesome-uploader
    removeFile(fileItem: FilePreviewModel): Observable<any> {
        return of(void 0);
    }

}
