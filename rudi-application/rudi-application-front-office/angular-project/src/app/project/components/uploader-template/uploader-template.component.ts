import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MediaSize} from '../../../core/services/breakpoint-observer.service';
import {HttpClient} from '@angular/common/http';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {Observable, of} from 'rxjs';
import {FilePickerAdapter, FilePreviewModel, FileValidationTypes, UploadResponse, ValidationError} from '@sleiss/ngx-awesome-uploader';
import {FormControl, FormGroup} from '@angular/forms';
import {delay} from 'rxjs/operators';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
    selector: 'app-uploader-template',
    templateUrl: './uploader-template.component.html',
    styleUrls: ['./uploader-template.component.scss']
})
export class UploaderTemplateComponent extends FilePickerAdapter implements OnInit {
    @Input() mediaSize: MediaSize;
    @Input() formGroup: FormGroup;
    @Output() imageChanged: EventEmitter<Blob> = new EventEmitter<Blob>();

    constructor(private http: HttpClient,
                private snackBarService: SnackBarService,
                private translateService: TranslateService,
                private matIconRegistry: MatIconRegistry,
                private domSanitizer: DomSanitizer) {
        super();
        this.matIconRegistry.addSvgIcon(
            'rudi_picto_image.svg',
            this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/images/rudi_picto_image.svg')
        );
    }

    ngOnInit(): void {
    }

    get control() {
        return this.control.control as FormControl;
    }


    public onValidationError(validationError: ValidationError): void {
        const {error} = validationError;
        const key = error === FileValidationTypes.fileMaxCount ? `common.fileValidationError.${error}` : 'common.fileExtensionsError';
        this.snackBarService.add(this.translateService.instant(key));
    }

    public myCustomValidator(file: File): Observable<boolean> {
        if (!file.name.includes('uploader')) {
            return of(true).pipe(delay(1000));
        }
        if (file.size > 2000) {
            this.snackBarService.add(this.translateService.instant('common.fileSizeError'));
        }
        return of(false).pipe(delay(1000));
    }

    uploadFile(fileItem?: FilePreviewModel): Observable<UploadResponse> {
        this.formGroup.controls.image.setValue(fileItem);
        this.imageChanged.emit(fileItem.file);
        return of();
    }

    public removeFile(fileItem: FilePreviewModel): Observable<any> {
        this.formGroup.controls.image.reset();
        this.imageChanged.emit(null);
        return of(true);
    }
}
