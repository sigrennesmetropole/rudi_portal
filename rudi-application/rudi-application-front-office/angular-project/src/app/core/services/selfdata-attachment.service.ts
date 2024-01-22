import {Injectable} from '@angular/core';
import {FilePreviewModel} from '@sleiss/ngx-awesome-uploader';
import {Observable} from 'rxjs';
import {HttpEvent} from '@angular/common/http';
import {DataSize} from '@shared/models/data-size';
import {DocumentMetadata, SelfdataService} from '@app/selfdata/selfdata-api';
import {PropertiesAdapter} from './properties-adapter';
import {FrontOfficeProperties} from '@app/selfdata/selfdata-model';
import {MatDialog} from '@angular/material/dialog';


@Injectable({
    providedIn: 'root'
})
export class SelfdataAttachmentService {

    private readonly propertiesAdapter: PropertiesAdapter<FrontOfficeProperties>;

    constructor(
        private readonly selfdataService: SelfdataService,
        private readonly dialog: MatDialog
    ) {
        this.propertiesAdapter = new class extends PropertiesAdapter<FrontOfficeProperties> {
            protected fetchBackendProperties(): Observable<FrontOfficeProperties> {
                return selfdataService.getFrontOfficeProperties();
            }
        }();
    }

    uploadAttachment(file: FilePreviewModel): Observable<HttpEvent<string>> {
        return this.selfdataService.uploadAttachment(file.file, 'events', true);
    }

    deleteAttachment(uuid: string): Observable<void> {
        return this.selfdataService.deleteAttachment(uuid);
    }

    getDataSizeProperty(key: string): Observable<DataSize> {
        return this.propertiesAdapter.getDataSize(key);
    }

    downloadAttachement(uuid: string): Observable<Blob> {
        return this.selfdataService.downloadAttachment(uuid);
    }

    getAttachmentMetadata(uuid: string): Observable<DocumentMetadata> {
        return this.selfdataService.getAttachmentMetadata(uuid);
    }
}
