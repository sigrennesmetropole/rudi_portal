import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TaskDetailHeaderComponent} from './task-detail-header.component';

describe('RequestDetailHeaderComponent', () => {
  let component: TaskDetailHeaderComponent;
  let fixture: ComponentFixture<TaskDetailHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TaskDetailHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskDetailHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
