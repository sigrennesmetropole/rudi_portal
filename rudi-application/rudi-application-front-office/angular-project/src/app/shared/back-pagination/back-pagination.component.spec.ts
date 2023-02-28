import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BackPaginationComponent } from './back-pagination.component';

describe('BackPaginationComponent', () => {
  let component: BackPaginationComponent;
  let fixture: ComponentFixture<BackPaginationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ BackPaginationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(BackPaginationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
