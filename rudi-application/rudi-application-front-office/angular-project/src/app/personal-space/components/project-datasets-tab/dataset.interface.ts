export interface NewDatasetRequestTableData extends RowTableData{
    description: string;
}

export interface DatasetsTableData extends RowTableData {
    datasetOrganizationId: string;
    datasetTitle: string;
    organization_name: string;
}

export interface RowTableData{
    uuid: string;
    status: string;
    functional_status: string;
    addedDate: string;
}
