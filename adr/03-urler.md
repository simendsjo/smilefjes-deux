# ADR 3: URL-oppsett

## Kontekst

Rapportene må være tilgjengelige på en URL, og de er allerede tilgjengelige på
en URL på matportalen.no.

## Beslutning

Det settes opp redirect for alle URL-er fra smilefjes.matportalen.no (gammel
løsning) til smilefjes.mattilsynet.no (denne løsningen) som viderefører pathen.

Vi sørger for at alle eksisterende URL-er fortsatt fungerer, av og til ved at vi
redirecter videre.

Vi har en "offentlig" URL som inneholder litt meningsbærende informasjon
(poststed og navn på spisested) som skrives om til en intern URL som kun har
id-en til spisestedet.

## Konsekvenser

Alle dagens lenker og bokmerker til smilefjesplakater på matportalen.no vil
fungere som før.

Alle smilefjesplakater blir tilgjengelig på en ny dedikert URL.

Den samme smilefjesplakaten kan nås via flere URL-er.

### Fordeler

URL-er som allerede er "in the wild" fortsetter å fungere. Skulle være nokså
ukontroversielt.

### Ulemper

Den meningsbærende informasjonen i dagens URL-er (poststed og navn på spisested)
kan ikke antas å være persistent. Siden vi ikke eier dataene kan vi heller ikke
gi stedene en permanent "slug"/tekstlig URL. Vi har derfor valgt å støtte disse
URL-ene med en nginx rewrite som godtar hva som helst i posisjonene til poststed
og navn på spisested. Dette gjør at man kan skrive mange URL-er som peker til
samme sted. Dette kan være negativt for søkemotorer, men det løses ved å legge
en link-tag med canonical URL på hver side.

## Alternativer

Vi vurderte å gå ut med kun /spisested/<id>/ men landet på at det er hyggeligere
med litt meningsbærende informasjon i URL-en.
