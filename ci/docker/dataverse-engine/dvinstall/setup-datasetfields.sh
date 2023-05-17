#!/bin/sh
curl $SERVER/admin/datasetfield/loadNAControlledVocabularyValue
# TODO: The "@" is confusing. Consider switching to --upload-file citation.tsv
curl $SERVER/admin/datasetfield/load -X POST --data-binary @data/metadatablocks/citation.tsv -H "Content-type: text/tab-separated-values"
curl $SERVER/admin/datasetfield/load -X POST --data-binary @data/metadatablocks/geospatial.tsv -H "Content-type: text/tab-separated-values"
curl $SERVER/admin/datasetfield/load -X POST --data-binary @data/metadatablocks/social_science.tsv -H "Content-type: text/tab-separated-values"
curl $SERVER/admin/datasetfield/load -X POST --data-binary @data/metadatablocks/astrophysics.tsv -H "Content-type: text/tab-separated-values"
curl $SERVER/admin/datasetfield/load -X POST --data-binary @data/metadatablocks/biomedical.tsv -H "Content-type: text/tab-separated-values"
curl $SERVER/admin/datasetfield/load -X POST --data-binary @data/metadatablocks/journals.tsv -H "Content-type: text/tab-separated-values"
