import {ComponentFixture, TestBed} from '@angular/core/testing';
import {SpreadsheetTabComponent} from '@features/data-set/components/spreadsheet-tab/spreadsheet-tab.component';

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
