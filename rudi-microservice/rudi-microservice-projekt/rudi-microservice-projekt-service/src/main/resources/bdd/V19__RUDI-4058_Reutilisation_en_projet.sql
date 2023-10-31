-- Ajout d'une colonne indiquant si on peut modifier les JDD restreints pour ce statut
ALTER TABLE projekt_data.reutilisation_status
  ADD COLUMN "restricted_linkeddataset_modification_allowed" BOOLEAN NOT NULL DEFAULT FALSE;

-- "Réutilisation - Finalisée" est le 2e choix de la liste
UPDATE projekt_data.reutilisation_status
	SET order_=1
	WHERE code = 'REUSE_FINISHED';
	
-- Insert new premier choix de la liste
INSERT INTO projekt_data.reutilisation_status(uuid, code, label, opening_date, order_, restricted_linkeddataset_modification_allowed)
VALUES ('553a14cc-e8e6-4513-bd9e-2a5ffa2724e3', 'REUSE_INPROGRESS', 'Réutilisation - En projet', now(), 0, true);