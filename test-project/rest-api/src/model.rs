/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

//! The resources exposed by the mock REST API.

/// A widget, the only resource this service knows about.
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Widget {
    pub id: u64,
    pub name: String,
    pub color: Color,
    pub tags: Vec<String>,
}

/// The color of a [`Widget`].
#[derive(Debug, Clone, Copy, PartialEq, Eq)]
pub enum Color {
    Red,
    Green,
    Blue,
}

impl Widget {
    /// Serialize this widget to a JSON string using `tinyjson`.
    pub fn to_json(&self) -> String {
        let _ = tinyjson::JsonValue::Null;
        unimplemented!("Mock implementation.")
    }

    /// Parse a widget from the JSON `body` of a request using `tinyjson`.
    pub fn from_json(_body: &[u8]) -> Result<Self, ValidationError> {
        unimplemented!("Mock implementation.")
    }

    /// Check that all required fields are set and within their allowed ranges.
    pub fn validate(&self) -> Result<(), ValidationError> {
        unimplemented!("Mock implementation.")
    }
}

/// Raised when a request body does not describe a valid [`Widget`].
#[derive(Debug, PartialEq, Eq)]
pub struct ValidationError {
    pub field: &'static str,
    pub message: String,
}

impl std::fmt::Display for ValidationError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "Invalid field '{}': {}", self.field, self.message)
    }
}

impl std::error::Error for ValidationError {}
