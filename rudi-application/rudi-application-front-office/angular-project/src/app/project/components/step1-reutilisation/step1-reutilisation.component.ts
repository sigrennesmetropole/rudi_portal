import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {ProjectType} from '../../../projekt/projekt-model';

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

    constructor() {
    }

    ngOnInit(): void {
    }
}
