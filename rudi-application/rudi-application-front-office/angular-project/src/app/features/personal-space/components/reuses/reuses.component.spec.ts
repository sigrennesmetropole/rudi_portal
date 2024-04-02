import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ReusesComponent} from './reuses.component';

describe('ReusesComponent', () => {
  let component: ReusesComponent;
  let fixture: ComponentFixture<ReusesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReusesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReusesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
