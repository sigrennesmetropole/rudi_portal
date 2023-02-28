CREATE TABLE document
(
    id            bigserial
        CONSTRAINT document_pkey PRIMARY KEY,
    uuid          uuid         NOT NULL,
    file_name     varchar(150) NOT NULL,
    content_type  varchar(150) NOT NULL,
    file_size     bigint       NOT NULL,
    file_contents oid          NOT NULL,
    encrypted     boolean      NOT NULL,
    uploader_uuid uuid         NOT NULL
);
