import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SelfdataInformationRequestDetailComponent} from './selfdata-information-request-detail.component';

describe('SelfdataDetailComponent', () => {
    let component: SelfdataInformationRequestDetailComponent;
    let fixture: ComponentFixture<SelfdataInformationRequestDetailComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [SelfdataInformationRequestDetailComponent]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(SelfdataInformationRequestDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
