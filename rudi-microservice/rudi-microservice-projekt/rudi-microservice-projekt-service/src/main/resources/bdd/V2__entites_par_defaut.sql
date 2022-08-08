-- Création des niveaux de confidentialité
INSERT INTO projekt_data.confidentiality(uuid, code, label, opening_date, order_)
VALUES ('ad9e3b79-eba7-4c91-bff2-621a5553c2b2', 'CONFIDENTIEL', 'Confidentiel', now(), 1),
       ('9724de3c-e2eb-4b35-a14e-fc0cdb35779c', 'OUVERT', 'Ouvert', now(), 2);
