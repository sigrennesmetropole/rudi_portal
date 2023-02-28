import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SelfdataRequestSectionComponent } from './selfdata-request-section.component';

describe('SelfdataRequestSectionComponent', () => {
  let component: SelfdataRequestSectionComponent;
  let fixture: ComponentFixture<SelfdataRequestSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SelfdataRequestSectionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SelfdataRequestSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
