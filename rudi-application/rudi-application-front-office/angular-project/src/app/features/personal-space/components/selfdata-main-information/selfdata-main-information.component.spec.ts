import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelfdataMainInformationComponent} from './selfdata-main-information.component';

describe('SelfdataMainInformationComponent', () => {
  let component: SelfdataMainInformationComponent;
  let fixture: ComponentFixture<SelfdataMainInformationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelfdataMainInformationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfdataMainInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
