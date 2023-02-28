-- Suppression du suffixe "_form" des formulaires
UPDATE form_definition
SET name = trim(TRAILING '__form' FROM name);

-- Suppression des labels des sections dont on ne souhaite pas voir la bordure apparaître (par défaut)
ALTER TABLE section_definition
    ALTER COLUMN "label" DROP NOT NULL;
-- noinspection SqlWithoutWhere
UPDATE section_definition
SET label = NULL;
