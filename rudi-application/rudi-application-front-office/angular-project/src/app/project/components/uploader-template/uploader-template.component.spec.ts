import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploaderTemplateComponent } from './uploader-template.component';

describe('UploaderTemplateComponent', () => {
  let component: UploaderTemplateComponent;
  let fixture: ComponentFixture<UploaderTemplateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UploaderTemplateComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UploaderTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
