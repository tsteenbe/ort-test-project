# ORT test project

This project is used for testing ORT (OSS Review Toolkit).

It contains a small, deliberately mock code base that several package managers can be run against, plus the ORT configuration to analyze, scan, evaluate and report on it.

## Repository layout

| Path | Committed | Description |
| --- | --- | --- |
| `ort/config` | yes | The ORT configuration: `config.yml`, evaluator rules, license classifications, curations and package configurations. Mounted into the container as `/home/ort/.ort`. |
| `ort/cache`, `ort/scanner` | no | Caches and the scanner file archive. Created by the tools on first run. |
| `test-project` | yes | The code base that is analyzed. Contains one project per package manager. |
| `test-project-ort-files` | no | Where every ORT tool writes its results. |
| `scancode-toolkit` | no | Optional mount point for a local ScanCode installation. Empty by default. |

Everything that is not committed is listed in `.gitignore`, so the results can be deleted and regenerated at any time.

### The test project

`test-project` holds four mock projects, one per package manager, plus a repository configuration:

| Path | Package manager | Manifest |
| --- | --- | --- |
| `test-project/lib-java` | Maven | `pom.xml` |
| `test-project/lib-python` | PIP | `requirements.txt` |
| `test-project/rest-api` | Cargo | `Cargo.toml` |
| `test-project/website` | NPM | `package.json` |
| `test-project/ort.yml` | — | Repository configuration, passed via `--repository-configuration-file`. |

The source code is mock and is not meant to be built or run. The dependencies, however, are real and are deliberately chosen to have at most one or two transitive dependencies each, so that the analyzer result stays small enough to inspect by hand. The lock files (`Cargo.lock`, `package-lock.json`) are committed so that the result is reproducible.

Analyzing `test-project` also yields an `Unmanaged` project for the directory itself, so a run reports five projects in total.

## How the Docker commands are set up

All commands are run from the root of this repository, so `${PWD}` is the repository root. They share the same two mounts:

* `${PWD}/ort` as `/home/ort/.ort`, which is where ORT looks for its configuration.
* A path that makes `test-project-ort-files` writable, so the results end up on the host.

### Why the Analyzer mounts the whole repository

The Analyzer mounts the repository root as `-v ${PWD}:/project` and then analyzes the subdirectory with `-i /project/test-project`, rather than mounting `test-project` on its own.

This is required because the projects declare their source code repository in their metadata: `<scm>` in `pom.xml`, `repository` in `package.json` and `Cargo.toml`. ORT reconciles that declared VCS information against the VCS working tree it detects for the analysis root. The `.git` directory sits at the root of this repository, one level above `test-project`. If only `test-project` is mounted, the container sees no working tree, ORT keeps the declared URLs from the manifests, and the run fails with:

```
java.lang.IllegalArgumentException: The VcsInfo(type=Git, url=https://github.com/tsteenbe/ort-test-project.git, revision=, path=)
of project 'Cargo::rest-api:0.2.0' cannot be found in Repository(vcs=VcsInfo(type=, url=, revision=, path=), ...)
```

Mounting the repository root lets ORT detect the working tree, so the repository and every project share the same VCS type, URL and revision, and the analyzer result records them:

```yaml
repository:
  vcs_processed:
    type: "Git"
    url: "ssh://git@github.com/tsteenbe/ort-test-project.git"
    revision: "965de87..."
    path: "test-project"
```

### Why the other commands keep the `test-project` mount

The Scanner, Advisor, Evaluator and Reporter deliberately keep the `${PWD}/test-project` mount and must **not** be switched over to the repository root mount. They read the result file produced by the previous step rather than the working tree, and their arguments are written against that layout, for example the Evaluator's `--repository-configuration-file=/test-project/ort.yml`.

The two mount styles are intentional and not an oversight: the Analyzer needs the repository root, the later steps need `test-project`. Making them uniform breaks the run.

## Results

Every tool reads the output of the previous one and writes into `test-project-ort-files` on the host:

| File | Written by | Input to |
| --- | --- | --- |
| `analyzer-result.yml` | `analyze` | `scan` |
| `scan-result.yml` | `scan` | `advise` |
| `advisor-result.yml` | `advise` | `evaluate` |
| `evaluation-result.yml` | `evaluate` | `report` |
| `evaluated-model.json` | `report -f EvaluatedModel` | — |
| `scan-report-web-app.html` | `report -f WebApp` | — |

