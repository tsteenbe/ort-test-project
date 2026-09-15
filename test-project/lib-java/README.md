# lib-java

A mock Maven library used to produce ORT analyzer results for the WebApp report.

The code does not do anything useful, the interesting part is `pom.xml`. Its dependency tree is
deliberately tiny:

| Dependency | Scope | Transitive dependencies |
| --- | --- | --- |
| `org.apache.commons:commons-text:1.12.0` | `compile` | `org.apache.commons:commons-lang3:3.14.0` |
| `org.slf4j:slf4j-api:2.0.13` | `compile` | none |
| `junit:junit:4.13.2` | `test` | `org.hamcrest:hamcrest-core:1.3` |

This gives the analyzer two scopes (`compile` and `test`) and five packages in total.
