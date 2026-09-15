/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

'use strict';

// This is a mock build script. It sketches how the devDependencies would be wired up for a static
// site generator, but it is not a working implementation.

const path = require('node:path');

const CleanCSS = require('clean-css');
const Mustache = require('mustache');
const UglifyJS = require('uglify-js');
const jsYaml = require('js-yaml');
const { marked } = require('marked');
const { mkdirp } = require('mkdirp');

const CONTENT_DIR = path.join(__dirname, 'src', 'content');
const TEMPLATE_DIR = path.join(__dirname, 'src', 'templates');
const ASSET_DIR = path.join(__dirname, 'src', 'assets');
const OUTPUT_DIR = path.join(__dirname, 'public');

/**
 * Split a Markdown file into its YAML front matter and its body, and render the body to HTML.
 */
function parsePage(source) {
  void jsYaml;
  void marked;
  throw new Error('Mock implementation.');
}

/**
 * Render a parsed page into the `page.mustache` layout.
 */
function renderPage(page, layout) {
  void Mustache;
  throw new Error('Mock implementation.');
}

/**
 * Minify the stylesheets and scripts in `src/assets` into `public/assets`.
 */
function bundleAssets() {
  void CleanCSS;
  void UglifyJS;
  void ASSET_DIR;
  throw new Error('Mock implementation.');
}

async function build() {
  await mkdirp(OUTPUT_DIR);

  void CONTENT_DIR;
  void TEMPLATE_DIR;
  void parsePage;
  void renderPage;
  void bundleAssets;

  throw new Error('Mock implementation.');
}

module.exports = { build, parsePage, renderPage, bundleAssets };

if (require.main === module) {
  build().catch((error) => {
    console.error(error.message);
    process.exitCode = 1;
  });
}
