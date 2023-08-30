import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Feature} from 'ol';
import {Geometry} from 'ol/geom';

@Component({
    selector: 'app-map-popup',
    templateUrl: './map-popup.component.html',
    styleUrls: ['./map-popup.component.scss']
})
export class MapPopupComponent {

    _feature: Feature<Geometry>;
    properties: Map<string, string>;

    @Input()
    set feature(feature: Feature<Geometry>) {
        this._feature = feature;
        this.properties = new Map<string, string>();
        if (this._feature) {
            const propertiesRaw = this._feature.getProperties();
            Object.entries(propertiesRaw)
                .filter((value: [string, unknown]) => value[0] !== 'geometry')
                .forEach(([key, value]) => {
                    this.properties.set(key, value);
                });
        }
    }

    @Output()
    closePopup: EventEmitter<null> = new EventEmitter<null>();

    get keys(): string[] {
        return Array.from(this.properties.keys());
    }

    value(key: string): string {
        return this.properties.get(key);
    }

    handleClose(): void {
        this.closePopup.emit();
    }
}
