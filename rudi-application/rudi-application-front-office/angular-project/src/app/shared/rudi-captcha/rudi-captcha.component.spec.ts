import {ComponentFixture, TestBed} from '@angular/core/testing';

import {RudiCaptchaComponent} from './rudi-captcha.component';

describe('RudiCaptchaComponent', () => {
  let component: RudiCaptchaComponent;
  let fixture: ComponentFixture<RudiCaptchaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RudiCaptchaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RudiCaptchaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
