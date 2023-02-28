export interface RequestToStudy {
    taskId: string;
    receivedDate: Date;
    description: string;

    /**
     * Prénom + Nom du porteur ou nom du demandeur OU nom de l'organisation
     */
    initiator: string;
    status: string;

    /**
     * Date au format ISO. Exemple : "2022-04-13T17:09:00+02:00"
     */
    endDate?: Date;

    /**
     * URL d'accès au détail de la demande
     */
    url?: string;

    /**
     * Type de la demande qui va être utilisé en clé de translate
     */
    processDefinitionKey?: string;
}
