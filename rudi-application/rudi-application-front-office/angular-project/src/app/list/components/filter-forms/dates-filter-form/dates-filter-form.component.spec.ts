import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DatesFilterFormComponent } from './dates-filter-form.component';

describe('DatesFilterFormComponent', () => {
  let component: DatesFilterFormComponent;
  let fixture: ComponentFixture<DatesFilterFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DatesFilterFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DatesFilterFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
