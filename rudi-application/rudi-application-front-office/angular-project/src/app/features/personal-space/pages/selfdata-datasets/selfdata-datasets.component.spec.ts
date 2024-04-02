import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelfdataDatasetsComponent} from './selfdata-datasets.component';

describe('SelfdataDatasetsComponent', () => {
  let component: SelfdataDatasetsComponent;
  let fixture: ComponentFixture<SelfdataDatasetsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelfdataDatasetsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfdataDatasetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
