import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {ProjectType} from '../../../projekt/projekt-model';
import {AdapterWithoutBackend} from '../../../shared/uploader/adapter-without-backend';
import {DataSize} from '../../../shared/models/data-size';
import {ProjektMetierService} from '../../../core/services/asset/project/projekt-metier.service';

@Component({
    selector: 'app-step1-reutilisation',
    templateUrl: './step1-reutilisation.component.html',
    styleUrls: ['./step1-reutilisation.component.scss']
})
export class Step1ReutilisationComponent implements OnInit {

    @Input()
    public step1FormGroup: FormGroup;

    @Input()
    public isPublished: boolean;

    @Input()
    public projectType: ProjectType[];

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

}
