import {Component} from '@angular/core';
import {IconRegistryService} from '@core/services/icon-registry.service';
import {ALL_TYPES} from '../models/title-icon-type';

@Component({
  selector: 'app-documentation-button',
  templateUrl: './documentation-button.component.html',
  styleUrls: ['./documentation-button.component.scss']
})
export class DocumentationButtonComponent {


    constructor(private readonly iconRegistryService: IconRegistryService, ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

}
