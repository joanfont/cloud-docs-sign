.PHONY: build push

build:
	docker build -t joanfont/cloud-docs-sign .

push:
	docker push joanfont/cloud-docs-sign