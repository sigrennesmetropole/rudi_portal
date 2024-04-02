import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DataSetButtonComponent} from './data-set-button.component';

describe('CustomButtonComponent', () => {
  let component: DataSetButtonComponent;
  let fixture: ComponentFixture<DataSetButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DataSetButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DataSetButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
