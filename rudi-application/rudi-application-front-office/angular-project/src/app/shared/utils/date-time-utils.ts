import * as moment from 'moment';
import {Moment} from 'moment';
import {Period} from '@app/api-kaccess';

const ISO_STRING_WITHOUT_OFFSET_LENGTH = '2020-01-01T00:00:00.000'.length;

export class DateTimeUtils {

    /**
     * @return supprime le décalage/fuseau horaire d'une heure, pour extraire l'heure locale et la convertit au format ISO
     * (sans décalage horaire) à envoyer au backend RUDI lorsqu'il requiert un LocalDateTime au lieu d'un OffsetDateTime.
     * Exemple : si on passe le 1er janvier 2020 à minuit de n'importe quel fuseau horaire, le résultat sera toujours
     *
     * <code>'2020-01-01T00:00:00.000'</code>
     */
    static extractLocalDateTimeToISOString(date: Moment | undefined): string | undefined {
        if (!date) {
            return undefined;
        }
        return date.toISOString(true).substring(0, ISO_STRING_WITHOUT_OFFSET_LENGTH);
    }

    /**
     * Convertit une periode (value, unit) en mois
     * @param period à convertir
     */
    static convertPeriodToMonths(period: Period): number {
        switch (period.unit) {
            case 'DAYS':
                return Math.ceil(period.value / 30);
            case 'YEARS':
                return period.value * 12;
            default:
                return period.value;
        }
    }

    static formatStringDate(date: string, format: string = 'YYYY-MM-DD'): string {
        return moment(date).format(format);
    }
}