Open `test-project-ort-files/scan-report-web-app.html` in a browser to view the WebApp report. It is a single self-contained HTML file.

Each command passes `-P ort.forceOverwrite=true`, so re-running a step overwrites its previous result instead of failing.

## Commands

Run below commands from within directory where cloned this repository.

### Terminal within Docker image

```
docker run -it --rm --pull always \
  --entrypoint /bin/bash \
  -v "${PWD}/ort:/ort" \
  -v "${PWD}/test-project:/test-project:ro" \
  -v "${PWD}/test-project-ort-files:/test-project-ort-files" \
  ghcr.io/oss-review-toolkit/ort-minimal
```

### Display ORT configuration

```
docker run --rm --pull always \
  -v ${PWD}/ort:/home/ort/.ort \
  -v ${PWD}/test-project:/test-project:ro \
  -v ${PWD}/test-project-ort-files:/test-project-ort-files \
  ghcr.io/oss-review-toolkit/ort-minimal \
  config --show-active
```

### Run ORT Analyzer to identify dependencies within test-project directory

Writes `test-project-ort-files/analyzer-result.yml`.

```
docker run --rm --pull always \
  -v ${PWD}/ort:/home/ort/.ort \
  -v "${PWD}/scancode-toolkit:/scancode-toolkit:ro" \
  -v ${PWD}:/project \
  ghcr.io/oss-review-toolkit/ort-minimal \
  -P ort.forceOverwrite=true --stacktrace \
  analyze -i /project/test-project \
  -o /project/test-project-ort-files \
  -f YAML \
  --repository-configuration-file=/project/test-project/ort.yml
```

### Run ORT Scanner to scan test-project project and dependencies for copyrights and licenses

Writes `test-project-ort-files/scan-result.yml`.

```
docker run --rm \
  -v ${PWD}/ort:/home/ort/.ort \
  -v ${PWD}/test-project:/test-project:ro \
  -v ${PWD}/test-project-ort-files:/test-project-ort-files \
  ghcr.io/oss-review-toolkit/ort-minimal \
  -P ort.forceOverwrite=true --stacktrace \
  --info \
  scan \
  -i /test-project-ort-files/analyzer-result.yml \
  -o /test-project-ort-files \
  -f YAML \
  --scanners ScanCode
```

### Run ORT Advisor to check for known security vulnerabilities

Writes `test-project-ort-files/advisor-result.yml`.

```
docker run --rm \
  -v ${PWD}/ort:/home/ort/.ort \
  -v ${PWD}/test-project:/test-project:ro \
  -v ${PWD}/test-project-ort-files:/test-project-ort-files \
  ghcr.io/oss-review-toolkit/ort-minimal \
  -P ort.forceOverwrite=true --stacktrace \
  --info \
  advise \
  -i /test-project-ort-files/scan-result.yml \
  -o /test-project-ort-files \
  --advisors=OSV \
  -f YAML
```

### Run ORT Evaluator to check if results are OK or NOT

Writes `test-project-ort-files/evaluation-result.yml`.

```
docker run --rm \
  -v ${PWD}/ort:/home/ort/.ort \
  -v ${PWD}/test-project:/test-project:ro \
  -v ${PWD}/test-project-ort-files:/test-project-ort-files \
  ghcr.io/oss-review-toolkit/ort-minimal \
  -P ort.forceOverwrite=true --stacktrace \
  evaluate \
  -i /test-project-ort-files/advisor-result.yml \
  -o /test-project-ort-files \
  -l project=oss-project \
  --package-curations-dir="/home/ort/.ort/config/curations" \
  --package-configurations-dir="/home/ort/.ort/config/package-configurations" \
  -f YAML \
  --repository-configuration-file=/test-project/ort.yml
```

### Run ORT Reporter to generate third-party notices, CycloneDX, SPDX SBOMS and the WebApp report

Writes `test-project-ort-files/evaluated-model.json` and `test-project-ort-files/scan-report-web-app.html`.

```
docker run --rm --pull always \
  -v ${PWD}/ort:/home/ort/.ort \
  -v ${PWD}/test-project-ort-files:/test-project-ort-files \
  ghcr.io/oss-review-toolkit/ort-minimal \
  -P ort.forceOverwrite=true --stacktrace \
  report \
  -i /test-project-ort-files/evaluation-result.yml \
  -o /test-project-ort-files \
  -f EvaluatedModel,WebApp
```
