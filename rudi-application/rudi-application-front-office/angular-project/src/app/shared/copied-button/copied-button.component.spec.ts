import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CopiedButtonComponent } from './copied-button.component';

describe('CopiedButtonComponent', () => {
  let component: CopiedButtonComponent;
  let fixture: ComponentFixture<CopiedButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CopiedButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CopiedButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
