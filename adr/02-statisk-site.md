# ADR 2: Implementere smilefjesplakater som en statisk site

## Kontekst

smilefjes.matportalen.no må avvikles ASAP, men løsningen er fortsatt i bruk.
Dataene som vises kommer fra data.norge.no, og oppdateres maks en gang om dagen.

## Beslutning

Vi implementerer nye smilefjesplakater etter samme modell som nylig lanserte
matvaretabellen, altså som en statisk site. Det vil si at vi bygger en kodebase
for å generere sider, og når løsningen skal i produksjon eksporterer vi alt
sammen til statiske HTML-filer og pakker det inn i et Docker image med nginx.

Vi bygger siten en gang om dagen for å holde data oppdatert.

## Konsekvenser

Løsningen får i praksis "ingen backend". Det vil si, den vil jo ha en kjørende
Docker container, men denne vil kun inneholde nginx som serverer statiske filer.

Vi har ikke muligheten til å gjøre dynamiske forespørsler til backenden, så alt
må enten forhåndsgenereres eller løses på klienten.

Løsningen bruker mer maskinkraft på Github Actions, ettersom det er der alle
sidene bygges. Dette er også tilfelle på lang sikt, ettersom siten bygges en
gang daglig.

### Fordeler

- Løsningen kan utvikles svært raskt, da den kun har features som allerede er
  levert på matvaretabellen
- Løsningen blir rask, stabil og krever få ressurser
- Løsningen har lavt behov for drift og vedlikehold
- Løsningen krever lite monitorering og overvåkning
- Løsningen kan verifiseres grundig før deploy (lenkesjekk på hele nettstedet,
  sjekke at alle bilder og assets er gyldige, etc)
- Billig drift

### Ulemper

- Kan ikke ha dynamisk server-generert innhold, feks brukerdefinerte Excel-filer
  (disse lages heller som CSV på klienten)
- Bruker relativt mange byggeminutter på Github actions

## Alternativer

Ingen alternativer ble vurdert.
