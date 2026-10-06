# klanke-mock
A mock server for testing purposes. A stateful mock server that can be used to simulate the real Klanke-app.

# Utility endpoints

These endpoints are not part of the original API being mocked, but can be used directly in tests:

| Method   | Path                   | Description                                                      |
|----------|------------------------|------------------------------------------------------------------|
| `GET`    | `/api/saker`           | List all saker, sorted by id                                     |
| `POST`   | `/api/saker`           | Create a sak, returns the saved `Sak`                            |
| `PUT`    | `/api/saker/{sakId}`   | Replace a sak (see below), returns the updated `Sak`             |
| `DELETE` | `/api/saker/{sakId}`   | Delete a sak (204 No Content, 404 if it does not exist)          |

## Updating a sak

The id of a sak is immutable. `PUT /api/saker/{sakId}` takes the id from the path only, and the body has every other `Sak` field, all required. A missing or `null` field gives `400 Bad Request`. The body does not need an `id`. If it has one that differs from `sakId`, the response is `400 Bad Request`, so the id can't be changed.

# Linting and verification

This project uses ktlint and detekt for linting and static code analysis. See internal Confluence page for Team Klage for more info.
