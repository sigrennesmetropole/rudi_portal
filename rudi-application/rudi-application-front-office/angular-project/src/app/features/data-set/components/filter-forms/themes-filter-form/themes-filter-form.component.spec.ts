import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ThemesFilterFormComponent} from './themes-filter-form.component';

describe('ThemesFilterFormComponent', () => {
  let component: ThemesFilterFormComponent;
  let fixture: ComponentFixture<ThemesFilterFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ThemesFilterFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ThemesFilterFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
