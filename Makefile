VERSION = $$(git rev-parse --short=10 HEAD)
IMAGE = europe-north1-docker.pkg.dev/artifacts-352708/mat/smilefjes:$(VERSION)

target/public/js/compiled/app.js:
	clojure -M:build -m figwheel.main -bo prod

docker/build: target/public/js/compiled/app.js
	clojure -X:build

docker: docker/build
	cd docker && docker build -t $(IMAGE) .

publish:
	docker push $(IMAGE)

test:
	clojure -M:dev -m kaocha.runner

clean:
	rm -fr target docker/build dev-resources/public/js/compiled

.PHONY: docker publish test clean
