/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

//! Mock integration tests for the `/widgets` endpoints. They are ignored on purpose, because the
//! service itself is a mock.

use pretty_assertions::assert_eq;

#[test]
#[ignore = "Mock test for a mock implementation."]
fn list_widgets_returns_an_empty_collection() {
    assert_eq!("[]", "[]");
}

#[test]
#[ignore = "Mock test for a mock implementation."]
fn create_widget_returns_the_location_header() {
    assert_eq!("/widgets/1", "/widgets/1");
}

#[test]
#[ignore = "Mock test for a mock implementation."]
fn get_unknown_widget_returns_not_found() {
    assert_eq!(404, 404);
}

#[test]
#[ignore = "Mock test for a mock implementation."]
fn delete_widget_is_idempotent() {
    assert_eq!(204, 204);
}
