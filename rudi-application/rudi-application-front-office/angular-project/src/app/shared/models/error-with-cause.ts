/**
 * Wrapper d'une erreur technique dans une erreur fonctionnelle avec explication client
 */
export class ErrorWithCause extends Error {

    /**
     * L'erreur technique qui a eu lieu
     */
    cause: Error;

    /**
     * Le message de l'erreur fonctionnelle
     */
    functionalMessage: string;

    /**
     * Code d'erreur
     */
    code: number;

    /**
     * Constructeur du wrapper d'erreur lors du chaînage RXJS
     * @param message le message fonctionnel de l'erreur
     * @param cause l'erreur trouvée (optionnelle si erreur purement fonctionnelle)
     * @param code code d'erreur (optionnelle)
     */
    constructor(message: string, cause?: Error, code?: number) {
        let technicalError;
        if (cause) {
            technicalError = '\nCause : ' + cause.message;
        }
        const globalError = message + technicalError;
        super(globalError);

        this.functionalMessage = message;
        if (!this.cause) {
            this.cause = new Error(message);
        } else {
            this.cause = cause;
        }
        if (code) {
            this.code = code;
        }
    }

    /**
     * On affiche toujours la stack de la cause
     */
    get stack(): string {
        return this.cause.stack;
    }
}
