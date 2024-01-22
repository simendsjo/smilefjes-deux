VERSION = $$(git rev-parse --short=10 HEAD)
IMAGE = europe-north1-docker.pkg.dev/artifacts-352708/mat/smilefjes:$(VERSION)

resources/fontawesome-icons:
	clojure -Sdeps "{:deps {no.cjohansen/fontawesome-clj {:mvn/version \"2023.10.26\"} \
	clj-http/clj-http {:mvn/version \"3.12.3\"} \
	hickory/hickory {:mvn/version \"0.7.1\"}}}" \
	-M -m fontawesome.import :download resources 6.4.2

resources/public/tailwind-out.css:
	npx tailwindcss -i ./src/tailwind.css -o resources/public/tailwind-out.css

target/public/js/compiled/app.js: resources/fontawesome-icons
	clojure -M:build -m figwheel.main -bo prod

docker/build: target/public/js/compiled/app.js resources/public/tailwind-out.css
	clojure -X:build

docker: docker/build
	cd docker && docker build -t $(IMAGE) .

publish:
	docker push $(IMAGE)

test:
	clojure -M:dev -m kaocha.runner

clean:
	rm -fr target docker/build dev-resources/public/js/compiled

node_modules:
	npm install

tailwind: node_modules
	npx tailwindcss -i ./src/tailwind.css -o ./resources/public/tailwind-out.css --watch

data/tilsyn.csv:
	./download.sh

data/vurderinger.csv:
	./download.sh

data:
	./download.sh

prepare-dev: resources/fontawesome-icons

.PHONY: docker publish test clean tailwind data prepare-dev
