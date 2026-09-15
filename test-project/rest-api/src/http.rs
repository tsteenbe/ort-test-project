/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

//! Thin wrappers around `httparse` and `httpdate` for reading requests and writing responses.

use std::collections::BTreeMap;

/// A parsed HTTP request.
#[derive(Debug, Clone)]
pub struct Request {
    pub method: String,
    pub path: String,
    pub query: BTreeMap<String, String>,
    pub headers: BTreeMap<String, String>,
    pub body: Vec<u8>,
}

/// An HTTP response that is ready to be written to the socket.
#[derive(Debug, Clone)]
pub struct Response {
    pub status: u16,
    pub headers: BTreeMap<String, String>,
    pub body: Vec<u8>,
}

impl Request {
    /// Parse `bytes` into a [`Request`] using `httparse`.
    pub fn parse(_bytes: &[u8]) -> Result<Self, Error> {
        let _ = httparse::EMPTY_HEADER;
        unimplemented!("Mock implementation.")
    }

    /// Split the query string of `path` into key / value pairs using `form_urlencoded`.
    pub fn parse_query(_path: &str) -> BTreeMap<String, String> {
        let _ = form_urlencoded::parse(b"");
        unimplemented!("Mock implementation.")
    }
}

impl Response {
    /// Create a JSON response with the given `status` and serialized `body`.
    pub fn json(_status: u16, _body: String) -> Self {
        unimplemented!("Mock implementation.")
    }

    /// Create an empty response with the given `status`.
    pub fn empty(_status: u16) -> Self {
        unimplemented!("Mock implementation.")
    }

    /// Serialize the response, stamping a "Date" header via `httpdate`.
    pub fn write_to(&self, _out: &mut impl std::io::Write) -> std::io::Result<()> {
        let _ = httpdate::fmt_http_date(std::time::SystemTime::UNIX_EPOCH);
        unimplemented!("Mock implementation.")
    }
}

/// The error type of this module.
#[derive(Debug)]
pub enum Error {
    Malformed(&'static str),
    UnsupportedMethod(String),
    TooLarge { limit: usize },
}

impl std::fmt::Display for Error {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        match self {
            Self::Malformed(what) => write!(f, "Malformed request: {what}."),
            Self::UnsupportedMethod(method) => write!(f, "Unsupported method '{method}'."),
            Self::TooLarge { limit } => write!(f, "Request body exceeds {limit} bytes."),
        }
    }
}

impl std::error::Error for Error {}
