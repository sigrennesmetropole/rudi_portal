export interface SelfdataDataset {
    updated_date: string;
    title: string;
    functional_status: string;
    process_key: string;
    dataset_uuid: string;
}

export interface PagedSelfdataDataset {
    total: number;
    elements: SelfdataDataset[];
}
