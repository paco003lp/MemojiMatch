# MemojiMatch

## Projektni tim

Ime i prezime | E-mail adresa (FOI) | JMBAG | Github korisničko ime
------------  | ------------------- | ----- | ---------------------
Matija Maljak | mmaljak22@student.foi.hr | 0016161603 | Makibrzina
Patrik Crnčić | pcrncic22@student.foi.hr  | 00161616500 | pcrncic22
Bruno Nađ | bnad22@student.foi.hr | 0016158632 | B-nad

## Opis domene
Igra MemojiMatch zamišljena je kao memorijska igra gdje igrač mora pronači dvije "kartice" s istim emotikonom. Kartice su na početku okrenute tako da se ne vide emotikoni te kada igrač dodirne karticu ona se okrene i prikaže koje je emotikon na njoj. Igra prati koliko je parova emotikona uspješno pronađeno te koliko je pogrešnih parova emotikona pronađeno te ovisno o tim brojevima daje "score" ili "rezultat" igraču. Kako igrač napreduje kroz nivoe tako će mu se sve više kartica prikazivati. Igra je namjenjena tome da testira i očvrsti igračevu memoriju. 

## Specifikacija projekta

Oznaka | Naziv | Kratki opis 
------ | ----- | ----------- 
F01 | Upravljanje profilima | Unos korisničkog imena i odabir avatara. Podaci se spremaju u Room bazu.
F02 | Glavni izbornik | Navigacija (Fragments/Navigation Component) između igre, profila i ljestvice.
F03 | Postavke sustava | Uključivanje/isključivanje zvuka i vibracije (korištenje SharedPreferences).
F04 | Logika uparivanja | Glavni engine igre: miješanje kartica, provjera parova i Handler za okretanje kartica.
F05 | Bodovni sustav | Izračun bodova na temelju preostalog vremena i broja pokušaja (kazneni bodovi).
F06 | Razine težine | Dinamičko generiranje mreže (4x4, 4x6, 6x6) ovisno o odabiru igrača.
F07 | Leaderboard | Dohvaćanje i prikaz Top 10 rezultata iz Room baze podataka u RecyclerView.
F08 | Povijest igara | Poseban ekran koji prikazuje zadnjih 5 odigranih partija korisnika (iz Room baze).
F09 | Multimedija i feedback | Implementacija zvučnih efekata (SoundPool) i animacija pri uspješnom spajanju parova.

## Tehnologije i oprema
Tenologije, oprema i alati koje ćemo koristiti su: GitHub, GitHub Desktop, GitHub Wiki, GitHub projects, Jira, Figma, android studio, kotlin. Razvijati ćemo softver agilnim pristupom koristeči SCRUM okriv.

## Više o dizajnu i razvoju možete pročitati na [Wiki stranici](https://github.com/foivz/rmai26-brzina-i-instruktori/wiki/Po%C4%8Detna-stranica)
