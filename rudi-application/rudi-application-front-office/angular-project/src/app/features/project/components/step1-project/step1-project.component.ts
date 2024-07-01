import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {ProjektMetierService} from '@core/services/asset/project/projekt-metier.service';
import {DataSize} from '@shared/models/data-size';
import {RadioListItem} from '@shared/radio-list/radio-list-item';
import {AdapterWithoutBackend} from '@shared/uploader/adapter-without-backend';
import {ReutilisationStatus} from 'micro_service_modules/projekt/projekt-api';
import {ProjectType, Support, TargetAudience, TerritorialScale} from 'micro_service_modules/projekt/projekt-model';

@Component({
    selector: 'app-step1-project',
    templateUrl: './step1-project.component.html',
    styleUrls: ['./step1-project.component.scss']
})
export class Step1ProjectComponent implements OnInit {

    @Input()
    public isPublished: boolean;

    @Input()
    public isConfidentialityValid: boolean;

    @Input()
    public step1FormGroup: FormGroup;

    @Input()
    public suggestions: RadioListItem[];

    @Input()
    public territoireScale: TerritorialScale[];

    @Input()
    public supports: Support[];

    @Input()
    public projectType: ProjectType[];

    @Input()
    public publicCible: TargetAudience[];

    @Input()
    public reuseStatus: ReutilisationStatus[];

    @Output()
    public imageModified: EventEmitter<Blob> = new EventEmitter<Blob>();

    adapter = new AdapterWithoutBackend();

    /**
     * Taille maximale acceptée par le backend, pour l'upload de fichier.
     */
    fileMaxSize: DataSize;

    /** Extensions acceptées par le backend, pour l'upload de fichier. */
    fileExtensions: string[];

    constructor(
        private readonly projektMetierService: ProjektMetierService,
    ) {
    }

    ngOnInit(): void {
        this.projektMetierService.getDataSizeProperty('spring.servlet.multipart.max-file-size')
            .subscribe(value => this.fileMaxSize = value);
        this.projektMetierService.getStrings('projekt.project-media.logo.extensions')
            .subscribe(value => this.fileExtensions = value);
    }

    /**
     * On gère l'événement de changement de l'image
     */
    public handleImageChanged(): void {
        const container = this.step1FormGroup.get('image').value;
        if (container) {
            this.imageModified.emit(container.file);
        } else {
            this.imageModified.emit(null);
        }
    }

    compareObjects(object1: any, object2: any): boolean {
        return object1 && object2 && object1.uuid === object2.uuid;
    }
}
