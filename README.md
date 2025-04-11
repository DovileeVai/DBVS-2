# DBVS
Duomenų valdymo sistemų 2 laboratorinis darbas – „Automobilių nuomos punktas“, realizuota naudojant Java ir PostgreSQL. <br>
Įgyvendintas automobilių nuomos servisas. <br>
Sukurta duomenų bazė, kurioje saugomas automobilių, kuriuos galima išsinuomoti, sąrašas, užregistruoti klientai, sudarytos nuomos sutartys, bei galimi draudėjai.
### Funkcionalumas:
- Galimybė ieškoti automobilių pagal pasirinktą kainą (kainos rėžiai).
- Naujo kliento registracija: asmens kodas, vardas, pavarde, telefono numeris.
- Nuomos sutarties pabaigos datos atnaujinimas: koreguojamos sutarties numeris, nauja pabaigos data.
- Kliento pašalinimas iš duomenų bazės: kliento numeris (unikalus ID).
- Naujos sutarties su draudimu registracija: automobilio ID, kliento numeris, sutarties pradžios data, sutarties pabaigos data, draudimo imonės kodas (iš pateikiamo sąrašo), draudimo tipas, draudimo kaina.
### ER diagrama:
![Automobiliu_nuomos_punktas_ER_diagrama](https://github.com/user-attachments/assets/dc4857a2-a768-4dd1-993b-3e4585418bbb)
### DB schema:
![Automobiliu_nuomos_punktas_DB_schema](https://github.com/user-attachments/assets/65e990e3-580c-4b72-bda5-5307efcda6d9)
