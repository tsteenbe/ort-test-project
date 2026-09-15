# rest-api

A mock REST API service used to produce ORT analyzer results for the WebApp report.

Every function body is `unimplemented!()`, the interesting part is `Cargo.toml`. The crate graph is
deliberately tiny:

| Dependency | Locked version | Kind | License | Transitive dependencies |
| --- | --- | --- | --- | --- |
| `httparse` | 1.10.1 | normal | `MIT OR Apache-2.0` | none |
| `httpdate` | 1.0.3 | normal | `MIT OR Apache-2.0` | none |
| `form_urlencoded` | 1.2.2 | normal | `MIT OR Apache-2.0` | `percent-encoding@2.3.2` |
| `log` | 0.4.34 | normal | `MIT OR Apache-2.0` | none (default features disabled) |
| `tinyjson` | 2.5.1 | normal | `MIT` | none |
| `pretty_assertions` | 1.4.1 | dev | `MIT OR Apache-2.0` | `diff@0.1.13`, `yansi@1.0.1` |

That is nine packages in total, spread over a `dependencies` and a `dev-dependencies` scope. The
dual `MIT OR Apache-2.0` licenses also give the WebApp report some license choices to resolve.

`Cargo.lock` is committed so that the analyzer result is reproducible.

## Endpoints

The service sketches a single `/widgets` resource:

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/widgets` | List widgets, filterable by `color` and `tag`. |
| `POST` | `/widgets` | Create a widget. |
| `GET` | `/widgets/{id}` | Read a single widget. |
| `PUT` | `/widgets/{id}` | Replace a widget. |
| `DELETE` | `/widgets/{id}` | Delete a widget. |
