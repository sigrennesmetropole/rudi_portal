import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelfdataDatasetsTableComponent} from './selfdata-datasets-table.component';

describe('SelfdataDatasetsTableComponent', () => {
  let component: SelfdataDatasetsTableComponent;
  let fixture: ComponentFixture<SelfdataDatasetsTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelfdataDatasetsTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfdataDatasetsTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
