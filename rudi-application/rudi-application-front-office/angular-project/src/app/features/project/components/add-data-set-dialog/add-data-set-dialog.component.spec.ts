import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AddDataSetDialogComponent} from './add-data-set-dialog.component';

describe('AjouterJeuDonneesDialogComponent', () => {
  let component: AddDataSetDialogComponent;
  let fixture: ComponentFixture<AddDataSetDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddDataSetDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddDataSetDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
