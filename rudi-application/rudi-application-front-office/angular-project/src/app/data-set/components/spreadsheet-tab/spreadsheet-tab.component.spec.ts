import { ComponentFixture, TestBed } from '@angular/core/testing';

describe('SpreadsheetTabComponent', () => {
  let component: SpreadsheetTabComponent;
  let fixture: ComponentFixture<SpreadsheetTabComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpreadsheetTabComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpreadsheetTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
