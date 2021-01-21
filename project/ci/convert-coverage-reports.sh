#!/usr/bin/env sh

for module in $(ls ./modules/); do
	python /opt/cover2cover.py modules/$module/target/jacoco-report.xml modules/$module/src modules/$module/src.test > modules/$module/target/cobertura-report.xml
	python /opt/source2filename.py modules/$module/target/cobertura-report.xml
done
