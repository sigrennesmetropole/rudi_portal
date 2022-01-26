import {Injectable} from '@angular/core';
import {environment} from "../../../environments/environment";


export let logLevel = environment.logLevel;

@Injectable()
export class LogService {
    private readonly logDebugEnable;
    private readonly logInfoEnable;
    private readonly logWarningEnable;
    private readonly logErrorEnable;

    constructor() {
        this.logDebugEnable = logLevel === 'DEBUG';
        this.logInfoEnable = this.logDebugEnable || (logLevel === 'INFO');
        this.logWarningEnable = this.logInfoEnable || (logLevel === 'WARNING');
        this.logErrorEnable = this.logWarningEnable || (logLevel === 'ERROR');
    }

    /**
     * Log DEBUG
     */
    debug(...data: any[]) {
        if (this.logDebugEnable) {
            // Si on souhaite afficher la date et l'heure dans la console, il suffit de configurer son navigateur
            console.log(...data);
        }
    }

    /**
     * Log INFO
     */
    info(...data: any[]) {
        if (this.logInfoEnable) {
            // Si on souhaite afficher la date et l'heure dans la console, il suffit de configurer son navigateur
            console.info(...data);
        }
    }

    /**
     * Log WARNING
     */
    warning(...data: any[]) {
        if (this.logWarningEnable) {
            // Si on souhaite afficher la date et l'heure dans la console, il suffit de configurer son navigateur
            console.warn(...data);
        }
    }

    /**
     * Log ERROR
     */
    error(...data: any[]) {
        if (this.logErrorEnable) {
            // Si on souhaite afficher la date et l'heure dans la console, il suffit de configurer son navigateur
            console.error(...data);
        }
    }
}
