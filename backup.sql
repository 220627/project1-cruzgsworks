--
-- PostgreSQL database dump
--

-- Dumped from database version 14.4
-- Dumped by pg_dump version 14.4

-- Started on 2022-07-25 16:00:06

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 8 (class 2615 OID 26713)
-- Name: ers; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA ers;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 232 (class 1259 OID 26755)
-- Name: ers_reimbursement; Type: TABLE; Schema: ers; Owner: -
--

CREATE TABLE ers.ers_reimbursement (
    reimb_id integer NOT NULL,
    reimb_amount numeric(10,2) NOT NULL,
    reimb_submitted timestamp without time zone NOT NULL,
    reimb_resolved timestamp without time zone,
    reimb_description character varying(250),
    reimb_receipt bytea,
    reimb_author integer NOT NULL,
    reimb_resolver integer,
    reimb_status_id integer NOT NULL,
    reimb_type_id integer NOT NULL
);


--
-- TOC entry 231 (class 1259 OID 26754)
-- Name: ers_reimbursement_reimb_id_seq; Type: SEQUENCE; Schema: ers; Owner: -
--

CREATE SEQUENCE ers.ers_reimbursement_reimb_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3386 (class 0 OID 0)
-- Dependencies: 231
-- Name: ers_reimbursement_reimb_id_seq; Type: SEQUENCE OWNED BY; Schema: ers; Owner: -
--

ALTER SEQUENCE ers.ers_reimbursement_reimb_id_seq OWNED BY ers.ers_reimbursement.reimb_id;


--
-- TOC entry 230 (class 1259 OID 26748)
-- Name: ers_reimbursement_status; Type: TABLE; Schema: ers; Owner: -
--

CREATE TABLE ers.ers_reimbursement_status (
    reimb_status_id integer NOT NULL,
    reimb_status character varying(10) NOT NULL
);


--
-- TOC entry 229 (class 1259 OID 26747)
-- Name: ers_reimbursement_status_reimb_status_id_seq; Type: SEQUENCE; Schema: ers; Owner: -
--

CREATE SEQUENCE ers.ers_reimbursement_status_reimb_status_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3387 (class 0 OID 0)
-- Dependencies: 229
-- Name: ers_reimbursement_status_reimb_status_id_seq; Type: SEQUENCE OWNED BY; Schema: ers; Owner: -
--

ALTER SEQUENCE ers.ers_reimbursement_status_reimb_status_id_seq OWNED BY ers.ers_reimbursement_status.reimb_status_id;


--
-- TOC entry 228 (class 1259 OID 26741)
-- Name: ers_reimbursement_type; Type: TABLE; Schema: ers; Owner: -
--

CREATE TABLE ers.ers_reimbursement_type (
    reimb_type_id integer NOT NULL,
    reimb_type character varying(10) NOT NULL
);


--
-- TOC entry 227 (class 1259 OID 26740)
-- Name: ers_reimbursement_type_reimb_type_id_seq; Type: SEQUENCE; Schema: ers; Owner: -
--

CREATE SEQUENCE ers.ers_reimbursement_type_reimb_type_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3388 (class 0 OID 0)
-- Dependencies: 227
-- Name: ers_reimbursement_type_reimb_type_id_seq; Type: SEQUENCE OWNED BY; Schema: ers; Owner: -
--

ALTER SEQUENCE ers.ers_reimbursement_type_reimb_type_id_seq OWNED BY ers.ers_reimbursement_type.reimb_type_id;


--
-- TOC entry 234 (class 1259 OID 36836)
-- Name: ers_user_roles; Type: TABLE; Schema: ers; Owner: -
--

CREATE TABLE ers.ers_user_roles (
    ers_user_role_id integer NOT NULL,
    user_role character varying(10) NOT NULL
);


--
-- TOC entry 233 (class 1259 OID 36835)
-- Name: ers_user_roles_ers_user_role_id_seq; Type: SEQUENCE; Schema: ers; Owner: -
--

CREATE SEQUENCE ers.ers_user_roles_ers_user_role_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3389 (class 0 OID 0)
-- Dependencies: 233
-- Name: ers_user_roles_ers_user_role_id_seq; Type: SEQUENCE OWNED BY; Schema: ers; Owner: -
--

ALTER SEQUENCE ers.ers_user_roles_ers_user_role_id_seq OWNED BY ers.ers_user_roles.ers_user_role_id;


--
-- TOC entry 226 (class 1259 OID 26715)
-- Name: ers_users; Type: TABLE; Schema: ers; Owner: -
--

CREATE TABLE ers.ers_users (
    ers_users_id integer NOT NULL,
    ers_username character varying(50) NOT NULL,
    ers_password character varying(50) NOT NULL,
    user_first_name character varying(100) NOT NULL,
    user_last_name character varying(100) NOT NULL,
    user_email character varying(150) NOT NULL,
    user_role_id integer NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 26714)
