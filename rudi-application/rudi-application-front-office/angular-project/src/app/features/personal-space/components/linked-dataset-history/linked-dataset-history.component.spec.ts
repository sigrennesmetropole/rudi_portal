import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LinkedDatasetHistoryComponent} from './linked-dataset-history.component';

describe('ProjectHistoryComponent', () => {
  let component: LinkedDatasetHistoryComponent;
  let fixture: ComponentFixture<LinkedDatasetHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LinkedDatasetHistoryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LinkedDatasetHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
