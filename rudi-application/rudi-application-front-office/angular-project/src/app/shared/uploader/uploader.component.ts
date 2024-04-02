import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AbstractControl} from '@angular/forms';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {SnackBarService} from '@core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {FilePreviewModel, ValidationError} from '@sleiss/ngx-awesome-uploader';
import {UploaderCaptions} from '@sleiss/ngx-awesome-uploader/lib/uploader-captions';
import {AdapterProxy} from './adapter-proxy';
import {UploaderAdapter} from './uploader.adapter';

@Component({
    selector: 'app-uploader',
    templateUrl: './uploader.component.html',
    styleUrls: ['./uploader.component.scss']
})
export class UploaderComponent<T> {

    @Input()
    set adapter(adapter: UploaderAdapter<T>) {
        this.adapterProxy = new AdapterProxy(this, adapter);
    }

    adapterProxy: AdapterProxy<T>;

    /**
     * On demande le formControl pour pouvoir le réinitialiser en cas d'erreur, mais on ne positionne pas sa valeur.
     * C'est au composant utilisant de le faire.
     */
    @Input() fileFormControl: AbstractControl;
    @Input() imageText: string;
    @Input() errorText: string;
    @Input() imageFormat: string;
    @Input() enableCropper = false;
    @Input() fileMaxCount: number;
    /** Max size of selected file in MB. Default: no limit */
    @Input() fileMaxSize: number;
    @Input() fileExtensions: string[];

    @Output() fileChanged: EventEmitter<FilePreviewModel> = new EventEmitter<FilePreviewModel>();

    constructor(
        public snackBarService: SnackBarService,
        public translateService: TranslateService,
        private matIconRegistry: MatIconRegistry,
        private domSanitizer: DomSanitizer,
    ) {
        this.matIconRegistry.addSvgIcon(
            'rudi_picto_image.svg',
            this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/images/rudi_picto_image.svg')
        );
    }

    public onValidationError(validationError: ValidationError): void {
        this.fileFormControl.reset();
        this.fileFormControl.updateValueAndValidity();
        const {error} = validationError;
        const key = `common.fileValidationError.${error}`;
        this.snackBarService.add(this.translateService.instant(key));
    }

    public onUploadSuccess($event: FilePreviewModel): void {
        this.fileChanged.emit($event);
    }

    get captions(): UploaderCaptions {
        return {
            dropzone: {
                title: '',
                or: '',
                browse: '',
            },
            cropper: {
                crop: this.translateService.instant('uploader.cropper.crop'),
                cancel: this.translateService.instant('uploader.cropper.cancel'),
            },
            previewCard: {
                remove: '',
                uploadError: '',
                download: ''
            }
        };
    }

    get cropperOptions(): object {
        return {
            aspectRatio: 416 / 220,
            minContainerWidth: 600,
            minContainerHeight: 450,
            autoCropArea: 1,
        };
    }

}

