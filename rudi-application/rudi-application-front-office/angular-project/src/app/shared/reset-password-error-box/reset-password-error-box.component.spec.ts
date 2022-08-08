import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResetPasswordErrorBoxComponent } from './reset-password-error-box.component';

describe('ResetPasswordErrorBoxComponent', () => {
  let component: ResetPasswordErrorBoxComponent;
  let fixture: ComponentFixture<ResetPasswordErrorBoxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ResetPasswordErrorBoxComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ResetPasswordErrorBoxComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
