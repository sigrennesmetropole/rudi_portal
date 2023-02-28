import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowFieldAttachmentPopinComponent } from './workflow-field-attachment-popin.component';

describe('WorkflowFieldAttachmentPopinComponent', () => {
  let component: WorkflowFieldAttachmentPopinComponent;
  let fixture: ComponentFixture<WorkflowFieldAttachmentPopinComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkflowFieldAttachmentPopinComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkflowFieldAttachmentPopinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
