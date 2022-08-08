import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuccessStep3TemplateComponent } from './success-step3-template.component';

describe('SuccessStep3TemplateComponent', () => {
  let component: SuccessStep3TemplateComponent;
  let fixture: ComponentFixture<SuccessStep3TemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SuccessStep3TemplateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SuccessStep3TemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
