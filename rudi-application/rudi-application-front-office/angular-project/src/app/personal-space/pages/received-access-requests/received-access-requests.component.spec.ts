import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceivedAccessRequestsComponent } from './received-access-requests.component';

describe('ReceivedAccessRequestsComponent', () => {
  let component: ReceivedAccessRequestsComponent;
  let fixture: ComponentFixture<ReceivedAccessRequestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReceivedAccessRequestsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceivedAccessRequestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
