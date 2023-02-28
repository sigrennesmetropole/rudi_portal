import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TemporalBarchartDataComponent } from './temporal-barchart-data.component';

describe('TemporalBarchartDataComponent', () => {
  let component: TemporalBarchartDataComponent;
  let fixture: ComponentFixture<TemporalBarchartDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TemporalBarchartDataComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TemporalBarchartDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
