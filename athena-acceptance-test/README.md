Athena Acceptance Test Framework
================================

Athena acceptance test framework is a slightly modified BDD, which
[eliminates the QA](https://spectrum.ieee.org/yahoos-engineers-move-to-coding-without-a-net)

To run all acceptance tests, run

    cd athena
    mvn clean verify

> Note that all unit & integration tests will also run by the command above

Development
-----------

**In general, Maven dependencies of this module should NOT be promoted to athena parent POM**
