-- Modification du nom de la colonne, la modification ne concerne pas que les JDD restreints
ALTER TABLE projekt_data.reutilisation_status
  RENAME COLUMN "restricted_linkeddataset_modification_allowed" TO "dataset_set_modification_allowed";