/*
 * Copyright (C) 2022 The ORT Project Authors (see <https://github.com/oss-review-toolkit/ort-config/blob/main/NOTICE>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

/**
 * Creates a map from Issues to their package identifiers, keyed by Issue hash.
 */
val issueHashToPackageId: Map<String, Identifier> = buildMap {
    val issues = ortResult.getIssues()

    issues.forEach { (id, issuesForId) ->
        issuesForId.forEach { put(it.hashCodeKey(), id) }
    }
}

/**
 * Return the version-range package configuration matcher for package [id].
 *
 * For example, if 'PyPI::flower:0.9.7' is found in your scan,
 * then this function will return:
 *
 * id: "PyPI::flower:(,0.9.7]"
 * source_code_origin: "VCS"
 *
 */
fun getPackageConfigurationMatcherText(id: Identifier): String {
    if (!ortResult.isPackage(id)) return ""

    val provenance = ortResult.getScanResultsForId(id).firstOrNull()?.provenance

    if (provenance is ArtifactProvenance) {
        return buildString {
                append("""id: "${id.toCoordinatesWithoutVersion()}:(,${id.version}]"""")
                appendLine()
                append("""     source_code_origin: "ARTIFACT"""")
            }
    }

    if (provenance is RepositoryProvenance) {
        return buildString {
                append("""id: "${id.toCoordinatesWithoutVersion()}:(,${id.version}]"""")
                appendLine()
                append("""     source_code_origin: "VCS"""")
            }
    }

    return ""
}

/**
 * Return the file path within ORT's configuration curations directory for [id].
 *
 * For example, if 'NPM::acorn:7.1.1' is found in your scan,
 * then this function will return 'curations/NPM/_/acorn.yml'.
 *
 */
fun getPackageCurationsFilePath(id: Identifier): String =
    "curations/${id.type}/${id.namespace.ifBlank { "_" }}/${id.name}.yml"

/**
 * Return file path of package configuration in the ORT configuration repository for package [id].
 *
 * For example, if 'PyPI::flower:0.9.7' is found in your scan,
 * then this function will return: 'package-configurations/PyPI/_/flower/configs.yml'
 *
 */
fun getVersionRangePackageConfigurationFilePath(id: Identifier): String =
    "package-configurations/${id.type}/${id.namespace.ifBlank { "_" }}/${id.name}/configs.yml"

/**
    * Return a unique hash for an Issue based on its constituent elements.
    */
fun Issue.hashCodeKey(): String {
    val raw = "$timestamp$source:$message:$severity$affectedPath"
    return raw.hashCode().toString()
}

/**
 * Return the coordinates without the version.
 *
 * For example, this function will return 'PyPI::flower' for package id 'PyPI::flower:0.9.7'
 * and 'Maven:org.antlr:antlr4' for 'Maven:org.antlr:antlr4:4.0.0'.
 *
 */
fun Identifier.toCoordinatesWithoutVersion() = "$type:$namespace:$name"

object : HowToFixTextProvider {
    private val messagePatternRegexCache = mutableMapOf<String, Regex>()

    val Issue.pkg: Identifier get() = issueHashToPackageId[hashCodeKey()] ?: Identifier.EMPTY

    /**
     * Variables used within how-to-fix instructions
     */
    val globTutorialMdLink = "[reference documentation](https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob)"
    val ivyVersionMatchersMdLink = "[Ivy version matchers](https://ant.apache.org/ivy/history/2.5.0/settings/version-matchers.html)"
    val ortConfigContributingMdLink = "[ort-config repository's CONTRIBUTING.md](https://github.com/oss-review-toolkit/ort-config/blob/main/CONTRIBUTING.md)"
    val ortConfigVcsMdLink = "[ort-config repository](https://github.com/oss-review-toolkit/ort-config)"
    val ortCurationsDeclaredLicenseMappingMdLink = "[declared license mapping curation](https://oss-review-toolkit.org/ort/docs/configuration/package-curations)"
    val ortCurationsSourceArtifactMdLink = "[source artifact curation](https://oss-review-toolkit.org/ort/docs/configuration/package-curations)"
    val ortCurationsSourceCodeOriginMdLink = "[source code origin curation](https://oss-review-toolkit.org/ort/docs/configuration/package-curations)"
    val ortCurationsVcsMdLink = "[VCS curation](https://oss-review-toolkit.org/ort/docs/configuration/package-curations)"
    val ortCurationsVcsRevisionMdLink = "[VCS revision curation](https://oss-review-toolkit.org/ort/docs/configuration/package-curations)"
    val ortCurationsVcsTypeMdLink = "[VCS type curation](https://oss-review-toolkit.org/ort/docs/configuration/package-curations)"
    val ortCurationsVcsUrlMdLink = "[VCS URL curation](https://oss-review-toolkit.org/ort/docs/configuration/package-curations)"
    val ortPackageConfigurationFileMdLink = "[package configuration](https://oss-review-toolkit.org/ort/docs/configuration/package-configurations)"
    val ortPathExcludeReasonMdLink = "[PathExcludeReason.kt](https://github.com/oss-review-toolkit/ort/blob/main/model/src/main/kotlin/config/PathExcludeReason.kt)"
    val ortResolutionsYmlIssueMdLink = "[issue resolution](https://oss-review-toolkit.org/ort/docs/configuration/resolutions#resolving-issues)"
    val ortYmlFileIssueResolutionMdLink = "[issue resolution](https://oss-review-toolkit.org/ort/docs/configuration/ort-yml#resolutions)"
    val ortYmlFileMdLink = "[.ort.yml file](https://oss-review-toolkit.org/ort/docs/configuration/ort-yml)"
    val ortYmlFilePathExcludeMdLink = "[path exclude](https://oss-review-toolkit.org/ort/docs/configuration/package-configurations#defining-path-excludes-and-license-finding-curations)"
    val ortYmlFileScopeExcludeMdLink = "[scope exclude](https://oss-review-toolkit.org/ort/docs/configuration/ort-yml#excluding-scopes)"
    val relatesToIssueText = "Relates-to: [Insert related issue number].".takeIf { ortResult.labels["jira"].isNullOrEmpty() } ?: "Relates-to: ${ortResult.labels["jira"]}."
    val resolveIssueGetHelpText = "Can't fix this tooling issue yourself? " +
        "Contact support for assistance and include links to the ORT run logs, the analyzer-result.yml, and the WebApp report."

    fun Issue.matchesMessage(pattern: String): Boolean =
        messagePatternRegexCache.getOrPut(pattern) {
            Regex(pattern, setOf(RegexOption.DOT_MATCHES_ALL))
        }.matches(message)

    override fun getHowToFixText(issue: Issue): String? {
        val pkg = ortResult.getProject(issue.pkg)?.toPackage()
            ?: ortResult.getPackage(issue.pkg)?.metadata
            ?: Package.EMPTY.copy(id = issue.pkg)
        val pkgId = pkg.id

        // How-to-fix instructions for when issue is caused due to network connectivity issue.
        if (issue.matchesMessage(".*DownloadException.*SocketTimeoutException.*")) {
            return """
                |This issue is likely caused by a temporary network problem; a scan re-run should resolve it.
                |
                |$resolveIssueGetHelpText
                |""".trimMargin()
        }

        // How-to-fix instructions for when code repository or source artifact for dependency cannot be accessed.
        if (issue.matchesMessage("IOException.*401.*") || issue.matchesMessage("IOException.*403.*")) {
            val provenance = ortResult.getScanResultsForId(pkgId).firstOrNull()?.provenance

            if (provenance is RepositoryProvenance && provenance.vcsInfo.url.isNotBlank()) {
                return """
                    |The review tooling tried to retrieve the source code for ${pkg.id.toCoordinates()} so it can be scanned,
                    |but access to the code repository (${provenance.vcsInfo.url}) was denied.
                    |
                    |To resolve this, grant the review tooling and/or the package manager the required access.
                    |
                    |$resolveIssueGetHelpText
                    """.trimMargin()
            } else if (provenance is ArtifactProvenance && provenance.sourceArtifact.url.isNotBlank()) {
                return """
                    |The review tooling tried to retrieve the source code for ${pkg.id.toCoordinates()} so it can be scanned,
                    |but access to the source artifact (${provenance.sourceArtifact.url}) was denied.
                    |
                    |$resolveIssueGetHelpText
                    """.trimMargin()
            } else {
                return """
                    |The review tooling ran into authentication issue so ${pkg.id.toCoordinates()} cannot be scanned.
                    |
                    |To resolve this, grant the review tooling and/or the package manager the required access.
                    |
                    |Are there no sources available for this package? See $ortConfigContributingMdLink for guidance.
                    |
                    |$resolveIssueGetHelpText
                    """.trimMargin()
            }
        }

        // How-to-fix instructions for when source artifact for dependency cannot be found.
        if (issue.matchesMessage("IOException.*Could not verify existence of source artifact.*HTTP request got response 404.*")) {
            return """
                |The review tooling cannot find the source artifact for ${pkg.id.toCoordinates()} so it cannot be scanned.
                |
                |Try to resolve this technical issue by following the advice below:
                |
                |1. If the package is not part of your project's released artifacts, then add a $ortYmlFilePathExcludeMdLink or
                |   $ortYmlFileScopeExcludeMdLink in your $ortYmlFileMdLink to resolve this issue.
                |2. Can't exclude this package? Find code repository and code revision/version tag for this version of the package.
                |3. Found the code repository for ${pkg.id.toCoordinates()}?
                |   You may then be able to use a $ortCurationsVcsMdLink to resolve this issue.
                |   - Use Git to clone the $ortConfigVcsMdLink.
                |   - Open or create using a text editor `${getPackageCurationsFilePath(pkg.id)}` and add a $ortCurationsVcsUrlMdLink.
                |   - For the standard VCS URL curation comments to use, see $ortConfigContributingMdLink - an example is shown below.
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinatesWithoutVersion()}"
                |       curations:
                |         comment: |
                |           Package code repository location is not declared in metadata or unavailable from used package registries.
                |           Found VCS via manual search, see
                |           [https://url-to-evidence-proving-vcs-url-is-the-correct-one-for-this-package].
                |         vcs:
                |           type: "[Repository type e.g., Git, Mercurial or Subversion]"
                |           url: "[Code repository url for this package]"
                |```
                |
                |   - Submit the above change to the $ortConfigVcsMdLink with a commit message as shown below.
                |
                |```
                |     feat(curations): Set VCS URL for `${pkg.id.toCoordinatesWithoutVersion()}`
                |
                |     $relatesToIssueText
                |```
                |
                |   - Once your $ortCurationsVcsMdLink is merged, re-scan to verify if the issue has been resolved.
                |
                |4. Unable to find code repository but you did find the source artifact for ${pkg.id.toCoordinates()}?
                |   You may then be able to use a $ortCurationsSourceArtifactMdLink to resolve this issue.
                |   - Use Git to clone the $ortConfigVcsMdLink.
                |   - Open or create using a text editor `${getPackageCurationsFilePath(pkg.id)}` and add a $ortCurationsSourceArtifactMdLink.
                |   - For the standard source artifact curation comments to use, see $ortConfigContributingMdLink - an example is shown below.
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinates()}"
                |       curations:
                |         comment: |
                |           Package code repository location is not declared in metadata or unavailable from used package registries.
                |           Found VCS via manual search, see
                |           [https://url-to-evidence-proving-artifact-url-is-the-correct-one-for-this-package].
                |         source_artifact:
                |           url: "[Source code artifact url for this package]"
                |```
                |
                |   - Submit your curation to the $ortConfigVcsMdLink with a commit message as shown below.
                |
                | ```
                |     feat(curations): Set source artifact for `${pkg.id.toCoordinates()}`
                |
                |     $relatesToIssueText
                |```
                |
                |   - Once your $ortCurationsSourceArtifactMdLink is merged, re-scan to verify if the issue has been resolved.
                |
                |5. $resolveIssueGetHelpText
                """.trimMargin()
        }

        // How-to-fix instructions for when a package’s type couldn't be determined.
        if (issue.matchesMessage("IOException.*Could not determine VCS for type.*")) {
            return """
                |The review tooling couldn't determine the type of code repository e.g.
                |Git, Mercurial or Subversion so it didn't download the sources to be scanned.
                |
                |Try to resolve this technical issue by following the advice below:
                |
                |1. If the package is not part of your project's released artifacts, then add a $ortYmlFilePathExcludeMdLink or
                |   $ortYmlFileScopeExcludeMdLink in your $ortYmlFileMdLink to resolve this issue.
                |2. Can't exclude this package? Verify that the found code repository and code revision/version tag
                |   is the correct one for the version of this package.
                |3. Is the found code repository and code revision/version tag incorrect?
                |   You may then be able to use a $ortCurationsVcsMdLink to resolve this issue.
                |   - Use Git to clone the $ortConfigVcsMdLink.
                |   - Open or create using a text editor `${getPackageCurationsFilePath(pkg.id)}`.
                |   - Code repository incorrect? Add a $ortCurationsVcsTypeMdLink.
                |     For the standard VCS URL curation comments to use, see $ortConfigContributingMdLink - an example is shown below.
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinatesWithoutVersion()}"
                |       curations:
                |         comment: |
                |           Package code repository location is not declared in metadata or unavailable from used package registries.
                |           Found via manual search, see
                |           [https://url-to-evidence-proving-vcs-type-is-the-correct-one-for-this-package].
                |         vcs:
                |           type: "[Repository type e.g., Git, Mercurial or Subversion]"
                |           url: "[Code repository url for this package]"
                |```
                |
                |   - Submit the above change to the $ortConfigVcsMdLink with a commit message as shown below.
                |
                |```
                |     feat(curations): Set VCS URL for `${pkg.id.toCoordinatesWithoutVersion()}`
                |
                |     $relatesToIssueText
                |```
                |
                |   - Once your $ortCurationsVcsTypeMdLink is merged, re-scan to verify if the issue has been resolved.
                |
                |4. $resolveIssueGetHelpText
                |""".trimMargin()
        }

        // How-to-fix instructions for when a package’s code revision cannot be found.
        if (issue.matchesMessage("IOException.*Could not resolve revision.*Could not find any revision candidates for package.*")) {
            if (ortResult.isProject(pkgId)) {
                if (pkgId.version.isBlank()) {
                    // How-to-fix instructions for when a project has no version or revision
                    return """
                        |No revision/version is provided for this package;
                        |the review tooling needs one to find the source code to scan
                        |
                        |Try to resolve this technical issue by following the advice below:
                        |
                        |1. Refer to the documentation of ${pkgId.type} to learn how to define
                        |   a version number for this package.
                        |2. Once you have implemented re-scan to verify the issue has been resolved.
                        |3. $resolveIssueGetHelpText
                        |""".trimMargin()
                } else {
                    // How-to-fix instructions for when revision for project cannot be found.
                    return """
                        |The review tooling cannot find either the code repository location or revision/version tag
                        |for this project so it could not find any source code to scan.
                        |
                        |Try to resolve this technical issue by following the advice below:
                        |
                        |1. Ensure this package metadata correctly declares code repository location
                        |   and if possible version tag or code revision.
                        |   Refer to the documentation of ${pkgId.type} to learn how to define this package metadata.
                        |2. Once you have implemented re-scan to verify the issue has been resolved.
                        |3. $resolveIssueGetHelpText
                        |""".trimMargin()
                }
            } else {
                return """
                    |The review tooling found a code repository location in this dependency's metadata
                    |but could not find any source code revision to scan.
                    |
                    |Try to resolve this technical issue by following the advice below:
                    |
                    |1. You may be able to use a $ortCurationsVcsMdLink to resolve this issue.
                    |   - Use Git to clone the $ortConfigVcsMdLink.
                    |   - Open or create using a text editor `${getPackageCurationsFilePath(pkg.id)}` and add a $ortCurationsVcsRevisionMdLink
                    |     For the standard VCS revision curation comments to use, see $ortConfigContributingMdLink - an example is shown below.
                    |
                    |```yaml
                    |     - id: "${pkg.id.toCoordinates()}"
                    |       curations:
                    |         comment: |
                    |           Package code repository missing version tag for this release.
                    |           The exact commit for this version could not be determined.
                    |           Using the closest commit based on the package release date and/or code repository filepath history, see
                    |           [https://url-to-package-version-in-package-registry] and
                    |           [https://url-to-evidence-version-tag-or-revision-is-the-correct-one-for-this-package].
                    |         vcs:
                    |           revision: "[Exact code revision for this version of the package]"
                    |```
                    |
                    |   - Submit the above curation to the $ortConfigVcsMdLink with a commit message as shown below.
                    |
                    |```
                    |     feat(curations): Set VCS revision for `${pkg.id.toCoordinates()}`
                    |
                    |     $relatesToIssueText
                    |```
                    |
                    |   - Once your $ortCurationsVcsMdLink is merged, re-scan to verify if the issue has been resolved.
                    |
                    |6. $resolveIssueGetHelpText
                    |""".trimMargin()
            }
        }

        // How-to-fix instructions for when a package’s code revision cannot be found.
        if (issue.matchesMessage(".*IOException: Could not resolve provenance for package .* because the requested VCS path '.*' does not exist.*")) {
            return """
                | A $ortCurationsVcsRevisionMdLink was applied to this package, but the specified path does not exist in the code repository.
                |
                |Try to resolve this technical issue by following the advice below:
                |
                |1. Use Git to clone the $ortConfigVcsMdLink.
                |2. Open or create using a text editor `${getPackageCurationsFilePath(pkg.id)}`.
                |3. Update the `${getPackageCurationsFilePath(pkg.id)}` so each curation is atomic.
                |   Define version-range curation entries using $ivyVersionMatchersMdLink to assign correct repository URLs and code paths across different package versions.
                |   See also below example for reference.
                |
                |```yaml
                |     - id: "NuGet:Microsoft.Extensions:Caching.Abstractions"
                |       curations:
                |         comment: |
                |           Enforce scanning the code repository because the source artifact does not contain all corresponding source files.
                |         source_code_origins: [VCS]
                |
                |     - id: "NuGet:Microsoft.Extensions:Caching.Abstractions:[10.0.1,)"
                |       curations:
                |         comment: |
                |           Package code repository location is not declared in metadata or unavailable from used package registries.
                |         vcs:
                |           type: "Git"
                |           url: "https://github.com/dotnet/dotnet.git"
                |
                |     - id: "NuGet:Microsoft.Extensions:Caching.Abstractions:[10.0.1,)"
                |       curations:
                |         comment: |
                |           Package resides in its own directory within code repository.
                |         vcs:
                |           path: "src/runtime/src/libraries/Microsoft.Extensions.Caching.Abstractions/src"
                |
                |     - id: "NuGet:Microsoft.Extensions:Caching.Abstractions:[5.0.0,10.0.0["
                |       curations:
                |         comment: |
                |           Package code repository location is not declared in metadata or unavailable from used package registries.
                |         vcs:
                |           type: "Git"
                |           url: "https://github.com/dotnet/runtime.git"
                |
                |     - id: "NuGet:Microsoft.Extensions:Caching.Abstractions:[5.0.0,10.0.0["
                |       curations:
                |         comment: |
                |           Package resides in its own directory within code repository.
                |         vcs:
                |           path: "src/libraries/Microsoft.Extensions.Caching.Abstractions/src"
                |
                |     - id: "NuGet:Microsoft.Extensions:Caching.Abstractions:[2.1.23,5.0.0["
                |       curations:
                |         comment: |
                |           Package code repository location is not declared in metadata or unavailable from used package registries.
                |         vcs:
                |           type: "Git"
                |           url: "https://github.com/dotnet/extensions.git"
                |
                |     - id: "NuGet:Microsoft.Extensions:Caching.Abstractions:[2.1.23,5.0.0["
                |       curations:
                |         comment: |
                |           Package resides in its own directory within code repository.
                |         vcs:
                |           path: "src/Caching/Abstractions/src"
                |
                |     - id: "NuGet:Microsoft.Extensions:Caching.Abstractions:[1.0.0,2.1.23["
                |       curations:
                |         comment: |
                |           Package code repository location is not declared, or the metadata is unavailable in internal Artifactory.
                |         vcs:
                |           type: "Git"
                |           url: "https://github.com/aspnet/Caching.git"
                |
                |     - id: "NuGet:Microsoft.Extensions:Caching.Abstractions:[1.0.0,2.1.23["
                |       curations:
                |         comment: |
                |           Package resides in its own directory within code repository.
                |         vcs:
                |           path: "src/Microsoft.Extensions.Caching.Abstractions"
                |```
                |
                |   - Submit your curation to the $ortConfigVcsMdLink with a commit message as shown below
                |
                |```
                |     fix(curations): Fix up `${pkg.id.toCoordinatesWithoutVersion()}`
                |
                |     $relatesToIssueText
                |```
                |
                |   - Once your $ortCurationsVcsMdLink is merged, re-scan to verify if the issue has been resolved.
                |4. $resolveIssueGetHelpText
                |""".trimMargin()
        }

        // How-to-fix instructions for other code-repository or source-artifact issues not detected by earlier checks.
        if (issue.matchesMessage(".*IOException: Could not resolve provenance for package '.*' for source code origins.*")) {
            return """
                |This package's metadata contains no repository location or revision/version,
                |so no source code could be found to scan.
                |
                |Try to resolve this technical issue by following the advice below:
                |
                |1. Exclude the package when it’s not part of your project's released artifacts, add a $ortYmlFilePathExcludeMdLink or
                |   $ortYmlFileScopeExcludeMdLink in your $ortYmlFileMdLink to resolve this issue.
                |2. Can't exclude this package? Search for the source code repository for this package.
                |3. Able to find the code repository for ${pkg.id.toCoordinates()}?
                |   You may then be able to use a $ortCurationsVcsMdLink to resolve this issue.
                |   - Use Git to clone the $ortConfigVcsMdLink.
                |   - Open or create using a text editor `${getPackageCurationsFilePath(pkg.id)}` and add a $ortCurationsVcsUrlMdLink.
                |     For the standard VCS URL curation comments to use, see $ortConfigContributingMdLink - an example is shown below.
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinatesWithoutVersion()}"
                |       curations:
                |         comment: |
                |           Package code repository location is not declared in metadata or unavailable from used package registries.
                |           Found VCS URL via manual search, see
                |           [https://url-to-evidence-proving-vcs-type-is-the-correct-one-for-this-package].
                |         vcs:
                |           type: "[Repository type e.g., Git, Mercurial or Subversion]"
                |           url: "[Code repository url for this package]"
                |```
                |
                |   - Alternatively, if code repository is known but the code revision for this package version is not, then add a $ortCurationsVcsRevisionMdLink
                |     For the standard VCS revision curation comments to use, see $ortConfigContributingMdLink - an example is shown below.
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinates()}"
                |       curations:
                |         comment: |
                |           Package code repository missing version tag for this release.
                |           The exact commit for this version could not be determined.
                |           Using the closest commit based on the package release date and/or code repository filepath history, see
                |           [https://url-to-package-version-in-package-registry] and
                |           [https://url-to-evidence-version-tag-or-revision-is-the-correct-one-for-this-package].
                |         vcs:
                |           revision: "[Exact code revision for this version of the package]"
                |```
                |
                |   - Submit your curation to the $ortConfigVcsMdLink with a commit message as shown below
                |     depending on whether you created a VCS URL or VCS revision curation.
                |
                |```
                |     feat(curations): Set VCS [URL or revision] for `${pkg.id.toCoordinates()}`
                |
                |     $relatesToIssueText
                |```
                |
                |   - Once your $ortCurationsVcsMdLink is merged, re-scan to verify if the issue has been resolved.
                |
                |4. Unable to find the code repository but you did found the source artifact for ${pkg.id.toCoordinates()}?
                |   You may then be able to use a $ortCurationsSourceArtifactMdLink to resolve this issue.
                |   - Use Git to clone the $ortConfigVcsMdLink.
                |   - Open or create using a text editor `${getPackageCurationsFilePath(pkg.id)}`.
                |   - Use the following template to fix up the declared source artifact via a $ortCurationsSourceArtifactMdLink.
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinates()}"
                |       curations:
                |         comment: |
                |           Package source (archive) artifact is not declared in metadata or unavailable from used package registries.
                |           Found artifact via manual search, see
                |           [https://url-to-evidence-proving-artifact-url-is-the-correct-one-for-this-package].
                |         source_artifact:
                |           url: "[Source code artifact url for this package]"
                |```
                |
                |   - Submit the above change to the $ortConfigVcsMdLink with a commit message as shown below.
                |
                |```
                |     feat(curations): Set source artifact for `${pkg.id.toCoordinatesWithoutVersion()}`
                |
                |     $relatesToIssueText
                |```
                |
                |   - Once your $ortCurationsSourceArtifactMdLink is merged, re-scan to verify if the issue has been resolved.
                |
                |5. If this is a third-party proprietary closed-sourced or open source package with no available source code, then:
                |   - Use Git to clone the $ortConfigVcsMdLink.
                |   - Open or create using a text editor `${getPackageCurationsFilePath(pkg.id)}` and add a $ortCurationsSourceCodeOriginMdLink.
                |     For the standard source code origin curation comments to use, see $ortConfigContributingMdLink.
                |   - Is third-party proprietary closed-sourced software from your organization?
                |     You may be able to use one of the below examples to fix your issue.
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinatesWithoutVersion()}"
                |       curations:
                |         comment: |
                |           Package is proprietary closed-sourced software from our organization.
                |           See confirmation in [https://link-to-ospo-jira-ticket-with-confirmation-from-developer].
                |        source_code_origins: []
                | ```
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinatesWithoutVersion()}"
                |       curations:
                |         comment: |
                |           Third party proprietary closed-sourced package for which no sources are available.
                |           See confirmation in [https://link-to-ospo-jira-ticket-with-confirmation-from-developer].
                |       source_code_origins: []
                |```
                |
                |   - Alternatively if it's an open source package, you may be able to use one of the below examples to fix your issue.
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinates()}"
                |       curations:
                |       comment: |
                |         Open source package for which no sources are available.
                |       source_code_origins: []
                |```
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinates()}"
                |       curations:
                |         comment: |
                |           Open source package with available sources but release artifacts are not linked
                |           to specific code repository revisions or sources archive artifacts.
                |         source_code_origins: []
                |```
                |
                |   - Submit your curation to the $ortConfigVcsMdLink with a commit message as shown below.
                |
                |```
                |     feat(curations): Set source code origin for `${pkg.id.toCoordinates()}`
                |
                |     $relatesToIssueText
                |```
                |
                |   - Once your $ortCurationsSourceCodeOriginMdLink is merged, re-scan to verify if the issue has been resolved.
                |
                |6. $resolveIssueGetHelpText
                |""".trimMargin()
        }

        // How-to-fix instructions for multiple projects with same id.
        if (issue.matchesMessage(".*Multiple projects with the same id.*")) {
            return """
                |The review tooling requires each package in a repository to have a unique ID
                |so findings can be reported per package.
                |
                |Try to resolve this ORT issue by following the advice below:
                |
                |1. In the Web App scan report, check the _Defined_ in field under _Details_ for the ${pkgId.toCoordinates()}
                |   to find where the package is defined in the repository.
                |2. Update the package identifier so it is unique within the repository.
                |3. Re-scan to verify the issue is resolved.
                |4. $resolveIssueGetHelpText
                |""".trimMargin()
        }

        // How-to-fix instructions for missing package manager lockfile.
        if (issue.matchesMessage(".*No lockfile found in .*")) {
            return """
                |This issue likely occurs because a package manager lockfile is missing.
                |
                |Try to resolve this scanner issue by following the advice below:
                |
                |1. Open a terminal and go to the project or directory mentioned in the error message where no lockfile was found.
                |2. Generate the lockfile (search online for '${pkgId.type} generate lockfile' if you're unsure how).
                |3. Add the lockfile to the appropriate location, merge it into your repository, then re-run the scan.
                |4. Is your Conan lockfile not detected? Specify its name in your $ortYmlFileMdLink as follows:
                |
                |```yaml
                |     ---
                |     analyzer:
                |       package_managers:
                |         Conan:
                |           options:
                |             lockfileName: conan.lock
                |```
                |
                |5. $resolveIssueGetHelpText
                |""".trimMargin()
        }

        // How-to-fix instructions for scanner timeout errors.
        if (issue.matchesMessage(".*Timeout after .* while scanning file.*")) {
            val filePath = issue.message.substringAfter("'").substringBefore("'")
            val howToFixPrefix = """
                |The scanner timed out because scanning one or more source files exceeded the configured maximum scan time.
                |This limit prevents excessively long scans and helps control operating costs.
                |
                |Try to resolve this scanner issue by following the advice below:
                |
                |""".trimMargin()

            if (ortResult.isProject(pkgId)) {
                return """
                    |$howToFixPrefix
                    |
                    |1. Check your project's build artifacts to see if `${filePath}` is included.
                    |2. If it is not included, exclude its containing directory by adding a $ortYmlFilePathExcludeMdLink to your $ortYmlFileMdLink.
                    |   - Add an $ortYmlFilePathExcludeMdLink to your _.ort.yml_ file
                    |     as shown in the following example.
                    |
                    |```yaml
                    |     ---
                    |     excludes:
                    |       paths:
                    |       - pattern: "A glob pattern matching files or paths."
                    |         reason: "One of PathExcludeReason e.g. BUILD_TOOL_OF, DOCUMENTATION_OF or TEST_OF."
                    |```
                    |
                    |   - Submit the above change in your code repository with a commit message as shown below.
                    |
                    |```
                    |     chore(ort.yml): Resolve scanner timeout for `${pkgId.toCoordinates()}`
                    |
                    |     $relatesToIssueText
                    |```
                    |
                    |   - Once the _.ort.yml_ file update is merged in your code repository,
                    |     re-scan to verify if the issue has been marked as resolved.
                    |3. When the filepath cannot be excluded, determine whether it introduces any new licenses applicable to your project.
                    |4. Open `${filePath}` in a text editor.
                    |5. Check if `${filePath}` contains any licenses not yet listed in the _Licenses_ section (of the WebApp report).
                    |6. If this is not the case and `${filePath}` is part of your project's source code then:
                    |   - Add an $ortYmlFileIssueResolutionMdLink to your _.ort.yml_ file
                    |     as shown in the following example.
                    |
                    |```yaml
                    |     ---
                    |     resolutions:
                    |       issues:
                    |       - message: "${issue.message}"
                    |         reason: "SCANNER_ISSUE"
                    |         comment: |
                    |           This error can be ignored as the file does not introduce any new licenses
                    |           compared to what is already detected for this package.
                    |```
                    |
                    |   - Submit the above change in your code repository with a commit message as shown below.
                    |
                    |```
                    |     chore(ort.yml): Resolve scanner timeout for `${pkgId.toCoordinates()}`
                    |
                    |     $relatesToIssueText
                    |```
                    |
                    |   - Once the _.ort.yml_ file update is merged in your code repository,
                    |     re-scan to verify if the issue has been marked as resolved.
                    |7.  If the file that caused the timeout introduces a new license relevant to your project,
                    |    add an adjacent text file with the same name and a `.license` extension (for example, `foobar.c.license`)
                    |    containing the applicable license text or an SPDX identifier. See also the [REUSE specification](https://reuse.software).
                    |8. $resolveIssueGetHelpText
                    |""".trimMargin()
            }  else {
                return """
                    |$howToFixPrefix
                    |
                    |1. Clone $ortConfigVcsMdLink using Git.
                    |2. Download and extract the binary/release artifact of `${pkgId.toCoordinates()}`.
                    |3. Check the binary/release artifact to see if `${filePath}` is included.
                    |4. If `${filePath}` is part of (extracted) package sources but not binary/release artifact then:
                    |   - Create `${getVersionRangePackageConfigurationFilePath(pkg.id)}`, if not present.
                    |   - Open the file `configs.yml` in a text editor
                    |   - Add or update the $ortPackageConfigurationFileMdLink entry with a $ortYmlFilePathExcludeMdLink
                    |     for each _directory_ found in the (extracted) sources but not binary/release artifact of `${pkgId.toCoordinates()}`.
                    |     Use the following template, changing the text in square brackets (`[...]`) as appropriate.
                    |
                    |```yaml
                    |     ---
                    |     ${getPackageConfigurationMatcherText(pkg.id)}
                    |     path_excludes:
                    |     - pattern: "[A glob pattern matching files or paths.]"
                    |       reason: "[One of PathExcludeReason e.g. BUILD_TOOL_OF, DOCUMENTATION_OF, EXAMPLE_OF or TEST_OF.]"
                    |```
                    |
                    |   - For information on how to write a glob pattern, please see this $globTutorialMdLink.
                    |     The available options for the `reason` field are defined in $ortPathExcludeReasonMdLink.
                    |
                    |   - Submit the above change to the $ortConfigVcsMdLink with a commit message as shown below.
                    |     Reviewers are set automatically.
                    |
                    |```
                    |     feat(package-configurations): Add `${pkg.id.toCoordinatesWithoutVersion()}`
                    |
                    |     $relatesToIssueText
                    |```
                    |
                    |   - Alternatively, when updating an existing package configuration, use
                    |
                    |```
                    |     fix(package-configurations): Fix up `${pkg.id.toCoordinatesWithoutVersion()}`
                    |
                    |     $relatesToIssueText
                    |```
                    |
                    |   - Once your $ortPackageConfigurationFileMdLink is merged, re-scan to verify if the issue has been resolved.
                    |
                    |5. $resolveIssueGetHelpText
                    |""".trimMargin()
            }
        }

        // How-to-fix instructions for declared license that couldn't be mapped to SPDX license id.
        if (issue.matchesMessage(".*could not be mapped to a valid license or parsed as an SPDX expression.*")) {
            return """
                |The review tooling cannot map one of the declared licenses to an SPDX license identifier,
                |so the appropriate policy rules cannot be applied.
                |
                |Try to resolve this technical issue by following the advice below:
                |
                |1. If the package is not part of your project's released artifacts, then add a $ortYmlFilePathExcludeMdLink or
                |   $ortYmlFileScopeExcludeMdLink in your $ortYmlFileMdLink to resolve this issue.
                |2. Can't exclude this package? Search for the main license file in the code repository for the version of this package.
                |3. Found the main license file for the version of this package? You may then be able to use a $ortCurationsDeclaredLicenseMappingMdLink to resolve this issue.
                |   - Use Git to clone the $ortConfigVcsMdLink.
                |   - Open or create using a text editor `${getPackageCurationsFilePath(pkg.id)}` and add a $ortCurationsDeclaredLicenseMappingMdLink.
                |   - For the standard declared license mapping curation comments to use, see $ortConfigContributingMdLink - an example is shown below.
                |
                |```yaml
                |     - id: "${pkg.id.toCoordinatesWithoutVersion()}"
                |       curations:
                |         comment: |
                |           Map declared license based on
                |           [https://url-to-repository/tag-or-revision-for-version-${pkg.id.version}/LICENSE] and
                |           [https://url-to-repository/tag-or-revision-for-version-${pkg.id.version}/package-metadata-file].
                |         declared_license_mapping:
                |           "[License A]": "[SPDX license identifier for license A]"
                |```
                |
                |   - Submit your curation to the $ortConfigVcsMdLink with a commit message as shown below.
                |
                |```
                |     feat(curations): Add declared license mapping for `${pkg.id.toCoordinatesWithoutVersion()}`
                |
                |     $relatesToIssueText
                |```
                |
                |   - Once your $ortCurationsDeclaredLicenseMappingMdLink is merged, re-scan to verify if the issue has been resolved.
                |
                |4. $resolveIssueGetHelpText
            """.trimMargin()
        }

        // No how-to-fix instructions exist for user issue, likely tool issue.
        return """
            |There are no specific fix instructions for this issue; it likely originates from your build or package manager.
            |Search the exact error message online to diagnose and resolve it.
            |
            |$resolveIssueGetHelpText
            |""".trimMargin()
    }
}