-- Name: ers_users_ers_users_id_seq; Type: SEQUENCE; Schema: ers; Owner: -
--

CREATE SEQUENCE ers.ers_users_ers_users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 3390 (class 0 OID 0)
-- Dependencies: 225
-- Name: ers_users_ers_users_id_seq; Type: SEQUENCE OWNED BY; Schema: ers; Owner: -
--

ALTER SEQUENCE ers.ers_users_ers_users_id_seq OWNED BY ers.ers_users.ers_users_id;


--
-- TOC entry 3213 (class 2604 OID 26758)
-- Name: ers_reimbursement reimb_id; Type: DEFAULT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement ALTER COLUMN reimb_id SET DEFAULT nextval('ers.ers_reimbursement_reimb_id_seq'::regclass);


--
-- TOC entry 3212 (class 2604 OID 26751)
-- Name: ers_reimbursement_status reimb_status_id; Type: DEFAULT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement_status ALTER COLUMN reimb_status_id SET DEFAULT nextval('ers.ers_reimbursement_status_reimb_status_id_seq'::regclass);


--
-- TOC entry 3211 (class 2604 OID 26744)
-- Name: ers_reimbursement_type reimb_type_id; Type: DEFAULT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement_type ALTER COLUMN reimb_type_id SET DEFAULT nextval('ers.ers_reimbursement_type_reimb_type_id_seq'::regclass);


--
-- TOC entry 3214 (class 2604 OID 36839)
-- Name: ers_user_roles ers_user_role_id; Type: DEFAULT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_user_roles ALTER COLUMN ers_user_role_id SET DEFAULT nextval('ers.ers_user_roles_ers_user_role_id_seq'::regclass);


--
-- TOC entry 3210 (class 2604 OID 26718)
-- Name: ers_users ers_users_id; Type: DEFAULT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_users ALTER COLUMN ers_users_id SET DEFAULT nextval('ers.ers_users_ers_users_id_seq'::regclass);


--
-- TOC entry 3378 (class 0 OID 26755)
-- Dependencies: 232
-- Data for Name: ers_reimbursement; Type: TABLE DATA; Schema: ers; Owner: -
--



--
-- TOC entry 3376 (class 0 OID 26748)
-- Dependencies: 230
-- Data for Name: ers_reimbursement_status; Type: TABLE DATA; Schema: ers; Owner: -
--



--
-- TOC entry 3374 (class 0 OID 26741)
-- Dependencies: 228
-- Data for Name: ers_reimbursement_type; Type: TABLE DATA; Schema: ers; Owner: -
--



--
-- TOC entry 3380 (class 0 OID 36836)
-- Dependencies: 234
-- Data for Name: ers_user_roles; Type: TABLE DATA; Schema: ers; Owner: -
--



--
-- TOC entry 3372 (class 0 OID 26715)
-- Dependencies: 226
-- Data for Name: ers_users; Type: TABLE DATA; Schema: ers; Owner: -
--



--
-- TOC entry 3391 (class 0 OID 0)
-- Dependencies: 231
-- Name: ers_reimbursement_reimb_id_seq; Type: SEQUENCE SET; Schema: ers; Owner: -
--

SELECT pg_catalog.setval('ers.ers_reimbursement_reimb_id_seq', 1, false);


--
-- TOC entry 3392 (class 0 OID 0)
-- Dependencies: 229
-- Name: ers_reimbursement_status_reimb_status_id_seq; Type: SEQUENCE SET; Schema: ers; Owner: -
--

SELECT pg_catalog.setval('ers.ers_reimbursement_status_reimb_status_id_seq', 1, false);


--
-- TOC entry 3393 (class 0 OID 0)
-- Dependencies: 227
-- Name: ers_reimbursement_type_reimb_type_id_seq; Type: SEQUENCE SET; Schema: ers; Owner: -
--

SELECT pg_catalog.setval('ers.ers_reimbursement_type_reimb_type_id_seq', 1, false);


--
-- TOC entry 3394 (class 0 OID 0)
-- Dependencies: 233
-- Name: ers_user_roles_ers_user_role_id_seq; Type: SEQUENCE SET; Schema: ers; Owner: -
--

SELECT pg_catalog.setval('ers.ers_user_roles_ers_user_role_id_seq', 1, false);


--
-- TOC entry 3395 (class 0 OID 0)
-- Dependencies: 225
-- Name: ers_users_ers_users_id_seq; Type: SEQUENCE SET; Schema: ers; Owner: -
--

SELECT pg_catalog.setval('ers.ers_users_ers_users_id_seq', 1, false);


--
-- TOC entry 3224 (class 2606 OID 26762)
-- Name: ers_reimbursement ers_reimbursement_pk; Type: CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement
    ADD CONSTRAINT ers_reimbursement_pk PRIMARY KEY (reimb_id);


