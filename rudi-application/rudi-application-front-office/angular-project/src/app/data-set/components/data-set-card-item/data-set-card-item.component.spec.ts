import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DataSetCardItemComponent } from './data-set-card-item.component';

describe('DataSetCardItemComponent', () => {
  let component: DataSetCardItemComponent;
  let fixture: ComponentFixture<DataSetCardItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DataSetCardItemComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DataSetCardItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
