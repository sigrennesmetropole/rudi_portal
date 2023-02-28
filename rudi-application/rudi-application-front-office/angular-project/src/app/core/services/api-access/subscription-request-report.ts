/**
 * Un rapport de souscriptions à un ensembles de JDDs
 */
import {SubscriptionRequestResult} from './subscription-request-result';

export class SubscriptionRequestReport {

    /**
     * Les JDDs auxquels on a réussi à souscrire
     */
    subscribed: SubscriptionRequestResult[] = [];

    /**
     * Les JDDS auxquels on avait déjà souscrit
     */
    ignored: SubscriptionRequestResult[] = [];

    /**
     * Les JDDs pour lesquels on a pas réussi à souscrire
     */
    failed: SubscriptionRequestResult[] = [];
}
