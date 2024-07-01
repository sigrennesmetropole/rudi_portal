import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SocialMediaSectionComponent } from './social-media-section.component';

describe('SocialMediaComponent', () => {
  let component: SocialMediaSectionComponent;
  let fixture: ComponentFixture<SocialMediaSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SocialMediaSectionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SocialMediaSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
