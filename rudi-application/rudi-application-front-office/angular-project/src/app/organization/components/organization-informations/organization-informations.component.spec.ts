import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganizationInformationsComponent } from './organization-informations.component';

describe('OrganizationInformationsComponent', () => {
  let component: OrganizationInformationsComponent;
  let fixture: ComponentFixture<OrganizationInformationsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OrganizationInformationsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganizationInformationsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
