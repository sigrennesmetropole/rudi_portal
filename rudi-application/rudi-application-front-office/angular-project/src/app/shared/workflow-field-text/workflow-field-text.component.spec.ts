import {ComponentFixture, TestBed} from '@angular/core/testing';

import {WorkflowFieldTextComponent} from './workflow-field-text.component';

describe('WorkflowFieldTextComponent', () => {
  let component: WorkflowFieldTextComponent;
  let fixture: ComponentFixture<WorkflowFieldTextComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkflowFieldTextComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkflowFieldTextComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
