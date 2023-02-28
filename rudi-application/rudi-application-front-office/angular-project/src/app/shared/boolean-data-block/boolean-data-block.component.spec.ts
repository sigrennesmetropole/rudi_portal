import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BooleanDataBlockComponent} from './boolean-data-block.component';

describe('BooleanDataBlockComponent', () => {
    let component: BooleanDataBlockComponent;
    let fixture: ComponentFixture<BooleanDataBlockComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [BooleanDataBlockComponent]
        })
            .compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(BooleanDataBlockComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
