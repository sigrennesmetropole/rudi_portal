import {Component} from '@angular/core';
import {ActivatedRoute, Params} from '@angular/router';
import {switchMap} from 'rxjs/operators';
import {KonsultMetierService} from '../../../core/services/konsult-metier.service';
import {Metadata, Period} from '../../../api-kaccess';
import {LogService} from '../../../core/services/log.service';
import {DateTimeUtils} from '../../../shared/utils/date-time-utils';

@Component({
    selector: 'app-selfdata-information-request-creation-success',
    templateUrl: './selfdata-information-request-creation-success.component.html',
})
export class SelfdataInformationRequestCreationSuccessComponent {
    metadata: Metadata;
    treatmentPeriod: Period;
    treatmentPeriodToMonths: number;
    isLoading: boolean;

    constructor(private readonly route: ActivatedRoute,
                private readonly konsultMetierService: KonsultMetierService,
                private readonly logService: LogService,) {
    }

    // tslint:disable-next-line:use-lifecycle-interface
    ngOnInit(): void {
        this.route.params.pipe(
            switchMap((params: Params) => {
                // Si uuid, on charge le JDD
                if (params.uuid) {
                    this.isLoading = true;
                    return this.konsultMetierService.getMetadataByUuid(params.uuid);
                } else {
                    // Sinon erreur on peut pas afficher la page
                    throw Error('Erreur pas d\'UUID de dataset');
                }
            })
        ).subscribe(
            {
                next: (value: Metadata) => {
                    this.metadata = value;
                    this.treatmentPeriod = value?.ext_metadata?.ext_selfdata?.ext_selfdata_content?.treatment_period;
                    this.treatmentPeriodToMonths = DateTimeUtils.convertPeriodToMonths(this.treatmentPeriod);
                    this.isLoading = false;
                },
                complete: () => {
                    this.isLoading = false;
                },
                error: err => {
                    this.logService.error(err);
                    this.isLoading = false;
                }
            }
        );
    }
}
