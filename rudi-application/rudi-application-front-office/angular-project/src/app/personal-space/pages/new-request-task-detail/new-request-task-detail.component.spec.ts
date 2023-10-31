import {ComponentFixture, TestBed} from '@angular/core/testing';

import {NewRequestTaskDetailComponent} from './new-request-task-detail.component';

describe('NewRequestDetailComponent', () => {
  let component: NewRequestTaskDetailComponent;
  let fixture: ComponentFixture<NewRequestTaskDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewRequestTaskDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewRequestTaskDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
