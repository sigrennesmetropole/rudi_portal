export interface RequestDisplayData {
    taskId: string;
    receivedDate: Date;
    datasetTitle: string;
    comment: string;

    /**
     * Prénom + Nom du porteur ou nom de l'organisation
     */
    ownerName: string;
    ownerEmail: string;
    status: string;

    /** Date au format ISO. Exemple : "2022-04-13T17:09:00+02:00" */
    endDate?: Date;
}
