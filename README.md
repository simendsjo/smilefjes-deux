# Smilefjes-plakater på nett

Her bor løsningen som viser resultatene fra smilefjestilsyn på alle norske
kaféer og restauranter som er underlagt ordningen på
https://smilefjes.mattilsynet.no.

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
en [(statisk) JSON payload](https://smilefjes.mattilsynet.no/search/index/nb.json),
og så har vi implementert [en liten søkemotor](./src/smilefjes/search_index.clj)
som kjører i nettleseren.

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

- Sørg for at du har [FontAwesome](https://fontawesome.com)-ikonene:

    ```
    make prepare-dev
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

## Oppdatering av postnummer

Vi har valgt å lagre data/postnummer.csv i dette repoet, da URL'en til Posten
Bring ikke ser ut til å være stabil. Du kan finne fila her:

https://www.bring.no/tjenester/adressetjenester/postnummer/postnummertabeller-veiledning

Sist gang lå lenken under overskriften "3. Postnummer i rekkefølge,
postnummertabellen" og het "Postnummerregister_ansi.txt". Vi har lagret den som
`data/postnummer.csv`.

## Oppdatering av ikke omfattede virksomheter

Kodeverket forteller oss at alle virksomheter med aktivitetsid DETALJ_NAER er
omfattet av smilefjesordningen. Den gang ei. Det er snarere et flertall med
denne aktivitetsid'en som IKKE er omfattet. Derfor har regionene måttet markere
over 100 000 virksomheter som "ikke omfattet" utenfor kodeverket.

Den lista ble litt i lengste laget for å sjekke inn her. Vi har derfor laget en
liten eksportrutine som kan gjøres manuelt ved behov. Du finner den i
m2n-repo'et - ettersom det er der vi for øyeblikket har tilgang til MATS-data.
