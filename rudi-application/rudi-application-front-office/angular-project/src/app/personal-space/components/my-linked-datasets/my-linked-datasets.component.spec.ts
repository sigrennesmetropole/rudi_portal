import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MyLinkedDatasetsComponent } from './my-linked-datasets.component';

describe('AllLinkedDatasetsComponent', () => {
  let component: MyLinkedDatasetsComponent;
  let fixture: ComponentFixture<MyLinkedDatasetsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MyLinkedDatasetsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MyLinkedDatasetsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
