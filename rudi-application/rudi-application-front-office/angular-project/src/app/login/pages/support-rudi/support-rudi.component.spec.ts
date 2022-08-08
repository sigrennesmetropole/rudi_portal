import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupportRudiComponent } from './support-rudi.component';

describe('SupportRudiComponent', () => {
  let component: SupportRudiComponent;
  let fixture: ComponentFixture<SupportRudiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SupportRudiComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SupportRudiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
