import {ComponentFixture, TestBed} from '@angular/core/testing';
import {AccessStatusFilterFormComponent} from './access-status-filter-form.component';


describe('RestrictedAccessFilterFormComponent', () => {
    let component: AccessStatusFilterFormComponent;
    let fixture: ComponentFixture<AccessStatusFilterFormComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [AccessStatusFilterFormComponent]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(AccessStatusFilterFormComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
