import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UserService} from '../../../core/services/user.service';
import {Metadata} from '../../../api-kaccess';
import {OwnerType} from '../../../projekt/projekt-api';
import {Level} from '../../../shared/notification-template/notification-template.component';
import {SnackBarService} from '../../../core/services/snack-bar.service';
import {TranslateService} from '@ngx-translate/core';
import {ErrorWithCause} from '../../../shared/models/error-with-cause';
import {LogService} from '../../../core/services/log.service';
import {GdataDataInterface} from '../../../core/services/selfdata-dataset/gdataData.interface';
import {SelfdataApiAccessService} from '../../../core/services/api-access/selfdata/selfdata-api-access-service';
import {SubscriptionRequestReport} from '../../../core/services/api-access/subscription-request-report';
import {BarChartData} from '../../../core/services/selfdata-dataset/tpbcData.interface';
import {SelfdataDatasetService} from '../../../core/services/selfdata-dataset/selfdata-dataset.service';
import {forkJoin, Observable, of} from 'rxjs';
import {catchError, mapTo, switchMap, tap} from 'rxjs/operators';
import {PropertiesMetierService} from '../../../core/services/properties-metier.service';

@Component({
    selector: 'app-selfdata-dataset-data-tab',
    templateUrl: './selfdata-dataset-data-tab.component.html',
    styleUrls: ['./selfdata-dataset-data-tab.component.scss']
})
export class SelfdataDatasetDataTabComponent implements OnInit {

    @Input() metadata: Metadata;
    ownerUuid: string;
    @Input() isDataTabEmpty: boolean;
    @Output() subscriptionSuccedEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

    public hideDataCard = true;
    public rudiDocLink: string;
    public password: string;
    public loading: boolean;
    public initLoader: boolean;
    public subscriptionErrorMessage: string;
    public gdataDataLoading: boolean;
    public tpbcDataLoading: boolean;
    public barChartData: BarChartData;
    public genericDataObject: GdataDataInterface;

    public gdataError: string;
    public tpbcError: string;


    constructor(private readonly userService: UserService,
                private readonly selfdataApiAccessService: SelfdataApiAccessService,
                private readonly snackBarService: SnackBarService,
                private readonly translateService: TranslateService,
                private readonly logService: LogService,
                private readonly selfdataService: SelfdataDatasetService,
                private readonly propertiesMetierService:PropertiesMetierService,) {
    }

    ngOnInit(): void {
        this.initLoader = true;
        this.userService.getConnectedUser().subscribe(
            {
                next: connectedUser => {
                    this.ownerUuid = connectedUser.uuid;
                    this.initLoader = false;
                },
                error: err => {
                    console.error(err);
                    this.initLoader = false;
                }
            }
        );
        this.propertiesMetierService.get('rudidatarennes.docRudiBzh').subscribe({
            next: (rudiDocLink: string) => {
                this.rudiDocLink = rudiDocLink;
            }
        });
    }

    /**
     * Méthode qui récupère le mot de passe entré par l'utilisateur
     */
    handlePasswordChanged(password: string): void {
        this.password = password;
    }

    /**
     * Lancement de la souscription au jeu de donnée selfdata dont on a eu la validation de la demande
     */
    validate(): void {
        this.loading = true;
        this.subscriptionErrorMessage = null;
        this.gdataError = null;
        this.tpbcError = null;
        this.accessData().pipe(
            switchMap(() => this.loadSelfdataData())
        ).subscribe({
            next: () => {
                this.loading = false;
                this.hideDataCard = false;
                this.emitSubscriptionSucced(true);
            },
            error: (error) => {
                if (error instanceof ErrorWithCause) {
                    this.subscriptionErrorMessage = error.functionalMessage;
                } else {
                    this.snackBarService.openSnackBar({
                        message: this.translateService.instant('error.technicalError'),
                        level: Level.ERROR,
                    });
                }
                this.loading = false;
            }
        });
    }

    accessData(): Observable<void> {
        return this.selfdataApiAccessService.checkPasswordAndDoSubscriptions(
            [this.metadata], this.password, OwnerType.User, this.ownerUuid)
            .pipe(
                tap((result: SubscriptionRequestReport) => {
                    if (result.failed.length > 0) {
                        throw new ErrorWithCause('La souscription à l\'API du JDD a échoué');
                    }
                }),

                mapTo(null)
            );
    }

    loadSelfdataData(): Observable<unknown> {

        const gdataObs: Observable<GdataDataInterface> = this.selfdataService.getGdataData(this.metadata.global_id).pipe(
            catchError((error) => {
                console.error(error);
                this.gdataError = 'Erreur lors du chargement des données au format GDATA';
                return of({});
            }),

            tap((gdata: GdataDataInterface) => {
                this.genericDataObject = gdata;
            })
        );

        const tpbcObs: Observable<BarChartData> = this.selfdataService.getTpbcData(this.metadata.global_id).pipe(
            catchError((error) => {
                console.error(error);
                this.tpbcError = 'Erreur lors du chargement des données au format TPBC';
                return of({});
            }),

            tap((tpbc: BarChartData) => {
                this.barChartData = tpbc;
            })
        );

        return forkJoin({gdataObs, tpbcObs});
    }

    /**
     * Grise le button validé quand l'utilisateur n'a pas saisie de mot de passe ou quand aucun traitement est en cours
     */
    isButtonValidateDisabled(): boolean {
        return !(this.password?.length) || this.loading;
    }

    /**
     * Méthode dit si la souscription s'est bien passée
     */
    emitSubscriptionSucced(value: boolean): void {
        this.subscriptionSuccedEmitter.emit(value);
    }
}
