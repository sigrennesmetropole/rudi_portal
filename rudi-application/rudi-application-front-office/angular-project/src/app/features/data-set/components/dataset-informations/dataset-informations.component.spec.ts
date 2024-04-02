import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DatasetInformationsComponent} from './dataset-informations.component';

describe('DatasetInformationsComponent', () => {
  let component: DatasetInformationsComponent;
  let fixture: ComponentFixture<DatasetInformationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DatasetInformationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DatasetInformationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
