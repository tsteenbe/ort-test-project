/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

//! An in-memory store for [`Widget`]s.

use std::collections::BTreeMap;
use std::sync::{Arc, Mutex};

use crate::model::Widget;

/// A thread-safe, in-memory collection of widgets.
#[derive(Debug, Clone, Default)]
pub struct WidgetStore {
    widgets: Arc<Mutex<BTreeMap<u64, Widget>>>,
}

impl WidgetStore {
    /// Create an empty store.
    pub fn new() -> Self {
        Self::default()
    }

    /// Return all widgets, ordered by their id.
    pub fn list(&self) -> Vec<Widget> {
        unimplemented!("Mock implementation.")
    }

    /// Return the widget with the given `id`, if any.
    pub fn get(&self, _id: u64) -> Option<Widget> {
        unimplemented!("Mock implementation.")
    }

    /// Insert `widget` under a freshly assigned id and return the stored widget.
    pub fn insert(&self, _widget: Widget) -> Widget {
        unimplemented!("Mock implementation.")
    }

    /// Replace the widget with the given `id` and return whether it existed before.
    pub fn replace(&self, _id: u64, _widget: Widget) -> bool {
        unimplemented!("Mock implementation.")
    }

    /// Remove the widget with the given `id` and return whether it existed.
    pub fn remove(&self, _id: u64) -> bool {
        let _ = &self.widgets;
        unimplemented!("Mock implementation.")
    }
}
