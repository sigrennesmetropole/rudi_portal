-- Renommage des niveaux de confidentialité
UPDATE confidentiality
SET code = 'CONFIDENTIAL'
WHERE code = 'CONFIDENTIEL';
UPDATE confidentiality
SET code = 'OPEN'
WHERE code = 'OUVERT';
