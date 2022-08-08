import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ProjectDatasetItem} from '../../model/project-dataset-item';
import {ProjectDatasetPictoType} from '../../model/project-dataset-picto-type';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';
import {ALL_TYPES as ALL_TITLE_ICON_TYPES, TitleIconType} from '../../../shared/models/title-icon-type';

@Component({
    selector: 'app-project-dataset-list',
    templateUrl: './project-dataset-list.component.html',
    styleUrls: ['./project-dataset-list.component.scss']
})
export class ProjectDatasetListComponent implements OnInit {

    @Input()
    public items: ProjectDatasetItem[];

    @Output()
    public edit: EventEmitter<ProjectDatasetItem> = new EventEmitter<ProjectDatasetItem>();

    @Output()
    public delete: EventEmitter<ProjectDatasetItem> = new EventEmitter<ProjectDatasetItem>();

    constructor(private readonly matIconRegistry: MatIconRegistry,
                private readonly domSanitizer: DomSanitizer) {
        this.matIconRegistry.addSvgIcon(
            'rudi_picto_nouvelle_demande',
            this.domSanitizer.bypassSecurityTrustResourceUrl('/assets/images/rudi_picto_nouvelle_demande.svg')
        );
        ALL_TITLE_ICON_TYPES.forEach(titleIconType => this.addSvgIconFromTitleIcon(titleIconType));
    }

    private addSvgIconFromTitleIcon(titleIconType: TitleIconType): void {
        this.matIconRegistry.addSvgIcon(
            titleIconType,
            this.domSanitizer.bypassSecurityTrustResourceUrl(`/assets/icons/${titleIconType}.svg`)
        );
    }

    ngOnInit(): void {
    }

    public isItemLogo(item: ProjectDatasetItem): boolean {
        return item.pictoType === ProjectDatasetPictoType.LOGO;
    }

    public handleEdit(item: ProjectDatasetItem): void {
        this.edit.emit(item);
    }

    public handleDelete(item: ProjectDatasetItem): void {
        this.delete.emit(item);
    }
}
