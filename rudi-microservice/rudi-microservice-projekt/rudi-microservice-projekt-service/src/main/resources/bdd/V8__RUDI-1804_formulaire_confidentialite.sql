-- Modification de la table confidentialité, rendre la colonne UNIQUE pour
-- éviter d'avoir plusieurs fois le même niveau de confidentialité affiché sur le front
alter table if exists projekt_data.confidentiality
    add constraint UK_e3u3snmur2wtwbieg45va7dfp unique (code);