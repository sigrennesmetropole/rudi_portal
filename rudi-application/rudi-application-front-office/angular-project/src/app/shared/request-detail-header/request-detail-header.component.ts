import {Component, Input, OnInit} from '@angular/core';
import {IconRegistryService} from '../../core/services/icon-registry.service';
import {ALL_TYPES} from '../models/title-icon-type';

@Component({
    selector: 'app-request-detail-header',
    templateUrl: './request-detail-header.component.html',
    styleUrls: ['./request-detail-header.component.scss']
})
export class RequestDetailHeaderComponent implements OnInit {

    @Input() headingLoading: boolean;
    @Input() pageTitle: string;
    @Input() pageSubTitle: string;
    @Input() status: string;
    @Input() icon: string;

    constructor(iconRegistryService: IconRegistryService,
    ) {
        iconRegistryService.addAllSvgIcons(ALL_TYPES);
    }

    ngOnInit(): void {
    }

}
