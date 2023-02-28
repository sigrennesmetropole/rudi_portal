import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchingDataCardComponent } from './matching-data-card.component';

describe('MatchingDataCardComponent', () => {
  let component: MatchingDataCardComponent;
  let fixture: ComponentFixture<MatchingDataCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MatchingDataCardComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MatchingDataCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
