# website

A mock static site generator used to produce ORT analyzer results for the WebApp report.

The build script is a stub, the interesting part is `package.json`. All packages are
`devDependencies`, which is what you would expect for a site that ships plain HTML, CSS and
JavaScript. The dependency tree is deliberately tiny:

| Dev dependency | Locked version | License | Transitive dependencies |
| --- | --- | --- | --- |
| `clean-css@^5.3.3` | 5.3.3 | MIT | `source-map@0.6.1` (BSD-3-Clause) |
| `js-yaml@^4.1.0` | 4.3.2 | MIT | `argparse@2.0.1` (Python-2.0) |
| `marked@^12.0.2` | 12.0.2 | MIT | none |
| `mkdirp@^3.0.1` | 3.0.1 | MIT | none |
| `mustache@^4.2.0` | 4.2.0 | MIT | none |
| `uglify-js@^3.17.4` | 3.19.3 | BSD-2-Clause | none |

That is eight packages in total, all in the `devDependencies` scope, covering four different
licenses.

`package-lock.json` is committed so that the analyzer result is reproducible. It was created with
`npm install --package-lock-only`, so there is no `node_modules` directory.
