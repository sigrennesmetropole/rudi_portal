import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkflowFormSubmitSuccessComponent} from './workflow-form-submit-success.component';

describe('WorkflowFormSubmitSuccessComponent', () => {
  let component: WorkflowFormSubmitSuccessComponent;
  let fixture: ComponentFixture<WorkflowFormSubmitSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkflowFormSubmitSuccessComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkflowFormSubmitSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
