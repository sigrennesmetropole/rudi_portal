import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RestrictedAccessFilterFormComponent } from './restricted-access-filter-form.component';

describe('RestrictedAccessFilterFormComponent', () => {
  let component: RestrictedAccessFilterFormComponent;
  let fixture: ComponentFixture<RestrictedAccessFilterFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RestrictedAccessFilterFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RestrictedAccessFilterFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
