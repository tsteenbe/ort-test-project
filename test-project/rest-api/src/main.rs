/*
 * Copyright (c) 2026 The Example.org Copyright Holders.
 *
 * SPDX-License-Identifier: BSD-2-Clause
 */

//! Entry point of the mock `rest-api` service.
//!
//! This is not a working implementation. It only sketches the shape of a small REST API service so
//! that the ORT analyzer has some source code next to `Cargo.toml` to look at.

mod handler;
mod http;
mod model;
mod store;

use std::net::SocketAddr;

use crate::store::WidgetStore;

const DEFAULT_ADDR: &str = "127.0.0.1:8080";

fn main() {
    let addr: SocketAddr = std::env::var("REST_API_ADDR")
        .unwrap_or_else(|_| DEFAULT_ADDR.to_string())
        .parse()
        .expect("REST_API_ADDR must be a socket address");

    log::info!("Starting the mock REST API service on {addr}.");

    let store = WidgetStore::new();
    serve(addr, store);
}

/// Accept connections on `addr` and dispatch each request to [`handler`].
fn serve(_addr: SocketAddr, _store: WidgetStore) -> ! {
    unimplemented!("Mock implementation.")
}
