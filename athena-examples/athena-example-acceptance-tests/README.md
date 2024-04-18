Athena Acceptance Test Framework
================================

![Cucumber Badge][Cucumber Badge]

Athena acceptance test framework is a slightly modified BDD, which
[eliminates the QA](https://spectrum.ieee.org/yahoos-engineers-move-to-coding-without-a-net)

The acceptances tests runs against the
[book example application](https://github.com/QubitPi/athena/tree/master/athena-examples/athena-example-books), which
can be brought up using its dedicated Docker Compose:

```console
cd athena/athena-examples/athena-example-books/
docker compose up
```

Then navigate to the project root and run all acceptance tests with

```console
mvn clean verify
```

[Cucumber Badge]: https://img.shields.io/badge/Cucumber-23D96C?style=for-the-badge&logo=cucumber&logoColor=white
