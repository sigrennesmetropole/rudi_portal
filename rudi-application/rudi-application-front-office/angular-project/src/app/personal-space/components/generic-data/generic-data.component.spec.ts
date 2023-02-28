import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericDataComponent } from './generic-data.component';

describe('GenericDataComponent', () => {
  let component: GenericDataComponent;
  let fixture: ComponentFixture<GenericDataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GenericDataComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(GenericDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
