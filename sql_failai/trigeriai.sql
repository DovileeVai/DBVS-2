-- Trigeriai


CREATE OR REPLACE FUNCTION PatikrintiMaxAutoNuomaIvedant() RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT COUNT(*) FROM dova7961.Nuomos_sutartis WHERE Klientas = NEW.Klientas) >= 5 THEN
        RAISE EXCEPTION 'Virsytas nuomos sutarciu skaicius vienam klientui ivedant';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER MaxAutoNuomaIvedant
BEFORE INSERT ON dova7961.Nuomos_sutartis
FOR EACH ROW 
EXECUTE FUNCTION PatikrintiMaxAutoNuomaIvedant();



CREATE OR REPLACE FUNCTION PatikrintiMaxAutoNuomaAtnaujinant() RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT COUNT(*) FROM dova7961.Nuomos_sutartis WHERE Klientas = NEW.Klientas) >= 5 THEN
        RAISE EXCEPTION 'Virsytas nuomos sutarciu skaicius vienam klientui atnaujinant';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER MaxAutoNuomaAtnaujinat
BEFORE UPDATE OF Klientas ON dova7961.Nuomos_sutartis
FOR EACH ROW 
EXECUTE FUNCTION PatikrintiMaxAutoNuomaAtnaujinant();



CREATE OR REPLACE FUNCTION PatikrintiButinasDraudimasIvedant() RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT Kaina_uz_diena FROM dova7961.Automobilis WHERE Automobilio_id = NEW.Automobilis) >= 200 THEN
        IF NOT EXISTS (SELECT 1 FROM dova7961.Draudimas WHERE Sutartis = NEW.Sutarties_nr AND Tipas = 'Kasko') THEN
            RAISE EXCEPTION 'Automobilis turi būti draustas Kasko draudimu, nes yra brangus (IVEDANT)';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER ButinasDraudimasIvedant
AFTER INSERT ON dova7961.Nuomos_sutartis
DEFERRABLE INITIALLY DEFERRED --kad butu ivykdoma tranzakcija ir tik tada patikrinama (atidedamas trigerio iskvietimas)
FOR EACH ROW
EXECUTE FUNCTION PatikrintiButinasDraudimasIvedant();



CREATE OR REPLACE FUNCTION PatikrintiButinasDraudimasAtnaujinant() RETURNS TRIGGER AS $$
BEGIN
    IF (SELECT Kaina_uz_diena FROM dova7961.Automobilis WHERE Automobilio_id = NEW.Automobilis) >= 200 THEN
        IF NOT EXISTS (SELECT 1 FROM dova7961.Draudimas WHERE Sutartis = NEW.Sutarties_nr AND Tipas = 'Kasko') THEN
            RAISE EXCEPTION 'Automobilis turi būti draustas Kasko draudimu, nes yra brangus (ATNAUJINANT)';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE CONSTRAINT TRIGGER ButinasDraudimasAtnaujinant
AFTER UPDATE OF Automobilis ON dova7961.Nuomos_sutartis
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW
EXECUTE FUNCTION PatikrintiButinasDraudimasAtnaujinant();










