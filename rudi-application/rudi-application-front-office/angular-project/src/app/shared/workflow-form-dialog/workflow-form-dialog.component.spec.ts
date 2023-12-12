import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkflowFormDialogComponent} from '@shared/workflow-form-dialog/workflow-form-dialog.component';

describe('WorkflowFormDialogComponent', () => {
    let component: WorkflowFormDialogComponent;
    let fixture: ComponentFixture<WorkflowFormDialogComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [WorkflowFormDialogComponent]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(WorkflowFormDialogComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
