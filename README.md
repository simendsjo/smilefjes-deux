# Smilefjes-plakater på nett

Her bor snart løsningen som viser resultatene fra smilefjestilsyn på alle norske
kaféer og restauranter som er underlagt ordningen på
https://smilefjes.mattilsynet.no.

Dagens løsning finnes (enn så lenge) på https://smilefjes.matportalen.no.

## Statisk site

Plakatene på nett baseres på smilefjes-tilsynene som publiseres på
[data.norge.no](https://data.norge.no/datasets/288aa74c-e3d3-492e-9ede-e71503b3bfd9).
Dette er data som oppdateres relativt sjelden, og vi har derfor valgt å bygge
plakatene som statiske HTML-sider. Med andre ord bygger vi alle rapportene i et
byggesteg og publiserer bare statiske filer til en nginx-server. Da får vi:

- lynraske sider
- færre kjørende prosesser å overvåke og betale for
- lavere karbonfotavtrykk

## Hva med søket?

Serveren [bygger en indeks](./src/smilefjes/search_index.clj) som serveres som
en [(statisk) JSON payload](https://smilefjes.mattilsynet.no/search/index/nb.json)
(~200kB gzippet), og så har vi implementert [en liten
søkemotor](./src/smilefjes/search_index.clj) som kjører i nettleseren.

Du kan lese litt om strategien i disse to blogginnleggene:

- [Hvordan søkemotoren er bygget](https://parenteser.mattilsynet.io/fulltekstsok/)
- [Hvordan vi har løst vekting](https://parenteser.mattilsynet.io/sok-vekting/)

Disse legger opp til en JavaScript-løsning. Vi valgte den bort til fordel for en
ClojureScript-løsning for at klienten kunne dele tokenizing-koden med backenden
(som er skrevet i Clojure) - et kritisk punkt for at søket skal fungere.

## Hvordan kjører jeg dette lokalt?

Dette oppsettet antar for øyeblikket at du sitter på en Mac. Du kan lese mer om
[hvordan dette er skrudd sammen](#arkitektur) lenger ned.

- Skaff Clojure

    ```
    brew install clojure
    ```

- Skaff Node

    ```
    brew install node
    ```

- Start Tailwind

   ```
   make tailwind
   ```

- Start ClojureScript-bygget (Emacs-brukere kan se nedenfor)

    ```
    clj -M:dev -m figwheel.main -b dev -r
    ```

- Hent ned tilsynsdata fra Data Norge.

    ```
    make data
    ```

- Start backenden:

    ```
    clj -M:dev
    (require 'smilefjes.dev)
    (smilefjes.dev/start)
    ```

### Emacs ❤️

Dersom du bruker Emacs - noe vi anbefaler på det aller varmeste - er det
`cider-jack-in` og deretter `cider-connect-sibling-cljs` som gjelder for å få
opp både backenden og frontenden.

## Automatisk bygg

Hvis du har behov for å finne secreten som bygget bruker for å snakke med Slack
anbefales the massive mengder beroligende, før du begir deg utpå den meningsløst
vanskelige oppgaven "finn en installert Slack app sin secret". Når du har gitt
opp så kan du klikke på [denne lenka](https://api.slack.com/apps/A061QTQNFC4/oauth?).

Den lenka kommer helt sikkert til å brekke på et tidspunkt, så her er noen hint
til neste skattejakt:

- Du vil til api.slack.com
- Du vil se på installerte apper - den Slack app-en du har laget
- Du vil til OAuth and Permissions
- Finn "OAuth Tokens for Your Workspace"
