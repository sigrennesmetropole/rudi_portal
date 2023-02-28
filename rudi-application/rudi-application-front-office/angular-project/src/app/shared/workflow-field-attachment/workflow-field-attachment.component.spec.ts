import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkflowFieldAttachmentComponent} from './workflow-field-attachment.component';

describe('WorkflowFieldAttachementComponent', () => {
    let component: WorkflowFieldAttachmentComponent;
    let fixture: ComponentFixture<WorkflowFieldAttachmentComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [WorkflowFieldAttachmentComponent]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(WorkflowFieldAttachmentComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
