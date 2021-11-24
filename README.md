![Build Status][buildstatus]
![Tested with Camel version][camelver]
![Project licence][licence]
![Egg Status][eggs]

# camel-demos

Some demos and tests for Apache Camel! Please enjoy!

These are intended to be used with the articles and tutorials at [tomd.xyz][tomd].

See also my book, [Camel Step-by-Step: A beginner's guide to Apache Camel][camelsbs].

Thanks, Tom.

## Troubleshooting

If you get strange missing class errors, make sure you're compiling with the latest parent POM: `mvn clean install -f parent/pom.xml`.

## In this repo

- **simple-tests** - Quick demos of Camel functionality implemented using Camel's test framework. Lift and shift the Camel route definitions into your own applications!
- **examples** - Complete example applications which demonstrate specific use cases and platforms.
- **spring-boot-tests** - Tests specifically using Spring Boot.

[tomd]: https://tomd.xyz
[camelsbs]: https://tomd.xyz/camelstepbystep
[buildstatus]: https://github.com/monodot/camel-demos/workflows/maven-test/badge.svg
[camelver]: https://img.shields.io/badge/dynamic/xml?label=Tested%20with%20Apache%20Camel&query=%2F%2A%5Blocal-name%28%29%3D%27project%27%5D%2F%2A%5Blocal-name%28%29%3D%27properties%27%5D%2F%2A%5Blocal-name%28%29%3D%27camel.version%27%5D&url=https%3A%2F%2Fraw.githubusercontent.com%2Fmonodot%2Fcamel-demos%2Fmaster%2Fparent%2Fpom.xml&color=orange
[licence]: https://img.shields.io/github/license/monodot/camel-demos.svg
[eggs]: https://img.shields.io/badge/eggs-poached-yellow.svg



