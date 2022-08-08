import {Component, ContentChild, Input, TemplateRef} from '@angular/core';
import {ALL_TYPES, TitleIconType} from '../models/title-icon-type';
import {IconRegistryService} from '../../core/services/icon-registry.service';

@Component({
    selector: 'app-tab',
    templateUrl: './tab.component.html',
    styleUrls: ['./tab.component.scss']
})
export class TabComponent {

    @Input()
    icon?: TitleIconType;

    @Input()
    label = 'tab';

    @Input()
    active = false;

    @Input()
    disabled = false;

    @ContentChild(TemplateRef)
        // tslint:disable-next-line:no-any
    templateRef: TemplateRef<any>;

    constructor(
        private readonly iconRegistryService: IconRegistryService,
    ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }
}
