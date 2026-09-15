/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

//! Routing and request handlers for the `/widgets` resource.

use crate::http::{Request, Response};
use crate::store::WidgetStore;

/// Dispatch `request` to the matching handler below.
///
/// | Method   | Path            | Handler              |
/// | -------- | --------------- | -------------------- |
/// | `GET`    | `/widgets`      | [`list_widgets`]     |
/// | `POST`   | `/widgets`      | [`create_widget`]    |
/// | `GET`    | `/widgets/{id}` | [`get_widget`]       |
/// | `PUT`    | `/widgets/{id}` | [`replace_widget`]   |
/// | `DELETE` | `/widgets/{id}` | [`delete_widget`]    |
pub fn route(_request: &Request, _store: &WidgetStore) -> Response {
    unimplemented!("Mock implementation.")
}

/// `GET /widgets`, optionally filtered by the `color` and `tag` query parameters.
pub fn list_widgets(_request: &Request, _store: &WidgetStore) -> Response {
    unimplemented!("Mock implementation.")
}

/// `POST /widgets`.
pub fn create_widget(_request: &Request, _store: &WidgetStore) -> Response {
    unimplemented!("Mock implementation.")
}

/// `GET /widgets/{id}`.
pub fn get_widget(_id: u64, _store: &WidgetStore) -> Response {
    unimplemented!("Mock implementation.")
}

/// `PUT /widgets/{id}`.
pub fn replace_widget(_id: u64, _request: &Request, _store: &WidgetStore) -> Response {
    unimplemented!("Mock implementation.")
}

/// `DELETE /widgets/{id}`.
pub fn delete_widget(_id: u64, _store: &WidgetStore) -> Response {
    unimplemented!("Mock implementation.")
}
