import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelfdataInformationRequestCreationSuccessComponent } from './selfdata-information-request-creation-success.component';

describe('SelfdataInformationRequestCreationSuccessComponent', () => {
  let component: SelfdataInformationRequestCreationSuccessComponent;
  let fixture: ComponentFixture<SelfdataInformationRequestCreationSuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelfdataInformationRequestCreationSuccessComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfdataInformationRequestCreationSuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
