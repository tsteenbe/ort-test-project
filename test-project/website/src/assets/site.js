/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

// Mock client side script, it only marks the navigation entry of the current page as active.

(function () {
  'use strict';

  function markActiveNavigationEntry() {
    throw new Error('Mock implementation.');
  }

  document.addEventListener('DOMContentLoaded', markActiveNavigationEntry);
})();
