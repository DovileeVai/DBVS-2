-- trigeriu patikrinimas

-- MaxAutoNuoma tikrinimui
INSERT INTO dova7961.Nuomos_sutartis VALUES(16, 2003, 204, '2024-05-01', '2024-05-06');

UPDATE dova7961.Nuomos_sutartis SET Klientas = 204 WHERE Sutarties_nr = 44;


-- ButinasDraudimas tikrinimui
INSERT INTO dova7961.Nuomos_sutartis VALUES (22, 3005, 221, '2024-05-02', '2024-05-05');

BEGIN; -- Pradedame transakciją

-- Įterpiame naują įrašą į Nuomos_sutartis lentelę
INSERT INTO dova7961.Nuomos_sutartis (Sutarties_nr, Automobilis, Klientas, Pradzia, Pabaiga)  VALUES (22, 3005, 221, '2024-05-02', '2024-05-05'));

-- Įterpiame naują įrašą į Draudimas lentelę, jei reikia
INSERT INTO dova7961.Draudimas (Draudimo_nr, Sutartis, Draudejas, Tipas, Kaina) VALUES (77777, 22, 303485437, 'Standartinis', 16.99);

-- Patikriname ar visi trigeriai buvo sėkmingai vykdyti
COMMIT; -- Patvirtiname transakciją


UPDATE dova7961.Nuomos_sutartis SET Automobilis = 3005 WHERE Sutarties_nr = 15;



-- kitu patikrinimas

--patikrinimas metu
INSERT INTO dova7961.Automobilis VALUES(1111, 'Audi', 'A3', 1998, 'Pilka', 50.00) ;

--patikrinimas kainos
INSERT INTO dova7961.Automobilis VALUES(1111, 'Audi', 'A3', 2001, 'Pilka', 1001.00) ;

--pradzios/pabaigos tikrinimas
INSERT INTO dova7961.Nuomos_sutartis VALUES(16, 1234, 203, '2024-03-08', '2024-03-01') ;

--draudimo tikrinimas (turi buti kasko)
INSERT INTO dova7961.Draudimas VALUES(31565, 62, 303485437, 'Standartinis', 205.66) ;





