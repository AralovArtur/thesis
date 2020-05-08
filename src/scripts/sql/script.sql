CREATE TABLE public.Signer
(
    id serial,
    serial_number character varying NOT NULL,
    given_name character varying NOT NULL,
    surname character varying NOT NULL,
    eID_tool character varying NOT NULL,
    CONSTRAINT signer_pkey PRIMARY KEY (id)
)

CREATE TABLE public.Certificate
(
    id serial,
    signer_id integer NOT NULL,
    public_key character varying NOT NULL,
    valid_not_before character varying NOT NULL,
    valid_not_after character varying NOT NULL,
    certificate_issuer character varying NOT NULL,
    CONSTRAINT certificate_pkey PRIMARY KEY (id),
    CONSTRAINT certificate_signer_id_fkey FOREIGN KEY (signer_id)
      REFERENCES signer (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)

CREATE TABLE public.OCSP
(
    ocsp_id serial,
    signer_id integer NOT NULL,
    response_time character varying NOT NULL,
    response_issuer character varying NOT NULL,
    certificate_issuer character varying NOT NULL,
    CONSTRAINT ocsp_pkey PRIMARY KEY (ocsp_id),
    CONSTRAINT ocsp_signer_id_fkey FOREIGN KEY (signer_id)
      REFERENCES signer (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)

CREATE TABLE public.Timestamp
(
    timestamp_id serial,
    signer_id integer NOT NULL,
    creation_time character varying NOT NULL,
    timestamp_issuer character varying NOT NULL,
    certificate_issuer character varying NOT NULL,
    CONSTRAINT timestamp_pkey PRIMARY KEY (timestamp_id),
    CONSTRAINT timestamp_signer_id_fkey FOREIGN KEY (signer_id)
      REFERENCES signer (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)