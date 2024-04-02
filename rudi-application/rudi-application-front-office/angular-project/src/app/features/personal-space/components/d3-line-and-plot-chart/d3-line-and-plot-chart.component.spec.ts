import {ComponentFixture, TestBed} from '@angular/core/testing';

import {D3LineAndPlotChartComponent} from './d3-line-and-plot-chart.component';

describe('D3LineAndPlotChartComponent', () => {
  let component: D3LineAndPlotChartComponent;
  let fixture: ComponentFixture<D3LineAndPlotChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ D3LineAndPlotChartComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(D3LineAndPlotChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
