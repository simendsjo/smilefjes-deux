# ADR 4: CSS

## Kontekst

Den nye smilefjesløsningen må ha et visuelt uttrykk. Det er en løsning med
begrenset omfang (forside, søk, plakatside).

Mattilsynets designsystem er ikke klart til bruk.

TailwindCSS er på radaren vår som en teknologi vi har tro på for videre arbeid.

## Beslutning

Vi prøver oss på å implementere hele designet med TailwindCSS.

## Konsekvenser

Markupen i koden kommer til å ha mange utility-klasser på seg for å gjenskape
skissene med Tailwind sine utilities.

Siden vi bygger samtidig som vi lærer vil koden nok ikke bli helt idiomatisk
TailwindCSS.

Koden bør ikke behandles som eksempel til etterfølgelse.

### Fordeler

Vi får erfaring med å bruke TailwindCSS, og kan forhåpentligvis danne oss noen
informerte meninger om hva det passer bra og mindre bra til.

Vi får implementert designskissene som foreligger.

Løsningen er liten og enkel nok til at eventuelle feil og nybegynnerbommerter
får begrensete konsekvenser.

### Ulemper

Koden kommer til bære preg av å være laget av folk som lærer en ny teknologi.

Allerede etter å ha brukt TailwindCSS litt er mangelen på et komponent-bibliotek
helt tydelig -- vi ønsker på sikt ikke å ha så mange detaljer om hvordan
komponenter ser ut nedfelt i klassenavn.

## Alternativer

Vi kunne også brukt en versjon av CSS-en som ble bygget for matvaretabellen. Det
ville spart oss for tid, men ikke vært uten arbeid: vi måtte ha themet det med
nye farger.
