import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProducerNamesFilterFormComponent } from './producer-names-filter-form.component';

describe('ProducerNamesFilterFormComponent', () => {
  let component: ProducerNamesFilterFormComponent;
  let fixture: ComponentFixture<ProducerNamesFilterFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProducerNamesFilterFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProducerNamesFilterFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
