import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewRequestDetailComponent } from './new-request-detail.component';

describe('NewRequestDetailComponent', () => {
  let component: NewRequestDetailComponent;
  let fixture: ComponentFixture<NewRequestDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ NewRequestDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NewRequestDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
