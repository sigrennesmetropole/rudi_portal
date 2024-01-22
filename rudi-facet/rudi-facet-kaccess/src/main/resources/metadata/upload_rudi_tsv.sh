# https://guides.dataverse.org/en/latest/admin/metadatacustomization.html

# upload TSV file for RUDI
## Karbon
#curl -k -v https://dataverse-engine-dev-karbon.rennes-metropole-rudi.karbon.open.global/api/admin/datasetfield/load -H "Content-type: text/tab-separated-values" -X POST --upload-file rudi.tsv
## IAAS
curl -k -v http://dv.open-dev.com:8095/api/admin/datasetfield/load -H "Content-type: text/tab-separated-values" -X POST --upload-file rudi.tsv