--
-- TOC entry 3222 (class 2606 OID 26753)
-- Name: ers_reimbursement_status ers_reimbursement_status_pk; Type: CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement_status
    ADD CONSTRAINT ers_reimbursement_status_pk PRIMARY KEY (reimb_status_id);


--
-- TOC entry 3220 (class 2606 OID 26746)
-- Name: ers_reimbursement_type ers_reimbursement_type_pk; Type: CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement_type
    ADD CONSTRAINT ers_reimbursement_type_pk PRIMARY KEY (reimb_type_id);


--
-- TOC entry 3226 (class 2606 OID 36841)
-- Name: ers_user_roles ers_user_roles_pk; Type: CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_user_roles
    ADD CONSTRAINT ers_user_roles_pk PRIMARY KEY (ers_user_role_id);


--
-- TOC entry 3216 (class 2606 OID 26720)
-- Name: ers_users ers_users_pk; Type: CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_users
    ADD CONSTRAINT ers_users_pk PRIMARY KEY (ers_users_id);


--
-- TOC entry 3218 (class 2606 OID 26722)
-- Name: ers_users ers_users_un; Type: CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_users
    ADD CONSTRAINT ers_users_un UNIQUE (ers_username, user_email);


--
-- TOC entry 3230 (class 2606 OID 26818)
-- Name: ers_reimbursement ers_reimbursement_auth_fk; Type: FK CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement
    ADD CONSTRAINT ers_reimbursement_auth_fk FOREIGN KEY (reimb_author) REFERENCES ers.ers_users(ers_users_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3396 (class 0 OID 0)
-- Dependencies: 3230
-- Name: CONSTRAINT ers_reimbursement_auth_fk ON ers_reimbursement; Type: COMMENT; Schema: ers; Owner: -
--

COMMENT ON CONSTRAINT ers_reimbursement_auth_fk ON ers.ers_reimbursement IS 'Has Requested';


--
-- TOC entry 3231 (class 2606 OID 26823)
-- Name: ers_reimbursement ers_reimbursement_reslvr_fk; Type: FK CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement
    ADD CONSTRAINT ers_reimbursement_reslvr_fk FOREIGN KEY (reimb_resolver) REFERENCES ers.ers_users(ers_users_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3397 (class 0 OID 0)
-- Dependencies: 3231
-- Name: CONSTRAINT ers_reimbursement_reslvr_fk ON ers_reimbursement; Type: COMMENT; Schema: ers; Owner: -
--

COMMENT ON CONSTRAINT ers_reimbursement_reslvr_fk ON ers.ers_reimbursement IS 'Has Resolved';


--
-- TOC entry 3228 (class 2606 OID 26783)
-- Name: ers_reimbursement ers_reimbursement_status_fk; Type: FK CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement
    ADD CONSTRAINT ers_reimbursement_status_fk FOREIGN KEY (reimb_status_id) REFERENCES ers.ers_reimbursement_status(reimb_status_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3398 (class 0 OID 0)
-- Dependencies: 3228
-- Name: CONSTRAINT ers_reimbursement_status_fk ON ers_reimbursement; Type: COMMENT; Schema: ers; Owner: -
--

COMMENT ON CONSTRAINT ers_reimbursement_status_fk ON ers.ers_reimbursement IS 'Has Status';


--
-- TOC entry 3229 (class 2606 OID 26788)
-- Name: ers_reimbursement ers_reimbursement_type_fk; Type: FK CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_reimbursement
    ADD CONSTRAINT ers_reimbursement_type_fk FOREIGN KEY (reimb_type_id) REFERENCES ers.ers_reimbursement_type(reimb_type_id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- TOC entry 3399 (class 0 OID 0)
-- Dependencies: 3229
-- Name: CONSTRAINT ers_reimbursement_type_fk ON ers_reimbursement; Type: COMMENT; Schema: ers; Owner: -
--

COMMENT ON CONSTRAINT ers_reimbursement_type_fk ON ers.ers_reimbursement IS 'Has Type';


--
-- TOC entry 3227 (class 2606 OID 36847)
-- Name: ers_users ers_users_fk; Type: FK CONSTRAINT; Schema: ers; Owner: -
--

ALTER TABLE ONLY ers.ers_users
    ADD CONSTRAINT ers_users_fk FOREIGN KEY (user_role_id) REFERENCES ers.ers_user_roles(ers_user_role_id);


--
-- TOC entry 3400 (class 0 OID 0)
-- Dependencies: 3227
-- Name: CONSTRAINT ers_users_fk ON ers_users; Type: COMMENT; Schema: ers; Owner: -
--

COMMENT ON CONSTRAINT ers_users_fk ON ers.ers_users IS 'Has Role';


-- Completed on 2022-07-25 16:00:06

--
-- PostgreSQL database dump complete
--

