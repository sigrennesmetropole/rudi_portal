import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestDetailHeaderComponent } from './request-detail-header.component';

describe('RequestDetailHeaderComponent', () => {
  let component: RequestDetailHeaderComponent;
  let fixture: ComponentFixture<RequestDetailHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RequestDetailHeaderComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestDetailHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
