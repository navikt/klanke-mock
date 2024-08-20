DO
$$
    BEGIN
        IF EXISTS
                (SELECT 1 FROM pg_roles WHERE rolname = 'cloudsqliamuser')
        THEN
            GRANT USAGE ON SCHEMA public TO cloudsqliamuser;
            GRANT USAGE ON SCHEMA klage TO cloudsqliamuser;
            GRANT SELECT ON ALL TABLES IN SCHEMA public TO cloudsqliamuser;
            GRANT SELECT ON ALL TABLES IN SCHEMA klage TO cloudsqliamuser;
            ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT SELECT ON TABLES TO cloudsqliamuser;
            ALTER DEFAULT PRIVILEGES IN SCHEMA klage GRANT SELECT ON TABLES TO cloudsqliamuser;
        END IF;
    END
$$;

CREATE TABLE klage.sak
(
    id                    TEXT PRIMARY KEY,
    fagsak_id             TEXT,
    tema                  TEXT,
    utfall                TEXT,
    enhetsnummer          TEXT,
    vedtaksdato_as_string TEXT,
    svardato_as_string    TEXT,
    fnr                   TEXT,
    sakstype              TEXT,
    status                TEXT,
    saksbehandler_ident   TEXT
);
