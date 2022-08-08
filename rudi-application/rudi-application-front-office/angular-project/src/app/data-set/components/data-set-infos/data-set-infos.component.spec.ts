import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DataSetInfosComponent} from './data-set-infos.component';

describe('DataSetsInfosComponent', () => {
  let component: DataSetInfosComponent;
  let fixture: ComponentFixture<DataSetInfosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DataSetInfosComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DataSetInfosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
