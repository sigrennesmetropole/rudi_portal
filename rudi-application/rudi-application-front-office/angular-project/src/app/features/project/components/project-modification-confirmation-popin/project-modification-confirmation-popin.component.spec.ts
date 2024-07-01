import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProjectModificationConfirmationPopinComponent } from './project-modification-confirmation-popin.component';

describe('ProjectModificationConfirmationPopinComponent', () => {
  let component: ProjectModificationConfirmationPopinComponent;
  let fixture: ComponentFixture<ProjectModificationConfirmationPopinComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProjectModificationConfirmationPopinComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ProjectModificationConfirmationPopinComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
