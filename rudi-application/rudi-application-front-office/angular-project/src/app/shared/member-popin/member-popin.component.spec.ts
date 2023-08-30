import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MemberPopinComponent } from './member-popin.component';

describe('MemberPopinComponent', () => {
  let component: MemberPopinComponent;
  let fixture: ComponentFixture<MemberPopinComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MemberPopinComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MemberPopinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
