import {HttpEvent, HttpEventType} from '@angular/common/http';
import {AbstractControl} from '@angular/forms';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {FilePickerAdapter, FilePreviewModel, UploadResponse, UploadStatus} from '@sleiss/ngx-awesome-uploader';
import {Observable} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {ErrorWithCause} from '../models/error-with-cause';
import {UploaderAdapter} from './uploader.adapter';
import {UploaderComponent} from './uploader.component';

export class AdapterProxy<T> extends FilePickerAdapter {

    constructor(
        private readonly uploaderComponent: UploaderComponent<T>,
        private readonly adapter: UploaderAdapter<T>,
    ) {
        super();
    }

    private get fileFormControl(): AbstractControl {
        return this.uploaderComponent.fileFormControl;
    }

    private get snackBarService(): SnackBarService {
        return this.uploaderComponent.snackBarService;
    }

    private get translateService(): TranslateService {
        return this.uploaderComponent.translateService;
    }

    private get errorText(): string {
        return this.uploaderComponent.errorText;
    }

    uploadFile(fileItem: FilePreviewModel): Observable<UploadResponse> {
        return this.adapter.uploadFile(fileItem).pipe(
            catchError(err => {
                console.error('error', err);
                const message = this.translateService.instant(this.errorText);
                this.snackBarService.add(message);
                throw new ErrorWithCause(message, err);
            }),
            map((httpEvent: HttpEvent<T>) => {
                if (httpEvent.type === HttpEventType.UploadProgress) {
                    return {
                        status: UploadStatus.IN_PROGRESS,
                        progress: Math.round(httpEvent.loaded / httpEvent.total * 100),
                    };
                } else if (httpEvent.type === HttpEventType.Response) {
                    return {
                        status: UploadStatus.UPLOADED,
                        body: httpEvent.body,
                    };
                } else {
                    return {
                        status: UploadStatus.IN_PROGRESS,
                    };
                }
            }),
            tap(uploadResponse => {
                if (uploadResponse.status === UploadStatus.UPLOADED) {
                    this.fileFormControl.setValue(uploadResponse.body);
                } else if (uploadResponse.status === UploadStatus.ERROR) {
                    this.fileFormControl.reset();
                    this.fileFormControl.updateValueAndValidity();
                }
            }),
        );
    }

    // tslint:disable-next-line:no-any : Dépend de la librairie ngx-awesome-uploader
    removeFile(fileItem: FilePreviewModel): Observable<void> {
        return this.adapter.removeFile(fileItem).pipe(
            tap(() => {
                this.fileFormControl.reset();
                this.fileFormControl.updateValueAndValidity();
            })
        );
    }

}
