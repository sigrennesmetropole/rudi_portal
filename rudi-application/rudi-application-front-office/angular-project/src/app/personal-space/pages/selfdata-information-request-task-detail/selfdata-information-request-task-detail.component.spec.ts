import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelfdataInformationRequestTaskDetailComponent} from './selfdata-information-request-task-detail.component';

describe('SelfdataDetailComponent', () => {
    let component: SelfdataInformationRequestTaskDetailComponent;
    let fixture: ComponentFixture<SelfdataInformationRequestTaskDetailComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [SelfdataInformationRequestTaskDetailComponent]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(SelfdataInformationRequestTaskDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
