import {Component, Input, OnInit} from '@angular/core';
import {Form} from '../../../selfdata/selfdata-api';
import {RequestDetailDependencies} from '../../pages/request-detail-dependencies';

@Component({
    selector: 'app-selfdata-main-information',
    templateUrl: './selfdata-main-information.component.html',
    styleUrls: ['./selfdata-main-information.component.scss']
})
export class SelfdataMainInformationComponent implements OnInit {
    @Input() task: RequestDetailDependencies;
    @Input() taskLoading: boolean;
    @Input() formLoading: boolean;
    @Input() form: Form;

    constructor() {
    }

    ngOnInit(): void {
    }
}
