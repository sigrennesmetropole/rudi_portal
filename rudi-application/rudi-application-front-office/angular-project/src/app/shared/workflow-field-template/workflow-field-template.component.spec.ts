import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowFieldTemplateComponent } from './workflow-field-template.component';

describe('WorkflowFieldTemplateComponent', () => {
  let component: WorkflowFieldTemplateComponent;
  let fixture: ComponentFixture<WorkflowFieldTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkflowFieldTemplateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkflowFieldTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
