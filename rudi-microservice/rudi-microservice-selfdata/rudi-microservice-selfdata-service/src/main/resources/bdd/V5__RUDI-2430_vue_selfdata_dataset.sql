CREATE VIEW selfdata_data.selfdata_dataset AS
SELECT id, process_definition_key, description, updated_date, dataset_uuid, initiator, functional_status
FROM selfdata_data.selfdata_information_request
WHERE id IN (
    SELECT request.id
    FROM selfdata_data.selfdata_information_request request
             JOIN (
        SELECT initiator, dataset_uuid, max(updated_date) AS updated_date
        FROM selfdata_data.selfdata_information_request
        GROUP BY dataset_uuid, initiator
    ) AS grouped_requests ON request.dataset_uuid = grouped_requests.dataset_uuid AND request.updated_date = grouped_requests.updated_date
);
