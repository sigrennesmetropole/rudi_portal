import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowFieldComponent } from './workflow-field.component';

describe('WorkflowFieldComponent', () => {
  let component: WorkflowFieldComponent;
  let fixture: ComponentFixture<WorkflowFieldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkflowFieldComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkflowFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
