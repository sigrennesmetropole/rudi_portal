import {ComponentFixture, TestBed} from '@angular/core/testing';

import {FiltersItemsListComponent} from './filters-items-list.component';

describe('KeywordsListComponent', () => {
  let component: FiltersItemsListComponent;
  let fixture: ComponentFixture<FiltersItemsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FiltersItemsListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FiltersItemsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
