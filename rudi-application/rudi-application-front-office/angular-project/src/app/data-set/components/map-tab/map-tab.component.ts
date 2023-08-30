import {Component, Input, OnInit} from '@angular/core';
import {DataSetAccessService} from '../../../core/services/data-set/data-set-access.service';
import {Media, Metadata} from '../../../api-kaccess';
import {LogService} from '../../../core/services/log.service';
import {switchMap} from 'rxjs/operators';
import {DisplayMapService} from '../../../core/services/data-set/display-map.service';
import {LayerInformation} from '../../../konsult/konsult-model';

@Component({
    selector: 'app-map-tab',
    templateUrl: './map-tab.component.html',
    styleUrls: ['./map-tab.component.scss']
})
export class MapTabComponent implements OnInit {

    @Input()
    metadata: Metadata;

    @Input()
    mediaToDisplay: Media;

    isMapLoading: boolean;
    isErrorAccess: boolean;
    isErrorServer: boolean;

    baseLayers: LayerInformation[] = [];

    constructor(
        private readonly datasetAccessService: DataSetAccessService,
        private readonly displayMapService: DisplayMapService,
        private readonly logService: LogService
    ) {
    }

    ngOnInit(): void {
        if (this.metadata && this.mediaToDisplay) {
            this.isMapLoading = true;
            this.datasetAccessService.hasAccess(this.metadata, this.mediaToDisplay).pipe(
                switchMap((hasAccess: boolean) => {
                    this.isErrorAccess = !hasAccess;
                    return this.displayMapService.getDatasetBaseLayers();
                })
            ).subscribe({
                next: (baseLayers: LayerInformation[]) => {
                    this.baseLayers = baseLayers;
                    this.isMapLoading = false;
                },
                error: (e) => {
                    this.logService.error(e);
                    this.isMapLoading = false;
                }
            });
        }
    }
}
