# ADR 1: Vi bygger en moderat visuelt oppdatert kopi av smilefjes.matportalen.no

## Kontekst

Journalister og andre bruker i dag smilefjes.matportalen.no for å se på
resultatet av smilefjestilsyn. Matportal var et samarbeid mellom Mattilsynet og
Helsedirektoratet som nå er avsluttet, og løsningen skal legges ned. Dagens
tekniske løsning er utdatert og utgjør en sikkerhetsrisiko. Etter at tjenesten
ble forsøkt stengt ble de klart at mange fortsatt var avhengige av informasjonen
i Smilefjesplakatene, og løsningen er derfor midlertidig oppe igjen.

Mat-teamet som har det tekniske ansvaret for tjenesten er opptatt med å bygge
verktøy for smilefjesinspektørene og har derfor begrenset med kapasitet til
flere prosjekter.

## Beslutning

Vi lager en rask kopi av smilefjes.matportalen.no og legger den på
smilefjes.mattilsynet.no. Det gjøres ikke noe innsiktsarbeid, eller andre forsøk
på å tenke tjenesten på nytt. Tjenesten får en noe oppdatert drakt i form av
Mattilsynets nye visuelle profil (ny logo, nye farger, ny font), men blir ellers
så lik som mulig.

## Konsekvenser

Journalister og andre får fortsatt lett tilgang til smilefjesresultatene.

Den gamle serveren kan skrus av.

Mat-teamet tar eierskap til en tjeneste som ligger under sitt ansvarsområde.

### Fordeler

- Tjenesten fortsetter å glede eksisterende brukere.
- Vi fjerner stor sikkerhetsrisiko
- Løsningen er rask å gjennomføre

### Ulemper

- Ingen substansielle forbedringer av tjenesten
- Tar fokus bort fra vårt hovedoppdrag

## Alternativer

Det eneste reelle alternativet er å kjøre opp den gamle koden i GCP. Løsningen
er gammel og skrevet i Java. Vi antar at å stunte en statisk site ala nylig
lanserte matvaretabellen.no vil gå omtrent like raskt som å få den gamle
løsningen opp og stå, og vil koste oss betydelig mindre hodebry i drift.
