import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DocumentationButtonComponent } from './documentation-button.component';

describe('DocumentationButtonComponent', () => {
  let component: DocumentationButtonComponent;
  let fixture: ComponentFixture<DocumentationButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DocumentationButtonComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DocumentationButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
