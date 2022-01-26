import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FilterSidenavContainerComponent} from './filter-sidenav-container.component';

describe('MobileFilterComponent', () => {
  let component: FilterSidenavContainerComponent;
  let fixture: ComponentFixture<FilterSidenavContainerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FilterSidenavContainerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FilterSidenavContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
