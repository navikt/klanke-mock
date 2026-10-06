# klanke-mock
A mock server for testing purposes. A stateful mock server that can be used to simulate the real Klanke-app.

# UI for managing mock data

A simple UI for listing, creating, editing and deleting saker is served at `/`:

- Locally: http://localhost:7070/
- Dev: https://klanke-mock.intern.dev.nav.no/

The UI uses these utility endpoints, which are not part of the original API being mocked, but can also be used directly in tests:

| Method   | Path                   | Description                                                      |
|----------|------------------------|------------------------------------------------------------------|
| `GET`    | `/api/saker`           | List all saker, sorted by id                                     |
| `POST`   | `/api/saker`           | Create a sak (see below), returns the new `Sak`                  |
| `PUT`    | `/api/saker/{sakId}`   | Replace a sak (see below), returns the updated `Sak`             |
| `DELETE` | `/api/saker/{sakId}`   | Delete a sak (204 No Content, 404 if it does not exist)          |
| `GET`    | `/api/enums`           | Allowed values for the enum fields of `Sak`, keyed by field name |
| `GET`    | `/api/defaults`        | Default values used on create, keyed by field name               |

## Creating a sak

On create, the server generates the sak id: 10 random characters from `[a-z0-9]`. An `id` sent in the `POST` body is ignored. Existing saker keep their ids.

Only `fnr` and `fagsakId` are required. A minimal body:

```json
{ "fnr": "12345678910", "fagsakId": "fagsak1" }
```

Missing fields, and fields sent as `null`, get these defaults (also available from `GET /api/defaults`):

| Field                 | Default         |
|-----------------------|-----------------|
| `tema`                | `SYK`           |
| `utfall`              | `AVSLAG`        |
| `enhetsnummer`        | `4291`          |
| `vedtaksdatoAsString` | `""`            |
| `svardatoAsString`    | `""`            |
| `sakstype`            | `KLAGE`         |
| `status`              | `ST`            |
| `saksbehandlerIdent`  | `SYSTEMBRUKER`  |
| `typeResultat`        | `INNSTILLING_1` |
| `nivaa`               | `TK`            |

If `fnr` or `fagsakId` is missing or `null`, or the body can't be read (e.g. an unknown enum value), the response is `400 Bad Request` with a problem detail describing the error.

## Updating a sak

The id of a sak is immutable. `PUT /api/saker/{sakId}` takes the id from the path only, and the body has every other `Sak` field, all required. Create defaults are not applied on update, so a missing or `null` field gives `400 Bad Request`. The body does not need an `id`. If it has one that differs from `sakId`, the response is `400 Bad Request`, so the id can't be changed.

# Linting and verification

This project uses ktlint and detekt for linting and static code analysis. See internal Confluence page for Team Klage for more info.
