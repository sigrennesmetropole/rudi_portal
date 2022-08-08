import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LinkedDatasetTasksComponent } from './linked-dataset-tasks.component';

describe('RequestsToStudyTabComponent', () => {
  let component: LinkedDatasetTasksComponent;
  let fixture: ComponentFixture<LinkedDatasetTasksComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LinkedDatasetTasksComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkedDatasetTasksComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
