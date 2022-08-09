import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkflowPopinComponent } from './workflow-popin.component';

describe('WorkflowPopinComponent', () => {
  let component: WorkflowPopinComponent;
  let fixture: ComponentFixture<WorkflowPopinComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ WorkflowPopinComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkflowPopinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
