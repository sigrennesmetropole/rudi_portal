--
-- PostgreSQL database dump
--

-- Dumped from database version 10.13 (Debian 10.13-1.pgdg90+1)
-- Dumped by pg_dump version 10.16

-- Started on 2021-07-12 16:55:55

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

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 196 (class 1259 OID 16385)
-- Name: EJB__TIMER__TBL; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public."EJB__TIMER__TBL" (
    "TIMERID" character varying(255) NOT NULL,
    "APPLICATIONID" bigint,
    "BLOB" bytea,
    "CONTAINERID" bigint,
    "CREATIONTIMERAW" bigint,
    "INITIALEXPIRATIONRAW" bigint,
    "INTERVALDURATION" bigint,
    "LASTEXPIRATIONRAW" bigint,
    "OWNERID" character varying(255),
    "PKHASHCODE" integer,
    "SCHEDULE" character varying(255),
    "STATE" integer
);


ALTER TABLE public."EJB__TIMER__TBL" OWNER TO dataverse;

--
-- TOC entry 197 (class 1259 OID 16391)
-- Name: actionlogrecord; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.actionlogrecord (
    id character varying(36) NOT NULL,
    actionresult character varying(255),
    actionsubtype character varying(255),
    actiontype character varying(255),
    endtime timestamp without time zone,
    info text,
    starttime timestamp without time zone,
    useridentifier character varying(255)
);


ALTER TABLE public.actionlogrecord OWNER TO dataverse;

--
-- TOC entry 198 (class 1259 OID 16397)
-- Name: alternativepersistentidentifier; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.alternativepersistentidentifier (
    id integer NOT NULL,
    authority character varying(255),
    globalidcreatetime timestamp without time zone,
    identifier character varying(255),
    identifierregistered boolean,
    protocol character varying(255),
    storagelocationdesignator boolean,
    dvobject_id bigint NOT NULL
);


ALTER TABLE public.alternativepersistentidentifier OWNER TO dataverse;

--
-- TOC entry 199 (class 1259 OID 16403)
-- Name: alternativepersistentidentifier_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.alternativepersistentidentifier_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.alternativepersistentidentifier_id_seq OWNER TO dataverse;

--
-- TOC entry 4267 (class 0 OID 0)
-- Dependencies: 199
-- Name: alternativepersistentidentifier_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.alternativepersistentidentifier_id_seq OWNED BY public.alternativepersistentidentifier.id;


--
-- TOC entry 200 (class 1259 OID 16405)
-- Name: apitoken; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.apitoken (
    id integer NOT NULL,
    createtime timestamp without time zone NOT NULL,
    disabled boolean NOT NULL,
    expiretime timestamp without time zone NOT NULL,
    tokenstring character varying(255) NOT NULL,
    authenticateduser_id bigint NOT NULL
);


ALTER TABLE public.apitoken OWNER TO dataverse;

--
-- TOC entry 201 (class 1259 OID 16408)
-- Name: apitoken_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.apitoken_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.apitoken_id_seq OWNER TO dataverse;

--
-- TOC entry 4268 (class 0 OID 0)
-- Dependencies: 201
-- Name: apitoken_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.apitoken_id_seq OWNED BY public.apitoken.id;


--
-- TOC entry 202 (class 1259 OID 16410)
-- Name: authenticateduser; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.authenticateduser (
    id integer NOT NULL,
    affiliation character varying(255),
    createdtime timestamp without time zone NOT NULL,
    email character varying(255) NOT NULL,
    emailconfirmed timestamp without time zone,
    firstname character varying(255),
    lastapiusetime timestamp without time zone,
    lastlogintime timestamp without time zone,
    lastname character varying(255),
    "position" character varying(255),
    superuser boolean,
    useridentifier character varying(255) NOT NULL,
    deactivated boolean NOT NULL,
    deactivatedtime timestamp without time zone
);


ALTER TABLE public.authenticateduser OWNER TO dataverse;

--
-- TOC entry 203 (class 1259 OID 16416)
-- Name: authenticateduser_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.authenticateduser_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authenticateduser_id_seq OWNER TO dataverse;

--
-- TOC entry 4269 (class 0 OID 0)
-- Dependencies: 203
-- Name: authenticateduser_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.authenticateduser_id_seq OWNED BY public.authenticateduser.id;


--
-- TOC entry 204 (class 1259 OID 16418)
-- Name: authenticateduserlookup; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.authenticateduserlookup (
    id integer NOT NULL,
    authenticationproviderid character varying(255),
    persistentuserid character varying(255),
    authenticateduser_id bigint NOT NULL
);


ALTER TABLE public.authenticateduserlookup OWNER TO dataverse;

--
-- TOC entry 205 (class 1259 OID 16424)
-- Name: authenticateduserlookup_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.authenticateduserlookup_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.authenticateduserlookup_id_seq OWNER TO dataverse;

--
-- TOC entry 4270 (class 0 OID 0)
-- Dependencies: 205
-- Name: authenticateduserlookup_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.authenticateduserlookup_id_seq OWNED BY public.authenticateduserlookup.id;


--
-- TOC entry 206 (class 1259 OID 16426)
-- Name: authenticationproviderrow; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.authenticationproviderrow (
    id character varying(255) NOT NULL,
    enabled boolean,
    factoryalias character varying(255),
    factorydata text,
    subtitle character varying(255),
    title character varying(255)
);


ALTER TABLE public.authenticationproviderrow OWNER TO dataverse;

--
-- TOC entry 207 (class 1259 OID 16432)
-- Name: auxiliaryfile; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.auxiliaryfile (
    id integer NOT NULL,
    checksum character varying(255),
    contenttype character varying(255),
    filesize bigint,
    formattag character varying(255),
    formatversion character varying(255),
    ispublic boolean,
    origin character varying(255),
    type character varying(255),
    datafile_id bigint NOT NULL
);


ALTER TABLE public.auxiliaryfile OWNER TO dataverse;

--
-- TOC entry 208 (class 1259 OID 16438)
-- Name: auxiliaryfile_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.auxiliaryfile_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.auxiliaryfile_id_seq OWNER TO dataverse;

--
-- TOC entry 4271 (class 0 OID 0)
-- Dependencies: 208
-- Name: auxiliaryfile_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.auxiliaryfile_id_seq OWNED BY public.auxiliaryfile.id;


--
-- TOC entry 209 (class 1259 OID 16440)
-- Name: bannermessage; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.bannermessage (
    id integer NOT NULL,
    active boolean,
    dismissiblebyuser boolean
);


ALTER TABLE public.bannermessage OWNER TO dataverse;

--
-- TOC entry 210 (class 1259 OID 16443)
-- Name: bannermessage_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.bannermessage_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.bannermessage_id_seq OWNER TO dataverse;

--
-- TOC entry 4272 (class 0 OID 0)
-- Dependencies: 210
-- Name: bannermessage_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.bannermessage_id_seq OWNED BY public.bannermessage.id;


--
-- TOC entry 211 (class 1259 OID 16445)
-- Name: bannermessagetext; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.bannermessagetext (
    id integer NOT NULL,
    lang text,
    message text,
    bannermessage_id bigint NOT NULL
);


ALTER TABLE public.bannermessagetext OWNER TO dataverse;

--
-- TOC entry 212 (class 1259 OID 16451)
-- Name: bannermessagetext_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.bannermessagetext_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.bannermessagetext_id_seq OWNER TO dataverse;

--
-- TOC entry 4273 (class 0 OID 0)
-- Dependencies: 212
-- Name: bannermessagetext_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.bannermessagetext_id_seq OWNED BY public.bannermessagetext.id;


--
-- TOC entry 213 (class 1259 OID 16453)
-- Name: builtinuser; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.builtinuser (
    id integer NOT NULL,
    encryptedpassword character varying(255),
    passwordencryptionversion integer,
    username character varying(255) NOT NULL
);


ALTER TABLE public.builtinuser OWNER TO dataverse;

--
-- TOC entry 214 (class 1259 OID 16459)
-- Name: builtinuser_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.builtinuser_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.builtinuser_id_seq OWNER TO dataverse;

--
-- TOC entry 4274 (class 0 OID 0)
-- Dependencies: 214
-- Name: builtinuser_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.builtinuser_id_seq OWNED BY public.builtinuser.id;


--
-- TOC entry 215 (class 1259 OID 16461)
-- Name: categorymetadata; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.categorymetadata (
    id integer NOT NULL,
    wfreq double precision,
    category_id bigint NOT NULL,
    variablemetadata_id bigint NOT NULL
);


ALTER TABLE public.categorymetadata OWNER TO dataverse;

--
-- TOC entry 216 (class 1259 OID 16464)
-- Name: categorymetadata_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.categorymetadata_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.categorymetadata_id_seq OWNER TO dataverse;

--
-- TOC entry 4275 (class 0 OID 0)
-- Dependencies: 216
-- Name: categorymetadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.categorymetadata_id_seq OWNED BY public.categorymetadata.id;


--
-- TOC entry 217 (class 1259 OID 16466)
-- Name: clientharvestrun; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.clientharvestrun (
    id integer NOT NULL,
    deleteddatasetcount bigint,
    faileddatasetcount bigint,
    finishtime timestamp without time zone,
    harvestresult integer,
    harvesteddatasetcount bigint,
    starttime timestamp without time zone,
    harvestingclient_id bigint NOT NULL
);


ALTER TABLE public.clientharvestrun OWNER TO dataverse;

--
-- TOC entry 218 (class 1259 OID 16469)
-- Name: clientharvestrun_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.clientharvestrun_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.clientharvestrun_id_seq OWNER TO dataverse;

--
-- TOC entry 4276 (class 0 OID 0)
-- Dependencies: 218
-- Name: clientharvestrun_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.clientharvestrun_id_seq OWNED BY public.clientharvestrun.id;


--
-- TOC entry 219 (class 1259 OID 16471)
-- Name: confirmemaildata; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.confirmemaildata (
    id integer NOT NULL,
    created timestamp without time zone NOT NULL,
    expires timestamp without time zone NOT NULL,
    token character varying(255),
    authenticateduser_id bigint NOT NULL
);


ALTER TABLE public.confirmemaildata OWNER TO dataverse;

--
-- TOC entry 220 (class 1259 OID 16474)
-- Name: confirmemaildata_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.confirmemaildata_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.confirmemaildata_id_seq OWNER TO dataverse;

--
-- TOC entry 4277 (class 0 OID 0)
-- Dependencies: 220
-- Name: confirmemaildata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.confirmemaildata_id_seq OWNED BY public.confirmemaildata.id;


--
-- TOC entry 221 (class 1259 OID 16476)
-- Name: controlledvocabalternate; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.controlledvocabalternate (
    id integer NOT NULL,
    strvalue text,
    controlledvocabularyvalue_id bigint NOT NULL,
    datasetfieldtype_id bigint NOT NULL
);


ALTER TABLE public.controlledvocabalternate OWNER TO dataverse;

--
-- TOC entry 222 (class 1259 OID 16482)
-- Name: controlledvocabalternate_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.controlledvocabalternate_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.controlledvocabalternate_id_seq OWNER TO dataverse;

--
-- TOC entry 4278 (class 0 OID 0)
-- Dependencies: 222
-- Name: controlledvocabalternate_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.controlledvocabalternate_id_seq OWNED BY public.controlledvocabalternate.id;


--
-- TOC entry 223 (class 1259 OID 16484)
-- Name: controlledvocabularyvalue; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.controlledvocabularyvalue (
    id integer NOT NULL,
    displayorder integer,
    identifier character varying(255),
    strvalue text,
    datasetfieldtype_id bigint
);


ALTER TABLE public.controlledvocabularyvalue OWNER TO dataverse;

--
-- TOC entry 224 (class 1259 OID 16490)
-- Name: controlledvocabularyvalue_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.controlledvocabularyvalue_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.controlledvocabularyvalue_id_seq OWNER TO dataverse;

--
-- TOC entry 4279 (class 0 OID 0)
-- Dependencies: 224
-- Name: controlledvocabularyvalue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.controlledvocabularyvalue_id_seq OWNED BY public.controlledvocabularyvalue.id;


--
-- TOC entry 225 (class 1259 OID 16492)
-- Name: customfieldmap; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.customfieldmap (
    id integer NOT NULL,
    sourcedatasetfield character varying(255),
    sourcetemplate character varying(255),
    targetdatasetfield character varying(255)
);


ALTER TABLE public.customfieldmap OWNER TO dataverse;

--
-- TOC entry 226 (class 1259 OID 16498)
-- Name: customfieldmap_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.customfieldmap_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customfieldmap_id_seq OWNER TO dataverse;

--
-- TOC entry 4280 (class 0 OID 0)
-- Dependencies: 226
-- Name: customfieldmap_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.customfieldmap_id_seq OWNED BY public.customfieldmap.id;


--
-- TOC entry 227 (class 1259 OID 16500)
-- Name: customquestion; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.customquestion (
    id integer NOT NULL,
    displayorder integer,
    hidden boolean,
    questionstring character varying(255) NOT NULL,
    questiontype character varying(255) NOT NULL,
    required boolean,
    guestbook_id bigint NOT NULL
);


ALTER TABLE public.customquestion OWNER TO dataverse;

--
-- TOC entry 228 (class 1259 OID 16506)
-- Name: customquestion_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.customquestion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customquestion_id_seq OWNER TO dataverse;

--
-- TOC entry 4281 (class 0 OID 0)
-- Dependencies: 228
-- Name: customquestion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.customquestion_id_seq OWNED BY public.customquestion.id;


--
-- TOC entry 229 (class 1259 OID 16508)
-- Name: customquestionresponse; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.customquestionresponse (
    id integer NOT NULL,
    response text,
    customquestion_id bigint NOT NULL,
    guestbookresponse_id bigint NOT NULL
);


ALTER TABLE public.customquestionresponse OWNER TO dataverse;

--
-- TOC entry 230 (class 1259 OID 16514)
-- Name: customquestionresponse_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.customquestionresponse_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customquestionresponse_id_seq OWNER TO dataverse;

--
-- TOC entry 4282 (class 0 OID 0)
-- Dependencies: 230
-- Name: customquestionresponse_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.customquestionresponse_id_seq OWNED BY public.customquestionresponse.id;


--
-- TOC entry 231 (class 1259 OID 16516)
-- Name: customquestionvalue; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.customquestionvalue (
    id integer NOT NULL,
    displayorder integer,
    valuestring character varying(255) NOT NULL,
    customquestion_id bigint NOT NULL
);


ALTER TABLE public.customquestionvalue OWNER TO dataverse;

--
-- TOC entry 232 (class 1259 OID 16519)
-- Name: customquestionvalue_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.customquestionvalue_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.customquestionvalue_id_seq OWNER TO dataverse;

--
-- TOC entry 4283 (class 0 OID 0)
-- Dependencies: 232
-- Name: customquestionvalue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.customquestionvalue_id_seq OWNED BY public.customquestionvalue.id;


--
-- TOC entry 233 (class 1259 OID 16521)
-- Name: customzipservicerequest; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.customzipservicerequest (
    key character varying(63),
    storagelocation character varying(255),
    filename character varying(255),
    issuetime timestamp without time zone
);


ALTER TABLE public.customzipservicerequest OWNER TO dataverse;

--
-- TOC entry 234 (class 1259 OID 16527)
-- Name: datafile; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datafile (
    id bigint NOT NULL,
    checksumtype character varying(255) NOT NULL,
    checksumvalue character varying(255) NOT NULL,
    contenttype character varying(255) NOT NULL,
    filesize bigint,
    ingeststatus character(1),
    previousdatafileid bigint,
    prov_entityname text,
    restricted boolean,
    rootdatafileid bigint NOT NULL
);


ALTER TABLE public.datafile OWNER TO dataverse;

--
-- TOC entry 235 (class 1259 OID 16533)
-- Name: datafilecategory; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datafilecategory (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    dataset_id bigint NOT NULL
);


ALTER TABLE public.datafilecategory OWNER TO dataverse;

--
-- TOC entry 236 (class 1259 OID 16536)
-- Name: datafilecategory_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datafilecategory_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datafilecategory_id_seq OWNER TO dataverse;

--
-- TOC entry 4284 (class 0 OID 0)
-- Dependencies: 236
-- Name: datafilecategory_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datafilecategory_id_seq OWNED BY public.datafilecategory.id;


--
-- TOC entry 237 (class 1259 OID 16538)
-- Name: datafiletag; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datafiletag (
    id integer NOT NULL,
    type integer NOT NULL,
    datafile_id bigint NOT NULL
);


ALTER TABLE public.datafiletag OWNER TO dataverse;

--
-- TOC entry 238 (class 1259 OID 16541)
-- Name: datafiletag_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datafiletag_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datafiletag_id_seq OWNER TO dataverse;

--
-- TOC entry 4285 (class 0 OID 0)
-- Dependencies: 238
-- Name: datafiletag_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datafiletag_id_seq OWNED BY public.datafiletag.id;


--
-- TOC entry 239 (class 1259 OID 16543)
-- Name: dataset; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataset (
    id bigint NOT NULL,
    fileaccessrequest boolean,
    harvestidentifier character varying(255),
    lastexporttime timestamp without time zone,
    storagedriver character varying(255),
    usegenericthumbnail boolean,
    citationdatedatasetfieldtype_id bigint,
    harvestingclient_id bigint,
    guestbook_id bigint,
    thumbnailfile_id bigint
);


ALTER TABLE public.dataset OWNER TO dataverse;

--
-- TOC entry 240 (class 1259 OID 16549)
-- Name: datasetexternalcitations; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetexternalcitations (
    id bigint NOT NULL,
    citedbyurl character varying(255) NOT NULL,
    dataset_id bigint NOT NULL
);


ALTER TABLE public.datasetexternalcitations OWNER TO dataverse;

--
-- TOC entry 241 (class 1259 OID 16552)
-- Name: datasetfield; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetfield (
    id integer NOT NULL,
    datasetfieldtype_id bigint NOT NULL,
    datasetversion_id bigint,
    parentdatasetfieldcompoundvalue_id bigint,
    template_id bigint
);


ALTER TABLE public.datasetfield OWNER TO dataverse;

--
-- TOC entry 242 (class 1259 OID 16555)
-- Name: datasetfield_controlledvocabularyvalue; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetfield_controlledvocabularyvalue (
    datasetfield_id bigint NOT NULL,
    controlledvocabularyvalues_id bigint NOT NULL
);


ALTER TABLE public.datasetfield_controlledvocabularyvalue OWNER TO dataverse;

--
-- TOC entry 243 (class 1259 OID 16558)
-- Name: datasetfield_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetfield_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetfield_id_seq OWNER TO dataverse;

--
-- TOC entry 4286 (class 0 OID 0)
-- Dependencies: 243
-- Name: datasetfield_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetfield_id_seq OWNED BY public.datasetfield.id;


--
-- TOC entry 244 (class 1259 OID 16560)
-- Name: datasetfieldcompoundvalue; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetfieldcompoundvalue (
    id integer NOT NULL,
    displayorder integer,
    parentdatasetfield_id bigint
);


ALTER TABLE public.datasetfieldcompoundvalue OWNER TO dataverse;

--
-- TOC entry 245 (class 1259 OID 16563)
-- Name: datasetfieldcompoundvalue_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetfieldcompoundvalue_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetfieldcompoundvalue_id_seq OWNER TO dataverse;

--
-- TOC entry 4287 (class 0 OID 0)
-- Dependencies: 245
-- Name: datasetfieldcompoundvalue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetfieldcompoundvalue_id_seq OWNED BY public.datasetfieldcompoundvalue.id;


--
-- TOC entry 246 (class 1259 OID 16565)
-- Name: datasetfielddefaultvalue; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetfielddefaultvalue (
    id integer NOT NULL,
    displayorder integer,
    strvalue text,
    datasetfield_id bigint NOT NULL,
    defaultvalueset_id bigint NOT NULL,
    parentdatasetfielddefaultvalue_id bigint
);


ALTER TABLE public.datasetfielddefaultvalue OWNER TO dataverse;

--
-- TOC entry 247 (class 1259 OID 16571)
-- Name: datasetfielddefaultvalue_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetfielddefaultvalue_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetfielddefaultvalue_id_seq OWNER TO dataverse;

--
-- TOC entry 4288 (class 0 OID 0)
-- Dependencies: 247
-- Name: datasetfielddefaultvalue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetfielddefaultvalue_id_seq OWNED BY public.datasetfielddefaultvalue.id;


--
-- TOC entry 248 (class 1259 OID 16573)
-- Name: datasetfieldtype; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetfieldtype (
    id integer NOT NULL,
    advancedsearchfieldtype boolean,
    allowcontrolledvocabulary boolean,
    allowmultiples boolean,
    description text,
    displayformat character varying(255),
    displayoncreate boolean,
    displayorder integer,
    facetable boolean,
    fieldtype character varying(255) NOT NULL,
    name text,
    required boolean,
    title text,
    uri text,
    validationformat character varying(255),
    watermark character varying(255),
    metadatablock_id bigint,
    parentdatasetfieldtype_id bigint
);


ALTER TABLE public.datasetfieldtype OWNER TO dataverse;

--
-- TOC entry 249 (class 1259 OID 16579)
-- Name: datasetfieldtype_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetfieldtype_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetfieldtype_id_seq OWNER TO dataverse;

--
-- TOC entry 4289 (class 0 OID 0)
-- Dependencies: 249
-- Name: datasetfieldtype_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetfieldtype_id_seq OWNED BY public.datasetfieldtype.id;


--
-- TOC entry 250 (class 1259 OID 16581)
-- Name: datasetfieldvalue; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetfieldvalue (
    id integer NOT NULL,
    displayorder integer,
    value text,
    datasetfield_id bigint NOT NULL
);


ALTER TABLE public.datasetfieldvalue OWNER TO dataverse;

--
-- TOC entry 251 (class 1259 OID 16587)
-- Name: datasetfieldvalue_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetfieldvalue_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetfieldvalue_id_seq OWNER TO dataverse;

--
-- TOC entry 4290 (class 0 OID 0)
-- Dependencies: 251
-- Name: datasetfieldvalue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetfieldvalue_id_seq OWNED BY public.datasetfieldvalue.id;


--
-- TOC entry 252 (class 1259 OID 16589)
-- Name: datasetlinkingdataverse; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetlinkingdataverse (
    id integer NOT NULL,
    linkcreatetime timestamp without time zone NOT NULL,
    dataset_id bigint NOT NULL,
    linkingdataverse_id bigint NOT NULL
);


ALTER TABLE public.datasetlinkingdataverse OWNER TO dataverse;

--
-- TOC entry 253 (class 1259 OID 16592)
-- Name: datasetlinkingdataverse_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetlinkingdataverse_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetlinkingdataverse_id_seq OWNER TO dataverse;

--
-- TOC entry 4291 (class 0 OID 0)
-- Dependencies: 253
-- Name: datasetlinkingdataverse_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetlinkingdataverse_id_seq OWNED BY public.datasetlinkingdataverse.id;


--
-- TOC entry 254 (class 1259 OID 16594)
-- Name: datasetlock; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetlock (
    id integer NOT NULL,
    info character varying(255),
    reason character varying(255) NOT NULL,
    starttime timestamp without time zone,
    dataset_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.datasetlock OWNER TO dataverse;

--
-- TOC entry 255 (class 1259 OID 16600)
-- Name: datasetlock_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetlock_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetlock_id_seq OWNER TO dataverse;

--
-- TOC entry 4292 (class 0 OID 0)
-- Dependencies: 255
-- Name: datasetlock_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetlock_id_seq OWNED BY public.datasetlock.id;


--
-- TOC entry 256 (class 1259 OID 16602)
-- Name: datasetmetrics; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetmetrics (
    id integer NOT NULL,
    countrycode character varying(255),
    downloadstotalmachine bigint,
    downloadstotalregular bigint,
    downloadsuniquemachine bigint,
    downloadsuniqueregular bigint,
    monthyear character varying(255),
    viewstotalmachine bigint,
    viewstotalregular bigint,
    viewsuniquemachine bigint,
    viewsuniqueregular bigint,
    dataset_id bigint NOT NULL
);


ALTER TABLE public.datasetmetrics OWNER TO dataverse;

--
-- TOC entry 257 (class 1259 OID 16608)
-- Name: datasetmetrics_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetmetrics_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetmetrics_id_seq OWNER TO dataverse;

--
-- TOC entry 4293 (class 0 OID 0)
-- Dependencies: 257
-- Name: datasetmetrics_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetmetrics_id_seq OWNED BY public.datasetmetrics.id;


--
-- TOC entry 258 (class 1259 OID 16610)
-- Name: datasetversion; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetversion (
    id integer NOT NULL,
    unf character varying(255),
    archivalcopylocation text,
    archivenote character varying(1000),
    archivetime timestamp without time zone,
    createtime timestamp without time zone NOT NULL,
    deaccessionlink character varying(255),
    lastupdatetime timestamp without time zone NOT NULL,
    minorversionnumber bigint,
    releasetime timestamp without time zone,
    version bigint,
    versionnote character varying(1000),
    versionnumber bigint,
    versionstate character varying(255),
    dataset_id bigint,
    termsofuseandaccess_id bigint
);


ALTER TABLE public.datasetversion OWNER TO dataverse;

--
-- TOC entry 259 (class 1259 OID 16616)
-- Name: datasetversion_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetversion_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetversion_id_seq OWNER TO dataverse;

--
-- TOC entry 4294 (class 0 OID 0)
-- Dependencies: 259
-- Name: datasetversion_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetversion_id_seq OWNED BY public.datasetversion.id;


--
-- TOC entry 260 (class 1259 OID 16618)
-- Name: datasetversionuser; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datasetversionuser (
    id integer NOT NULL,
    lastupdatedate timestamp without time zone NOT NULL,
    authenticateduser_id bigint,
    datasetversion_id bigint
);


ALTER TABLE public.datasetversionuser OWNER TO dataverse;

--
-- TOC entry 261 (class 1259 OID 16621)
-- Name: datasetversionuser_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datasetversionuser_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datasetversionuser_id_seq OWNER TO dataverse;

--
-- TOC entry 4295 (class 0 OID 0)
-- Dependencies: 261
-- Name: datasetversionuser_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datasetversionuser_id_seq OWNED BY public.datasetversionuser.id;


--
-- TOC entry 262 (class 1259 OID 16623)
-- Name: datatable; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datatable (
    id integer NOT NULL,
    casequantity bigint,
    originalfileformat character varying(255),
    originalfilename character varying(255),
    originalfilesize bigint,
    originalformatversion character varying(255),
    recordspercase bigint,
    unf character varying(255) NOT NULL,
    varquantity bigint,
    datafile_id bigint NOT NULL
);


ALTER TABLE public.datatable OWNER TO dataverse;

--
-- TOC entry 263 (class 1259 OID 16629)
-- Name: datatable_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datatable_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datatable_id_seq OWNER TO dataverse;

--
-- TOC entry 4296 (class 0 OID 0)
-- Dependencies: 263
-- Name: datatable_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datatable_id_seq OWNED BY public.datatable.id;


--
-- TOC entry 264 (class 1259 OID 16631)
-- Name: datavariable; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.datavariable (
    id integer NOT NULL,
    factor boolean,
    fileendposition bigint,
    fileorder integer,
    filestartposition bigint,
    format character varying(255),
    formatcategory character varying(255),
    "interval" integer,
    label text,
    name character varying(255),
    numberofdecimalpoints bigint,
    orderedfactor boolean,
    recordsegmentnumber bigint,
    type integer,
    unf character varying(255),
    weighted boolean,
    datatable_id bigint NOT NULL
);


ALTER TABLE public.datavariable OWNER TO dataverse;

--
-- TOC entry 265 (class 1259 OID 16637)
-- Name: datavariable_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.datavariable_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.datavariable_id_seq OWNER TO dataverse;

--
-- TOC entry 4297 (class 0 OID 0)
-- Dependencies: 265
-- Name: datavariable_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.datavariable_id_seq OWNED BY public.datavariable.id;


--
-- TOC entry 266 (class 1259 OID 16639)
-- Name: dataverse; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataverse (
    id bigint NOT NULL,
    affiliation character varying(255),
    alias character varying(255) NOT NULL,
    dataversetype character varying(255) NOT NULL,
    description text,
    facetroot boolean,
    guestbookroot boolean,
    metadatablockroot boolean,
    name character varying(255) NOT NULL,
    permissionroot boolean,
    storagedriver character varying(255),
    templateroot boolean,
    themeroot boolean,
    defaultcontributorrole_id bigint,
    defaulttemplate_id bigint
);


ALTER TABLE public.dataverse OWNER TO dataverse;

--
-- TOC entry 267 (class 1259 OID 16645)
-- Name: dataverse_citationdatasetfieldtypes; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataverse_citationdatasetfieldtypes (
    dataverse_id bigint NOT NULL,
    citationdatasetfieldtype_id bigint NOT NULL
);


ALTER TABLE public.dataverse_citationdatasetfieldtypes OWNER TO dataverse;

--
-- TOC entry 268 (class 1259 OID 16648)
-- Name: dataverse_metadatablock; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataverse_metadatablock (
    dataverse_id bigint NOT NULL,
    metadatablocks_id bigint NOT NULL
);


ALTER TABLE public.dataverse_metadatablock OWNER TO dataverse;

--
-- TOC entry 269 (class 1259 OID 16651)
-- Name: dataversecontact; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataversecontact (
    id integer NOT NULL,
    contactemail character varying(255) NOT NULL,
    displayorder integer,
    dataverse_id bigint
);


ALTER TABLE public.dataversecontact OWNER TO dataverse;

--
-- TOC entry 270 (class 1259 OID 16654)
-- Name: dataversecontact_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.dataversecontact_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataversecontact_id_seq OWNER TO dataverse;

--
-- TOC entry 4298 (class 0 OID 0)
-- Dependencies: 270
-- Name: dataversecontact_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.dataversecontact_id_seq OWNED BY public.dataversecontact.id;


--
-- TOC entry 271 (class 1259 OID 16656)
-- Name: dataversefacet; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataversefacet (
    id integer NOT NULL,
    displayorder integer,
    datasetfieldtype_id bigint,
    dataverse_id bigint
);


ALTER TABLE public.dataversefacet OWNER TO dataverse;

--
-- TOC entry 272 (class 1259 OID 16659)
-- Name: dataversefacet_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.dataversefacet_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataversefacet_id_seq OWNER TO dataverse;

--
-- TOC entry 4299 (class 0 OID 0)
-- Dependencies: 272
-- Name: dataversefacet_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.dataversefacet_id_seq OWNED BY public.dataversefacet.id;


--
-- TOC entry 273 (class 1259 OID 16661)
-- Name: dataversefeatureddataverse; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataversefeatureddataverse (
    id integer NOT NULL,
    displayorder integer,
    dataverse_id bigint,
    featureddataverse_id bigint
);


ALTER TABLE public.dataversefeatureddataverse OWNER TO dataverse;

--
-- TOC entry 274 (class 1259 OID 16664)
-- Name: dataversefeatureddataverse_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.dataversefeatureddataverse_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataversefeatureddataverse_id_seq OWNER TO dataverse;

--
-- TOC entry 4300 (class 0 OID 0)
-- Dependencies: 274
-- Name: dataversefeatureddataverse_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.dataversefeatureddataverse_id_seq OWNED BY public.dataversefeatureddataverse.id;


--
-- TOC entry 275 (class 1259 OID 16666)
-- Name: dataversefieldtypeinputlevel; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataversefieldtypeinputlevel (
    id integer NOT NULL,
    include boolean,
    required boolean,
    datasetfieldtype_id bigint,
    dataverse_id bigint
);


ALTER TABLE public.dataversefieldtypeinputlevel OWNER TO dataverse;

--
-- TOC entry 276 (class 1259 OID 16669)
-- Name: dataversefieldtypeinputlevel_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.dataversefieldtypeinputlevel_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataversefieldtypeinputlevel_id_seq OWNER TO dataverse;

--
-- TOC entry 4301 (class 0 OID 0)
-- Dependencies: 276
-- Name: dataversefieldtypeinputlevel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.dataversefieldtypeinputlevel_id_seq OWNED BY public.dataversefieldtypeinputlevel.id;


--
-- TOC entry 277 (class 1259 OID 16671)
-- Name: dataverselinkingdataverse; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataverselinkingdataverse (
    id integer NOT NULL,
    linkcreatetime timestamp without time zone,
    dataverse_id bigint NOT NULL,
    linkingdataverse_id bigint NOT NULL
);


ALTER TABLE public.dataverselinkingdataverse OWNER TO dataverse;

--
-- TOC entry 278 (class 1259 OID 16674)
-- Name: dataverselinkingdataverse_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.dataverselinkingdataverse_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataverselinkingdataverse_id_seq OWNER TO dataverse;

--
-- TOC entry 4302 (class 0 OID 0)
-- Dependencies: 278
-- Name: dataverselinkingdataverse_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.dataverselinkingdataverse_id_seq OWNED BY public.dataverselinkingdataverse.id;


--
-- TOC entry 279 (class 1259 OID 16676)
-- Name: dataverserole; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataverserole (
    id integer NOT NULL,
    alias character varying(255) NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    permissionbits bigint,
    owner_id bigint
);


ALTER TABLE public.dataverserole OWNER TO dataverse;

--
-- TOC entry 280 (class 1259 OID 16682)
-- Name: dataverserole_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.dataverserole_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataverserole_id_seq OWNER TO dataverse;

--
-- TOC entry 4303 (class 0 OID 0)
-- Dependencies: 280
-- Name: dataverserole_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.dataverserole_id_seq OWNED BY public.dataverserole.id;


--
-- TOC entry 281 (class 1259 OID 16684)
-- Name: dataversesubjects; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataversesubjects (
    dataverse_id bigint NOT NULL,
    controlledvocabularyvalue_id bigint NOT NULL
);


ALTER TABLE public.dataversesubjects OWNER TO dataverse;

--
-- TOC entry 282 (class 1259 OID 16687)
-- Name: dataversetheme; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dataversetheme (
    id integer NOT NULL,
    backgroundcolor character varying(255),
    linkcolor character varying(255),
    linkurl character varying(255),
    logo character varying(255),
    logoalignment character varying(255),
    logobackgroundcolor character varying(255),
    logofooter character varying(255),
    logofooteralignment integer,
    logofooterbackgroundcolor character varying(255),
    logoformat character varying(255),
    tagline character varying(255),
    textcolor character varying(255),
    dataverse_id bigint
);


ALTER TABLE public.dataversetheme OWNER TO dataverse;

--
-- TOC entry 283 (class 1259 OID 16693)
-- Name: dataversetheme_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.dataversetheme_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataversetheme_id_seq OWNER TO dataverse;

--
-- TOC entry 4304 (class 0 OID 0)
-- Dependencies: 283
-- Name: dataversetheme_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.dataversetheme_id_seq OWNED BY public.dataversetheme.id;


--
-- TOC entry 284 (class 1259 OID 16695)
-- Name: defaultvalueset; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.defaultvalueset (
    id integer NOT NULL,
    name character varying(255) NOT NULL
);


ALTER TABLE public.defaultvalueset OWNER TO dataverse;

--
-- TOC entry 285 (class 1259 OID 16698)
-- Name: defaultvalueset_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.defaultvalueset_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.defaultvalueset_id_seq OWNER TO dataverse;

--
-- TOC entry 4305 (class 0 OID 0)
-- Dependencies: 285
-- Name: defaultvalueset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.defaultvalueset_id_seq OWNED BY public.defaultvalueset.id;


--
-- TOC entry 286 (class 1259 OID 16700)
-- Name: doidataciteregistercache; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.doidataciteregistercache (
    id integer NOT NULL,
    doi character varying(255),
    status character varying(255),
    url character varying(255),
    xml text
);


ALTER TABLE public.doidataciteregistercache OWNER TO dataverse;

--
-- TOC entry 287 (class 1259 OID 16706)
-- Name: doidataciteregistercache_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.doidataciteregistercache_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.doidataciteregistercache_id_seq OWNER TO dataverse;

--
-- TOC entry 4306 (class 0 OID 0)
-- Dependencies: 287
-- Name: doidataciteregistercache_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.doidataciteregistercache_id_seq OWNED BY public.doidataciteregistercache.id;


--
-- TOC entry 288 (class 1259 OID 16708)
-- Name: dvobject; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.dvobject (
    id integer NOT NULL,
    dtype character varying(31),
    authority character varying(255),
    createdate timestamp without time zone NOT NULL,
    globalidcreatetime timestamp without time zone,
    identifier character varying(255),
    identifierregistered boolean,
    indextime timestamp without time zone,
    modificationtime timestamp without time zone NOT NULL,
    permissionindextime timestamp without time zone,
    permissionmodificationtime timestamp without time zone,
    previewimageavailable boolean,
    protocol character varying(255),
    publicationdate timestamp without time zone,
    storageidentifier character varying(255),
    creator_id bigint,
    owner_id bigint,
    releaseuser_id bigint
);


ALTER TABLE public.dvobject OWNER TO dataverse;

--
-- TOC entry 289 (class 1259 OID 16714)
-- Name: dvobject_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.dvobject_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dvobject_id_seq OWNER TO dataverse;

--
-- TOC entry 4307 (class 0 OID 0)
-- Dependencies: 289
-- Name: dvobject_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.dvobject_id_seq OWNED BY public.dvobject.id;


--
-- TOC entry 290 (class 1259 OID 16716)
-- Name: explicitgroup; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.explicitgroup (
    id integer NOT NULL,
    description character varying(1024),
    displayname character varying(255),
    groupalias character varying(255),
    groupaliasinowner character varying(255),
    owner_id bigint
);


ALTER TABLE public.explicitgroup OWNER TO dataverse;

--
-- TOC entry 291 (class 1259 OID 16722)
-- Name: explicitgroup_authenticateduser; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.explicitgroup_authenticateduser (
    explicitgroup_id bigint NOT NULL,
    containedauthenticatedusers_id bigint NOT NULL
);


ALTER TABLE public.explicitgroup_authenticateduser OWNER TO dataverse;

--
-- TOC entry 292 (class 1259 OID 16725)
-- Name: explicitgroup_containedroleassignees; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.explicitgroup_containedroleassignees (
    explicitgroup_id bigint,
    containedroleassignees character varying(255)
);


ALTER TABLE public.explicitgroup_containedroleassignees OWNER TO dataverse;

--
-- TOC entry 293 (class 1259 OID 16728)
-- Name: explicitgroup_explicitgroup; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.explicitgroup_explicitgroup (
    explicitgroup_id bigint NOT NULL,
    containedexplicitgroups_id bigint NOT NULL
);


ALTER TABLE public.explicitgroup_explicitgroup OWNER TO dataverse;

--
-- TOC entry 294 (class 1259 OID 16731)
-- Name: explicitgroup_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.explicitgroup_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.explicitgroup_id_seq OWNER TO dataverse;

--
-- TOC entry 4308 (class 0 OID 0)
-- Dependencies: 294
-- Name: explicitgroup_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.explicitgroup_id_seq OWNED BY public.explicitgroup.id;


--
-- TOC entry 295 (class 1259 OID 16733)
-- Name: externaltool; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.externaltool (
    id integer NOT NULL,
    contenttype text,
    description text,
    displayname character varying(255) NOT NULL,
    scope character varying(255) NOT NULL,
    toolname character varying(255),
    toolparameters character varying(255) NOT NULL,
    toolurl character varying(255) NOT NULL
);


ALTER TABLE public.externaltool OWNER TO dataverse;

--
-- TOC entry 296 (class 1259 OID 16739)
-- Name: externaltool_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.externaltool_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.externaltool_id_seq OWNER TO dataverse;

--
-- TOC entry 4309 (class 0 OID 0)
-- Dependencies: 296
-- Name: externaltool_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.externaltool_id_seq OWNED BY public.externaltool.id;


--
-- TOC entry 297 (class 1259 OID 16741)
-- Name: externaltooltype; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.externaltooltype (
    id integer NOT NULL,
    type character varying(255) NOT NULL,
    externaltool_id bigint NOT NULL
);


ALTER TABLE public.externaltooltype OWNER TO dataverse;

--
-- TOC entry 298 (class 1259 OID 16744)
-- Name: externaltooltype_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.externaltooltype_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.externaltooltype_id_seq OWNER TO dataverse;

--
-- TOC entry 4310 (class 0 OID 0)
-- Dependencies: 298
-- Name: externaltooltype_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.externaltooltype_id_seq OWNED BY public.externaltooltype.id;


--
-- TOC entry 299 (class 1259 OID 16746)
-- Name: fileaccessrequests; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.fileaccessrequests (
    datafile_id bigint NOT NULL,
    authenticated_user_id bigint NOT NULL
);


ALTER TABLE public.fileaccessrequests OWNER TO dataverse;

--
-- TOC entry 300 (class 1259 OID 16749)
-- Name: filedownload; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.filedownload (
    downloadtimestamp timestamp without time zone,
    downloadtype character varying(255),
    guestbookresponse_id bigint NOT NULL,
    sessionid character varying(255)
);


ALTER TABLE public.filedownload OWNER TO dataverse;

--
-- TOC entry 301 (class 1259 OID 16755)
-- Name: filemetadata; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.filemetadata (
    id integer NOT NULL,
    description text,
    directorylabel character varying(255),
    label character varying(255) NOT NULL,
    prov_freeform text,
    restricted boolean,
    version bigint,
    datafile_id bigint NOT NULL,
    datasetversion_id bigint NOT NULL
);


ALTER TABLE public.filemetadata OWNER TO dataverse;

--
-- TOC entry 302 (class 1259 OID 16761)
-- Name: filemetadata_datafilecategory; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.filemetadata_datafilecategory (
    filecategories_id bigint NOT NULL,
    filemetadatas_id bigint NOT NULL
);


ALTER TABLE public.filemetadata_datafilecategory OWNER TO dataverse;

--
-- TOC entry 303 (class 1259 OID 16764)
-- Name: filemetadata_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.filemetadata_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.filemetadata_id_seq OWNER TO dataverse;

--
-- TOC entry 4311 (class 0 OID 0)
-- Dependencies: 303
-- Name: filemetadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.filemetadata_id_seq OWNED BY public.filemetadata.id;


--
-- TOC entry 304 (class 1259 OID 16766)
-- Name: flyway_schema_history; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.flyway_schema_history (
    installed_rank integer NOT NULL,
    version character varying(50),
    description character varying(200) NOT NULL,
    type character varying(20) NOT NULL,
    script character varying(1000) NOT NULL,
    checksum integer,
    installed_by character varying(100) NOT NULL,
    installed_on timestamp without time zone DEFAULT now() NOT NULL,
    execution_time integer NOT NULL,
    success boolean NOT NULL
);


ALTER TABLE public.flyway_schema_history OWNER TO dataverse;

--
-- TOC entry 305 (class 1259 OID 16773)
-- Name: foreignmetadatafieldmapping; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.foreignmetadatafieldmapping (
    id integer NOT NULL,
    datasetfieldname text,
    foreignfieldxpath text,
    isattribute boolean,
    foreignmetadataformatmapping_id bigint,
    parentfieldmapping_id bigint
);


ALTER TABLE public.foreignmetadatafieldmapping OWNER TO dataverse;

--
-- TOC entry 306 (class 1259 OID 16779)
-- Name: foreignmetadatafieldmapping_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.foreignmetadatafieldmapping_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.foreignmetadatafieldmapping_id_seq OWNER TO dataverse;

--
-- TOC entry 4312 (class 0 OID 0)
-- Dependencies: 306
-- Name: foreignmetadatafieldmapping_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.foreignmetadatafieldmapping_id_seq OWNED BY public.foreignmetadatafieldmapping.id;


--
-- TOC entry 307 (class 1259 OID 16781)
-- Name: foreignmetadataformatmapping; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.foreignmetadataformatmapping (
    id integer NOT NULL,
    displayname character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    schemalocation character varying(255),
    startelement character varying(255)
);


ALTER TABLE public.foreignmetadataformatmapping OWNER TO dataverse;

--
-- TOC entry 308 (class 1259 OID 16787)
-- Name: foreignmetadataformatmapping_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.foreignmetadataformatmapping_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.foreignmetadataformatmapping_id_seq OWNER TO dataverse;

--
-- TOC entry 4313 (class 0 OID 0)
-- Dependencies: 308
-- Name: foreignmetadataformatmapping_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.foreignmetadataformatmapping_id_seq OWNED BY public.foreignmetadataformatmapping.id;


--
-- TOC entry 309 (class 1259 OID 16789)
-- Name: guestbook; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.guestbook (
    id integer NOT NULL,
    createtime timestamp without time zone NOT NULL,
    emailrequired boolean,
    enabled boolean,
    institutionrequired boolean,
    name character varying(255),
    namerequired boolean,
    positionrequired boolean,
    dataverse_id bigint
);


ALTER TABLE public.guestbook OWNER TO dataverse;

--
-- TOC entry 310 (class 1259 OID 16792)
-- Name: guestbook_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.guestbook_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.guestbook_id_seq OWNER TO dataverse;

--
-- TOC entry 4314 (class 0 OID 0)
-- Dependencies: 310
-- Name: guestbook_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.guestbook_id_seq OWNED BY public.guestbook.id;


--
-- TOC entry 311 (class 1259 OID 16794)
-- Name: guestbookresponse; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.guestbookresponse (
    id integer NOT NULL,
    email character varying(255),
    institution character varying(255),
    name character varying(255),
    "position" character varying(255),
    responsetime timestamp without time zone,
    authenticateduser_id bigint,
    datafile_id bigint NOT NULL,
    dataset_id bigint NOT NULL,
    datasetversion_id bigint,
    guestbook_id bigint NOT NULL
);


ALTER TABLE public.guestbookresponse OWNER TO dataverse;

--
-- TOC entry 312 (class 1259 OID 16800)
-- Name: guestbookresponse_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.guestbookresponse_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.guestbookresponse_id_seq OWNER TO dataverse;

--
-- TOC entry 4315 (class 0 OID 0)
-- Dependencies: 312
-- Name: guestbookresponse_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.guestbookresponse_id_seq OWNED BY public.guestbookresponse.id;


--
-- TOC entry 313 (class 1259 OID 16802)
-- Name: harvestingclient; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.harvestingclient (
    id integer NOT NULL,
    archivedescription text,
    archiveurl character varying(255),
    deleted boolean,
    harveststyle character varying(255),
    harvesttype character varying(255),
    harvestingnow boolean,
    harvestingset character varying(255),
    harvestingurl character varying(255),
    metadataprefix character varying(255),
    name character varying(255) NOT NULL,
    scheduledayofweek integer,
    schedulehourofday integer,
    scheduleperiod character varying(255),
    scheduled boolean,
    dataverse_id bigint
);


ALTER TABLE public.harvestingclient OWNER TO dataverse;

--
-- TOC entry 314 (class 1259 OID 16808)
-- Name: harvestingclient_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.harvestingclient_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.harvestingclient_id_seq OWNER TO dataverse;

--
-- TOC entry 4316 (class 0 OID 0)
-- Dependencies: 314
-- Name: harvestingclient_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.harvestingclient_id_seq OWNED BY public.harvestingclient.id;


--
-- TOC entry 315 (class 1259 OID 16810)
-- Name: harvestingdataverseconfig; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.harvestingdataverseconfig (
    id bigint NOT NULL,
    archivedescription text,
    archiveurl character varying(255),
    harveststyle character varying(255),
    harvesttype character varying(255),
    harvestingset character varying(255),
    harvestingurl character varying(255),
    dataverse_id bigint
);


ALTER TABLE public.harvestingdataverseconfig OWNER TO dataverse;

--
-- TOC entry 316 (class 1259 OID 16816)
-- Name: ingestreport; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.ingestreport (
    id integer NOT NULL,
    endtime timestamp without time zone,
    report text,
    starttime timestamp without time zone,
    status integer,
    type integer,
    datafile_id bigint NOT NULL
);


ALTER TABLE public.ingestreport OWNER TO dataverse;

--
-- TOC entry 317 (class 1259 OID 16822)
-- Name: ingestreport_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.ingestreport_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ingestreport_id_seq OWNER TO dataverse;

--
-- TOC entry 4317 (class 0 OID 0)
-- Dependencies: 317
-- Name: ingestreport_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.ingestreport_id_seq OWNED BY public.ingestreport.id;


--
-- TOC entry 318 (class 1259 OID 16824)
-- Name: ingestrequest; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.ingestrequest (
    id integer NOT NULL,
    controlcard character varying(255),
    forcetypecheck boolean,
    labelsfile character varying(255),
    textencoding character varying(255),
    datafile_id bigint
);


ALTER TABLE public.ingestrequest OWNER TO dataverse;

--
-- TOC entry 319 (class 1259 OID 16830)
-- Name: ingestrequest_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.ingestrequest_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.ingestrequest_id_seq OWNER TO dataverse;

--
-- TOC entry 4318 (class 0 OID 0)
-- Dependencies: 319
-- Name: ingestrequest_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.ingestrequest_id_seq OWNED BY public.ingestrequest.id;


--
-- TOC entry 320 (class 1259 OID 16832)
-- Name: ipv4range; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.ipv4range (
    id bigint NOT NULL,
    bottomaslong bigint,
    topaslong bigint,
    owner_id bigint
);


ALTER TABLE public.ipv4range OWNER TO dataverse;

--
-- TOC entry 321 (class 1259 OID 16835)
-- Name: ipv6range; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.ipv6range (
    id bigint NOT NULL,
    bottoma bigint,
    bottomb bigint,
    bottomc bigint,
    bottomd bigint,
    topa bigint,
    topb bigint,
    topc bigint,
    topd bigint,
    owner_id bigint
);


ALTER TABLE public.ipv6range OWNER TO dataverse;

--
-- TOC entry 322 (class 1259 OID 16838)
-- Name: metadatablock; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.metadatablock (
    id integer NOT NULL,
    displayname character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    namespaceuri text,
    owner_id bigint
);


ALTER TABLE public.metadatablock OWNER TO dataverse;

--
-- TOC entry 323 (class 1259 OID 16844)
-- Name: metadatablock_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.metadatablock_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.metadatablock_id_seq OWNER TO dataverse;

--
-- TOC entry 4319 (class 0 OID 0)
-- Dependencies: 323
-- Name: metadatablock_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.metadatablock_id_seq OWNED BY public.metadatablock.id;


--
-- TOC entry 324 (class 1259 OID 16846)
-- Name: metric; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.metric (
    id integer NOT NULL,
    datalocation text,
    daystring text,
    lastcalleddate timestamp without time zone NOT NULL,
    name character varying(255) NOT NULL,
    valuejson text,
    dataverse_id bigint
);


ALTER TABLE public.metric OWNER TO dataverse;

--
-- TOC entry 325 (class 1259 OID 16852)
-- Name: metric_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.metric_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.metric_id_seq OWNER TO dataverse;

--
-- TOC entry 4320 (class 0 OID 0)
-- Dependencies: 325
-- Name: metric_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.metric_id_seq OWNED BY public.metric.id;


--
-- TOC entry 326 (class 1259 OID 16854)
-- Name: oairecord; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.oairecord (
    id integer NOT NULL,
    globalid character varying(255),
    lastupdatetime timestamp without time zone,
    removed boolean,
    setname character varying(255)
);


ALTER TABLE public.oairecord OWNER TO dataverse;

--
-- TOC entry 327 (class 1259 OID 16860)
-- Name: oairecord_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.oairecord_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.oairecord_id_seq OWNER TO dataverse;

--
-- TOC entry 4321 (class 0 OID 0)
-- Dependencies: 327
-- Name: oairecord_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.oairecord_id_seq OWNED BY public.oairecord.id;


--
-- TOC entry 328 (class 1259 OID 16862)
-- Name: oaiset; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.oaiset (
    id integer NOT NULL,
    definition text,
    deleted boolean,
    description text,
    name text,
    spec text,
    updateinprogress boolean,
    version bigint
);


ALTER TABLE public.oaiset OWNER TO dataverse;

--
-- TOC entry 329 (class 1259 OID 16868)
-- Name: oaiset_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.oaiset_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.oaiset_id_seq OWNER TO dataverse;

--
-- TOC entry 4322 (class 0 OID 0)
-- Dependencies: 329
-- Name: oaiset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.oaiset_id_seq OWNED BY public.oaiset.id;


--
-- TOC entry 330 (class 1259 OID 16870)
-- Name: oauth2tokendata; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.oauth2tokendata (
    id integer NOT NULL,
    accesstoken text,
    expirydate timestamp without time zone,
    oauthproviderid character varying(255),
    rawresponse text,
    refreshtoken character varying(64),
    tokentype character varying(32),
    user_id bigint
);


ALTER TABLE public.oauth2tokendata OWNER TO dataverse;

--
-- TOC entry 331 (class 1259 OID 16876)
-- Name: oauth2tokendata_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.oauth2tokendata_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.oauth2tokendata_id_seq OWNER TO dataverse;

--
-- TOC entry 4323 (class 0 OID 0)
-- Dependencies: 331
-- Name: oauth2tokendata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.oauth2tokendata_id_seq OWNED BY public.oauth2tokendata.id;


--
-- TOC entry 332 (class 1259 OID 16878)
-- Name: passwordresetdata; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.passwordresetdata (
    id integer NOT NULL,
    created timestamp without time zone NOT NULL,
    expires timestamp without time zone NOT NULL,
    reason character varying(255),
    token character varying(255),
    builtinuser_id bigint NOT NULL
);


ALTER TABLE public.passwordresetdata OWNER TO dataverse;

--
-- TOC entry 333 (class 1259 OID 16884)
-- Name: passwordresetdata_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.passwordresetdata_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.passwordresetdata_id_seq OWNER TO dataverse;

--
-- TOC entry 4324 (class 0 OID 0)
-- Dependencies: 333
-- Name: passwordresetdata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.passwordresetdata_id_seq OWNED BY public.passwordresetdata.id;


--
-- TOC entry 334 (class 1259 OID 16886)
-- Name: pendingworkflowinvocation; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.pendingworkflowinvocation (
    invocationid character varying(255) NOT NULL,
    datasetexternallyreleased boolean,
    ipaddress character varying(255),
    nextminorversionnumber bigint,
    nextversionnumber bigint,
    pendingstepidx integer,
    typeordinal integer,
    userid character varying(255),
    workflow_id bigint,
    dataset_id bigint,
    lockid bigint
);


ALTER TABLE public.pendingworkflowinvocation OWNER TO dataverse;

--
-- TOC entry 335 (class 1259 OID 16892)
-- Name: pendingworkflowinvocation_localdata; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.pendingworkflowinvocation_localdata (
    pendingworkflowinvocation_invocationid character varying(255),
    localdata character varying(255),
    localdata_key character varying(255)
);


ALTER TABLE public.pendingworkflowinvocation_localdata OWNER TO dataverse;

--
-- TOC entry 336 (class 1259 OID 16898)
-- Name: persistedglobalgroup; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.persistedglobalgroup (
    id bigint NOT NULL,
    dtype character varying(31),
    description character varying(255),
    displayname character varying(255),
    persistedgroupalias character varying(255),
    emaildomains character varying(255),
    isregex boolean DEFAULT false NOT NULL
);


ALTER TABLE public.persistedglobalgroup OWNER TO dataverse;

--
-- TOC entry 337 (class 1259 OID 16905)
-- Name: roleassignment; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.roleassignment (
    id integer NOT NULL,
    assigneeidentifier character varying(255) NOT NULL,
    privateurltoken character varying(255),
    definitionpoint_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE public.roleassignment OWNER TO dataverse;

--
-- TOC entry 338 (class 1259 OID 16911)
-- Name: roleassignment_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.roleassignment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.roleassignment_id_seq OWNER TO dataverse;

--
-- TOC entry 4325 (class 0 OID 0)
-- Dependencies: 338
-- Name: roleassignment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.roleassignment_id_seq OWNED BY public.roleassignment.id;


--
-- TOC entry 339 (class 1259 OID 16913)
-- Name: savedsearch; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.savedsearch (
    id integer NOT NULL,
    query text,
    creator_id bigint NOT NULL,
    definitionpoint_id bigint NOT NULL
);


ALTER TABLE public.savedsearch OWNER TO dataverse;

--
-- TOC entry 340 (class 1259 OID 16919)
-- Name: savedsearch_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.savedsearch_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.savedsearch_id_seq OWNER TO dataverse;

--
-- TOC entry 4326 (class 0 OID 0)
-- Dependencies: 340
-- Name: savedsearch_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.savedsearch_id_seq OWNED BY public.savedsearch.id;


--
-- TOC entry 341 (class 1259 OID 16921)
-- Name: savedsearchfilterquery; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.savedsearchfilterquery (
    id integer NOT NULL,
    filterquery text,
    savedsearch_id bigint NOT NULL
);


ALTER TABLE public.savedsearchfilterquery OWNER TO dataverse;

--
-- TOC entry 342 (class 1259 OID 16927)
-- Name: savedsearchfilterquery_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.savedsearchfilterquery_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.savedsearchfilterquery_id_seq OWNER TO dataverse;

--
-- TOC entry 4327 (class 0 OID 0)
-- Dependencies: 342
-- Name: savedsearchfilterquery_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.savedsearchfilterquery_id_seq OWNED BY public.savedsearchfilterquery.id;


--
-- TOC entry 343 (class 1259 OID 16929)
-- Name: sequence; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.sequence (
    seq_name character varying(50) NOT NULL,
    seq_count numeric(38,0)
);


ALTER TABLE public.sequence OWNER TO dataverse;

--
-- TOC entry 344 (class 1259 OID 16932)
-- Name: setting; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.setting (
    id integer NOT NULL,
    content text,
    lang text,
    name text,
    CONSTRAINT non_empty_lang CHECK ((lang <> ''::text))
);


ALTER TABLE public.setting OWNER TO dataverse;

--
-- TOC entry 345 (class 1259 OID 16939)
-- Name: setting_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.setting_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.setting_id_seq OWNER TO dataverse;

--
-- TOC entry 4328 (class 0 OID 0)
-- Dependencies: 345
-- Name: setting_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.setting_id_seq OWNED BY public.setting.id;


--
-- TOC entry 346 (class 1259 OID 16941)
-- Name: setting_id_seq1; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.setting_id_seq1
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.setting_id_seq1 OWNER TO dataverse;

--
-- TOC entry 4329 (class 0 OID 0)
-- Dependencies: 346
-- Name: setting_id_seq1; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.setting_id_seq1 OWNED BY public.setting.id;


--
-- TOC entry 347 (class 1259 OID 16943)
-- Name: shibgroup; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.shibgroup (
    id integer NOT NULL,
    attribute character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    pattern character varying(255) NOT NULL
);


ALTER TABLE public.shibgroup OWNER TO dataverse;

--
-- TOC entry 348 (class 1259 OID 16949)
-- Name: shibgroup_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.shibgroup_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.shibgroup_id_seq OWNER TO dataverse;

--
-- TOC entry 4330 (class 0 OID 0)
-- Dependencies: 348
-- Name: shibgroup_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.shibgroup_id_seq OWNED BY public.shibgroup.id;


--
-- TOC entry 349 (class 1259 OID 16951)
-- Name: storagesite; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.storagesite (
    id integer NOT NULL,
    hostname text,
    name text,
    primarystorage boolean NOT NULL,
    transferprotocols text
);


ALTER TABLE public.storagesite OWNER TO dataverse;

--
-- TOC entry 350 (class 1259 OID 16957)
-- Name: storagesite_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.storagesite_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.storagesite_id_seq OWNER TO dataverse;

--
-- TOC entry 4331 (class 0 OID 0)
-- Dependencies: 350
-- Name: storagesite_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.storagesite_id_seq OWNED BY public.storagesite.id;


--
-- TOC entry 351 (class 1259 OID 16959)
-- Name: summarystatistic; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.summarystatistic (
    id integer NOT NULL,
    type integer,
    value character varying(255),
    datavariable_id bigint NOT NULL
);


ALTER TABLE public.summarystatistic OWNER TO dataverse;

--
-- TOC entry 352 (class 1259 OID 16962)
-- Name: summarystatistic_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.summarystatistic_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.summarystatistic_id_seq OWNER TO dataverse;

--
-- TOC entry 4332 (class 0 OID 0)
-- Dependencies: 352
-- Name: summarystatistic_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.summarystatistic_id_seq OWNED BY public.summarystatistic.id;


--
-- TOC entry 353 (class 1259 OID 16964)
-- Name: template; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.template (
    id integer NOT NULL,
    createtime timestamp without time zone NOT NULL,
    name character varying(255) NOT NULL,
    usagecount bigint,
    dataverse_id bigint,
    termsofuseandaccess_id bigint
);


ALTER TABLE public.template OWNER TO dataverse;

--
-- TOC entry 354 (class 1259 OID 16967)
-- Name: template_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.template_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.template_id_seq OWNER TO dataverse;

--
-- TOC entry 4333 (class 0 OID 0)
-- Dependencies: 354
-- Name: template_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.template_id_seq OWNED BY public.template.id;


--
-- TOC entry 355 (class 1259 OID 16969)
-- Name: termsofuseandaccess; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.termsofuseandaccess (
    id integer NOT NULL,
    availabilitystatus text,
    citationrequirements text,
    conditions text,
    confidentialitydeclaration text,
    contactforaccess text,
    dataaccessplace text,
    depositorrequirements text,
    disclaimer text,
    fileaccessrequest boolean,
    license character varying(255),
    originalarchive text,
    restrictions text,
    sizeofcollection text,
    specialpermissions text,
    studycompletion text,
    termsofaccess text,
    termsofuse text
);


ALTER TABLE public.termsofuseandaccess OWNER TO dataverse;

--
-- TOC entry 356 (class 1259 OID 16975)
-- Name: termsofuseandaccess_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.termsofuseandaccess_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.termsofuseandaccess_id_seq OWNER TO dataverse;

--
-- TOC entry 4334 (class 0 OID 0)
-- Dependencies: 356
-- Name: termsofuseandaccess_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.termsofuseandaccess_id_seq OWNED BY public.termsofuseandaccess.id;


--
-- TOC entry 357 (class 1259 OID 16977)
-- Name: userbannermessage; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.userbannermessage (
    id integer NOT NULL,
    bannerdismissaltime timestamp without time zone NOT NULL,
    bannermessage_id bigint NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE public.userbannermessage OWNER TO dataverse;

--
-- TOC entry 358 (class 1259 OID 16980)
-- Name: userbannermessage_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.userbannermessage_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.userbannermessage_id_seq OWNER TO dataverse;

--
-- TOC entry 4335 (class 0 OID 0)
-- Dependencies: 358
-- Name: userbannermessage_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.userbannermessage_id_seq OWNED BY public.userbannermessage.id;


--
-- TOC entry 359 (class 1259 OID 16982)
-- Name: usernotification; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.usernotification (
    id integer NOT NULL,
    emailed boolean,
    objectid bigint,
    readnotification boolean,
    senddate timestamp without time zone,
    type integer NOT NULL,
    requestor_id bigint,
    user_id bigint NOT NULL
);


ALTER TABLE public.usernotification OWNER TO dataverse;

--
-- TOC entry 360 (class 1259 OID 16985)
-- Name: usernotification_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.usernotification_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.usernotification_id_seq OWNER TO dataverse;

--
-- TOC entry 4336 (class 0 OID 0)
-- Dependencies: 360
-- Name: usernotification_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.usernotification_id_seq OWNED BY public.usernotification.id;


--
-- TOC entry 361 (class 1259 OID 16987)
-- Name: vargroup; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.vargroup (
    id integer NOT NULL,
    label text,
    filemetadata_id bigint NOT NULL
);


ALTER TABLE public.vargroup OWNER TO dataverse;

--
-- TOC entry 362 (class 1259 OID 16993)
-- Name: vargroup_datavariable; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.vargroup_datavariable (
    vargroup_id bigint NOT NULL,
    varsingroup_id bigint NOT NULL
);


ALTER TABLE public.vargroup_datavariable OWNER TO dataverse;

--
-- TOC entry 363 (class 1259 OID 16996)
-- Name: vargroup_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.vargroup_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.vargroup_id_seq OWNER TO dataverse;

--
-- TOC entry 4337 (class 0 OID 0)
-- Dependencies: 363
-- Name: vargroup_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.vargroup_id_seq OWNED BY public.vargroup.id;


--
-- TOC entry 364 (class 1259 OID 16998)
-- Name: variablecategory; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.variablecategory (
    id integer NOT NULL,
    catorder integer,
    frequency double precision,
    label character varying(255),
    missing boolean,
    value character varying(255),
    datavariable_id bigint NOT NULL
);


ALTER TABLE public.variablecategory OWNER TO dataverse;

--
-- TOC entry 365 (class 1259 OID 17004)
-- Name: variablecategory_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.variablecategory_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.variablecategory_id_seq OWNER TO dataverse;

--
-- TOC entry 4338 (class 0 OID 0)
-- Dependencies: 365
-- Name: variablecategory_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.variablecategory_id_seq OWNED BY public.variablecategory.id;


--
-- TOC entry 366 (class 1259 OID 17006)
-- Name: variablemetadata; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.variablemetadata (
    id integer NOT NULL,
    interviewinstruction text,
    isweightvar boolean,
    label text,
    literalquestion text,
    notes text,
    postquestion text,
    universe character varying(255),
    weighted boolean,
    datavariable_id bigint NOT NULL,
    filemetadata_id bigint NOT NULL,
    weightvariable_id bigint
);


ALTER TABLE public.variablemetadata OWNER TO dataverse;

--
-- TOC entry 367 (class 1259 OID 17012)
-- Name: variablemetadata_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.variablemetadata_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.variablemetadata_id_seq OWNER TO dataverse;

--
-- TOC entry 4339 (class 0 OID 0)
-- Dependencies: 367
-- Name: variablemetadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.variablemetadata_id_seq OWNED BY public.variablemetadata.id;


--
-- TOC entry 368 (class 1259 OID 17014)
-- Name: variablerange; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.variablerange (
    id integer NOT NULL,
    beginvalue character varying(255),
    beginvaluetype integer,
    endvalue character varying(255),
    endvaluetype integer,
    datavariable_id bigint NOT NULL
);


ALTER TABLE public.variablerange OWNER TO dataverse;

--
-- TOC entry 369 (class 1259 OID 17020)
-- Name: variablerange_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.variablerange_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.variablerange_id_seq OWNER TO dataverse;

--
-- TOC entry 4340 (class 0 OID 0)
-- Dependencies: 369
-- Name: variablerange_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.variablerange_id_seq OWNED BY public.variablerange.id;


--
-- TOC entry 370 (class 1259 OID 17022)
-- Name: variablerangeitem; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.variablerangeitem (
    id integer NOT NULL,
    value numeric(38,0),
    datavariable_id bigint NOT NULL
);


ALTER TABLE public.variablerangeitem OWNER TO dataverse;

--
-- TOC entry 371 (class 1259 OID 17025)
-- Name: variablerangeitem_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.variablerangeitem_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.variablerangeitem_id_seq OWNER TO dataverse;

--
-- TOC entry 4341 (class 0 OID 0)
-- Dependencies: 371
-- Name: variablerangeitem_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.variablerangeitem_id_seq OWNED BY public.variablerangeitem.id;


--
-- TOC entry 372 (class 1259 OID 17027)
-- Name: workflow; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.workflow (
    id integer NOT NULL,
    name character varying(255)
);


ALTER TABLE public.workflow OWNER TO dataverse;

--
-- TOC entry 373 (class 1259 OID 17030)
-- Name: workflow_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.workflow_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.workflow_id_seq OWNER TO dataverse;

--
-- TOC entry 4342 (class 0 OID 0)
-- Dependencies: 373
-- Name: workflow_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.workflow_id_seq OWNED BY public.workflow.id;


--
-- TOC entry 374 (class 1259 OID 17032)
-- Name: workflowcomment; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.workflowcomment (
    id integer NOT NULL,
    created timestamp without time zone NOT NULL,
    message text,
    type character varying(255) NOT NULL,
    authenticateduser_id bigint,
    datasetversion_id bigint NOT NULL,
    tobeshown boolean
);


ALTER TABLE public.workflowcomment OWNER TO dataverse;

--
-- TOC entry 375 (class 1259 OID 17038)
-- Name: workflowcomment_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.workflowcomment_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.workflowcomment_id_seq OWNER TO dataverse;

--
-- TOC entry 4343 (class 0 OID 0)
-- Dependencies: 375
-- Name: workflowcomment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.workflowcomment_id_seq OWNED BY public.workflowcomment.id;


--
-- TOC entry 376 (class 1259 OID 17040)
-- Name: workflowstepdata; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.workflowstepdata (
    id integer NOT NULL,
    providerid character varying(255),
    steptype character varying(255),
    parent_id bigint,
    index integer
);


ALTER TABLE public.workflowstepdata OWNER TO dataverse;

--
-- TOC entry 377 (class 1259 OID 17046)
-- Name: workflowstepdata_id_seq; Type: SEQUENCE; Schema: public; Owner: dataverse
--

CREATE SEQUENCE public.workflowstepdata_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.workflowstepdata_id_seq OWNER TO dataverse;

--
-- TOC entry 4344 (class 0 OID 0)
-- Dependencies: 377
-- Name: workflowstepdata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: dataverse
--

ALTER SEQUENCE public.workflowstepdata_id_seq OWNED BY public.workflowstepdata.id;


--
-- TOC entry 378 (class 1259 OID 17048)
-- Name: workflowstepdata_stepparameters; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.workflowstepdata_stepparameters (
    workflowstepdata_id bigint,
    stepparameters character varying(2048),
    stepparameters_key character varying(255)
);


ALTER TABLE public.workflowstepdata_stepparameters OWNER TO dataverse;

--
-- TOC entry 379 (class 1259 OID 17054)
-- Name: workflowstepdata_stepsettings; Type: TABLE; Schema: public; Owner: dataverse
--

CREATE TABLE public.workflowstepdata_stepsettings (
    workflowstepdata_id bigint,
    stepsettings character varying(2048),
    stepsettings_key character varying(255)
);


ALTER TABLE public.workflowstepdata_stepsettings OWNER TO dataverse;

--
-- TOC entry 3365 (class 2604 OID 17060)
-- Name: alternativepersistentidentifier id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.alternativepersistentidentifier ALTER COLUMN id SET DEFAULT nextval('public.alternativepersistentidentifier_id_seq'::regclass);


--
-- TOC entry 3366 (class 2604 OID 17061)
-- Name: apitoken id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.apitoken ALTER COLUMN id SET DEFAULT nextval('public.apitoken_id_seq'::regclass);


--
-- TOC entry 3367 (class 2604 OID 17062)
-- Name: authenticateduser id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticateduser ALTER COLUMN id SET DEFAULT nextval('public.authenticateduser_id_seq'::regclass);


--
-- TOC entry 3368 (class 2604 OID 17063)
-- Name: authenticateduserlookup id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticateduserlookup ALTER COLUMN id SET DEFAULT nextval('public.authenticateduserlookup_id_seq'::regclass);


--
-- TOC entry 3369 (class 2604 OID 17064)
-- Name: auxiliaryfile id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.auxiliaryfile ALTER COLUMN id SET DEFAULT nextval('public.auxiliaryfile_id_seq'::regclass);


--
-- TOC entry 3370 (class 2604 OID 17065)
-- Name: bannermessage id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.bannermessage ALTER COLUMN id SET DEFAULT nextval('public.bannermessage_id_seq'::regclass);


--
-- TOC entry 3371 (class 2604 OID 17066)
-- Name: bannermessagetext id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.bannermessagetext ALTER COLUMN id SET DEFAULT nextval('public.bannermessagetext_id_seq'::regclass);


--
-- TOC entry 3372 (class 2604 OID 17067)
-- Name: builtinuser id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.builtinuser ALTER COLUMN id SET DEFAULT nextval('public.builtinuser_id_seq'::regclass);


--
-- TOC entry 3373 (class 2604 OID 17068)
-- Name: categorymetadata id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.categorymetadata ALTER COLUMN id SET DEFAULT nextval('public.categorymetadata_id_seq'::regclass);


--
-- TOC entry 3374 (class 2604 OID 17069)
-- Name: clientharvestrun id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.clientharvestrun ALTER COLUMN id SET DEFAULT nextval('public.clientharvestrun_id_seq'::regclass);


--
-- TOC entry 3375 (class 2604 OID 17070)
-- Name: confirmemaildata id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.confirmemaildata ALTER COLUMN id SET DEFAULT nextval('public.confirmemaildata_id_seq'::regclass);


--
-- TOC entry 3376 (class 2604 OID 17071)
-- Name: controlledvocabalternate id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.controlledvocabalternate ALTER COLUMN id SET DEFAULT nextval('public.controlledvocabalternate_id_seq'::regclass);


--
-- TOC entry 3377 (class 2604 OID 17072)
-- Name: controlledvocabularyvalue id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.controlledvocabularyvalue ALTER COLUMN id SET DEFAULT nextval('public.controlledvocabularyvalue_id_seq'::regclass);


--
-- TOC entry 3378 (class 2604 OID 17073)
-- Name: customfieldmap id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customfieldmap ALTER COLUMN id SET DEFAULT nextval('public.customfieldmap_id_seq'::regclass);


--
-- TOC entry 3379 (class 2604 OID 17074)
-- Name: customquestion id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestion ALTER COLUMN id SET DEFAULT nextval('public.customquestion_id_seq'::regclass);


--
-- TOC entry 3380 (class 2604 OID 17075)
-- Name: customquestionresponse id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestionresponse ALTER COLUMN id SET DEFAULT nextval('public.customquestionresponse_id_seq'::regclass);


--
-- TOC entry 3381 (class 2604 OID 17076)
-- Name: customquestionvalue id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestionvalue ALTER COLUMN id SET DEFAULT nextval('public.customquestionvalue_id_seq'::regclass);


--
-- TOC entry 3382 (class 2604 OID 17077)
-- Name: datafilecategory id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datafilecategory ALTER COLUMN id SET DEFAULT nextval('public.datafilecategory_id_seq'::regclass);


--
-- TOC entry 3383 (class 2604 OID 17078)
-- Name: datafiletag id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datafiletag ALTER COLUMN id SET DEFAULT nextval('public.datafiletag_id_seq'::regclass);


--
-- TOC entry 3384 (class 2604 OID 17079)
-- Name: datasetfield id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfield ALTER COLUMN id SET DEFAULT nextval('public.datasetfield_id_seq'::regclass);


--
-- TOC entry 3385 (class 2604 OID 17080)
-- Name: datasetfieldcompoundvalue id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldcompoundvalue ALTER COLUMN id SET DEFAULT nextval('public.datasetfieldcompoundvalue_id_seq'::regclass);


--
-- TOC entry 3386 (class 2604 OID 17081)
-- Name: datasetfielddefaultvalue id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfielddefaultvalue ALTER COLUMN id SET DEFAULT nextval('public.datasetfielddefaultvalue_id_seq'::regclass);


--
-- TOC entry 3387 (class 2604 OID 17082)
-- Name: datasetfieldtype id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldtype ALTER COLUMN id SET DEFAULT nextval('public.datasetfieldtype_id_seq'::regclass);


--
-- TOC entry 3388 (class 2604 OID 17083)
-- Name: datasetfieldvalue id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldvalue ALTER COLUMN id SET DEFAULT nextval('public.datasetfieldvalue_id_seq'::regclass);


--
-- TOC entry 3389 (class 2604 OID 17084)
-- Name: datasetlinkingdataverse id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetlinkingdataverse ALTER COLUMN id SET DEFAULT nextval('public.datasetlinkingdataverse_id_seq'::regclass);


--
-- TOC entry 3390 (class 2604 OID 17085)
-- Name: datasetlock id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetlock ALTER COLUMN id SET DEFAULT nextval('public.datasetlock_id_seq'::regclass);


--
-- TOC entry 3391 (class 2604 OID 17086)
-- Name: datasetmetrics id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetmetrics ALTER COLUMN id SET DEFAULT nextval('public.datasetmetrics_id_seq'::regclass);


--
-- TOC entry 3392 (class 2604 OID 17087)
-- Name: datasetversion id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetversion ALTER COLUMN id SET DEFAULT nextval('public.datasetversion_id_seq'::regclass);


--
-- TOC entry 3393 (class 2604 OID 17088)
-- Name: datasetversionuser id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetversionuser ALTER COLUMN id SET DEFAULT nextval('public.datasetversionuser_id_seq'::regclass);


--
-- TOC entry 3394 (class 2604 OID 17089)
-- Name: datatable id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datatable ALTER COLUMN id SET DEFAULT nextval('public.datatable_id_seq'::regclass);


--
-- TOC entry 3395 (class 2604 OID 17090)
-- Name: datavariable id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datavariable ALTER COLUMN id SET DEFAULT nextval('public.datavariable_id_seq'::regclass);


--
-- TOC entry 3396 (class 2604 OID 17091)
-- Name: dataversecontact id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversecontact ALTER COLUMN id SET DEFAULT nextval('public.dataversecontact_id_seq'::regclass);


--
-- TOC entry 3397 (class 2604 OID 17092)
-- Name: dataversefacet id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefacet ALTER COLUMN id SET DEFAULT nextval('public.dataversefacet_id_seq'::regclass);


--
-- TOC entry 3398 (class 2604 OID 17093)
-- Name: dataversefeatureddataverse id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefeatureddataverse ALTER COLUMN id SET DEFAULT nextval('public.dataversefeatureddataverse_id_seq'::regclass);


--
-- TOC entry 3399 (class 2604 OID 17094)
-- Name: dataversefieldtypeinputlevel id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefieldtypeinputlevel ALTER COLUMN id SET DEFAULT nextval('public.dataversefieldtypeinputlevel_id_seq'::regclass);


--
-- TOC entry 3400 (class 2604 OID 17095)
-- Name: dataverselinkingdataverse id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverselinkingdataverse ALTER COLUMN id SET DEFAULT nextval('public.dataverselinkingdataverse_id_seq'::regclass);


--
-- TOC entry 3401 (class 2604 OID 17096)
-- Name: dataverserole id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverserole ALTER COLUMN id SET DEFAULT nextval('public.dataverserole_id_seq'::regclass);


--
-- TOC entry 3402 (class 2604 OID 17097)
-- Name: dataversetheme id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversetheme ALTER COLUMN id SET DEFAULT nextval('public.dataversetheme_id_seq'::regclass);


--
-- TOC entry 3403 (class 2604 OID 17098)
-- Name: defaultvalueset id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.defaultvalueset ALTER COLUMN id SET DEFAULT nextval('public.defaultvalueset_id_seq'::regclass);


--
-- TOC entry 3404 (class 2604 OID 17099)
-- Name: doidataciteregistercache id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.doidataciteregistercache ALTER COLUMN id SET DEFAULT nextval('public.doidataciteregistercache_id_seq'::regclass);


--
-- TOC entry 3405 (class 2604 OID 17100)
-- Name: dvobject id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dvobject ALTER COLUMN id SET DEFAULT nextval('public.dvobject_id_seq'::regclass);


--
-- TOC entry 3406 (class 2604 OID 17101)
-- Name: explicitgroup id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup ALTER COLUMN id SET DEFAULT nextval('public.explicitgroup_id_seq'::regclass);


--
-- TOC entry 3407 (class 2604 OID 17102)
-- Name: externaltool id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.externaltool ALTER COLUMN id SET DEFAULT nextval('public.externaltool_id_seq'::regclass);


--
-- TOC entry 3408 (class 2604 OID 17103)
-- Name: externaltooltype id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.externaltooltype ALTER COLUMN id SET DEFAULT nextval('public.externaltooltype_id_seq'::regclass);


--
-- TOC entry 3409 (class 2604 OID 17104)
-- Name: filemetadata id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.filemetadata ALTER COLUMN id SET DEFAULT nextval('public.filemetadata_id_seq'::regclass);


--
-- TOC entry 3411 (class 2604 OID 17105)
-- Name: foreignmetadatafieldmapping id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.foreignmetadatafieldmapping ALTER COLUMN id SET DEFAULT nextval('public.foreignmetadatafieldmapping_id_seq'::regclass);


--
-- TOC entry 3412 (class 2604 OID 17106)
-- Name: foreignmetadataformatmapping id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.foreignmetadataformatmapping ALTER COLUMN id SET DEFAULT nextval('public.foreignmetadataformatmapping_id_seq'::regclass);


--
-- TOC entry 3413 (class 2604 OID 17107)
-- Name: guestbook id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbook ALTER COLUMN id SET DEFAULT nextval('public.guestbook_id_seq'::regclass);


--
-- TOC entry 3414 (class 2604 OID 17108)
-- Name: guestbookresponse id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbookresponse ALTER COLUMN id SET DEFAULT nextval('public.guestbookresponse_id_seq'::regclass);


--
-- TOC entry 3415 (class 2604 OID 17109)
-- Name: harvestingclient id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.harvestingclient ALTER COLUMN id SET DEFAULT nextval('public.harvestingclient_id_seq'::regclass);


--
-- TOC entry 3416 (class 2604 OID 17110)
-- Name: ingestreport id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ingestreport ALTER COLUMN id SET DEFAULT nextval('public.ingestreport_id_seq'::regclass);


--
-- TOC entry 3417 (class 2604 OID 17111)
-- Name: ingestrequest id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ingestrequest ALTER COLUMN id SET DEFAULT nextval('public.ingestrequest_id_seq'::regclass);


--
-- TOC entry 3418 (class 2604 OID 17112)
-- Name: metadatablock id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.metadatablock ALTER COLUMN id SET DEFAULT nextval('public.metadatablock_id_seq'::regclass);


--
-- TOC entry 3419 (class 2604 OID 17113)
-- Name: metric id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.metric ALTER COLUMN id SET DEFAULT nextval('public.metric_id_seq'::regclass);


--
-- TOC entry 3420 (class 2604 OID 17114)
-- Name: oairecord id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.oairecord ALTER COLUMN id SET DEFAULT nextval('public.oairecord_id_seq'::regclass);


--
-- TOC entry 3421 (class 2604 OID 17115)
-- Name: oaiset id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.oaiset ALTER COLUMN id SET DEFAULT nextval('public.oaiset_id_seq'::regclass);


--
-- TOC entry 3422 (class 2604 OID 17116)
-- Name: oauth2tokendata id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.oauth2tokendata ALTER COLUMN id SET DEFAULT nextval('public.oauth2tokendata_id_seq'::regclass);


--
-- TOC entry 3423 (class 2604 OID 17117)
-- Name: passwordresetdata id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.passwordresetdata ALTER COLUMN id SET DEFAULT nextval('public.passwordresetdata_id_seq'::regclass);


--
-- TOC entry 3425 (class 2604 OID 17118)
-- Name: roleassignment id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.roleassignment ALTER COLUMN id SET DEFAULT nextval('public.roleassignment_id_seq'::regclass);


--
-- TOC entry 3426 (class 2604 OID 17119)
-- Name: savedsearch id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.savedsearch ALTER COLUMN id SET DEFAULT nextval('public.savedsearch_id_seq'::regclass);


--
-- TOC entry 3427 (class 2604 OID 17120)
-- Name: savedsearchfilterquery id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.savedsearchfilterquery ALTER COLUMN id SET DEFAULT nextval('public.savedsearchfilterquery_id_seq'::regclass);


--
-- TOC entry 3428 (class 2604 OID 17121)
-- Name: setting id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.setting ALTER COLUMN id SET DEFAULT nextval('public.setting_id_seq'::regclass);


--
-- TOC entry 3430 (class 2604 OID 17122)
-- Name: shibgroup id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.shibgroup ALTER COLUMN id SET DEFAULT nextval('public.shibgroup_id_seq'::regclass);


--
-- TOC entry 3431 (class 2604 OID 17123)
-- Name: storagesite id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.storagesite ALTER COLUMN id SET DEFAULT nextval('public.storagesite_id_seq'::regclass);


--
-- TOC entry 3432 (class 2604 OID 17124)
-- Name: summarystatistic id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.summarystatistic ALTER COLUMN id SET DEFAULT nextval('public.summarystatistic_id_seq'::regclass);


--
-- TOC entry 3433 (class 2604 OID 17125)
-- Name: template id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.template ALTER COLUMN id SET DEFAULT nextval('public.template_id_seq'::regclass);


--
-- TOC entry 3434 (class 2604 OID 17126)
-- Name: termsofuseandaccess id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.termsofuseandaccess ALTER COLUMN id SET DEFAULT nextval('public.termsofuseandaccess_id_seq'::regclass);


--
-- TOC entry 3435 (class 2604 OID 17127)
-- Name: userbannermessage id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.userbannermessage ALTER COLUMN id SET DEFAULT nextval('public.userbannermessage_id_seq'::regclass);


--
-- TOC entry 3436 (class 2604 OID 17128)
-- Name: usernotification id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.usernotification ALTER COLUMN id SET DEFAULT nextval('public.usernotification_id_seq'::regclass);


--
-- TOC entry 3437 (class 2604 OID 17129)
-- Name: vargroup id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.vargroup ALTER COLUMN id SET DEFAULT nextval('public.vargroup_id_seq'::regclass);


--
-- TOC entry 3438 (class 2604 OID 17130)
-- Name: variablecategory id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablecategory ALTER COLUMN id SET DEFAULT nextval('public.variablecategory_id_seq'::regclass);


--
-- TOC entry 3439 (class 2604 OID 17131)
-- Name: variablemetadata id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablemetadata ALTER COLUMN id SET DEFAULT nextval('public.variablemetadata_id_seq'::regclass);


--
-- TOC entry 3440 (class 2604 OID 17132)
-- Name: variablerange id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablerange ALTER COLUMN id SET DEFAULT nextval('public.variablerange_id_seq'::regclass);


--
-- TOC entry 3441 (class 2604 OID 17133)
-- Name: variablerangeitem id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablerangeitem ALTER COLUMN id SET DEFAULT nextval('public.variablerangeitem_id_seq'::regclass);


--
-- TOC entry 3442 (class 2604 OID 17134)
-- Name: workflow id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflow ALTER COLUMN id SET DEFAULT nextval('public.workflow_id_seq'::regclass);


--
-- TOC entry 3443 (class 2604 OID 17135)
-- Name: workflowcomment id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflowcomment ALTER COLUMN id SET DEFAULT nextval('public.workflowcomment_id_seq'::regclass);


--
-- TOC entry 3444 (class 2604 OID 17136)
-- Name: workflowstepdata id; Type: DEFAULT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflowstepdata ALTER COLUMN id SET DEFAULT nextval('public.workflowstepdata_id_seq'::regclass);


--
-- TOC entry 4077 (class 0 OID 16385)
-- Dependencies: 196
-- Data for Name: EJB__TIMER__TBL; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public."EJB__TIMER__TBL" ("TIMERID", "APPLICATIONID", "BLOB", "CONTAINERID", "CREATIONTIMERAW", "INITIALEXPIRATIONRAW", "INTERVALDURATION", "LASTEXPIRATIONRAW", "OWNERID", "PKHASHCODE", "SCHEDULE", "STATE") FROM stdin;
1@@1618997990903@@server@@production	106102650051035136	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106102650051099930	1618997990905	1619310600000	0	0	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
2@@1618997990903@@server@@production	106102650051035136	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106102650051056091	1618997990963	1618998600962	3600000	0	server	0	\N	0
3@@1618997990903@@server@@production	106102650051035136	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106102650051056091	1618997990967	1619056800967	86400000	0	server	0	\N	0
1@@1619076824898@@server@@production	106107816700805120	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106107816700869914	1619076824900	1619310600000	0	0	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
1@@1619599206376@@server@@production	106142051117629440	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106142051117694234	1619599206380	1619915400000	0	1620520200008	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
2@@1619109847762@@server@@production	106107816700805120	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106107816700826075	1619109847775	1619143200774	86400000	0	server	0	\N	0
1@@1619109847762@@server@@production	106107816700805120	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106107816700826075	1619109847763	1619110200761	3600000	0	server	0	\N	0
1@@1618559066625@@server@@production	105898209768636416	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	105898209768657371	1618559066625	1618559400624	3600000	0	server	0	\N	0
2@@1618559066625@@server@@production	105898209768636416	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	105898209768657371	1618559066637	1618624800637	86400000	0	server	0	\N	0
1@@1615878478026@@server@@production	105898209768636416	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	105898209768701210	1615878478029	1616286600000	0	1618705800016	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
1@@1619509498125@@server@@production	106136172324978688	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106136172325043482	1619509498127	1619915400000	0	0	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
3@@1619509498125@@server@@production	106136172324978688	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106136172324999643	1619509498189	1619575200189	86400000	0	server	0	\N	0
2@@1619509498125@@server@@production	106136172324978688	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106136172324999643	1619509498185	1619509800183	3600000	0	server	0	\N	0
2@@1620746807515@@server@@production	106217260554256384	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106217260554277339	1620746807581	1620748200580	3600000	0	server	0	\N	0
3@@1620746807515@@server@@production	106217260554256384	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106217260554277339	1620746807585	1620784800585	86400000	0	server	0	\N	0
1@@1620746536816@@server@@production	106142051117629440	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106142051117650395	1620746536816	1620748200815	3600000	0	server	0	\N	0
2@@1620746536816@@server@@production	106142051117629440	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106142051117650395	1620746536830	1620784800829	86400000	0	server	0	\N	0
1@@1620746807515@@server@@production	106217260554256384	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106217260554321178	1620746807517	1621125000000	0	0	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
1@@1620749306332@@server@@production	106217424350871552	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106217424350936346	1620749306334	1621125000000	0	0	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
3@@1620749306332@@server@@production	106217424350871552	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106217424350892507	1620749306703	1620784800702	86400000	0	server	0	\N	0
2@@1620749306332@@server@@production	106217424350871552	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106217424350892507	1620749306698	1620751800697	3600000	0	server	0	\N	0
1@@1620749966214@@server@@production	106217467652603904	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106217467652668698	1620749966216	1621125000000	0	0	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
2@@1620749966214@@server@@production	106217467652603904	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106217467652624859	1620749966585	1620751800584	3600000	0	server	0	\N	0
3@@1620749966214@@server@@production	106217467652603904	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106217467652624859	1620749966590	1620784800589	86400000	0	server	0	\N	0
3@@1620800047709@@server@@production	106220749725696000	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106220749725716955	1620800047777	1620871200776	86400000	0	server	0	\N	0
2@@1620800047709@@server@@production	106220749725696000	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106220749725716955	1620800047772	1620802200771	3600000	0	server	0	\N	0
1@@1620800047709@@server@@production	106220749725696000	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106220749725760794	1620800047711	1621125000000	0	1621729800008	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
4@@1621932391866@@server@@production	106294958881439744	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106294958881504538	1621933048507	1622334600000	0	0	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
5@@1621932391866@@server@@production	106294958881439744	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106294958881460699	1621933048527	1621936200526	3600000	0	server	0	\N	0
6@@1621932391866@@server@@production	106294958881439744	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106294958881460699	1621933048529	1621994400528	86400000	0	server	0	\N	0
2@@1622209444843@@server@@production	106294958881570816	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106294958881591771	1622209444856	1622253600855	86400000	0	server	0	\N	0
1@@1622209444843@@server@@production	106294958881570816	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106294958881591771	1622209444843	1622209800842	3600000	0	server	0	\N	0
3@@1622214811139@@server@@production	106313467443740672	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4578706f727454696d6572496e666f0bf7b87a75b4093b0200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106313467443761627	1622214811216	1622253600216	86400000	0	server	0	\N	0
2@@1622214811139@@server@@production	106313467443740672	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e00017870757200025b42acf317f8060854e0020000787000000069aced00057372002e6564752e686172766172642e69712e6461746176657273652e74696d65722e4d6f7468657254696d6572496e666fa60b9505b0fae3450200014c000873657276657249647400124c6a6176612f6c616e672f537472696e673b770300013a78707070	106313467443761627	1622214811212	1622217000211	3600000	0	server	0	\N	0
1@@1622214811139@@server@@production	106313467443740672	\\xaced0005737200326f72672e676c617373666973682e656a622e70657273697374656e742e74696d65722e54696d6572537461746524426c6f6245b42025117023f80200025b000a696e666f42797465735f7400025b425b00107072696d6172794b657942797465735f71007e000178707070	106313467443805466	1622214811141	1622334600000	0	1622939400009	server	0	0 # 30 # 0 # * # * # 0 # * # null # null # null # true # makeLinksForAllSavedSearchesTimer # 0	0
\.


--
-- TOC entry 4078 (class 0 OID 16391)
-- Dependencies: 197
-- Data for Name: actionlogrecord; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.actionlogrecord (id, actionresult, actionsubtype, actiontype, endtime, info, starttime, useridentifier) FROM stdin;
\.


--
-- TOC entry 4079 (class 0 OID 16397)
-- Dependencies: 198
-- Data for Name: alternativepersistentidentifier; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.alternativepersistentidentifier (id, authority, globalidcreatetime, identifier, identifierregistered, protocol, storagelocationdesignator, dvobject_id) FROM stdin;
\.


--
-- TOC entry 4081 (class 0 OID 16405)
-- Dependencies: 200
-- Data for Name: apitoken; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.apitoken (id, createtime, disabled, expiretime, tokenstring, authenticateduser_id) FROM stdin;
1	2021-03-16 07:08:47.511	f	2022-03-16 07:08:47.511	55ce2276-3a16-47e1-9064-09514e5eb80e	1
\.


--
-- TOC entry 4083 (class 0 OID 16410)
-- Dependencies: 202
-- Data for Name: authenticateduser; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.authenticateduser (id, affiliation, createdtime, email, emailconfirmed, firstname, lastapiusetime, lastlogintime, lastname, "position", superuser, useridentifier, deactivated, deactivatedtime) FROM stdin;
1	Dataverse.org	2021-03-16 07:08:47.455	dataverse@mailinator.com	\N	Dataverse	2021-06-09 09:58:37.941	2021-07-12 14:54:28.886	Admin	Admin	f	dataverseAdmin	f	\N
\.


--
-- TOC entry 4085 (class 0 OID 16418)
-- Dependencies: 204
-- Data for Name: authenticateduserlookup; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.authenticateduserlookup (id, authenticationproviderid, persistentuserid, authenticateduser_id) FROM stdin;
1	builtin	dataverseAdmin	1
\.


--
-- TOC entry 4087 (class 0 OID 16426)
-- Dependencies: 206
-- Data for Name: authenticationproviderrow; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.authenticationproviderrow (id, enabled, factoryalias, factorydata, subtitle, title) FROM stdin;
builtin	t	BuiltinAuthenticationProvider		Datavers' Internal Authentication provider	Dataverse Local
\.


--
-- TOC entry 4088 (class 0 OID 16432)
-- Dependencies: 207
-- Data for Name: auxiliaryfile; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.auxiliaryfile (id, checksum, contenttype, filesize, formattag, formatversion, ispublic, origin, type, datafile_id) FROM stdin;
\.


--
-- TOC entry 4090 (class 0 OID 16440)
-- Dependencies: 209
-- Data for Name: bannermessage; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.bannermessage (id, active, dismissiblebyuser) FROM stdin;
\.


--
-- TOC entry 4092 (class 0 OID 16445)
-- Dependencies: 211
-- Data for Name: bannermessagetext; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.bannermessagetext (id, lang, message, bannermessage_id) FROM stdin;
\.


--
-- TOC entry 4094 (class 0 OID 16453)
-- Dependencies: 213
-- Data for Name: builtinuser; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.builtinuser (id, encryptedpassword, passwordencryptionversion, username) FROM stdin;
1	$2a$10$HTmQEw7IhiBIr0Ww.Gu51ePl218vVEej38pSRyllFTqHmM9pEi2Tu	1	dataverseAdmin
\.


--
-- TOC entry 4096 (class 0 OID 16461)
-- Dependencies: 215
-- Data for Name: categorymetadata; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.categorymetadata (id, wfreq, category_id, variablemetadata_id) FROM stdin;
\.


--
-- TOC entry 4098 (class 0 OID 16466)
-- Dependencies: 217
-- Data for Name: clientharvestrun; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.clientharvestrun (id, deleteddatasetcount, faileddatasetcount, finishtime, harvestresult, harvesteddatasetcount, starttime, harvestingclient_id) FROM stdin;
\.


--
-- TOC entry 4100 (class 0 OID 16471)
-- Dependencies: 219
-- Data for Name: confirmemaildata; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.confirmemaildata (id, created, expires, token, authenticateduser_id) FROM stdin;
1	2021-03-16 07:08:47.492	2021-03-17 07:08:47.492	3e7a1c36-b89d-4bab-b014-bfe93800404d	1
\.


--
-- TOC entry 4102 (class 0 OID 16476)
-- Dependencies: 221
-- Data for Name: controlledvocabalternate; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.controlledvocabalternate (id, strvalue, controlledvocabularyvalue_id, datasetfieldtype_id) FROM stdin;
1	BOTSWANA	272	80
2	Brasil	274	80
3	Gambia, The	323	80
4	Germany (Federal Republic of)	325	80
5	GHANA	326	80
6	INDIA	345	80
7	Sumatra	346	80
8	Iran (Islamic Republic of)	347	80
9	Iran	347	80
10	IRAQ	348	80
11	Laos	364	80
12	LESOTHO	367	80
13	MOZAMBIQUE	394	80
14	NAMIBIA	396	80
15	SWAZILAND	456	80
16	Taiwan	460	80
17	Tanzania	462	80
18	UAE	476	80
19	U.S.A.	478	80
20	USA	478	80
21	U.S.A	478	80
22	United States of America	478	80
23	YEMEN	489	80
\.


--
-- TOC entry 4104 (class 0 OID 16484)
-- Dependencies: 223
-- Data for Name: controlledvocabularyvalue; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.controlledvocabularyvalue (id, displayorder, identifier, strvalue, datasetfieldtype_id) FROM stdin;
1	0	\N	N/A	\N
2	0	D01	Agricultural Sciences	20
3	1	D0	Arts and Humanities	20
4	2	D1	Astronomy and Astrophysics	20
5	3	D2	Business and Management	20
6	4	D3	Chemistry	20
7	5	D7	Computer and Information Science	20
8	6	D4	Earth and Environmental Sciences	20
9	7	D5	Engineering	20
10	8	D8	Law	20
11	9	D9	Mathematical Sciences	20
12	10	D6	Medicine, Health and Life Sciences	20
13	11	D10	Physics	20
14	12	D11	Social Sciences	20
15	13	D12	Other	20
16	0		ark	31
17	1		arXiv	31
18	2		bibcode	31
19	3		doi	31
20	4		ean13	31
21	5		eissn	31
22	6		handle	31
23	7		isbn	31
24	8		issn	31
25	9		istc	31
26	10		lissn	31
27	11		lsid	31
28	12		pmid	31
29	13		purl	31
30	14		upc	31
31	15		url	31
32	16		urn	31
33	0		Data Collector	45
34	1		Data Curator	45
35	2		Data Manager	45
36	3		Editor	45
37	4		Funder	45
38	5		Hosting Institution	45
39	6		Project Leader	45
40	7		Project Manager	45
41	8		Project Member	45
42	9		Related Person	45
43	10		Researcher	45
44	11		Research Group	45
45	12		Rights Holder	45
46	13		Sponsor	45
47	14		Supervisor	45
48	15		Work Package Leader	45
49	16		Other	45
50	0		ORCID	11
51	1		ISNI	11
52	2		LCNA	11
53	3		VIAF	11
54	4		GND	11
55	5		DAI	11
56	6		ResearcherID	11
57	7		ScopusID	11
58	0		Abkhaz	35
59	1		Afar	35
60	2		Afrikaans	35
61	3		Akan	35
62	4		Albanian	35
63	5		Amharic	35
64	6		Arabic	35
65	7		Aragonese	35
66	8		Armenian	35
67	9		Assamese	35
68	10		Avaric	35
69	11		Avestan	35
70	12		Aymara	35
71	13		Azerbaijani	35
72	14		Bambara	35
73	15		Bashkir	35
74	16		Basque	35
75	17		Belarusian	35
76	18		Bengali, Bangla	35
77	19		Bihari	35
78	20		Bislama	35
79	21		Bosnian	35
80	22		Breton	35
81	23		Bulgarian	35
82	24		Burmese	35
83	25		Catalan,Valencian	35
84	26		Chamorro	35
85	27		Chechen	35
86	28		Chichewa, Chewa, Nyanja	35
87	29		Chinese	35
88	30		Chuvash	35
89	31		Cornish	35
90	32		Corsican	35
91	33		Cree	35
92	34		Croatian	35
93	35		Czech	35
94	36		Danish	35
95	37		Divehi, Dhivehi, Maldivian	35
96	38		Dutch	35
97	39		Dzongkha	35
98	40		English	35
99	41		Esperanto	35
100	42		Estonian	35
101	43		Ewe	35
102	44		Faroese	35
103	45		Fijian	35
104	46		Finnish	35
105	47		French	35
106	48		Fula, Fulah, Pulaar, Pular	35
107	49		Galician	35
108	50		Georgian	35
109	51		German	35
110	52		Greek (modern)	35
111	53		Guaraní	35
112	54		Gujarati	35
113	55		Haitian, Haitian Creole	35
114	56		Hausa	35
115	57		Hebrew (modern)	35
116	58		Herero	35
117	59		Hindi	35
118	60		Hiri Motu	35
119	61		Hungarian	35
120	62		Interlingua	35
121	63		Indonesian	35
122	64		Interlingue	35
123	65		Irish	35
124	66		Igbo	35
125	67		Inupiaq	35
126	68		Ido	35
127	69		Icelandic	35
128	70		Italian	35
129	71		Inuktitut	35
130	72		Japanese	35
131	73		Javanese	35
132	74		Kalaallisut, Greenlandic	35
133	75		Kannada	35
134	76		Kanuri	35
135	77		Kashmiri	35
136	78		Kazakh	35
137	79		Khmer	35
138	80		Kikuyu, Gikuyu	35
139	81		Kinyarwanda	35
140	82		Kyrgyz	35
141	83		Komi	35
142	84		Kongo	35
143	85		Korean	35
144	86		Kurdish	35
145	87		Kwanyama, Kuanyama	35
146	88		Latin	35
147	89		Luxembourgish, Letzeburgesch	35
148	90		Ganda	35
149	91		Limburgish, Limburgan, Limburger	35
150	92		Lingala	35
151	93		Lao	35
152	94		Lithuanian	35
153	95		Luba-Katanga	35
154	96		Latvian	35
155	97		Manx	35
156	98		Macedonian	35
157	99		Malagasy	35
158	100		Malay	35
159	101		Malayalam	35
160	102		Maltese	35
161	103		Māori	35
162	104		Marathi (Marāṭhī)	35
163	105		Marshallese	35
164	106		Mixtepec Mixtec	35
165	107		Mongolian	35
166	108		Nauru	35
167	109		Navajo, Navaho	35
168	110		Northern Ndebele	35
169	111		Nepali	35
170	112		Ndonga	35
171	113		Norwegian Bokmål	35
172	114		Norwegian Nynorsk	35
173	115		Norwegian	35
174	116		Nuosu	35
175	117		Southern Ndebele	35
176	118		Occitan	35
177	119		Ojibwe, Ojibwa	35
178	120		Old Church Slavonic,Church Slavonic,Old Bulgarian	35
179	121		Oromo	35
180	122		Oriya	35
181	123		Ossetian, Ossetic	35
182	124		Panjabi, Punjabi	35
183	125		Pāli	35
184	126		Persian (Farsi)	35
185	127		Polish	35
186	128		Pashto, Pushto	35
187	129		Portuguese	35
188	130		Quechua	35
189	131		Romansh	35
190	132		Kirundi	35
191	133		Romanian	35
192	134		Russian	35
193	135		Sanskrit (Saṁskṛta)	35
194	136		Sardinian	35
195	137		Sindhi	35
196	138		Northern Sami	35
197	139		Samoan	35
198	140		Sango	35
199	141		Serbian	35
200	142		Scottish Gaelic, Gaelic	35
201	143		Shona	35
202	144		Sinhala, Sinhalese	35
203	145		Slovak	35
204	146		Slovene	35
205	147		Somali	35
206	148		Southern Sotho	35
207	149		Spanish, Castilian	35
208	150		Sundanese	35
209	151		Swahili	35
210	152		Swati	35
211	153		Swedish	35
212	154		Tamil	35
213	155		Telugu	35
214	156		Tajik	35
215	157		Thai	35
216	158		Tigrinya	35
217	159		Tibetan Standard, Tibetan, Central	35
218	160		Turkmen	35
219	161		Tagalog	35
220	162		Tswana	35
221	163		Tonga (Tonga Islands)	35
222	164		Turkish	35
223	165		Tsonga	35
224	166		Tatar	35
225	167		Twi	35
226	168		Tahitian	35
227	169		Uyghur, Uighur	35
228	170		Ukrainian	35
229	171		Urdu	35
230	172		Uzbek	35
231	173		Venda	35
232	174		Vietnamese	35
233	175		Volapük	35
234	176		Walloon	35
235	177		Welsh	35
236	178		Wolof	35
237	179		Western Frisian	35
238	180		Xhosa	35
239	181		Yiddish	35
240	182		Yoruba	35
241	183		Zhuang, Chuang	35
242	184		Zulu	35
243	185		Not applicable	35
244	0		Afghanistan	80
245	1		Albania	80
246	2		Algeria	80
247	3		American Samoa	80
248	4		Andorra	80
249	5		Angola	80
250	6		Anguilla	80
251	7		Antarctica	80
252	8		Antigua and Barbuda	80
253	9		Argentina	80
254	10		Armenia	80
255	11		Aruba	80
256	12		Australia	80
257	13		Austria	80
258	14		Azerbaijan	80
259	15		Bahamas	80
260	16		Bahrain	80
261	17		Bangladesh	80
262	18		Barbados	80
263	19		Belarus	80
264	20		Belgium	80
265	21		Belize	80
266	22		Benin	80
267	23		Bermuda	80
268	24		Bhutan	80
269	25		Bolivia, Plurinational State of	80
270	26		Bonaire, Sint Eustatius and Saba	80
271	27		Bosnia and Herzegovina	80
272	28		Botswana	80
273	29		Bouvet Island	80
274	30		Brazil	80
410	166		Pakistan	80
275	31		British Indian Ocean Territory	80
276	32		Brunei Darussalam	80
277	33		Bulgaria	80
278	34		Burkina Faso	80
279	35		Burundi	80
280	36		Cambodia	80
281	37		Cameroon	80
282	38		Canada	80
283	39		Cape Verde	80
284	40		Cayman Islands	80
285	41		Central African Republic	80
286	42		Chad	80
287	43		Chile	80
288	44		China	80
289	45		Christmas Island	80
290	46		Cocos (Keeling) Islands	80
291	47		Colombia	80
292	48		Comoros	80
293	49		Congo	80
294	50		Congo, the Democratic Republic of the	80
295	51		Cook Islands	80
296	52		Costa Rica	80
297	53		Croatia	80
298	54		Cuba	80
299	55		Curaçao	80
300	56		Cyprus	80
301	57		Czech Republic	80
302	58		Côte d'Ivoire	80
303	59		Denmark	80
304	60		Djibouti	80
305	61		Dominica	80
306	62		Dominican Republic	80
307	63		Ecuador	80
308	64		Egypt	80
309	65		El Salvador	80
310	66		Equatorial Guinea	80
311	67		Eritrea	80
312	68		Estonia	80
313	69		Ethiopia	80
314	70		Falkland Islands (Malvinas)	80
315	71		Faroe Islands	80
316	72		Fiji	80
317	73		Finland	80
318	74		France	80
319	75		French Guiana	80
320	76		French Polynesia	80
321	77		French Southern Territories	80
322	78		Gabon	80
323	79		Gambia	80
324	80		Georgia	80
325	81		Germany	80
326	82		Ghana	80
327	83		Gibraltar	80
328	84		Greece	80
329	85		Greenland	80
330	86		Grenada	80
331	87		Guadeloupe	80
332	88		Guam	80
333	89		Guatemala	80
334	90		Guernsey	80
335	91		Guinea	80
336	92		Guinea-Bissau	80
337	93		Guyana	80
338	94		Haiti	80
339	95		Heard Island and Mcdonald Islands	80
340	96		Holy See (Vatican City State)	80
341	97		Honduras	80
342	98		Hong Kong	80
343	99		Hungary	80
344	100		Iceland	80
345	101		India	80
346	102		Indonesia	80
347	103		Iran, Islamic Republic of	80
348	104		Iraq	80
349	105		Ireland	80
350	106		Isle of Man	80
351	107		Israel	80
352	108		Italy	80
353	109		Jamaica	80
354	110		Japan	80
355	111		Jersey	80
356	112		Jordan	80
357	113		Kazakhstan	80
358	114		Kenya	80
359	115		Kiribati	80
360	116		Korea, Democratic People's Republic of	80
361	117		Korea, Republic of	80
362	118		Kuwait	80
363	119		Kyrgyzstan	80
364	120		Lao People's Democratic Republic	80
365	121		Latvia	80
366	122		Lebanon	80
367	123		Lesotho	80
368	124		Liberia	80
369	125		Libya	80
370	126		Liechtenstein	80
371	127		Lithuania	80
372	128		Luxembourg	80
373	129		Macao	80
374	130		Macedonia, the Former Yugoslav Republic of	80
375	131		Madagascar	80
376	132		Malawi	80
377	133		Malaysia	80
378	134		Maldives	80
379	135		Mali	80
380	136		Malta	80
381	137		Marshall Islands	80
382	138		Martinique	80
383	139		Mauritania	80
384	140		Mauritius	80
385	141		Mayotte	80
386	142		Mexico	80
387	143		Micronesia, Federated States of	80
388	144		Moldova, Republic of	80
389	145		Monaco	80
390	146		Mongolia	80
391	147		Montenegro	80
392	148		Montserrat	80
393	149		Morocco	80
394	150		Mozambique	80
395	151		Myanmar	80
396	152		Namibia	80
397	153		Nauru	80
398	154		Nepal	80
399	155		Netherlands	80
400	156		New Caledonia	80
401	157		New Zealand	80
402	158		Nicaragua	80
403	159		Niger	80
404	160		Nigeria	80
405	161		Niue	80
406	162		Norfolk Island	80
407	163		Northern Mariana Islands	80
408	164		Norway	80
409	165		Oman	80
411	167		Palau	80
412	168		Palestine, State of	80
413	169		Panama	80
414	170		Papua New Guinea	80
415	171		Paraguay	80
416	172		Peru	80
417	173		Philippines	80
418	174		Pitcairn	80
419	175		Poland	80
420	176		Portugal	80
421	177		Puerto Rico	80
422	178		Qatar	80
423	179		Romania	80
424	180		Russian Federation	80
425	181		Rwanda	80
426	182		Réunion	80
427	183		Saint Barthélemy	80
428	184		Saint Helena, Ascension and Tristan da Cunha	80
429	185		Saint Kitts and Nevis	80
430	186		Saint Lucia	80
431	187		Saint Martin (French part)	80
432	188		Saint Pierre and Miquelon	80
433	189		Saint Vincent and the Grenadines	80
434	190		Samoa	80
435	191		San Marino	80
436	192		Sao Tome and Principe	80
437	193		Saudi Arabia	80
438	194		Senegal	80
439	195		Serbia	80
440	196		Seychelles	80
441	197		Sierra Leone	80
442	198		Singapore	80
443	199		Sint Maarten (Dutch part)	80
444	200		Slovakia	80
445	201		Slovenia	80
446	202		Solomon Islands	80
447	203		Somalia	80
448	204		South Africa	80
449	205		South Georgia and the South Sandwich Islands	80
450	206		South Sudan	80
451	207		Spain	80
452	208		Sri Lanka	80
453	209		Sudan	80
454	210		Suriname	80
455	211		Svalbard and Jan Mayen	80
456	212		Swaziland	80
457	213		Sweden	80
458	214		Switzerland	80
459	215		Syrian Arab Republic	80
460	216		Taiwan, Province of China	80
461	217		Tajikistan	80
462	218		Tanzania, United Republic of	80
463	219		Thailand	80
464	220		Timor-Leste	80
465	221		Togo	80
466	222		Tokelau	80
467	223		Tonga	80
468	224		Trinidad and Tobago	80
469	225		Tunisia	80
470	226		Turkey	80
471	227		Turkmenistan	80
472	228		Turks and Caicos Islands	80
473	229		Tuvalu	80
474	230		Uganda	80
475	231		Ukraine	80
476	232		United Arab Emirates	80
477	233		United Kingdom	80
478	234		United States	80
479	235		United States Minor Outlying Islands	80
480	236		Uruguay	80
481	237		Uzbekistan	80
482	238		Vanuatu	80
483	239		Venezuela, Bolivarian Republic of	80
484	240		Viet Nam	80
485	241		Virgin Islands, British	80
486	242		Virgin Islands, U.S.	80
487	243		Wallis and Futuna	80
488	244		Western Sahara	80
489	245		Yemen	80
490	246		Zambia	80
491	247		Zimbabwe	80
492	248		Åland Islands	80
493	0		Image	116
494	1		Mosaic	116
495	2		EventList	116
496	3		Spectrum	116
497	4		Cube	116
498	5		Table	116
499	6		Catalog	116
500	7		LightCurve	116
501	8		Simulation	116
502	9		Figure	116
503	10		Artwork	116
504	11		Animation	116
505	12		PrettyPicture	116
506	13		Documentation	116
507	14		Other	116
508	15		Library	116
509	16		Press Release	116
510	17		Facsimile	116
511	18		Historical	116
512	19		Observation	116
513	20		Object	116
514	21		Value	116
515	22		ValuePair	116
516	23		Survey	116
517	0	EFO_0001427	Case Control	142
518	1	EFO_0001428	Cross Sectional	142
519	2	OCRE100078	Cohort Study	142
520	3	NCI_C48202	Nested Case Control Design	142
521	4	OTHER_DESIGN	Not Specified	142
522	5	OBI_0500006	Parallel Group Design	142
523	6	OBI_0001033	Perturbation Design	142
524	7	MESH_D016449	Randomized Controlled Trial	142
525	8	TECH_DESIGN	Technological Design	142
526	0	EFO_0000246	Age	143
527	1	BIOMARKERS	Biomarkers	143
528	2	CELL_SURFACE_M	Cell Surface Markers	143
529	3	EFO_0000324;EFO_0000322	Cell Type/Cell Line	143
530	4	EFO_0000399	Developmental Stage	143
531	5	OBI_0001293	Disease State	143
532	6	IDO_0000469	Drug Susceptibility	143
533	7	FBcv_0010001	Extract Molecule	143
534	8	OBI_0001404	Genetic Characteristics	143
535	9	OBI_0000690	Immunoprecipitation Antibody	143
536	10	OBI_0100026	Organism	143
537	11	OTHER_FACTOR	Other	143
538	12	PASSAGES_FACTOR	Passages	143
539	13	OBI_0000050	Platform	143
540	14	EFO_0000695	Sex	143
541	15	EFO_0005135	Strain	143
542	16	EFO_0000724	Time Point	143
543	17	BTO_0001384	Tissue Type	143
544	18	EFO_0000369	Treatment Compound	143
545	19	EFO_0000727	Treatment Type	143
546	0	ERO_0001899	cell counting	146
547	1	CHMO_0001085	cell sorting	146
548	2	OBI_0000520	clinical chemistry analysis	146
549	3	OBI_0000537	copy number variation profiling	146
550	4	OBI_0000634	DNA methylation profiling	146
551	5	OBI_0000748	DNA methylation profiling (Bisulfite-Seq)	146
552	6	_OBI_0000634	DNA methylation profiling (MeDIP-Seq)	146
553	7	_IDO_0000469	drug susceptibility	146
554	8	ENV_GENE_SURVEY	environmental gene survey	146
555	9	ERO_0001183	genome sequencing	146
556	10	OBI_0000630	hematology	146
557	11	OBI_0600020	histology	146
558	12	OBI_0002017	Histone Modification (ChIP-Seq)	146
559	13	SO_0001786	loss of heterozygosity profiling	146
560	14	OBI_0000366	metabolite profiling	146
561	15	METAGENOME_SEQ	metagenome sequencing	146
562	16	OBI_0000615	protein expression profiling	146
563	17	ERO_0000346	protein identification	146
564	18	PROTEIN_DNA_BINDING	protein-DNA binding site identification	146
565	19	OBI_0000288	protein-protein interaction detection	146
566	20	PROTEIN_RNA_BINDING	protein-RNA binding (RIP-Seq)	146
567	21	OBI_0000435	SNP analysis	146
568	22	TARGETED_SEQ	targeted sequencing	146
569	23	OBI_0002018	transcription factor binding (ChIP-Seq)	146
570	24	OBI_0000291	transcription factor binding site identification	146
572	27	TRANSCRIPTION_PROF	transcription profiling (Microarray)	146
573	28	OBI_0001271	transcription profiling (RNA-Seq)	146
574	29	TRAP_TRANS_PROF	TRAP translational profiling	146
575	30	OTHER_MEASUREMENT	Other	146
576	0	NCBITaxon_3702	Arabidopsis thaliana	144
577	1	NCBITaxon_9913	Bos taurus	144
578	2	NCBITaxon_6239	Caenorhabditis elegans	144
579	3	NCBITaxon_3055	Chlamydomonas reinhardtii	144
580	4	NCBITaxon_7955	Danio rerio (zebrafish)	144
581	5	NCBITaxon_44689	Dictyostelium discoideum	144
582	6	NCBITaxon_7227	Drosophila melanogaster	144
583	7	NCBITaxon_562	Escherichia coli	144
584	8	NCBITaxon_11103	Hepatitis C virus	144
585	9	NCBITaxon_9606	Homo sapiens	144
586	10	NCBITaxon_10090	Mus musculus	144
587	11	NCBITaxon_33894	Mycobacterium africanum	144
588	12	NCBITaxon_78331	Mycobacterium canetti	144
589	13	NCBITaxon_1773	Mycobacterium tuberculosis	144
590	14	NCBITaxon_2104	Mycoplasma pneumoniae	144
591	15	NCBITaxon_4530	Oryza sativa	144
592	16	NCBITaxon_5833	Plasmodium falciparum	144
593	17	NCBITaxon_4754	Pneumocystis carinii	144
594	18	NCBITaxon_10116	Rattus norvegicus	144
595	19	NCBITaxon_4932	Saccharomyces cerevisiae (brewer's yeast)	144
596	20	NCBITaxon_4896	Schizosaccharomyces pombe	144
597	21	NCBITaxon_31033	Takifugu rubripes	144
598	22	NCBITaxon_8355	Xenopus laevis	144
599	23	NCBITaxon_4577	Zea mays	144
600	24	OTHER_TAXONOMY	Other	144
601	0	CULTURE_DRUG_TEST_SINGLE	culture based drug susceptibility testing, single concentration	148
602	1	CULTURE_DRUG_TEST_TWO	culture based drug susceptibility testing, two concentrations	148
603	2	CULTURE_DRUG_TEST_THREE	culture based drug susceptibility testing, three or more concentrations (minimium inhibitory concentration measurement)	148
604	3	OBI_0400148	DNA microarray	148
605	4	OBI_0000916	flow cytometry	148
606	5	OBI_0600053	gel electrophoresis	148
607	6	OBI_0000470	mass spectrometry	148
608	7	OBI_0000623	NMR spectroscopy	148
609	8	OBI_0000626	nucleotide sequencing	148
610	9	OBI_0400149	protein microarray	148
611	10	OBI_0000893	real time PCR	148
612	11	NO_TECHNOLOGY	no technology required	148
613	12	OTHER_TECHNOLOGY	Other	148
614	0	210_MS_GC	210-MS GC Ion Trap (Varian)	149
615	1	220_MS_GC	220-MS GC Ion Trap (Varian)	149
616	2	225_MS_GC	225-MS GC Ion Trap (Varian)	149
617	3	240_MS_GC	240-MS GC Ion Trap (Varian)	149
618	4	300_MS_GCMS	300-MS quadrupole GC/MS (Varian)	149
619	5	320_MS_LCMS	320-MS LC/MS (Varian)	149
620	6	325_MS_LCMS	325-MS LC/MS (Varian)	149
621	7	500_MS_GCMS	320-MS GC/MS (Varian)	149
622	8	500_MS_LCMS	500-MS LC/MS (Varian)	149
623	9	800D	800D (Jeol)	149
624	10	910_MS_TQFT	910-MS TQ-FT (Varian)	149
625	11	920_MS_TQFT	920-MS TQ-FT (Varian)	149
626	12	3100_MASS_D	3100 Mass Detector (Waters)	149
627	13	6110_QUAD_LCMS	6110 Quadrupole LC/MS (Agilent)	149
628	14	6120_QUAD_LCMS	6120 Quadrupole LC/MS (Agilent)	149
629	15	6130_QUAD_LCMS	6130 Quadrupole LC/MS (Agilent)	149
630	16	6140_QUAD_LCMS	6140 Quadrupole LC/MS (Agilent)	149
631	17	6310_ION_LCMS	6310 Ion Trap LC/MS (Agilent)	149
632	18	6320_ION_LCMS	6320 Ion Trap LC/MS (Agilent)	149
633	19	6330_ION_LCMS	6330 Ion Trap LC/MS (Agilent)	149
634	20	6340_ION_LCMS	6340 Ion Trap LC/MS (Agilent)	149
635	21	6410_TRIPLE_LCMS	6410 Triple Quadrupole LC/MS (Agilent)	149
636	22	6430_TRIPLE_LCMS	6430 Triple Quadrupole LC/MS (Agilent)	149
637	23	6460_TRIPLE_LCMS	6460 Triple Quadrupole LC/MS (Agilent)	149
638	24	6490_TRIPLE_LCMS	6490 Triple Quadrupole LC/MS (Agilent)	149
639	25	6530_Q_TOF_LCMS	6530 Q-TOF LC/MS (Agilent)	149
640	26	6540_Q_TOF_LCMS	6540 Q-TOF LC/MS (Agilent)	149
641	27	6210_Q_TOF_LCMS	6210 TOF LC/MS (Agilent)	149
642	28	6220_Q_TOF_LCMS	6220 TOF LC/MS (Agilent)	149
643	29	6230_Q_TOF_LCMS	6230 TOF LC/MS (Agilent)	149
644	30	700B_TRIPLE_GCMS	7000B Triple Quadrupole GC/MS (Agilent)	149
645	31	ACCUTO_DART	AccuTO DART (Jeol)	149
646	32	ACCUTOF_GC	AccuTOF GC (Jeol)	149
647	33	ACCUTOF_LC	AccuTOF LC (Jeol)	149
648	34	ACQUITY_SQD	ACQUITY SQD (Waters)	149
649	35	ACQUITY_TQD	ACQUITY TQD (Waters)	149
650	36	AGILENT	Agilent	149
651	37	AGILENT_ 5975E_GCMSD	Agilent 5975E GC/MSD (Agilent)	149
652	38	AGILENT_5975T_LTM_GCMSD	Agilent 5975T LTM GC/MSD (Agilent)	149
653	39	5975C_GCMSD	5975C Series GC/MSD (Agilent)	149
654	40	AFFYMETRIX	Affymetrix	149
655	41	AMAZON_ETD_ESI	amaZon ETD ESI Ion Trap (Bruker)	149
656	42	AMAZON_X_ESI	amaZon X ESI Ion Trap (Bruker)	149
657	43	APEX_ULTRA_QQ_FTMS	apex-ultra hybrid Qq-FTMS (Bruker)	149
658	44	API_2000	API 2000 (AB Sciex)	149
659	45	API_3200	API 3200 (AB Sciex)	149
660	46	API_3200_QTRAP	API 3200 QTRAP (AB Sciex)	149
661	47	API_4000	API 4000 (AB Sciex)	149
662	48	API_4000_QTRAP	API 4000 QTRAP (AB Sciex)	149
663	49	API_5000	API 5000 (AB Sciex)	149
664	50	API_5500	API 5500 (AB Sciex)	149
665	51	API_5500_QTRAP	API 5500 QTRAP (AB Sciex)	149
666	52	APPLIED_BIOSYSTEMS	Applied Biosystems Group (ABI)	149
667	53	AQI_BIOSCIENCES	AQI Biosciences	149
668	54	ATMOS_GC	Atmospheric Pressure GC (Waters)	149
669	55	AUTOFLEX_III_MALDI_TOF_MS	autoflex III MALDI-TOF MS (Bruker)	149
670	56	AUTOFLEX_SPEED	autoflex speed(Bruker)	149
671	57	AUTOSPEC_PREMIER	AutoSpec Premier (Waters)	149
672	58	AXIMA_MEGA_TOF	AXIMA Mega TOF (Shimadzu)	149
673	59	AXIMA_PERF_MALDI_TOF	AXIMA Performance MALDI TOF/TOF (Shimadzu)	149
674	60	A_10_ANALYZER	A-10 Analyzer (Apogee)	149
675	61	A_40_MINIFCM	A-40-MiniFCM (Apogee)	149
676	62	BACTIFLOW	Bactiflow (Chemunex SA)	149
677	63	BASE4INNOVATION	Base4innovation	149
678	64	BD_BACTEC_MGIT_320	BD BACTEC MGIT 320	149
679	65	BD_BACTEC_MGIT_960	BD BACTEC MGIT 960	149
680	66	BD_RADIO_BACTEC_460TB	BD Radiometric BACTEC 460TB	149
681	67	BIONANOMATRIX	BioNanomatrix	149
682	68	CELL_LAB_QUANTA_SC	Cell Lab Quanta SC (Becman Coulter)	149
683	69	CLARUS_560_D_GCMS	Clarus 560 D GC/MS (PerkinElmer)	149
684	70	CLARUS_560_S_GCMS	Clarus 560 S GC/MS (PerkinElmer)	149
685	71	CLARUS_600_GCMS	Clarus 600 GC/MS (PerkinElmer)	149
686	72	COMPLETE_GENOMICS	Complete Genomics	149
687	73	CYAN	Cyan (Dako Cytomation)	149
688	74	CYFLOW_ML	CyFlow ML (Partec)	149
689	75	CYFLOW_SL	Cyow SL (Partec)	149
690	76	CYFLOW_SL3	CyFlow SL3 (Partec)	149
691	77	CYTOBUOY	CytoBuoy (Cyto Buoy Inc)	149
692	78	CYTOSENCE	CytoSence (Cyto Buoy Inc)	149
693	79	CYTOSUB	CytoSub (Cyto Buoy Inc)	149
694	80	DANAHER	Danaher	149
695	81	DFS	DFS (Thermo Scientific)	149
696	82	EXACTIVE	Exactive(Thermo Scientific)	149
697	83	FACS_CANTO	FACS Canto (Becton Dickinson)	149
698	84	FACS_CANTO2	FACS Canto2 (Becton Dickinson)	149
699	85	FACS_SCAN	FACS Scan (Becton Dickinson)	149
700	86	FC_500	FC 500 (Becman Coulter)	149
701	87	GCMATE_II	GCmate II GC/MS (Jeol)	149
702	88	GCMS_QP2010_PLUS	GCMS-QP2010 Plus (Shimadzu)	149
703	89	GCMS_QP2010S_PLUS	GCMS-QP2010S Plus (Shimadzu)	149
704	90	GCT_PREMIER	GCT Premier (Waters)	149
705	91	GENEQ	GENEQ	149
706	92	GENOME_CORP	Genome Corp.	149
707	93	GENOVOXX	GenoVoxx	149
708	94	GNUBIO	GnuBio	149
709	95	GUAVA_EASYCYTE_MINI	Guava EasyCyte Mini (Millipore)	149
710	96	GUAVA_EASYCYTE_PLUS	Guava EasyCyte Plus (Millipore)	149
711	97	GUAVA_PERSONAL_CELL	Guava Personal Cell Analysis (Millipore)	149
712	98	GUAVA_PERSONAL_CELL_96	Guava Personal Cell Analysis-96 (Millipore)	149
713	99	HELICOS_BIO	Helicos BioSciences	149
714	100	ILLUMINA	Illumina	149
715	101	INDIRECT_LJ_MEDIUM	Indirect proportion method on LJ medium	149
716	102	INDIRECT_AGAR_7H9	Indirect proportion method on Middlebrook Agar 7H9	149
717	103	INDIRECT_AGAR_7H10	Indirect proportion method on Middlebrook Agar 7H10	149
718	104	INDIRECT_AGAR_7H11	Indirect proportion method on Middlebrook Agar 7H11	149
719	105	INFLUX_ANALYZER	inFlux Analyzer (Cytopeia)	149
720	106	INTELLIGENT_BIOSYSTEMS	Intelligent Bio-Systems	149
721	107	ITQ_700	ITQ 700 (Thermo Scientific)	149
722	108	ITQ_900	ITQ 900 (Thermo Scientific)	149
723	109	ITQ_1100	ITQ 1100 (Thermo Scientific)	149
724	110	JMS_53000_SPIRAL	JMS-53000 SpiralTOF (Jeol)	149
725	111	LASERGEN	LaserGen	149
726	112	LCMS_2020	LCMS-2020 (Shimadzu)	149
727	113	LCMS_2010EV	LCMS-2010EV (Shimadzu)	149
728	114	LCMS_IT_TOF	LCMS-IT-TOF (Shimadzu)	149
729	115	LI_COR	Li-Cor	149
730	116	LIFE_TECH	Life Tech	149
731	117	LIGHTSPEED_GENOMICS	LightSpeed Genomics	149
732	118	LCT_PREMIER_XE	LCT Premier XE (Waters)	149
733	119	LCQ_DECA_XP_MAX	LCQ Deca XP MAX (Thermo Scientific)	149
734	120	LCQ_FLEET	LCQ Fleet (Thermo Scientific)	149
735	121	LXQ_THERMO	LXQ (Thermo Scientific)	149
736	122	LTQ_CLASSIC	LTQ Classic (Thermo Scientific)	149
737	123	LTQ_XL	LTQ XL (Thermo Scientific)	149
738	124	LTQ_VELOS	LTQ Velos (Thermo Scientific)	149
739	125	LTQ_ORBITRAP_CLASSIC	LTQ Orbitrap Classic (Thermo Scientific)	149
740	126	LTQ_ORBITRAP_XL	LTQ Orbitrap XL (Thermo Scientific)	149
741	127	LTQ_ORBITRAP_DISCOVERY	LTQ Orbitrap Discovery (Thermo Scientific)	149
742	128	LTQ_ORBITRAP_VELOS	LTQ Orbitrap Velos (Thermo Scientific)	149
743	129	LUMINEX_100	Luminex 100 (Luminex)	149
744	130	LUMINEX_200	Luminex 200 (Luminex)	149
745	131	MACS_QUANT	MACS Quant (Miltenyi)	149
746	132	MALDI_SYNAPT_G2_HDMS	MALDI SYNAPT G2 HDMS (Waters)	149
747	133	MALDI_SYNAPT_G2_MS	MALDI SYNAPT G2 MS (Waters)	149
748	134	MALDI_SYNAPT_HDMS	MALDI SYNAPT HDMS (Waters)	149
749	135	MALDI_SYNAPT_MS	MALDI SYNAPT MS (Waters)	149
750	136	MALDI_MICROMX	MALDI micro MX (Waters)	149
751	137	MAXIS	maXis (Bruker)	149
752	138	MAXISG4	maXis G4 (Bruker)	149
753	139	MICROFLEX_LT_MALDI_TOF_MS	microflex LT MALDI-TOF MS (Bruker)	149
754	140	MICROFLEX_LRF_MALDI_TOF_MS	microflex LRF MALDI-TOF MS (Bruker)	149
755	141	MICROFLEX_III_TOF_MS	microflex III MALDI-TOF MS (Bruker)	149
756	142	MICROTOF_II_ESI_TOF	micrOTOF II ESI TOF (Bruker)	149
757	143	MICROTOF_Q_II_ESI_QQ_TOF	micrOTOF-Q II ESI-Qq-TOF (Bruker)	149
758	144	MICROPLATE_ALAMAR_BLUE_COLORIMETRIC	microplate Alamar Blue (resazurin) colorimetric method	149
759	145	MSTATION	Mstation (Jeol)	149
760	146	MSQ_PLUS	MSQ Plus (Thermo Scientific)	149
761	147	NABSYS	NABsys	149
762	148	NANOPHOTONICS_BIOSCIENCES	Nanophotonics Biosciences	149
763	149	NETWORK_BIOSYSTEMS	Network Biosystems	149
764	150	NIMBLEGEN	Nimblegen	149
765	151	OXFORD_NANOPORE_TECHNOLOGIES	Oxford Nanopore Technologies	149
766	152	PACIFIC_BIOSCIENCES	Pacific Biosciences	149
767	153	POPULATION_GENETICS_TECHNOLOGIES	Population Genetics Technologies	149
768	154	Q1000GC_ULTRAQUAD	Q1000GC UltraQuad (Jeol)	149
769	155	QUATTRO_MICRO_API	Quattro micro API (Waters)	149
770	156	QUATTRO_MICRO_GC	Quattro micro GC (Waters)	149
771	157	QUATTRO_PREMIER_XE	Quattro Premier XE (Waters)	149
772	158	QSTAR	QSTAR (AB Sciex)	149
773	159	REVEO	Reveo	149
774	160	ROCHE	Roche	149
775	161	SEIRAD	Seirad	149
776	162	SOLARIX_HYBRID_QQ_FTMS	solariX hybrid Qq-FTMS (Bruker)	149
777	163	SOMACOUNT	Somacount (Bently Instruments)	149
778	164	SOMASCOPE	SomaScope (Bently Instruments)	149
779	165	SYNAPT_G2_HDMS	SYNAPT G2 HDMS (Waters)	149
780	166	SYNAPT_G2_MS	SYNAPT G2 MS (Waters)	149
781	167	SYNAPT_HDMS	SYNAPT HDMS (Waters)	149
782	168	SYNAPT_MS	SYNAPT MS (Waters)	149
783	169	TRIPLETOF_5600	TripleTOF 5600 (AB Sciex)	149
784	170	TSQ_QUANTUM_ULTRA	TSQ Quantum Ultra (Thermo Scientific)	149
785	171	TSQ_QUANTUM_ACCESS	TSQ Quantum Access (Thermo Scientific)	149
786	172	TSQ_QUANTUM_ACCESS_MAX	TSQ Quantum Access MAX (Thermo Scientific)	149
787	173	TSQ_QUANTUM_DISCOVERY_MAX	TSQ Quantum Discovery MAX (Thermo Scientific)	149
788	174	TSQ_QUANTUM_GC	TSQ Quantum GC (Thermo Scientific)	149
789	175	TSQ_QUANTUM_XLS	TSQ Quantum XLS (Thermo Scientific)	149
790	176	TSQ_VANTAGE	TSQ Vantage (Thermo Scientific)	149
791	177	ULTRAFLEXTREME_MALDI_TOF_MS	ultrafleXtreme MALDI-TOF MS (Bruker)	149
792	178	VISIGEN_BIO	VisiGen Biotechnologies	149
793	179	XEVO_G2_QTOF	Xevo G2 QTOF (Waters)	149
794	180	XEVO_QTOF_MS	Xevo QTof MS (Waters)	149
795	181	XEVO_TQ_MS	Xevo TQ MS (Waters)	149
796	182	XEVO_TQ_S	Xevo TQ-S (Waters)	149
797	183	OTHER_PLATFORM	Other	149
798	0		abstract	155
799	1		addendum	155
800	2		announcement	155
801	3		article-commentary	155
802	4		book review	155
803	5		books received	155
804	6		brief report	155
805	7		calendar	155
806	8		case report	155
807	9		collection	155
808	10		correction	155
809	11		data paper	155
810	12		discussion	155
811	13		dissertation	155
812	14		editorial	155
813	15		in brief	155
814	16		introduction	155
815	17		letter	155
816	18		meeting report	155
817	19		news	155
818	20		obituary	155
819	21		oration	155
820	22		partial retraction	155
821	23		product review	155
822	24		rapid communication	155
823	25		reply	155
824	26		reprint	155
825	27		research article	155
826	28		retraction	155
827	29		review article	155
828	30		translation	155
829	31		other	155
834	1	STANDARD	STANDARD	278
835	2	CUSTOM	CUSTOM	278
571	26	EFO_0001032	transcription profiling	146
\.


--
-- TOC entry 4106 (class 0 OID 16492)
-- Dependencies: 225
-- Data for Name: customfieldmap; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.customfieldmap (id, sourcedatasetfield, sourcetemplate, targetdatasetfield) FROM stdin;
\.


--
-- TOC entry 4108 (class 0 OID 16500)
-- Dependencies: 227
-- Data for Name: customquestion; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.customquestion (id, displayorder, hidden, questionstring, questiontype, required, guestbook_id) FROM stdin;
\.


--
-- TOC entry 4110 (class 0 OID 16508)
-- Dependencies: 229
-- Data for Name: customquestionresponse; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.customquestionresponse (id, response, customquestion_id, guestbookresponse_id) FROM stdin;
\.


--
-- TOC entry 4112 (class 0 OID 16516)
-- Dependencies: 231
-- Data for Name: customquestionvalue; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.customquestionvalue (id, displayorder, valuestring, customquestion_id) FROM stdin;
\.


--
-- TOC entry 4114 (class 0 OID 16521)
-- Dependencies: 233
-- Data for Name: customzipservicerequest; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.customzipservicerequest (key, storagelocation, filename, issuetime) FROM stdin;
\.


--
-- TOC entry 4115 (class 0 OID 16527)
-- Dependencies: 234
-- Data for Name: datafile; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datafile (id, checksumtype, checksumvalue, contenttype, filesize, ingeststatus, previousdatafileid, prov_entityname, restricted, rootdatafileid) FROM stdin;
\.


--
-- TOC entry 4116 (class 0 OID 16533)
-- Dependencies: 235
-- Data for Name: datafilecategory; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datafilecategory (id, name, dataset_id) FROM stdin;
\.


--
-- TOC entry 4118 (class 0 OID 16538)
-- Dependencies: 237
-- Data for Name: datafiletag; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datafiletag (id, type, datafile_id) FROM stdin;
\.


--
-- TOC entry 4120 (class 0 OID 16543)
-- Dependencies: 239
-- Data for Name: dataset; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataset (id, fileaccessrequest, harvestidentifier, lastexporttime, storagedriver, usegenericthumbnail, citationdatedatasetfieldtype_id, harvestingclient_id, guestbook_id, thumbnailfile_id) FROM stdin;
\.


--
-- TOC entry 4121 (class 0 OID 16549)
-- Dependencies: 240
-- Data for Name: datasetexternalcitations; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetexternalcitations (id, citedbyurl, dataset_id) FROM stdin;
\.


--
-- TOC entry 4122 (class 0 OID 16552)
-- Dependencies: 241
-- Data for Name: datasetfield; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetfield (id, datasetfieldtype_id, datasetversion_id, parentdatasetfieldcompoundvalue_id, template_id) FROM stdin;
\.


--
-- TOC entry 4123 (class 0 OID 16555)
-- Dependencies: 242
-- Data for Name: datasetfield_controlledvocabularyvalue; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetfield_controlledvocabularyvalue (datasetfield_id, controlledvocabularyvalues_id) FROM stdin;
\.


--
-- TOC entry 4125 (class 0 OID 16560)
-- Dependencies: 244
-- Data for Name: datasetfieldcompoundvalue; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetfieldcompoundvalue (id, displayorder, parentdatasetfield_id) FROM stdin;
\.


--
-- TOC entry 4127 (class 0 OID 16565)
-- Dependencies: 246
-- Data for Name: datasetfielddefaultvalue; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetfielddefaultvalue (id, displayorder, strvalue, datasetfield_id, defaultvalueset_id, parentdatasetfielddefaultvalue_id) FROM stdin;
\.


--
-- TOC entry 4129 (class 0 OID 16573)
-- Dependencies: 248
-- Data for Name: datasetfieldtype; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetfieldtype (id, advancedsearchfieldtype, allowcontrolledvocabulary, allowmultiples, description, displayformat, displayoncreate, displayorder, facetable, fieldtype, name, required, title, uri, validationformat, watermark, metadatablock_id, parentdatasetfieldtype_id) FROM stdin;
1	t	f	f	Full title by which the Dataset is known.		t	0	f	TEXT	title	t	Title	http://purl.org/dc/terms/title	\N	Enter title...	1	\N
2	f	f	f	A secondary title used to amplify or state certain limitations on the main title.		f	1	f	TEXT	subtitle	f	Subtitle	\N	\N		1	\N
3	f	f	f	A title by which the work is commonly referred, or an abbreviation of the title.		f	2	f	TEXT	alternativeTitle	f	Alternative Title	http://purl.org/dc/terms/alternative	\N		1	\N
4	f	f	f	A URL where the dataset can be viewed, such as a personal or project website.  	<a href="#VALUE" target="_blank">#VALUE</a>	f	3	f	URL	alternativeURL	f	Alternative URL	https://schema.org/distribution	\N	Enter full URL, starting with http://	1	\N
5	f	f	t	Another unique identifier that identifies this Dataset (e.g., producer's or another repository's number).	:	f	4	f	NONE	otherId	f	Other ID	\N	\N		1	\N
6	f	f	f	Name of agency which generated this identifier.	#VALUE	f	5	f	TEXT	otherIdAgency	f	Agency	\N	\N		1	5
7	f	f	f	Other identifier that corresponds to this Dataset.	#VALUE	f	6	f	TEXT	otherIdValue	f	Identifier	\N	\N		1	5
9	t	f	f	The author's Family Name, Given Name or the name of the organization responsible for this Dataset.	#VALUE	t	8	t	TEXT	authorName	t	Name	\N	\N	FamilyName, GivenName or Organization	1	8
10	t	f	f	The organization with which the author is affiliated.	(#VALUE)	t	9	t	TEXT	authorAffiliation	f	Affiliation	\N	\N		1	8
11	f	t	f	Name of the identifier scheme (ORCID, ISNI).	- #VALUE:	t	10	f	TEXT	authorIdentifierScheme	f	Identifier Scheme	http://purl.org/spar/datacite/AgentIdentifierScheme	\N		1	8
12	f	f	f	Uniquely identifies an individual author or organization, according to various schemes.	#VALUE	t	11	f	TEXT	authorIdentifier	f	Identifier	http://purl.org/spar/datacite/AgentIdentifier	\N		1	8
14	f	f	f	The contact's Family Name, Given Name or the name of the organization.	#VALUE	t	13	f	TEXT	datasetContactName	f	Name	\N	\N	FamilyName, GivenName or Organization	1	13
15	f	f	f	The organization with which the contact is affiliated.	(#VALUE)	t	14	f	TEXT	datasetContactAffiliation	f	Affiliation	\N	\N		1	13
16	f	f	f	The e-mail address(es) of the contact(s) for the Dataset. This will not be displayed.	#EMAIL	t	15	f	EMAIL	datasetContactEmail	t	E-mail	\N	\N		1	13
18	t	f	f	A summary describing the purpose, nature, and scope of the Dataset.	#VALUE	t	17	f	TEXTBOX	dsDescriptionValue	t	Text	\N	\N		1	17
19	f	f	f	In cases where a Dataset contains more than one description (for example, one might be supplied by the data producer and another prepared by the data repository where the data are deposited), the date attribute is used to distinguish between the two descriptions. The date attribute follows the ISO convention of YYYY-MM-DD.	(#VALUE)	t	18	f	DATE	dsDescriptionDate	f	Date	\N	\N	YYYY-MM-DD	1	17
20	t	t	t	Domain-specific Subject Categories that are topically relevant to the Dataset.		t	19	t	TEXT	subject	t	Subject	http://purl.org/dc/terms/subject	\N		1	\N
21	f	f	t	Key terms that describe important aspects of the Dataset.		t	20	f	NONE	keyword	f	Keyword	\N	\N		1	\N
24	f	f	f	Keyword vocabulary URL points to the web presence that describes the keyword vocabulary, if appropriate. Enter an absolute URL where the keyword vocabulary web site is found, such as http://www.my.org.	<a href="#VALUE" target="_blank" rel="noopener">#VALUE</a>	t	23	f	URL	keywordVocabularyURI	f	Vocabulary URL	\N	\N	Enter full URL, starting with http://	1	21
25	f	f	t	The classification field indicates the broad important topic(s) and subjects that the data cover. Library of Congress subject terms may be used here.  		f	24	f	NONE	topicClassification	f	Topic Classification	\N	\N		1	\N
28	f	f	f	Specifies the URL location for the full controlled vocabulary.	<a href="#VALUE" target="_blank" rel="noopener">#VALUE</a>	f	27	f	URL	topicClassVocabURI	f	Vocabulary URL	\N	\N	Enter full URL, starting with http://	1	25
29	f	f	t	Publications that use the data from this Dataset. The full list of Related Publications will be displayed on the metadata tab.		t	28	f	NONE	publication	f	Related Publication	http://purl.org/dc/terms/isReferencedBy	\N		1	\N
30	t	f	f	The full bibliographic citation for this related publication.	#VALUE	t	29	f	TEXTBOX	publicationCitation	f	Citation	http://purl.org/dc/terms/bibliographicCitation	\N		1	29
31	t	t	f	The type of digital identifier used for this publication (e.g., Digital Object Identifier (DOI)).	#VALUE: 	t	30	f	TEXT	publicationIDType	f	ID Type	http://purl.org/spar/datacite/ResourceIdentifierScheme	\N		1	29
32	t	f	f	The identifier for the selected ID type.	#VALUE	t	31	f	TEXT	publicationIDNumber	f	ID Number	http://purl.org/spar/datacite/ResourceIdentifier	\N		1	29
33	f	f	f	Link to the publication web page (e.g., journal article page, archive record page, or other).	<a href="#VALUE" target="_blank" rel="noopener">#VALUE</a>	f	32	f	URL	publicationURL	f	URL	https://schema.org/distribution	\N	Enter full URL, starting with http://	1	29
34	f	f	f	Additional important information about the Dataset.		t	33	f	TEXTBOX	notesText	f	Notes	\N	\N		1	\N
35	t	t	t	Language of the Dataset		f	34	t	TEXT	language	f	Language	http://purl.org/dc/terms/language	\N		1	\N
36	f	f	t	Person or organization with the financial or administrative responsibility over this Dataset		f	35	f	NONE	producer	f	Producer	\N	\N		1	\N
27	f	f	f	Provided for specification of the controlled vocabulary in use, e.g., LCSH, MeSH, etc.	(#VALUE)	f	25	f	TEXT	topicClassVocab	f	Vocabulary	\N	\N		1	25
26	t	f	f	Topic or Subject term that is relevant to this Dataset.	#VALUE	f	26	t	TEXT	topicClassValue	f	Term	\N	\N		1	25
23	f	f	f	For the specification of the keyword controlled vocabulary in use, such as LCSH, MeSH, or others.	(#VALUE)	t	21	f	TEXT	keywordVocabulary	f	Vocabulary	\N	\N		1	21
37	t	f	f	Producer name	#VALUE	f	36	t	TEXT	producerName	f	Name	\N	\N	FamilyName, GivenName or Organization	1	36
38	f	f	f	The organization with which the producer is affiliated.	(#VALUE)	f	37	f	TEXT	producerAffiliation	f	Affiliation	\N	\N		1	36
39	f	f	f	The abbreviation by which the producer is commonly known. (ex. IQSS, ICPSR)	(#VALUE)	f	38	f	TEXT	producerAbbreviation	f	Abbreviation	\N	\N		1	36
40	f	f	f	Producer URL points to the producer's web presence, if appropriate. Enter an absolute URL where the producer's web site is found, such as http://www.my.org.  	<a href="#VALUE" target="_blank" rel="noopener">#VALUE</a>	f	39	f	URL	producerURL	f	URL	\N	\N	Enter full URL, starting with http://	1	36
179	f	f	f	The controlled vocabulary that is used.		f	123	f	TEXT	cmm-samplingProcedure-vocabulary	f	Vocabulary	\N	\N		7	178
41	f	f	f	URL for the producer's logo, which points to this  producer's web-accessible logo image. Enter an absolute URL where the producer's logo image is found, such as http://www.my.org/images/logo.gif.	<img src="#VALUE" alt="#NAME" class="metadata-logo"/><br/>	f	40	f	URL	producerLogoURL	f	Logo URL	\N	\N	Enter full URL for image, starting with http://	1	36
42	t	f	f	Date when the data collection or other materials were produced (not distributed, published or archived).		f	41	t	DATE	productionDate	f	Production Date	\N	\N	YYYY-MM-DD	1	\N
43	f	f	f	The location where the data collection and any other related materials were produced.		f	42	f	TEXT	productionPlace	f	Production Place	\N	\N		1	\N
44	f	f	t	The organization or person responsible for either collecting, managing, or otherwise contributing in some form to the development of the resource.	:	f	43	f	NONE	contributor	f	Contributor	http://purl.org/dc/terms/contributor	\N		1	\N
45	t	t	f	The type of contributor of the  resource.  	#VALUE 	f	44	t	TEXT	contributorType	f	Type	\N	\N		1	44
46	t	f	f	The Family Name, Given Name or organization name of the contributor.	#VALUE	f	45	t	TEXT	contributorName	f	Name	\N	\N	FamilyName, GivenName or Organization	1	44
47	f	f	t	Grant Information	:	f	46	f	NONE	grantNumber	f	Grant Information	https://schema.org/sponsor	\N		1	\N
48	t	f	f	Grant Number Agency	#VALUE	f	47	t	TEXT	grantNumberAgency	f	Grant Agency	\N	\N		1	47
49	t	f	f	The grant or contract number of the project that  sponsored the effort.	#VALUE	f	48	t	TEXT	grantNumberValue	f	Grant Number	\N	\N		1	47
50	f	f	t	The organization designated by the author or producer to generate copies of the particular work including any necessary editions or revisions.		f	49	f	NONE	distributor	f	Distributor	\N	\N		1	\N
51	t	f	f	Distributor name	#VALUE	f	50	t	TEXT	distributorName	f	Name	\N	\N	FamilyName, GivenName or Organization	1	50
52	f	f	f	The organization with which the distributor contact is affiliated.	(#VALUE)	f	51	f	TEXT	distributorAffiliation	f	Affiliation	\N	\N		1	50
53	f	f	f	The abbreviation by which this distributor is commonly known (e.g., IQSS, ICPSR).	(#VALUE)	f	52	f	TEXT	distributorAbbreviation	f	Abbreviation	\N	\N		1	50
54	f	f	f	Distributor URL points to the distributor's web presence, if appropriate. Enter an absolute URL where the distributor's web site is found, such as http://www.my.org.	<a href="#VALUE" target="_blank" rel="noopener">#VALUE</a>	f	53	f	URL	distributorURL	f	URL	\N	\N	Enter full URL, starting with http://	1	50
55	f	f	f	URL of the distributor's logo, which points to this  distributor's web-accessible logo image. Enter an absolute URL where the distributor's logo image is found, such as http://www.my.org/images/logo.gif.	<img src="#VALUE" alt="#NAME" class="metadata-logo"/><br/>	f	54	f	URL	distributorLogoURL	f	Logo URL	\N	\N	Enter full URL for image, starting with http://	1	50
56	t	f	f	Date that the work was made available for distribution/presentation.		f	55	t	DATE	distributionDate	f	Distribution Date	\N	\N	YYYY-MM-DD	1	\N
57	f	f	f	The person (Family Name, Given Name) or the name of the organization that deposited this Dataset to the repository.		f	56	f	TEXT	depositor	f	Depositor	\N	\N		1	\N
58	f	f	f	Date that the Dataset was deposited into the repository.		f	57	t	DATE	dateOfDeposit	f	Deposit Date	http://purl.org/dc/terms/dateSubmitted	\N	YYYY-MM-DD	1	\N
59	f	f	t	Time period to which the data refer. This item reflects the time period covered by the data, not the dates of coding or making documents machine-readable or the dates the data were collected. Also known as span.	;	f	58	f	NONE	timePeriodCovered	f	Time Period Covered	https://schema.org/temporalCoverage	\N		1	\N
60	t	f	f	Start date which reflects the time period covered by the data, not the dates of coding or making documents machine-readable or the dates the data were collected.	#NAME: #VALUE 	f	59	t	DATE	timePeriodCoveredStart	f	Start	\N	\N	YYYY-MM-DD	1	59
61	t	f	f	End date which reflects the time period covered by the data, not the dates of coding or making documents machine-readable or the dates the data were collected.	#NAME: #VALUE 	f	60	t	DATE	timePeriodCoveredEnd	f	End	\N	\N	YYYY-MM-DD	1	59
62	f	f	t	Contains the date(s) when the data were collected.	;	f	61	f	NONE	dateOfCollection	f	Date of Collection	\N	\N		1	\N
63	f	f	f	Date when the data collection started.	#NAME: #VALUE 	f	62	f	DATE	dateOfCollectionStart	f	Start	\N	\N	YYYY-MM-DD	1	62
64	f	f	f	Date when the data collection ended.	#NAME: #VALUE 	f	63	f	DATE	dateOfCollectionEnd	f	End	\N	\N	YYYY-MM-DD	1	62
65	t	f	t	Type of data included in the file: survey data, census/enumeration data, aggregate data, clinical data, event/transaction data, program source code, machine-readable text, administrative records data, experimental data, psychological test, textual data, coded textual, coded documents, time budget diaries, observation data/ratings, process-produced data, or other.		f	64	t	TEXT	kindOfData	f	Kind of Data	http://rdf-vocabulary.ddialliance.org/discovery#kindOfData	\N		1	\N
66	f	f	f	Information about the Dataset series.	:	f	65	f	NONE	series	f	Series	\N	\N		1	\N
67	t	f	f	Name of the dataset series to which the Dataset belongs.	#VALUE	f	66	t	TEXT	seriesName	f	Name	\N	\N		1	66
68	f	f	f	History of the series and summary of those features that apply to the series as a whole.	#VALUE	f	67	f	TEXTBOX	seriesInformation	f	Information	\N	\N		1	66
69	f	f	t	Information about the software used to generate the Dataset.	,	f	68	f	NONE	software	f	Software	https://www.w3.org/TR/prov-o/#wasGeneratedBy	\N		1	\N
70	f	t	f	Name of software used to generate the Dataset.	#VALUE	f	69	f	TEXT	softwareName	f	Name	\N	\N		1	69
71	f	f	f	Version of the software used to generate the Dataset.	#NAME: #VALUE	f	70	f	TEXT	softwareVersion	f	Version	\N	\N		1	69
72	f	f	t	Any material related to this Dataset.		f	71	f	TEXTBOX	relatedMaterial	f	Related Material	\N	\N		1	\N
73	f	f	t	Any Datasets that are related to this Dataset, such as previous research on this subject.		f	72	f	TEXTBOX	relatedDatasets	f	Related Datasets	http://purl.org/dc/terms/relation	\N		1	\N
74	f	f	t	Any references that would serve as background or supporting material to this Dataset.		f	73	f	TEXT	otherReferences	f	Other References	http://purl.org/dc/terms/references	\N		1	\N
226	f	f	f	Connector URL	#VALUE	t	22	f	TEXT	rudi_media_connector_url	f	Media Connector URL	\N	\N		8	223
75	f	f	t	List of books, articles, serials, or machine-readable data files that served as the sources of the data collection.		f	74	f	TEXTBOX	dataSources	f	Data Sources	https://www.w3.org/TR/prov-o/#wasDerivedFrom	\N		1	\N
76	f	f	f	For historical materials, information about the origin of the sources and the rules followed in establishing the sources should be specified.		f	75	f	TEXTBOX	originOfSources	f	Origin of Sources	\N	\N		1	\N
77	f	f	f	Assessment of characteristics and source material.		f	76	f	TEXTBOX	characteristicOfSources	f	Characteristic of Sources Noted	\N	\N		1	\N
78	f	f	f	Level of documentation of the original sources.		f	77	f	TEXTBOX	accessToSources	f	Documentation and Access to Sources	\N	\N		1	\N
79	f	f	t	Information on the geographic coverage of the data. Includes the total geographic scope of the data.		f	0	f	NONE	geographicCoverage	f	Geographic Coverage	\N	\N		2	\N
80	t	t	f	The country or nation that the Dataset is about.	#VALUE, 	f	1	t	TEXT	country	f	Country / Nation	\N	\N		2	79
81	t	f	f	The state or province that the Dataset is about. Use GeoNames for correct spelling and avoid abbreviations.	#VALUE, 	f	2	t	TEXT	state	f	State / Province	\N	\N		2	79
82	t	f	f	The name of the city that the Dataset is about. Use GeoNames for correct spelling and avoid abbreviations.	#VALUE, 	f	3	t	TEXT	city	f	City	\N	\N		2	79
83	f	f	f	Other information on the geographic coverage of the data.	#VALUE, 	f	4	f	TEXT	otherGeographicCoverage	f	Other	\N	\N		2	79
84	t	f	t	Lowest level of geographic aggregation covered by the Dataset, e.g., village, county, region.		f	5	t	TEXT	geographicUnit	f	Geographic Unit	\N	\N		2	\N
85	f	f	t	The fundamental geometric description for any Dataset that models geography is the geographic bounding box. It describes the minimum box, defined by west and east longitudes and north and south latitudes, which includes the largest geographic extent of the  Dataset's geographic coverage. This element is used in the first pass of a coordinate-based search. Inclusion of this element in the codebook is recommended, but is required if the bound polygon box is included. 		f	6	f	NONE	geographicBoundingBox	f	Geographic Bounding Box	\N	\N		2	\N
86	f	f	f	Westernmost coordinate delimiting the geographic extent of the Dataset. A valid range of values,  expressed in decimal degrees, is -180,0 <= West  Bounding Longitude Value <= 180,0.		f	7	f	TEXT	westLongitude	f	West Longitude	\N	\N		2	85
87	f	f	f	Easternmost coordinate delimiting the geographic extent of the Dataset. A valid range of values,  expressed in decimal degrees, is -180,0 <= East Bounding Longitude Value <= 180,0.		f	8	f	TEXT	eastLongitude	f	East Longitude	\N	\N		2	85
88	f	f	f	Northernmost coordinate delimiting the geographic extent of the Dataset. A valid range of values,  expressed in decimal degrees, is -90,0 <= North Bounding Latitude Value <= 90,0.		f	9	f	TEXT	northLongitude	f	North Latitude	\N	\N		2	85
89	f	f	f	Southernmost coordinate delimiting the geographic extent of the Dataset. A valid range of values,  expressed in decimal degrees, is -90,0 <= South Bounding Latitude Value <= 90,0.		f	10	f	TEXT	southLongitude	f	South Latitude	\N	\N		2	85
90	t	f	t	Basic unit of analysis or observation that this Dataset describes, such as individuals, families/households, groups, institutions/organizations, administrative units, and more. For information about the DDI's controlled vocabulary for this element, please refer to the DDI web page at http://www.ddialliance.org/controlled-vocabularies.		f	0	t	TEXTBOX	unitOfAnalysis	f	Unit of Analysis	\N	\N		3	\N
91	t	f	t	Description of the population covered by the data in the file; the group of people or other elements that are the object of the study and to which the study results refer. Age, nationality, and residence commonly help to  delineate a given universe, but any number of other factors may be used, such as age limits, sex, marital status, race, ethnic group, nationality, income, veteran status, criminal convictions, and more. The universe may consist of elements other than persons, such as housing units, court cases, deaths, countries, and so on. In general, it should be possible to tell from the description of the universe whether a given individual or element is a member of the population under study. Also known as the universe of interest, population of interest, and target population.		f	1	t	TEXTBOX	universe	f	Universe	\N	\N		3	\N
92	t	f	f	The time method or time dimension of the data collection, such as panel, cross-sectional, trend, time- series, or other.		f	2	t	TEXT	timeMethod	f	Time Method	\N	\N		3	\N
93	f	f	f	Individual, agency or organization responsible for  administering the questionnaire or interview or compiling the data.		f	3	f	TEXT	dataCollector	f	Data Collector	\N	\N	FamilyName, GivenName or Organization	3	\N
94	f	f	f	Type of training provided to the data collector		f	4	f	TEXT	collectorTraining	f	Collector Training	\N	\N		3	\N
95	t	f	f	If the data collected includes more than one point in time, indicate the frequency with which the data was collected; that is, monthly, quarterly, or other.		f	5	t	TEXT	frequencyOfDataCollection	f	Frequency	\N	\N		3	\N
96	f	f	f	Type of sample and sample design used to select the survey respondents to represent the population. May include reference to the target sample size and the sampling fraction.		f	6	f	TEXTBOX	samplingProcedure	f	Sampling Procedure	\N	\N		3	\N
97	f	f	f	Specific information regarding the target sample size, actual  sample size, and the formula used to determine this.		f	7	f	NONE	targetSampleSize	f	Target Sample Size	\N	\N		3	\N
98	f	f	f	Actual sample size.		f	8	f	INT	targetSampleActualSize	f	Actual	\N	\N	Enter an integer...	3	97
99	f	f	f	Formula used to determine target sample size.		f	9	f	TEXT	targetSampleSizeFormula	f	Formula	\N	\N		3	97
100	f	f	f	Show correspondence as well as discrepancies between the sampled units (obtained) and available statistics for the population (age, sex-ratio, marital status, etc.) as a whole.		f	10	f	TEXT	deviationsFromSampleDesign	f	Major Deviations for Sample Design	\N	\N		3	\N
101	f	f	f	Method used to collect the data; instrumentation characteristics (e.g., telephone interview, mail questionnaire, or other).		f	11	f	TEXTBOX	collectionMode	f	Collection Mode	\N	\N		3	\N
141	f	f	f	The maximum value of the redshift (unitless) or Doppler velocity (km/s in the data object.		f	25	f	FLOAT	coverage.Redshift.MaximumValue	f	Maximum	\N	\N	Enter a floating-point number.	4	139
102	f	f	f	Type of data collection instrument used. Structured indicates an instrument in which all respondents are asked the same questions/tests, possibly with precoded answers. If a small portion of such a questionnaire includes open-ended questions, provide appropriate comments. Semi-structured indicates that the research instrument contains mainly open-ended questions. Unstructured indicates that in-depth interviews were conducted.		f	12	f	TEXT	researchInstrument	f	Type of Research Instrument	\N	\N		3	\N
145	t	f	t	If Other was selected in Organism, list any other organisms that were used in this Dataset. Terms from the NCBI Taxonomy are recommended.		f	3	t	TEXT	studyAssayOtherOrganism	f	Other Organism	\N	\N		5	\N
103	f	f	f	Description of noteworthy aspects of the data collection situation. Includes information on factors such as cooperativeness of respondents, duration of interviews, number of call backs, or similar.		f	13	f	TEXTBOX	dataCollectionSituation	f	Characteristics of Data Collection Situation	\N	\N		3	\N
104	f	f	f	Summary of actions taken to minimize data loss. Include information on actions such as follow-up visits, supervisory checks, historical matching, estimation, and so on.		f	14	f	TEXT	actionsToMinimizeLoss	f	Actions to Minimize Losses	\N	\N		3	\N
105	f	f	f	Control OperationsMethods to facilitate data control performed by the primary investigator or by the data archive.		f	15	f	TEXT	controlOperations	f	Control Operations	\N	\N		3	\N
106	f	f	f	The use of sampling procedures might make it necessary to apply weights to produce accurate statistical results. Describes the criteria for using weights in analysis of a collection. If a weighting formula or coefficient was developed, the formula is provided, its elements are defined, and it is indicated how the formula was applied to the data.		f	16	f	TEXTBOX	weighting	f	Weighting	\N	\N		3	\N
107	f	f	f	Methods used to clean the data collection, such as consistency checking, wildcode checking, or other.		f	17	f	TEXT	cleaningOperations	f	Cleaning Operations	\N	\N		3	\N
108	f	f	f	Note element used for any information annotating or clarifying the methodology and processing of the study. 		f	18	f	TEXT	datasetLevelErrorNotes	f	Study Level Error Notes	\N	\N		3	\N
109	t	f	f	Percentage of sample members who provided information.		f	19	t	TEXTBOX	responseRate	f	Response Rate	\N	\N		3	\N
110	f	f	f	Measure of how precisely one can estimate a population value from a given sample.		f	20	f	TEXT	samplingErrorEstimates	f	Estimates of Sampling Error	\N	\N		3	\N
111	f	f	f	Other issues pertaining to the data appraisal. Describe issues such as response variance, nonresponse rate  and testing for bias, interviewer and response bias, confidence levels, question bias, or similar.		f	21	f	TEXT	otherDataAppraisal	f	Other Forms of Data Appraisal	\N	\N		3	\N
112	f	f	f	General notes about this Dataset.		f	22	f	NONE	socialScienceNotes	f	Notes	\N	\N		3	\N
113	f	f	f	Type of note.		f	23	f	TEXT	socialScienceNotesType	f	Type	\N	\N		3	112
114	f	f	f	Note subject.		f	24	f	TEXT	socialScienceNotesSubject	f	Subject	\N	\N		3	112
115	f	f	f	Text for this note.		f	25	f	TEXTBOX	socialScienceNotesText	f	Text	\N	\N		3	112
116	t	t	t	The nature or genre of the content of the files in the dataset.		f	0	t	TEXT	astroType	f	Type	\N	\N		4	\N
117	t	t	t	The observatory or facility where the data was obtained. 		f	1	t	TEXT	astroFacility	f	Facility	\N	\N		4	\N
118	t	t	t	The instrument used to collect the data.		f	2	t	TEXT	astroInstrument	f	Instrument	\N	\N		4	\N
119	t	f	t	Astronomical Objects represented in the data (Given as SIMBAD recognizable names preferred).		f	3	t	TEXT	astroObject	f	Object	\N	\N		4	\N
120	t	f	f	The spatial (angular) resolution that is typical of the observations, in decimal degrees.		f	4	t	TEXT	resolution.Spatial	f	Spatial Resolution	\N	\N		4	\N
121	t	f	f	The spectral resolution that is typical of the observations, given as the ratio \\u03bb/\\u0394\\u03bb.		f	5	t	TEXT	resolution.Spectral	f	Spectral Resolution	\N	\N		4	\N
122	f	f	f	The temporal resolution that is typical of the observations, given in seconds.		f	6	f	TEXT	resolution.Temporal	f	Time Resolution	\N	\N		4	\N
123	t	t	t	Conventional bandpass name		f	7	t	TEXT	coverage.Spectral.Bandpass	f	Bandpass	\N	\N		4	\N
124	t	f	t	The central wavelength of the spectral bandpass, in meters.		f	8	t	FLOAT	coverage.Spectral.CentralWavelength	f	Central Wavelength (m)	\N	\N	Enter a floating-point number.	4	\N
125	f	f	t	The minimum and maximum wavelength of the spectral bandpass.		f	9	f	NONE	coverage.Spectral.Wavelength	f	Wavelength Range	\N	\N	Enter a floating-point number.	4	\N
126	t	f	f	The minimum wavelength of the spectral bandpass, in meters.		f	10	t	FLOAT	coverage.Spectral.MinimumWavelength	f	Minimum (m)	\N	\N	Enter a floating-point number.	4	125
127	t	f	f	The maximum wavelength of the spectral bandpass, in meters.		f	11	t	FLOAT	coverage.Spectral.MaximumWavelength	f	Maximum (m)	\N	\N	Enter a floating-point number.	4	125
128	f	f	t	 Time period covered by the data.		f	12	f	NONE	coverage.Temporal	f	Dataset Date Range	\N	\N		4	\N
129	t	f	f	Dataset Start Date		f	13	t	DATE	coverage.Temporal.StartTime	f	Start	\N	\N	YYYY-MM-DD	4	128
130	t	f	f	Dataset End Date		f	14	t	DATE	coverage.Temporal.StopTime	f	End	\N	\N	YYYY-MM-DD	4	128
131	f	f	t	The sky coverage of the data object.		f	15	f	TEXT	coverage.Spatial	f	Sky Coverage	\N	\N		4	\N
132	f	f	f	The (typical) depth coverage, or sensitivity, of the data object in Jy.		f	16	f	FLOAT	coverage.Depth	f	Depth Coverage	\N	\N	Enter a floating-point number.	4	\N
133	f	f	f	The (typical) density of objects, catalog entries, telescope pointings, etc., on the sky, in number per square degree.		f	17	f	FLOAT	coverage.ObjectDensity	f	Object Density	\N	\N	Enter a floating-point number.	4	\N
134	f	f	f	The total number of objects, catalog entries, etc., in the data object.		f	18	f	INT	coverage.ObjectCount	f	Object Count	\N	\N	Enter an integer.	4	\N
135	f	f	f	The fraction of the sky represented in the observations, ranging from 0 to 1.		f	19	f	FLOAT	coverage.SkyFraction	f	Fraction of Sky	\N	\N	Enter a floating-point number.	4	\N
136	f	f	f	The polarization coverage		f	20	f	TEXT	coverage.Polarization	f	Polarization	\N	\N		4	\N
137	f	f	f	RedshiftType string C "Redshift"; or "Optical" or "Radio" definitions of Doppler velocity used in the data object.		f	21	f	TEXT	redshiftType	f	RedshiftType	\N	\N		4	\N
138	f	f	f	The resolution in redshift (unitless) or Doppler velocity (km/s) in the data object.		f	22	f	FLOAT	resolution.Redshift	f	Redshift Resolution	\N	\N	Enter a floating-point number.	4	\N
139	f	f	t	The value of the redshift (unitless) or Doppler velocity (km/s in the data object.		f	23	f	FLOAT	coverage.RedshiftValue	f	Redshift Value	\N	\N	Enter a floating-point number.	4	\N
140	f	f	f	The minimum value of the redshift (unitless) or Doppler velocity (km/s in the data object.		f	24	f	FLOAT	coverage.Redshift.MinimumValue	f	Minimum	\N	\N	Enter a floating-point number.	4	139
142	t	t	t	Design types that are based on the overall experimental design.		f	0	t	TEXT	studyDesignType	f	Design Type	\N	\N		5	\N
143	t	t	t	Factors used in the Dataset. 		f	1	t	TEXT	studyFactorType	f	Factor Type	\N	\N		5	\N
144	t	t	t	The taxonomic name of the organism used in the Dataset or from which the  starting biological material derives.		f	2	t	TEXT	studyAssayOrganism	f	Organism	\N	\N		5	\N
146	t	t	t	A term to qualify the endpoint, or what is being measured (e.g. gene expression profiling; protein identification). 		f	4	t	TEXT	studyAssayMeasurementType	f	Measurement Type	\N	\N		5	\N
147	t	f	t	If Other was selected in Measurement Type, list any other measurement types that were used. Terms from NCBO Bioportal are recommended.		f	5	t	TEXT	studyAssayOtherMeasurmentType	f	Other Measurement Type	\N	\N		5	\N
148	t	t	t	A term to identify the technology used to perform the measurement (e.g. DNA microarray; mass spectrometry).		f	6	t	TEXT	studyAssayTechnologyType	f	Technology Type	\N	\N		5	\N
149	t	t	t	The manufacturer and name of the technology platform used in the assay (e.g. Bruker AVANCE).		f	7	t	TEXT	studyAssayPlatform	f	Technology Platform	\N	\N		5	\N
150	t	t	t	The name of the cell line from which the source or sample derives.		f	8	t	TEXT	studyAssayCellType	f	Cell Type	\N	\N		5	\N
151	f	f	t	Indicates the volume, issue and date of a journal, which this Dataset is associated with.		f	0	f	NONE	journalVolumeIssue	f	Journal	\N	\N		6	\N
152	t	f	f	The journal volume which this Dataset is associated with (e.g., Volume 4).		f	1	t	TEXT	journalVolume	f	Volume	\N	\N		6	151
153	t	f	f	The journal issue number which this Dataset is associated with (e.g., Number 2, Autumn).		f	2	t	TEXT	journalIssue	f	Issue	\N	\N		6	151
154	t	f	f	The publication date for this journal volume/issue, which this Dataset is associated with (e.g., 1999).		f	3	t	DATE	journalPubDate	f	Publication Date	\N	\N	YYYY or YYYY-MM or YYYY-MM-DD	6	151
155	t	t	f	Indicates what kind of article this is, for example, a research article, a commentary, a book or product review, a case report, a calendar, etc (based on JATS). 		f	4	t	TEXT	journalArticleType	f	Type of Article	\N	\N		6	\N
157	t	t	f	The country or nation that the Dataset is about.		t	101	t	TEXT	cmm-country	t	Country / Nation	\N	\N		7	156
158	t	f	f	The state or province that the Dataset is about. Use GeoNames for correct spelling and avoid abbreviations.		t	102	t	TEXT	cmm-state	f	State/Province	\N	\N		7	156
159	t	f	f	The name of the city that the Dataset is about. Use GeoNames for correct spelling and avoid abbreviations.		t	103	t	TEXT	cmm-city	f	City	\N	\N		7	156
160	f	f	f	Other information on the geographic coverage of the data.		t	104	f	TEXT	cmm-otherGeographicCoverage	f	Other	\N	\N		7	156
161	t	f	t	Lowest level of geographic aggregation covered by the Dataset, e.g., village, county, region.		f	105	t	TEXT	cmm-geographicUnit	f	Geographic Unit	\N	\N		7	\N
162	f	f	t	The fundamental geometric description for any Dataset that models geography is the geographic bounding box. It describes the minimum box, defined by west and east longitudes and north and south latitudes, which includes the largest geographic extent of the Dataset's geographic coverage. This element is used in the first pass of a coordinate-based search. Inclusion of this element in the codebook is recommended, but is required if the bound polygon box is included.		f	106	f	NONE	cmm-geographicBoundingBox	f	Geographic Bounding Box	\N	\N		7	\N
163	f	f	f	Westernmost coordinate delimiting the geographic extent of the Dataset. A valid range of values, expressed in decimal degrees, is -180,0 <= West Bounding Longitude Value <= 180,0.		f	107	f	TEXT	cmm-westLongitude	f	West Longitude	\N	\N		7	162
164	f	f	f	Easternmost coordinate delimiting the geographic extent of the Dataset. A valid range of values, expressed in decimal degrees, is -180,0 <= East Bounding Longitude Value <= 180,0.		f	108	f	TEXT	cmm-eastLongitude	f	East Longitude	\N	\N		7	162
165	f	f	f	Northernmost coordinate delimiting the geographic extent of the Dataset. A valid range of values, expressed in decimal degrees, is -90,0 <= North Bounding Latitude Value <= 90,0.		f	109	f	TEXT	cmm-northLongitude	f	North Latitude	\N	\N		7	162
166	f	f	f	Southernmost coordinate delimiting the geographic extent of the Dataset. A valid range of values, expressed in decimal degrees, is -90,0 <= South Bounding Latitude Value <= 90,0.		f	110	f	TEXT	cmm-southLongitude	f	South Latitude	\N	\N		7	162
167	f	f	t	Basic unit of analysis or observation that this Dataset describes. Select from the available options in the DDI Controlled Vocabularies for Analysis Unit.		f	111	f	NONE	cmm-unitOfAnalysis-cv	t	Unit of Analysis	\N	\N		7	\N
168	f	f	f	The controlled vocabulary that is used.		f	112	f	TEXT	cmm-unitOfAnalysis-vocabulary	f	Vocabulary	\N	\N		7	167
169	t	t	f	The term picked from the controlled vocabulary.		f	113	t	TEXT	cmm-unitOfAnalysis-term	f	Unit of Analysis Term	\N	\N		7	167
170	f	t	f	The URL for the term chosen from the vocabulary.		f	114	f	URL	cmm-unitOfAnalysis-url	f	Vocabulary Term URI	\N	\N		7	167
171	t	f	f	"Description of the population covered by the data in the file; the group of people or other elements that are the object of the study and to which the study results refer. Age, nationality, and residence commonly help to delineate a given universe, but any number of other factors may be used, such as age limits, sex, marital status, race, ethnic group, nationality, income, veteran status, criminal convictions, and more. The universe may consist of elements other than persons, such as housing units, court cases, deaths, countries, and so on. In general, it should be possible to tell from the description of the universe whether a given individual or element is a member of the population under study. Also known as the universe of interest, population of interest, and target population."		f	115	t	TEXTBOX	cmm-universe	f	Universe	\N	\N		7	\N
172	f	f	t	The time method or time dimension of the data collection. Select from the available options in the DDI Controlled Vocabularies for Time method.		f	116	f	NONE	cmm-timeMethod-cv	f	Time Method	\N	\N		7	\N
173	f	f	f	The controlled vocabulary that is used.		f	117	f	TEXT	cmm-timeMethod-vocabulary	f	Vocabulary	\N	\N		7	172
174	t	f	f	The term picked from the controlled vocabulary.		f	118	t	TEXT	cmm-timeMethod-term	f	Time Method Term	\N	\N		7	172
175	f	f	f	The URL for the term chosen from the vocabulary.		f	119	f	URL	cmm-timeMethod-url	f	Vocabulary Term URI	\N	\N		7	172
176	f	f	t	Individual, agency or organization responsible for administering the questionnaire or interview or compiling the data.		f	120	f	TEXT	cmm-dataCollector	f	Data Collector	\N	\N	FamilyName, GivenName or Organization	7	\N
177	t	f	f	"If the data collected includes more than one point in time, indicate the frequency with which the data was collected; that is, monthly, quarterly, or other."		f	121	t	TEXT	cmm-frequencyOfDataCollection	f	Frequency	\N	\N		7	\N
178	f	f	t	Type of sample and sample design used to select the survey respondents to represent the population. Select from the available options in the DDI Controlled Vocabularies for Sampling Procedure.		f	122	f	NONE	cmm-samplingProcedure-cv	f	Sampling Procedure	\N	\N		7	\N
180	t	f	f	The term picked from the controlled vocabulary.		f	124	t	TEXT	cmm-samplingProcedure-term	f	Sampling Procedure Term	\N	\N		7	178
181	f	f	f	The URL for the term chosen from the vocabulary.		f	125	f	URL	cmm-samplingProcedure-url	f	Vocabulary Term URI	\N	\N		7	178
182	f	f	f	Type of sample and sample design used to select the survey respondents to represent the population.		f	126	f	TEXTBOX	cmm-samplingProcedureFreeText	f	Sampling Procedure Text	\N	\N		7	\N
183	f	f	f	Specific information regarding the target sample size, actual sample size, and the formula used to determine this.		f	127	f	NONE	cmm-targetSampleSize	f	Target Sample Size	\N	\N		7	\N
184	f	f	f	Actual sample size.		f	128	f	INT	cmm-targetSampleActualSize	f	Actual	\N	\N	Enter an integer...	7	183
185	f	f	f	Formula used to determine target sample size.		f	129	f	TEXT	cmm-targetSampleSizeFormula	f	Formula	\N	\N		7	183
186	f	f	f	Show correspondence as well as discrepancies between the sampled units (obtained) and available statistics for the population (age, sex-ratio, marital status, etc.) as a whole.		f	130	f	TEXT	cmm-deviationsFromSampleDesign	f	Major Deviations for Sample Design	\N	\N		7	\N
187	f	f	t	"Method used to collect the data; instrumentation characteristics. Select from the available options in the DDI Controlled Vocabularies for Mode of Collection."		f	131	f	NONE	cmm-collectionMode-cv	f	Collection Mode	\N	\N		7	\N
188	f	f	f	The controlled vocabulary that is used.		f	132	f	TEXT	cmm-collectionMode-vocabulary	f	Vocabulary	\N	\N		7	187
189	t	f	f	The term picked from the controlled vocabulary.		f	133	t	TEXT	cmm-collectionMode-term	f	Collection Mode Term	\N	\N		7	187
190	f	f	f	The URL for the term chosen from the vocabulary.		f	134	f	URL	cmm-collectionMode-url	f	Vocabulary Term URI	\N	\N		7	187
191	f	t	t	Type of data collection instrument used. Select from the available options in the DDI Controlled Vocabularies for Type of Instrument.		f	135	f	NONE	cmm-researchInstrument-cv	f	Type of Research Instrument	\N	\N		7	\N
192	f	f	f	The controlled vocabulary that is used.		f	136	f	TEXT	cmm-researchInstrument-vocabulary	f	Vocabulary	\N	\N		7	191
193	t	f	f	The term picked from the controlled vocabulary.		f	137	t	TEXT	cmm-researchInstrument-term	f	Type of Research Instrument Term	\N	\N		7	191
194	f	f	f	The URL for the term chosen from the vocabulary.		f	138	f	URL	cmm-researchInstrument-url	f	Vocabulary Term URI	\N	\N		7	191
195	f	f	f	Description of noteworthy aspects of the data collection situation. Includes information on factors such as cooperativeness of respondents, duration of interviews, number of call backs, or similar.		f	139	f	TEXTBOX	cmm-dataCollectionSituation	f	Characteristics of Data Collection Situation	\N	\N		7	\N
196	f	f	f	Summary of actions taken to minimize data loss. Include information on actions such as follow-up visits, supervisory checks, historical matching, estimation, and so on.		f	140	f	TEXT	cmm-actionsToMinimizeLoss	f	Actions to Minimize Losses	\N	\N		7	\N
197	f	f	f	Control OperationsMethods to facilitate data control performed by the primary investigator or by the data archive.		f	141	f	TEXT	cmm-controlOperations	f	Control Operations	\N	\N		7	\N
198	f	f	f	The use of sampling procedures might make it necessary to apply weights to produce accurate statistical results. Describes the criteria for using weights in analysis of a collection. If a weighting formula or coefficient was developed, the formula is provided, its elements are defined, and it is indicated how the formula was applied to the data.		f	142	f	TEXTBOX	cmm-weighting	f	Weighting	\N	\N		7	\N
199	f	f	f	Methods used to clean the data collection, such as consistency checking, wildcode checking, or other.		f	143	f	TEXT	cmm-cleaningOperations	f	Cleaning Operations	\N	\N		7	\N
200	t	f	f	Percentage of sample members who provided information.		f	144	t	TEXTBOX	cmm-responseRate	f	Response Rate	\N	\N		7	\N
208	f	f	t	More precise description for the whole dataset	";"	t	4	f	NONE	rudi_summary	f	Summary	\N	\N		8	\N
209	f	f	f	Summary language	#VALUE	t	5	f	TEXT	rudi_summary_language	f	langage	\N	\N		8	208
210	t	f	f	Summary text	#VALUE	t	6	f	TEXTBOX	rudi_summary_text	f	text	\N	\N		8	208
211	t	f	f	Category for thematic classification of the data	#VALUE	t	7	t	TEXT	rudi_theme	f	Theme	\N	\N		8	\N
212	t	f	t	List of tags that can be used to retrieve the data	#VALUE	t	8	t	TEXT	rudi_keywords	f	Keywords	\N	\N		8	\N
213	f	f	f	Entity that produces the data on the Producer node	";"	t	9	f	NONE	rudi_producer	f	Producer	\N	\N		8	\N
202	t	f	f	Identifier in the data producer system	#VALUE	t	1	f	TEXT	rudi_local_id	f	Local Id	\N	\N		8	\N
203	t	f	f	Digital object identifier of the provider	#VALUE	t	2	f	TEXT	rudi_doi	f	Doi	\N	\N		8	\N
204	t	f	f	Simple name for the resource	#VALUE	t	3	f	TEXT	rudi_resource_title	t	Resource Title	\N	\N	Enter title...	8	\N
205	f	f	t	Short description for the whole dataset	";"	t	3	f	NONE	rudi_abstract	f	Synopsis	\N	\N		8	\N
206	f	f	f	Synopsis langage	#VALUE	t	4	f	TEXT	rudi_abstract_language	f	langage	\N	\N		8	205
207	t	f	f	Synopsis text	#VALUE	t	5	f	TEXTBOX	rudi_abstract_text	f	text	\N	\N		8	205
215	t	f	f	Updated offical name of the organization	#VALUE	t	11	t	TEXT	rudi_producer_organization_name	f	organization name	\N	\N		8	213
216	f	f	f	Updated offical postal address of the organization	#VALUE	t	12	f	TEXTBOX	rudi_producer_organization_address	f	organization address	\N	\N		8	213
217	f	f	t	Address to ask details on the dataset and its production	";"	t	13	f	NONE	rudi_contact	f	Contact	\N	\N		8	\N
219	f	f	f	Unique identifier of the contact	#VALUE	t	15	f	TEXT	rudi_contact_id	f	uuid	\N	\N		8	217
221	f	f	f	Updated status of the contact person	#VALUE	t	17	f	TEXT	rudi_contact_role	f	role	\N	\N		8	217
222	f	f	f	E-mail address of the contact	#EMAIL	t	18	f	EMAIL	rudi_contact_email	f	email	\N	\N		8	217
223	f	f	t	Available formats	";"	t	19	f	NONE	rudi_media	f	Available format	\N	\N		8	\N
227	f	f	f	Link towards the interface contract defined with RUDI Portal	#VALUE	t	23	f	TEXT	rudi_media_connector_interface_contract	f	Media Connector interface contract	\N	\N		8	223
228	f	f	f	Link towards the resource that describes the structure of the data	#VALUE	t	24	f	TEXT	rudi_mediafile_structure	f	File structure	\N	\N		8	223
229	f	f	f	File size	#VALUE	t	25	f	INT	rudi_mediafile_size	f	File size	\N	\N		8	223
231	f	f	f	Source encoding of the data	#VALUE	t	27	f	TEXT	rudi_mediafile_encoding	f	File encoding	\N	\N		8	223
232	f	f	f	Method for computing the integrity hash of the data	#VALUE	t	28	f	TEXT	rudi_mediafile_checksum_algo	f	File checksum algo	\N	\N		8	223
233	f	f	f	hash	#VALUE	t	29	f	TEXT	rudi_mediafile_checksum_hash	f	File checksum hash	\N	\N		8	223
234	f	f	f	Theorical delay between the production of the record and its availability in milliseconds	#VALUE	t	30	f	INT	rudi_mediaseries_latency	f	Series latency	\N	\N		8	223
235	f	f	f	Theorical delay between the production of two records in milliseconds	#VALUE	t	31	f	INT	rudi_mediaseries_period	f	Series period	\N	\N		8	223
236	f	f	f	Actual number of records	#VALUE	t	32	f	INT	rudi_mediaseries_current_number_of_records	f	Series current number of records	\N	\N		8	223
237	f	f	f	Actual size of the data in bytes	#VALUE	t	33	f	INT	rudi_mediaseries_current_size	f	Series current size	\N	\N		8	223
238	f	f	f	Estimated total number of records	#VALUE	t	34	f	INT	rudi_mediaseries_total_number_of_records	f	Series total number of records	\N	\N		8	223
239	f	f	f	Estimated total size of the data in bytes	#VALUE	t	35	f	INT	rudi_mediaseries_total_size	f	Series total size	\N	\N		8	223
241	f	f	f	Period of time described by the data	";"	t	37	f	NONE	rudi_temporal_spread	f	Temporal spread	\N	\N		8	\N
242	t	f	f	Start date	#VALUE	t	38	t	FLOAT	rudi_temporal_spread_start_date	f	Start date	\N	\N		8	241
243	t	f	f	End date	#VALUE	t	39	t	FLOAT	rudi_temporal_spread_end_date	f	End date	\N	\N		8	241
244	f	f	f	Geographic localisation of the data	";"	t	40	f	NONE	rudi_geography	f	Geography	\N	\N		8	\N
249	f	f	f	Precise geographic distribution of the data	#VALUE	t	44	f	TEXT	rudi_geography_geographic_distribution	f	Geographic distribution	\N	\N		8	244
250	f	f	f	Cartographic projection used to describe the data	#VALUE	t	45	f	TEXT	rudi_geography_projection	f	Projection	\N	\N		8	244
251	f	f	f	Indicative size of the data	";"	t	46	f	NONE	rudi_dataset_size	f	Dataset size	\N	\N		8	\N
252	f	f	f	Number of records	#VALUE	t	47	f	INT	rudi_dataset_size_numbers_of_records	f	Number of records	\N	\N		8	251
253	f	f	f	Number of fields	#VALUE	t	48	f	INT	rudi_dataset_size_number_of_fields	f	Number of fields	\N	\N		8	251
254	f	f	f	Dates of the actions performed on the data	";"	t	49	f	NONE	rudi_dataset_dates	f	Dataset dates	\N	\N		8	\N
261	f	f	f	Metadata on the metadata	";"	t	55	f	NONE	rudi_metadata_info	f	Metadata info	\N	\N		8	\N
218	t	f	f	Updated offical name of the organization	#VALUE	t	14	f	TEXT	rudi_contact_organization_name	f	organization	\N	\N		8	217
220	t	f	f	Updated name of the service, or possibly the person	#VALUE	t	16	f	TEXT	rudi_contact_name	f	name	\N	\N		8	217
224	t	f	f	Unique identifier of the media	#VALUE	t	20	f	TEXT	rudi_media_id	f	Media uuid	\N	\N		8	223
230	t	f	f	File type	#VALUE	t	26	f	TEXT	rudi_mediafile_type	f	File type	\N	\N		8	223
240	t	f	t	Resource language	#VALUE	t	36	f	TEXT	rudi_resource_language	f	Resource language	\N	\N		8	\N
245	t	f	f	Bounding box West Longitude	#VALUE	t	41	f	FLOAT	rudi_geography_bounding_box_west_longitude	f	Bounding box West Longitude	\N	\N		8	244
246	t	f	f	Bounding box East Longitude	#VALUE	t	42	f	FLOAT	rudi_geography_bounding_box_east_longitude	f	Bounding box East Longitude	\N	\N		8	244
247	t	f	f	Bounding box North Latitude	#VALUE	t	43	f	FLOAT	rudi_geography_bounding_box_north_latitude	f	Bounding box North Latitude	\N	\N		8	244
248	t	f	f	Bounding box South Latitude	#VALUE	t	44	f	FLOAT	rudi_geography_bounding_box_south_latitude	f	Bounding box South Latitude	\N	\N		8	244
255	t	f	f	Dataset creation date	#VALUE	t	50	t	FLOAT	rudi_dataset_dates_created	f	Created	\N	\N		8	254
256	t	f	f	Dataset validation date	#VALUE	t	51	t	FLOAT	rudi_dataset_dates_validated	f	Validated	\N	\N		8	254
257	t	f	f	Dataset publication date	#VALUE	t	52	t	FLOAT	rudi_dataset_dates_published	f	Published	\N	\N		8	254
258	t	f	f	Dataset update date	#VALUE	t	53	t	FLOAT	rudi_dataset_dates_updated	f	Updated	\N	\N		8	254
259	t	f	f	Dataset date of deletion	#VALUE	t	54	t	FLOAT	rudi_dataset_dates_deleted	f	Deleted	\N	\N		8	254
260	t	f	f	Status of the storage of the dataset	#VALUE	t	54	t	TEXT	rudi_storage_status	f	Storage status	\N	\N		8	\N
262	t	f	f	Version of the API	#VALUE	t	55	f	TEXT	rudi_metadata_info_api_version	f	API version	\N	\N		8	261
264	t	f	f	Metadata validation date	#VALUE	t	57	f	FLOAT	rudi_metadata_info_dates_validated	f	Metadata validation date	\N	\N		8	261
225	t	f	f	Transmission mode	#VALUE	t	21	f	TEXT	rudi_media_type	f	Media type	\N	\N		8	223
263	t	f	f	Metadata creation date	#VALUE	t	56	f	FLOAT	rudi_metadata_info_dates_created	f	Metadata creation date	\N	\N		8	261
270	f	f	f	Provider organization address	#VALUE	t	63	f	TEXTBOX	rudi_metadata_info_provider_organization_address	f	Provider organization address	\N	\N		8	261
271	f	f	t	Metadata contact	";"	t	63	f	NONE	rudi_metadata_info_contact	f	Metadata info contact	\N	\N		8	\N
275	f	f	f	Updated status of the metadata contact person	#VALUE	t	67	f	TEXT	rudi_metadata_info_contact_role	f	Metadata contact role	\N	\N		8	271
276	f	f	f	E-mail address of the metadata contact	#EMAIL	t	68	f	EMAIL	rudi_metadata_info_contact_email	f	Metadata contact email	\N	\N		8	271
201	t	f	f	global id for dataset	#VALUE	t	0	f	TEXT	rudi_global_id	t	Global Id	\N	\N		8	\N
214	t	f	f	Unique identifier of the organization in RUDI system	#VALUE	t	10	f	TEXT	rudi_producer_organization_id	f	organization uuid	\N	\N		8	213
268	t	f	f	Provider organization uuid	#VALUE	t	61	f	TEXT	rudi_metadata_info_provider_organization_id	f	Provider organization uuid	\N	\N		8	261
269	t	f	f	Provider organization name	#VALUE	t	62	t	TEXT	rudi_metadata_info_provider_organization_name	f	Provider organization name	\N	\N		8	261
272	t	f	f	Unique identifier of the metadata contact	#VALUE	t	64	f	TEXT	rudi_metadata_info_contact_id	f	Metadata contact uuid	\N	\N		8	271
273	t	f	f	Updated offical name of the organization metadata contact	#VALUE	t	65	f	TEXT	rudi_metadata_info_contact_organization_name	f	Metadata contact organization name	\N	\N		8	271
274	t	f	f	Updated name of the service, or possibly the person	#VALUE	t	66	f	TEXT	rudi_metadata_info_contact_name	f	Metadata contact name	\N	\N		8	271
265	t	f	f	Metadata publication date	#VALUE	t	58	f	FLOAT	rudi_metadata_info_dates_published	f	Metadata publication date	\N	\N		8	261
266	t	f	f	Metadata update date	#VALUE	t	59	f	FLOAT	rudi_metadata_info_dates_updated	f	Metadata update date	\N	\N		8	261
267	t	f	f	Metadata date of deletion	#VALUE	t	60	f	FLOAT	rudi_metadata_info_dates_deleted	f	Metadata date of deletion	\N	\N		8	261
287	f	f	t	Describes how constrained is the use of the resource	";"	t	78	f	TEXT	rudi_access_condition_usage_constraint	f	Usage Constraint	\N	\N		8	\N
288	f	f	f		#VALUE	t	79	f	TEXT	rudi_access_condition_usage_constraint_lang	f	Language	\N	\N		8	287
277	f	f	f	Access restrictions for the use of data in the form of licence, confidentiality, terms of service, habilitation or required rights, economical model. Default is open licence. #TODO: to be defined. Possible redundencies with other fields! 	";"	t	69	f	NONE	rudi_access_condition	f	Access Condition	\N	\N		8	\N
289	f	f	f		#VALUE	t	80	f	TEXTBOX	rudi_access_condition_usage_constraint_text	f	Text	\N	\N		8	287
280	f	f	f	True if the dataset has a restricted access. False for open data 	#VALUE	t	70	f	TEXT	rudi_access_condition_confidentiality_restricted_access	f	Confidentiality : Restricted Access	\N	\N		8	277
281	f	f	f	True if the dataset embeds personal data 	#VALUE	t	71	f	TEXT	rudi_access_condition_confidentiality_gdpr_sensitive	f	Confidentiality : GDPR Sensitive	\N	\N		8	277
278	f	t	f	Licence type	#VALUE	t	72	f	TEXT	rudi_access_condition_licence_licence_type	f	Licence type	\N	\N		8	277
279	f	f	f	Value of the field SkosConcept.concept_code	#VALUE	t	73	t	TEXT	rudi_access_condition_licence_licence_label	f	Standard Licence label	\N	\N		8	277
282	f	f	f	URL towards the custom licence	#VALUE	t	74	f	URL	rudi_access_condition_licence_custom_licence_uri	f	Custom Licence URI	\N	\N		8	277
283	f	f	t	Title of the custom licence	";"	t	75	f	TEXT	rudi_access_condition_licence_custom_licence_label	f	Custom Licence Label	\N	\N		8	\N
285	f	f	f		#VALUE	t	76	f	TEXT	rudi_access_condition_licence_custom_licence_label_lang	f	Language	\N	\N		8	283
286	f	f	f		#VALUE	t	77	f	TEXTBOX	rudi_access_condition_licence_custom_licence_label_text	f	Text	\N	\N		8	283
290	f	f	t	Information that MUST be cited every time the data is used, most likely a BibTeX entry	";"	t	81	f	TEXT	rudi_access_condition_bibliographical_reference	f	Bibliographical Reference	\N	\N		8	\N
291	f	f	f		#VALUE	t	82	f	TEXT	rudi_access_condition_bibliographical_reference_lang	f	Language	\N	\N		8	290
292	f	f	f		#VALUE	t	83	f	TEXTBOX	rudi_access_condition_bibliographical_reference_text	f	Text	\N	\N		8	290
293	f	f	t	Mention that must be cited verbatim in every publication that makes use of the data	";"	t	84	f	TEXT	rudi_access_condition_mandatory_mention	f	Mandatory Mention	\N	\N		8	\N
294	f	f	f		#VALUE	t	85	f	TEXT	rudi_access_condition_mandatory_mention_lang	f	Language	\N	\N		8	293
295	f	f	f		#VALUE	t	86	f	TEXTBOX	rudi_access_condition_mandatory_mention_text	f	Text	\N	\N		8	293
296	f	f	t		";"	t	87	f	TEXT	rudi_access_condition_access_constraint	f	Access Constraint	\N	\N		8	\N
297	f	f	f		#VALUE	t	88	f	TEXT	rudi_access_condition_access_constraint_lang	f	Language	\N	\N		8	296
298	f	f	f		#VALUE	t	89	f	TEXTBOX	rudi_access_condition_access_constraint_text	f	Text	\N	\N		8	296
299	f	f	t		";"	t	90	f	TEXT	rudi_access_condition_other_constraints	f	Other Constraints	\N	\N		8	\N
300	f	f	f		#VALUE	t	91	f	TEXT	rudi_access_condition_other_constraints_lang	f	Language	\N	\N		8	299
301	f	f	f		#VALUE	t	92	f	TEXTBOX	rudi_access_condition_other_constraints_text	f	Text	\N	\N		8	299
22	t	f	f	Key terms that describe important aspects of the Dataset. Can be used for building keyword indexes and for classification and retrieval purposes. A controlled vocabulary can be employed. The vocab attribute is provided for specification of the controlled vocabulary in use, such as LCSH, MeSH, or others. The vocabURI attribute specifies the location for the full controlled vocabulary.	#VALUE	t	22	t	TEXT	keywordValue	f	Term	\N	\N		1	21
8	f	f	t	The person(s), corporate body(ies), or agency(ies) responsible for creating the work.		t	7	f	NONE	author	t	Author	http://purl.org/dc/terms/creator	\N		1	\N
13	f	f	t	The contact(s) for this Dataset.		t	12	f	NONE	datasetContact	t	Contact	\N	\N		1	\N
17	f	f	t	A summary describing the purpose, nature, and scope of the Dataset.		t	16	f	NONE	dsDescription	t	Description	\N	\N		1	\N
156	f	f	t	Information on the geographic coverage of the data. Includes the total geographic scope of the data.		t	100	f	NONE	cmm-geographicCoverage	t	Geographic Coverage	\N	\N		7	\N
\.


--
-- TOC entry 4131 (class 0 OID 16581)
-- Dependencies: 250
-- Data for Name: datasetfieldvalue; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetfieldvalue (id, displayorder, value, datasetfield_id) FROM stdin;
\.


--
-- TOC entry 4133 (class 0 OID 16589)
-- Dependencies: 252
-- Data for Name: datasetlinkingdataverse; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetlinkingdataverse (id, linkcreatetime, dataset_id, linkingdataverse_id) FROM stdin;
\.


--
-- TOC entry 4135 (class 0 OID 16594)
-- Dependencies: 254
-- Data for Name: datasetlock; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetlock (id, info, reason, starttime, dataset_id, user_id) FROM stdin;
\.


--
-- TOC entry 4137 (class 0 OID 16602)
-- Dependencies: 256
-- Data for Name: datasetmetrics; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetmetrics (id, countrycode, downloadstotalmachine, downloadstotalregular, downloadsuniquemachine, downloadsuniqueregular, monthyear, viewstotalmachine, viewstotalregular, viewsuniquemachine, viewsuniqueregular, dataset_id) FROM stdin;
\.


--
-- TOC entry 4139 (class 0 OID 16610)
-- Dependencies: 258
-- Data for Name: datasetversion; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetversion (id, unf, archivalcopylocation, archivenote, archivetime, createtime, deaccessionlink, lastupdatetime, minorversionnumber, releasetime, version, versionnote, versionnumber, versionstate, dataset_id, termsofuseandaccess_id) FROM stdin;
\.


--
-- TOC entry 4141 (class 0 OID 16618)
-- Dependencies: 260
-- Data for Name: datasetversionuser; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datasetversionuser (id, lastupdatedate, authenticateduser_id, datasetversion_id) FROM stdin;
\.


--
-- TOC entry 4143 (class 0 OID 16623)
-- Dependencies: 262
-- Data for Name: datatable; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datatable (id, casequantity, originalfileformat, originalfilename, originalfilesize, originalformatversion, recordspercase, unf, varquantity, datafile_id) FROM stdin;
\.


--
-- TOC entry 4145 (class 0 OID 16631)
-- Dependencies: 264
-- Data for Name: datavariable; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.datavariable (id, factor, fileendposition, fileorder, filestartposition, format, formatcategory, "interval", label, name, numberofdecimalpoints, orderedfactor, recordsegmentnumber, type, unf, weighted, datatable_id) FROM stdin;
\.


--
-- TOC entry 4147 (class 0 OID 16639)
-- Dependencies: 266
-- Data for Name: dataverse; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataverse (id, affiliation, alias, dataversetype, description, facetroot, guestbookroot, metadatablockroot, name, permissionroot, storagedriver, templateroot, themeroot, defaultcontributorrole_id, defaulttemplate_id) FROM stdin;
1	\N	root	UNCATEGORIZED	The root dataverse.	t	f	t	Root	t	\N	f	t	6	\N
2	Dataverse.org	rudi	UNCATEGORIZED	Dataverse contenant tous les dataverses liés à RUDI	t	f	t	RUDI Root	t	\N	f	t	6	\N
3	Dataverse.org	rudi_data	UNCATEGORIZED	Dataverse contenant les jeux de données actifs de RUDI	f	f	f	RUDI Data	t	\N	f	t	6	\N
4	Dataverse.org	rudi_archive	UNCATEGORIZED	Dataverse contenant les jeux de données archivés de RUDI	f	f	f	RUDI Archive	t	\N	f	t	6	\N
5	Dataverse.org	rudi_test	UNCATEGORIZED	Dataverse utilisé pour les tests unitaires de RUDI	f	f	f	RUDI Test	t	\N	f	t	6	\N
557	Dataverse.org	rudi_media	UNCATEGORIZED	Dataverse Root contenant tous les dataverses liés aux médias de RUDI	t	f	t	RUDI Media Root	t	\N	f	t	6	\N
563	Dataverse.org	rudi_media_test	UNCATEGORIZED	Dataverse de test contenant les médias de RUDI, comme  les images des fournisseurs et des producteurs	f	f	f	RUDI Media Test	t	\N	f	t	6	\N
558	Dataverse.org	rudi_media_data	UNCATEGORIZED	Dataverse contenant les médias de RUDI, comme  les images des fournisseurs et des producteurs	f	f	f	RUDI Media Data	t	\N	f	t	6	\N
7228	Dataverse.org	rudi_media_test_manual	UNCATEGORIZED	Dataverse de test contenant les médias de RUDI, comme  les images des fournisseurs et des producteurs utilisés pour des tests manuels	f	f	f	RUDI Media Test Manual	t	\N	f	t	6	\N
6988	Dataverse.org	rudi_test_manual	UNCATEGORIZED	Dataverse brouillon pour tester des modifications manuellement avant de les effectuer sur le Dataverse réel de test (rudi_test)	f	f	f	RUDI Test Manual	t	\N	f	t	6	\N
\.


--
-- TOC entry 4148 (class 0 OID 16645)
-- Dependencies: 267
-- Data for Name: dataverse_citationdatasetfieldtypes; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataverse_citationdatasetfieldtypes (dataverse_id, citationdatasetfieldtype_id) FROM stdin;
\.


--
-- TOC entry 4149 (class 0 OID 16648)
-- Dependencies: 268
-- Data for Name: dataverse_metadatablock; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataverse_metadatablock (dataverse_id, metadatablocks_id) FROM stdin;
1	1
2	1
2	8
557	1
\.


--
-- TOC entry 4150 (class 0 OID 16651)
-- Dependencies: 269
-- Data for Name: dataversecontact; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataversecontact (id, contactemail, displayorder, dataverse_id) FROM stdin;
1	root@mailinator.com	0	1
2	dataverse@mailinator.com	0	2
3	dataverse@mailinator.com	0	3
4	dataverse@mailinator.com	0	4
5	dataverse@mailinator.com	0	5
6	dataverse@mailinator.com	0	557
7	dataverse@mailinator.com	0	558
8	dataverse@mailinator.com	0	563
9	dataverse@mailinator.com	0	6988
10	dataverse@mailinator.com	0	7228
\.


--
-- TOC entry 4152 (class 0 OID 16656)
-- Dependencies: 271
-- Data for Name: dataversefacet; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataversefacet (id, displayorder, datasetfieldtype_id, dataverse_id) FROM stdin;
1	3	58	1
2	2	22	1
3	0	9	1
4	1	20	1
114	0	9	557
115	1	10	557
116	2	65	557
117	3	58	2
118	4	211	2
119	5	212	2
120	7	242	2
121	1	20	2
122	8	243	2
123	2	22	2
124	6	215	2
125	0	9	2
\.


--
-- TOC entry 4154 (class 0 OID 16661)
-- Dependencies: 273
-- Data for Name: dataversefeatureddataverse; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataversefeatureddataverse (id, displayorder, dataverse_id, featureddataverse_id) FROM stdin;
\.


--
-- TOC entry 4156 (class 0 OID 16666)
-- Dependencies: 275
-- Data for Name: dataversefieldtypeinputlevel; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataversefieldtypeinputlevel (id, include, required, datasetfieldtype_id, dataverse_id) FROM stdin;
1141	f	f	30	557
1142	f	f	77	557
1143	f	f	71	557
1144	f	f	35	557
1145	f	f	39	557
1146	f	f	73	557
1147	f	f	38	557
1149	f	f	61	557
1150	f	f	28	557
1153	f	f	53	557
1154	f	f	63	557
1155	f	f	32	557
1156	f	f	42	557
1157	f	f	64	557
1158	f	f	34	557
1159	f	f	57	557
1160	f	f	78	557
1161	f	f	51	557
1162	f	f	75	557
1163	f	f	26	557
1165	f	f	22	557
1167	f	f	60	557
1168	f	f	6	557
1169	f	f	46	557
1170	f	f	4	557
1171	f	f	43	557
1173	f	f	38	2
1174	f	f	3	2
1176	f	f	54	2
1177	f	f	41	2
1178	f	f	60	2
1179	f	f	64	2
1182	f	f	61	2
1185	f	f	22	2
1187	f	f	28	2
1188	f	f	74	2
1189	f	f	27	2
1190	f	f	42	2
1191	f	f	6	2
1193	f	f	30	2
1194	f	f	40	2
1195	f	f	76	2
1196	f	f	24	2
1197	f	f	77	2
1198	f	f	7	2
1199	f	f	56	2
1200	f	f	31	2
1201	f	f	43	2
1202	f	f	4	2
1203	f	f	46	2
1204	f	f	68	2
1205	f	f	63	2
1206	f	f	58	2
1207	f	f	72	2
1208	f	f	53	2
1210	f	f	45	2
1211	f	f	71	2
1212	f	f	23	2
1213	f	f	70	2
1214	f	f	32	2
1215	f	f	57	2
1217	f	f	52	2
1219	f	f	67	2
1220	f	f	48	2
1221	f	f	49	2
1222	f	f	73	2
1223	f	f	78	2
1224	f	f	34	2
1225	f	f	65	2
1227	f	f	37	2
1228	f	f	75	2
1229	f	f	55	2
1230	f	f	35	2
1231	f	f	26	2
1232	f	f	51	2
1234	f	f	33	2
1111	f	f	3	557
1112	f	f	56	557
1113	f	f	33	557
1114	f	f	40	557
1115	f	f	67	557
1116	f	f	70	557
1117	f	f	68	557
1118	f	f	58	557
1119	f	f	55	557
1120	f	f	2	557
1121	f	f	27	557
1122	f	f	49	557
1124	f	f	45	557
1126	f	f	48	557
1127	f	f	24	557
1128	f	f	52	557
1129	f	f	23	557
1130	f	f	41	557
1131	f	f	37	557
1132	f	f	31	557
1133	f	f	72	557
1134	f	f	76	557
1135	f	f	74	557
1139	f	f	54	557
1140	f	f	7	557
1235	f	f	39	2
1236	t	t	21	557
1237	t	t	29	2
1238	t	t	47	2
1239	t	t	59	2
1240	t	t	47	557
1241	t	t	69	2
1242	t	t	66	557
1243	t	t	29	557
1244	t	t	44	2
1245	t	t	66	2
1246	t	t	50	2
1247	t	t	5	557
1248	t	t	50	557
1249	t	t	44	557
1250	t	t	36	557
1251	t	t	5	2
1252	t	t	25	2
1253	t	t	25	557
1254	t	t	36	2
1255	t	t	69	557
1256	t	t	59	557
1257	t	t	62	2
1258	t	t	21	2
1259	t	t	62	557
\.


--
-- TOC entry 4158 (class 0 OID 16671)
-- Dependencies: 277
-- Data for Name: dataverselinkingdataverse; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataverselinkingdataverse (id, linkcreatetime, dataverse_id, linkingdataverse_id) FROM stdin;
\.


--
-- TOC entry 4160 (class 0 OID 16676)
-- Dependencies: 279
-- Data for Name: dataverserole; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataverserole (id, alias, description, name, permissionbits, owner_id) FROM stdin;
1	admin	A person who has all permissions for dataverses, datasets, and files.	Admin	8191	\N
2	fileDownloader	A person who can download a published file.	File Downloader	16	\N
3	fullContributor	A person who can add subdataverses and datasets within a dataverse.	Dataverse + Dataset Creator	3	\N
4	dvContributor	A person who can add subdataverses within a dataverse.	Dataverse Creator	1	\N
5	dsContributor	A person who can add datasets within a dataverse.	Dataset Creator	2	\N
6	contributor	For datasets, a person who can edit License + Terms, and then submit them for review.	Contributor	4184	\N
7	curator	For datasets, a person who can edit License + Terms, edit Permissions, and publish datasets.	Curator	5471	\N
8	member	A person who can view both unpublished dataverses and datasets.	Member	28	\N
\.


--
-- TOC entry 4162 (class 0 OID 16684)
-- Dependencies: 281
-- Data for Name: dataversesubjects; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataversesubjects (dataverse_id, controlledvocabularyvalue_id) FROM stdin;
\.


--
-- TOC entry 4163 (class 0 OID 16687)
-- Dependencies: 282
-- Data for Name: dataversetheme; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dataversetheme (id, backgroundcolor, linkcolor, linkurl, logo, logoalignment, logobackgroundcolor, logofooter, logofooteralignment, logofooterbackgroundcolor, logoformat, tagline, textcolor, dataverse_id) FROM stdin;
\.


--
-- TOC entry 4165 (class 0 OID 16695)
-- Dependencies: 284
-- Data for Name: defaultvalueset; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.defaultvalueset (id, name) FROM stdin;
\.


--
-- TOC entry 4167 (class 0 OID 16700)
-- Dependencies: 286
-- Data for Name: doidataciteregistercache; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.doidataciteregistercache (id, doi, status, url, xml) FROM stdin;
\.


--
-- TOC entry 4169 (class 0 OID 16708)
-- Dependencies: 288
-- Data for Name: dvobject; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.dvobject (id, dtype, authority, createdate, globalidcreatetime, identifier, identifierregistered, indextime, modificationtime, permissionindextime, permissionmodificationtime, previewimageavailable, protocol, publicationdate, storageidentifier, creator_id, owner_id, releaseuser_id) FROM stdin;
1	Dataverse	\N	2021-03-16 07:08:47.712	\N	\N	f	\N	2021-03-16 07:08:48.091	\N	2021-03-16 07:08:47.755	f	\N	\N	\N	1	\N	\N
4	Dataverse	\N	2021-03-16 08:11:00.429	\N	\N	f	2021-05-25 09:01:43.947	2021-05-25 09:01:43.855	2021-05-25 09:01:44.212	2021-03-16 08:11:00.439	f	\N	\N	\N	1	2	\N
557	Dataverse	\N	2021-04-08 14:25:17.197	\N	\N	f	2021-05-25 09:03:36.187	2021-05-25 09:03:35.944	2021-05-25 09:03:36.235	2021-04-08 14:25:18.03	f	\N	\N	\N	1	1	\N
7228	Dataverse	\N	2021-05-05 14:21:07.532	\N	\N	f	2021-05-25 09:03:57.937	2021-05-25 09:03:57.828	2021-05-25 09:03:58.483	2021-05-05 14:21:07.544	f	\N	\N	\N	1	557	\N
563	Dataverse	\N	2021-04-08 15:04:14.333	\N	\N	f	2021-06-09 09:25:36.461	2021-05-25 09:03:54.793	2021-06-09 09:25:36.493	2021-04-08 15:04:14.374	f	\N	\N	\N	1	557	\N
3	Dataverse	\N	2021-03-16 08:10:15.254	\N	\N	f	2021-06-09 09:57:26.285	2021-05-25 09:03:58.953	2021-06-09 09:57:26.317	2021-03-16 08:10:15.268	f	\N	\N	\N	1	2	\N
5	Dataverse	\N	2021-03-16 08:11:54.429	\N	\N	f	2021-06-09 09:58:19.915	2021-05-25 09:03:55.945	2021-06-09 09:58:19.948	2021-03-16 08:11:54.438	f	\N	\N	\N	1	2	\N
6988	Dataverse	\N	2021-05-03 07:41:51.797	\N	\N	f	2021-05-25 09:03:57.014	2021-05-25 09:03:56.959	2021-05-25 09:03:57.053	2021-05-03 07:41:51.83	f	\N	\N	\N	1	2	\N
2	Dataverse	\N	2021-03-16 08:05:31.701	\N	\N	f	2021-05-25 09:04:00.434	2021-05-25 09:04:00.261	2021-05-25 09:04:00.501	2021-03-16 08:05:31.801	f	\N	\N	\N	1	1	\N
558	Dataverse	\N	2021-04-08 14:30:23.139	\N	\N	f	2021-05-25 09:04:05.836	2021-05-25 09:04:05.723	2021-05-25 09:04:05.907	2021-04-08 14:30:23.146	f	\N	\N	\N	1	557	\N
\.


--
-- TOC entry 4171 (class 0 OID 16716)
-- Dependencies: 290
-- Data for Name: explicitgroup; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.explicitgroup (id, description, displayname, groupalias, groupaliasinowner, owner_id) FROM stdin;
\.


--
-- TOC entry 4172 (class 0 OID 16722)
-- Dependencies: 291
-- Data for Name: explicitgroup_authenticateduser; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.explicitgroup_authenticateduser (explicitgroup_id, containedauthenticatedusers_id) FROM stdin;
\.


--
-- TOC entry 4173 (class 0 OID 16725)
-- Dependencies: 292
-- Data for Name: explicitgroup_containedroleassignees; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.explicitgroup_containedroleassignees (explicitgroup_id, containedroleassignees) FROM stdin;
\.


--
-- TOC entry 4174 (class 0 OID 16728)
-- Dependencies: 293
-- Data for Name: explicitgroup_explicitgroup; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.explicitgroup_explicitgroup (explicitgroup_id, containedexplicitgroups_id) FROM stdin;
\.


--
-- TOC entry 4176 (class 0 OID 16733)
-- Dependencies: 295
-- Data for Name: externaltool; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.externaltool (id, contenttype, description, displayname, scope, toolname, toolparameters, toolurl) FROM stdin;
\.


--
-- TOC entry 4178 (class 0 OID 16741)
-- Dependencies: 297
-- Data for Name: externaltooltype; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.externaltooltype (id, type, externaltool_id) FROM stdin;
\.


--
-- TOC entry 4180 (class 0 OID 16746)
-- Dependencies: 299
-- Data for Name: fileaccessrequests; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.fileaccessrequests (datafile_id, authenticated_user_id) FROM stdin;
\.


--
-- TOC entry 4181 (class 0 OID 16749)
-- Dependencies: 300
-- Data for Name: filedownload; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.filedownload (downloadtimestamp, downloadtype, guestbookresponse_id, sessionid) FROM stdin;
\.


--
-- TOC entry 4182 (class 0 OID 16755)
-- Dependencies: 301
-- Data for Name: filemetadata; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.filemetadata (id, description, directorylabel, label, prov_freeform, restricted, version, datafile_id, datasetversion_id) FROM stdin;
\.


--
-- TOC entry 4183 (class 0 OID 16761)
-- Dependencies: 302
-- Data for Name: filemetadata_datafilecategory; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.filemetadata_datafilecategory (filecategories_id, filemetadatas_id) FROM stdin;
\.


--
-- TOC entry 4185 (class 0 OID 16766)
-- Dependencies: 304
-- Data for Name: flyway_schema_history; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success) FROM stdin;
1	1	<< Flyway Baseline >>	BASELINE	<< Flyway Baseline >>	\N	dataverse	2021-03-16 07:07:58.436824	0	t
2	4.11	5513-database-variablemetadata	SQL	V4.11__5513-database-variablemetadata.sql	767369850	dataverse	2021-03-16 07:07:58.480326	4	t
3	4.11.0.1	5565-sanitize-directory-labels	SQL	V4.11.0.1__5565-sanitize-directory-labels.sql	-274470039	dataverse	2021-03-16 07:07:58.491722	5	t
4	4.12.0.1	4.13-re-sanitize-filemetadata	SQL	V4.12.0.1__4.13-re-sanitize-filemetadata.sql	-95635412	dataverse	2021-03-16 07:07:58.504064	4	t
5	4.13.0.1	3575-usernames	SQL	V4.13.0.1__3575-usernames.sql	1916037265	dataverse	2021-03-16 07:07:58.513867	4	t
6	4.14.0.1	5822-export-var-meta	SQL	V4.14.0.1__5822-export-var-meta.sql	2019772659	dataverse	2021-03-16 07:07:58.525723	2	t
7	4.15.0.1	2043-split-gbr-table	SQL	V4.15.0.1__2043-split-gbr-table.sql	-1955706731	dataverse	2021-03-16 07:07:58.533522	9	t
8	4.16.0.1	5303-addColumn-to-settingTable	SQL	V4.16.0.1__5303-addColumn-to-settingTable.sql	1442682945	dataverse	2021-03-16 07:07:58.55096	12	t
9	4.16.0.2	5028-dataset-explore	SQL	V4.16.0.2__5028-dataset-explore.sql	797098461	dataverse	2021-03-16 07:07:58.569485	2	t
10	4.16.0.3	6156-FooterImageforSub-Dataverse	SQL	V4.16.0.3__6156-FooterImageforSub-Dataverse.sql	-88679435	dataverse	2021-03-16 07:07:58.582616	1	t
11	4.17.0.1	5991-update-scribejava	SQL	V4.17.0.1__5991-update-scribejava.sql	-1195698165	dataverse	2021-03-16 07:07:58.592425	1	t
12	4.17.0.2	3578-file-page-preview	SQL	V4.17.0.2__3578-file-page-preview.sql	-4976721	dataverse	2021-03-16 07:07:58.602014	2	t
13	4.18.1.1	6459-contenttype-nullable	SQL	V4.18.1.1__6459-contenttype-nullable.sql	-294036505	dataverse	2021-03-16 07:07:58.612421	1	t
14	4.19.0.1	6485 multistore	SQL	V4.19.0.1__6485_multistore.sql	-889428141	dataverse	2021-03-16 07:07:58.620236	2	t
15	4.19.0.2	6644-update-editor-role-alias	SQL	V4.19.0.2__6644-update-editor-role-alias.sql	1822084145	dataverse	2021-03-16 07:07:58.631586	1	t
16	4.20.0.1	2734-alter-data-table-add-orig-file-name	SQL	V4.20.0.1__2734-alter-data-table-add-orig-file-name.sql	-842134191	dataverse	2021-03-16 07:07:58.642289	1	t
17	4.20.0.2	6748-configure-dropdown-toolname	SQL	V4.20.0.2__6748-configure-dropdown-toolname.sql	-222908387	dataverse	2021-03-16 07:07:58.649985	1	t
18	4.20.0.3	6558-file-validation	SQL	V4.20.0.3__6558-file-validation.sql	1209461763	dataverse	2021-03-16 07:07:58.655897	1	t
19	4.20.0.4	6936-maildomain-groups	SQL	V4.20.0.4__6936-maildomain-groups.sql	576953306	dataverse	2021-03-16 07:07:58.664576	1	t
20	4.20.0.5	6505-zipdownload-jobs	SQL	V4.20.0.5__6505-zipdownload-jobs.sql	-409990981	dataverse	2021-03-16 07:07:58.675259	4	t
21	5.0.0.1	6872-assign-storage-drivers-to-datasets	SQL	V5.0.0.1__6872-assign-storage-drivers-to-datasets.sql	-2016308089	dataverse	2021-03-16 07:07:58.684392	1	t
22	5.1.1.1	7344-maildomaingroups-add-regex-flag	SQL	V5.1.1.1__7344-maildomaingroups-add-regex-flag.sql	1450587934	dataverse	2021-07-12 12:36:22.986361	41	t
23	5.1.1.2	6919-preview-tools	SQL	V5.1.1.2__6919-preview-tools.sql	-806791328	dataverse	2021-07-12 12:36:23.043038	14	t
24	5.2.0.1	7256-purge-referencedata	SQL	V5.2.0.1__7256-purge-referencedata.sql	-1946003894	dataverse	2021-07-12 12:36:23.065991	7	t
25	5.3.0.1	7409-remove-worldmap-geoconnect	SQL	V5.3.0.1__7409-remove-worldmap-geoconnect.sql	-1002675895	dataverse	2021-07-12 12:36:23.083317	13	t
26	5.3.0.2	7512-update-notification-types	SQL	V5.3.0.2__7512-update-notification-types.sql	1667988938	dataverse	2021-07-12 12:36:23.107103	3	t
27	5.3.0.3	7551-expanded-compound-datasetfield-validation	SQL	V5.3.0.3__7551-expanded-compound-datasetfield-validation.sql	-1982379152	dataverse	2021-07-12 12:36:23.121845	8	t
28	5.3.0.4	7563-workflow	SQL	V5.3.0.4__7563-workflow.sql	-1920845036	dataverse	2021-07-12 12:36:23.13955	2	t
29	5.3.0.5	7564-workflow	SQL	V5.3.0.5__7564-workflow.sql	-1999395162	dataverse	2021-07-12 12:36:23.148454	1	t
30	5.3.0.6	2419-deactivate-users	SQL	V5.3.0.6__2419-deactivate-users.sql	-655412585	dataverse	2021-07-12 12:36:23.156686	6	t
31	5.4.1.1	7400-opendp-download	SQL	V5.4.1.1__7400-opendp-download.sql	-949830723	dataverse	2021-07-12 12:36:23.17	2	t
32	5.5.0.1	7826-set-deactivated-not-null	SQL	V5.5.0.1__7826-set-deactivated-not-null.sql	-1012465059	dataverse	2021-07-12 12:36:23.179439	2	t
33	5.5.0.2	7177-newmetrics	SQL	V5.5.0.2__7177-newmetrics.sql	-1326087984	dataverse	2021-07-12 12:36:23.188049	4	t
\.


--
-- TOC entry 4186 (class 0 OID 16773)
-- Dependencies: 305
-- Data for Name: foreignmetadatafieldmapping; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.foreignmetadatafieldmapping (id, datasetfieldname, foreignfieldxpath, isattribute, foreignmetadataformatmapping_id, parentfieldmapping_id) FROM stdin;
1	title	:title	f	1	\N
2	otherIdValue	:identifier	f	1	\N
3	authorName	:creator	f	1	\N
4	productionDate	:date	f	1	\N
5	keywordValue	:subject	f	1	\N
6	dsDescriptionValue	:description	f	1	\N
7	relatedMaterial	:relation	f	1	\N
8	publicationCitation	:isReferencedBy	f	1	\N
9	publicationURL	holdingsURI	t	1	8
10	publicationIDType	agency	t	1	8
11	publicationIDNumber	IDNo	t	1	8
12	otherGeographicCoverage	:coverage	f	1	\N
13	kindOfData	:type	f	1	\N
14	dataSources	:source	f	1	\N
15	authorAffiliation	affiliation	t	1	3
16	contributorName	:contributor	f	1	\N
17	contributorType	type	t	1	16
18	producerName	:publisher	f	1	\N
19	language	:language	f	1	\N
\.


--
-- TOC entry 4188 (class 0 OID 16781)
-- Dependencies: 307
-- Data for Name: foreignmetadataformatmapping; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.foreignmetadataformatmapping (id, displayname, name, schemalocation, startelement) FROM stdin;
1	dcterms: DCMI Metadata Terms	http://purl.org/dc/terms/	http://dublincore.org/schemas/xmls/qdc/dcterms.xsd	entry
\.


--
-- TOC entry 4190 (class 0 OID 16789)
-- Dependencies: 309
-- Data for Name: guestbook; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.guestbook (id, createtime, emailrequired, enabled, institutionrequired, name, namerequired, positionrequired, dataverse_id) FROM stdin;
1	2021-03-16 07:08:39.2542	f	t	f	Default	f	f	\N
2	2021-04-21 09:40:30.625174	f	t	f	Default	f	f	\N
3	2021-04-22 07:34:25.797428	f	t	f	Default	f	f	\N
4	2021-04-27 07:45:36.20207	f	t	f	Default	f	f	\N
5	2021-04-28 08:39:53.246205	f	t	f	Default	f	f	\N
6	2021-05-11 15:27:30.813281	f	t	f	Default	f	f	\N
7	2021-05-11 16:09:08.599639	f	t	f	Default	f	f	\N
8	2021-05-11 16:20:10.264083	f	t	f	Default	f	f	\N
9	2021-05-12 06:14:49.7425	f	t	f	Default	f	f	\N
10	2021-05-25 08:46:24.987941	f	t	f	Default	f	f	\N
\.


--
-- TOC entry 4192 (class 0 OID 16794)
-- Dependencies: 311
-- Data for Name: guestbookresponse; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.guestbookresponse (id, email, institution, name, "position", responsetime, authenticateduser_id, datafile_id, dataset_id, datasetversion_id, guestbook_id) FROM stdin;
\.


--
-- TOC entry 4194 (class 0 OID 16802)
-- Dependencies: 313
-- Data for Name: harvestingclient; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.harvestingclient (id, archivedescription, archiveurl, deleted, harveststyle, harvesttype, harvestingnow, harvestingset, harvestingurl, metadataprefix, name, scheduledayofweek, schedulehourofday, scheduleperiod, scheduled, dataverse_id) FROM stdin;
\.


--
-- TOC entry 4196 (class 0 OID 16810)
-- Dependencies: 315
-- Data for Name: harvestingdataverseconfig; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.harvestingdataverseconfig (id, archivedescription, archiveurl, harveststyle, harvesttype, harvestingset, harvestingurl, dataverse_id) FROM stdin;
\.


--
-- TOC entry 4197 (class 0 OID 16816)
-- Dependencies: 316
-- Data for Name: ingestreport; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.ingestreport (id, endtime, report, starttime, status, type, datafile_id) FROM stdin;
\.


--
-- TOC entry 4199 (class 0 OID 16824)
-- Dependencies: 318
-- Data for Name: ingestrequest; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.ingestrequest (id, controlcard, forcetypecheck, labelsfile, textencoding, datafile_id) FROM stdin;
\.


--
-- TOC entry 4201 (class 0 OID 16832)
-- Dependencies: 320
-- Data for Name: ipv4range; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.ipv4range (id, bottomaslong, topaslong, owner_id) FROM stdin;
\.


--
-- TOC entry 4202 (class 0 OID 16835)
-- Dependencies: 321
-- Data for Name: ipv6range; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.ipv6range (id, bottoma, bottomb, bottomc, bottomd, topa, topb, topc, topd, owner_id) FROM stdin;
\.


--
-- TOC entry 4203 (class 0 OID 16838)
-- Dependencies: 322
-- Data for Name: metadatablock; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.metadatablock (id, displayname, name, namespaceuri, owner_id) FROM stdin;
1	Citation Metadata	citation	https://dataverse.org/schema/citation/	\N
2	Geospatial Metadata	geospatial	\N	\N
3	Social Science and Humanities Metadata	socialscience	\N	\N
4	Astronomy and Astrophysics Metadata	astrophysics	\N	\N
5	Life Sciences Metadata	biomedical	\N	\N
6	Journal Metadata	journal	\N	\N
7	CESSDA Metadata Model	cmm-cessda	\N	\N
8	Rudi Metadata	rudi	\N	\N
\.


--
-- TOC entry 4205 (class 0 OID 16846)
-- Dependencies: 324
-- Data for Name: metric; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.metric (id, datalocation, daystring, lastcalleddate, name, valuejson, dataverse_id) FROM stdin;
\.


--
-- TOC entry 4207 (class 0 OID 16854)
-- Dependencies: 326
-- Data for Name: oairecord; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.oairecord (id, globalid, lastupdatetime, removed, setname) FROM stdin;
\.


--
-- TOC entry 4209 (class 0 OID 16862)
-- Dependencies: 328
-- Data for Name: oaiset; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.oaiset (id, definition, deleted, description, name, spec, updateinprogress, version) FROM stdin;
\.


--
-- TOC entry 4211 (class 0 OID 16870)
-- Dependencies: 330
-- Data for Name: oauth2tokendata; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.oauth2tokendata (id, accesstoken, expirydate, oauthproviderid, rawresponse, refreshtoken, tokentype, user_id) FROM stdin;
\.


--
-- TOC entry 4213 (class 0 OID 16878)
-- Dependencies: 332
-- Data for Name: passwordresetdata; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.passwordresetdata (id, created, expires, reason, token, builtinuser_id) FROM stdin;
\.


--
-- TOC entry 4215 (class 0 OID 16886)
-- Dependencies: 334
-- Data for Name: pendingworkflowinvocation; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.pendingworkflowinvocation (invocationid, datasetexternallyreleased, ipaddress, nextminorversionnumber, nextversionnumber, pendingstepidx, typeordinal, userid, workflow_id, dataset_id, lockid) FROM stdin;
\.


--
-- TOC entry 4216 (class 0 OID 16892)
-- Dependencies: 335
-- Data for Name: pendingworkflowinvocation_localdata; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.pendingworkflowinvocation_localdata (pendingworkflowinvocation_invocationid, localdata, localdata_key) FROM stdin;
\.


--
-- TOC entry 4217 (class 0 OID 16898)
-- Dependencies: 336
-- Data for Name: persistedglobalgroup; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.persistedglobalgroup (id, dtype, description, displayname, persistedgroupalias, emaildomains, isregex) FROM stdin;
\.


--
-- TOC entry 4218 (class 0 OID 16905)
-- Dependencies: 337
-- Data for Name: roleassignment; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.roleassignment (id, assigneeidentifier, privateurltoken, definitionpoint_id, role_id) FROM stdin;
1	@dataverseAdmin	\N	1	1
2	@dataverseAdmin	\N	2	1
3	@dataverseAdmin	\N	3	1
4	@dataverseAdmin	\N	4	1
5	@dataverseAdmin	\N	5	1
557	@dataverseAdmin	\N	557	1
558	@dataverseAdmin	\N	558	1
563	@dataverseAdmin	\N	563	1
6981	@dataverseAdmin	\N	6988	1
7221	@dataverseAdmin	\N	7228	1
\.


--
-- TOC entry 4220 (class 0 OID 16913)
-- Dependencies: 339
-- Data for Name: savedsearch; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.savedsearch (id, query, creator_id, definitionpoint_id) FROM stdin;
\.


--
-- TOC entry 4222 (class 0 OID 16921)
-- Dependencies: 341
-- Data for Name: savedsearchfilterquery; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.savedsearchfilterquery (id, filterquery, savedsearch_id) FROM stdin;
\.


--
-- TOC entry 4224 (class 0 OID 16929)
-- Dependencies: 343
-- Data for Name: sequence; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.sequence (seq_name, seq_count) FROM stdin;
SEQ_GEN	0
\.


--
-- TOC entry 4225 (class 0 OID 16932)
-- Dependencies: 344
-- Data for Name: setting; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.setting (id, content, lang, name) FROM stdin;
1	yes	\N	:AllowSignUp
2	/dataverseuser.xhtml?editMode=CREATE	\N	:SignUpUrl
3	doi	\N	:Protocol
4	10.5072	\N	:Authority
5	FK2/	\N	:Shoulder
6	FAKE	\N	:DoiProvider
7	burrito	\N	BuiltinUsers.KEY
8	localhost-only	\N	:BlockedApiPolicy
9	native/http	\N	:UploadMethods
10	solr:8983	\N	:SolrHostColonPort
11	[{\n  "vocab-name":"topicClassification",\n  "cvm-url":"https://ns.dataverse.org.ua/",\n  "language":"en",\n  "vocabs":["mesh"],\n  "protocol": "skosmos",\n  "vocab-codes": ["topicClassVocab","topicClassValue","topicClassVocabURI"]\n},\n{\n  "vocab-name":"keyword",\n  "cvm-url":"https://ns.dataverse.org.ua/",\n  "language":"en",\n  "protocol": "skosmos",\n  "vocabs":["unesco","mesh","dansorg","wikidata","thesaurus","iptc","agrovoc","faechersystematik"],\n  "vocab-codes": ["keywordVocabulary", "keywordValue", "keywordVocabularyURI"]\n}]\n	\N	:CVMConf
\.


--
-- TOC entry 4228 (class 0 OID 16943)
-- Dependencies: 347
-- Data for Name: shibgroup; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.shibgroup (id, attribute, name, pattern) FROM stdin;
\.


--
-- TOC entry 4230 (class 0 OID 16951)
-- Dependencies: 349
-- Data for Name: storagesite; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.storagesite (id, hostname, name, primarystorage, transferprotocols) FROM stdin;
\.


--
-- TOC entry 4232 (class 0 OID 16959)
-- Dependencies: 351
-- Data for Name: summarystatistic; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.summarystatistic (id, type, value, datavariable_id) FROM stdin;
\.


--
-- TOC entry 4234 (class 0 OID 16964)
-- Dependencies: 353
-- Data for Name: template; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.template (id, createtime, name, usagecount, dataverse_id, termsofuseandaccess_id) FROM stdin;
\.


--
-- TOC entry 4236 (class 0 OID 16969)
-- Dependencies: 355
-- Data for Name: termsofuseandaccess; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.termsofuseandaccess (id, availabilitystatus, citationrequirements, conditions, confidentialitydeclaration, contactforaccess, dataaccessplace, depositorrequirements, disclaimer, fileaccessrequest, license, originalarchive, restrictions, sizeofcollection, specialpermissions, studycompletion, termsofaccess, termsofuse) FROM stdin;
\.


--
-- TOC entry 4238 (class 0 OID 16977)
-- Dependencies: 357
-- Data for Name: userbannermessage; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.userbannermessage (id, bannerdismissaltime, bannermessage_id, user_id) FROM stdin;
\.


--
-- TOC entry 4240 (class 0 OID 16982)
-- Dependencies: 359
-- Data for Name: usernotification; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.usernotification (id, emailed, objectid, readnotification, senddate, type, requestor_id, user_id) FROM stdin;
\.


--
-- TOC entry 4242 (class 0 OID 16987)
-- Dependencies: 361
-- Data for Name: vargroup; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.vargroup (id, label, filemetadata_id) FROM stdin;
\.


--
-- TOC entry 4243 (class 0 OID 16993)
-- Dependencies: 362
-- Data for Name: vargroup_datavariable; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.vargroup_datavariable (vargroup_id, varsingroup_id) FROM stdin;
\.


--
-- TOC entry 4245 (class 0 OID 16998)
-- Dependencies: 364
-- Data for Name: variablecategory; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.variablecategory (id, catorder, frequency, label, missing, value, datavariable_id) FROM stdin;
\.


--
-- TOC entry 4247 (class 0 OID 17006)
-- Dependencies: 366
-- Data for Name: variablemetadata; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.variablemetadata (id, interviewinstruction, isweightvar, label, literalquestion, notes, postquestion, universe, weighted, datavariable_id, filemetadata_id, weightvariable_id) FROM stdin;
\.


--
-- TOC entry 4249 (class 0 OID 17014)
-- Dependencies: 368
-- Data for Name: variablerange; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.variablerange (id, beginvalue, beginvaluetype, endvalue, endvaluetype, datavariable_id) FROM stdin;
\.


--
-- TOC entry 4251 (class 0 OID 17022)
-- Dependencies: 370
-- Data for Name: variablerangeitem; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.variablerangeitem (id, value, datavariable_id) FROM stdin;
\.


--
-- TOC entry 4253 (class 0 OID 17027)
-- Dependencies: 372
-- Data for Name: workflow; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.workflow (id, name) FROM stdin;
\.


--
-- TOC entry 4255 (class 0 OID 17032)
-- Dependencies: 374
-- Data for Name: workflowcomment; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.workflowcomment (id, created, message, type, authenticateduser_id, datasetversion_id, tobeshown) FROM stdin;
\.


--
-- TOC entry 4257 (class 0 OID 17040)
-- Dependencies: 376
-- Data for Name: workflowstepdata; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.workflowstepdata (id, providerid, steptype, parent_id, index) FROM stdin;
\.


--
-- TOC entry 4259 (class 0 OID 17048)
-- Dependencies: 378
-- Data for Name: workflowstepdata_stepparameters; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.workflowstepdata_stepparameters (workflowstepdata_id, stepparameters, stepparameters_key) FROM stdin;
\.


--
-- TOC entry 4260 (class 0 OID 17054)
-- Dependencies: 379
-- Data for Name: workflowstepdata_stepsettings; Type: TABLE DATA; Schema: public; Owner: dataverse
--

COPY public.workflowstepdata_stepsettings (workflowstepdata_id, stepsettings, stepsettings_key) FROM stdin;
\.


--
-- TOC entry 4345 (class 0 OID 0)
-- Dependencies: 199
-- Name: alternativepersistentidentifier_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.alternativepersistentidentifier_id_seq', 1, false);


--
-- TOC entry 4346 (class 0 OID 0)
-- Dependencies: 201
-- Name: apitoken_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.apitoken_id_seq', 1, true);


--
-- TOC entry 4347 (class 0 OID 0)
-- Dependencies: 203
-- Name: authenticateduser_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.authenticateduser_id_seq', 1, true);


--
-- TOC entry 4348 (class 0 OID 0)
-- Dependencies: 205
-- Name: authenticateduserlookup_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.authenticateduserlookup_id_seq', 1, true);


--
-- TOC entry 4349 (class 0 OID 0)
-- Dependencies: 208
-- Name: auxiliaryfile_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.auxiliaryfile_id_seq', 1, false);


--
-- TOC entry 4350 (class 0 OID 0)
-- Dependencies: 210
-- Name: bannermessage_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.bannermessage_id_seq', 1, false);


--
-- TOC entry 4351 (class 0 OID 0)
-- Dependencies: 212
-- Name: bannermessagetext_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.bannermessagetext_id_seq', 1, false);


--
-- TOC entry 4352 (class 0 OID 0)
-- Dependencies: 214
-- Name: builtinuser_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.builtinuser_id_seq', 1, true);


--
-- TOC entry 4353 (class 0 OID 0)
-- Dependencies: 216
-- Name: categorymetadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.categorymetadata_id_seq', 1, false);


--
-- TOC entry 4354 (class 0 OID 0)
-- Dependencies: 218
-- Name: clientharvestrun_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.clientharvestrun_id_seq', 1, false);


--
-- TOC entry 4355 (class 0 OID 0)
-- Dependencies: 220
-- Name: confirmemaildata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.confirmemaildata_id_seq', 1, true);


--
-- TOC entry 4356 (class 0 OID 0)
-- Dependencies: 222
-- Name: controlledvocabalternate_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.controlledvocabalternate_id_seq', 23, true);


--
-- TOC entry 4357 (class 0 OID 0)
-- Dependencies: 224
-- Name: controlledvocabularyvalue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.controlledvocabularyvalue_id_seq', 837, true);


--
-- TOC entry 4358 (class 0 OID 0)
-- Dependencies: 226
-- Name: customfieldmap_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.customfieldmap_id_seq', 1, false);


--
-- TOC entry 4359 (class 0 OID 0)
-- Dependencies: 228
-- Name: customquestion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.customquestion_id_seq', 1, false);


--
-- TOC entry 4360 (class 0 OID 0)
-- Dependencies: 230
-- Name: customquestionresponse_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.customquestionresponse_id_seq', 1, false);


--
-- TOC entry 4361 (class 0 OID 0)
-- Dependencies: 232
-- Name: customquestionvalue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.customquestionvalue_id_seq', 1, false);


--
-- TOC entry 4362 (class 0 OID 0)
-- Dependencies: 236
-- Name: datafilecategory_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datafilecategory_id_seq', 1, true);


--
-- TOC entry 4363 (class 0 OID 0)
-- Dependencies: 238
-- Name: datafiletag_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datafiletag_id_seq', 1, false);


--
-- TOC entry 4364 (class 0 OID 0)
-- Dependencies: 243
-- Name: datasetfield_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetfield_id_seq', 727190, true);


--
-- TOC entry 4365 (class 0 OID 0)
-- Dependencies: 245
-- Name: datasetfieldcompoundvalue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetfieldcompoundvalue_id_seq', 136937, true);


--
-- TOC entry 4366 (class 0 OID 0)
-- Dependencies: 247
-- Name: datasetfielddefaultvalue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetfielddefaultvalue_id_seq', 1, false);


--
-- TOC entry 4367 (class 0 OID 0)
-- Dependencies: 249
-- Name: datasetfieldtype_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetfieldtype_id_seq', 301, true);


--
-- TOC entry 4368 (class 0 OID 0)
-- Dependencies: 251
-- Name: datasetfieldvalue_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetfieldvalue_id_seq', 598980, true);


--
-- TOC entry 4369 (class 0 OID 0)
-- Dependencies: 253
-- Name: datasetlinkingdataverse_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetlinkingdataverse_id_seq', 1, false);


--
-- TOC entry 4370 (class 0 OID 0)
-- Dependencies: 255
-- Name: datasetlock_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetlock_id_seq', 1226, true);


--
-- TOC entry 4371 (class 0 OID 0)
-- Dependencies: 257
-- Name: datasetmetrics_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetmetrics_id_seq', 1, false);


--
-- TOC entry 4372 (class 0 OID 0)
-- Dependencies: 259
-- Name: datasetversion_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetversion_id_seq', 9975, true);


--
-- TOC entry 4373 (class 0 OID 0)
-- Dependencies: 261
-- Name: datasetversionuser_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datasetversionuser_id_seq', 9975, true);


--
-- TOC entry 4374 (class 0 OID 0)
-- Dependencies: 263
-- Name: datatable_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datatable_id_seq', 1, false);


--
-- TOC entry 4375 (class 0 OID 0)
-- Dependencies: 265
-- Name: datavariable_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.datavariable_id_seq', 1, false);


--
-- TOC entry 4376 (class 0 OID 0)
-- Dependencies: 270
-- Name: dataversecontact_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.dataversecontact_id_seq', 10, true);


--
-- TOC entry 4377 (class 0 OID 0)
-- Dependencies: 272
-- Name: dataversefacet_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.dataversefacet_id_seq', 125, true);


--
-- TOC entry 4378 (class 0 OID 0)
-- Dependencies: 274
-- Name: dataversefeatureddataverse_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.dataversefeatureddataverse_id_seq', 1, false);


--
-- TOC entry 4379 (class 0 OID 0)
-- Dependencies: 276
-- Name: dataversefieldtypeinputlevel_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.dataversefieldtypeinputlevel_id_seq', 1259, true);


--
-- TOC entry 4380 (class 0 OID 0)
-- Dependencies: 278
-- Name: dataverselinkingdataverse_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.dataverselinkingdataverse_id_seq', 1, false);


--
-- TOC entry 4381 (class 0 OID 0)
-- Dependencies: 280
-- Name: dataverserole_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.dataverserole_id_seq', 64, true);


--
-- TOC entry 4382 (class 0 OID 0)
-- Dependencies: 283
-- Name: dataversetheme_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.dataversetheme_id_seq', 1, false);


--
-- TOC entry 4383 (class 0 OID 0)
-- Dependencies: 285
-- Name: defaultvalueset_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.defaultvalueset_id_seq', 1, false);


--
-- TOC entry 4384 (class 0 OID 0)
-- Dependencies: 287
-- Name: doidataciteregistercache_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.doidataciteregistercache_id_seq', 1, false);


--
-- TOC entry 4385 (class 0 OID 0)
-- Dependencies: 289
-- Name: dvobject_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.dvobject_id_seq', 11105, true);


--
-- TOC entry 4386 (class 0 OID 0)
-- Dependencies: 294
-- Name: explicitgroup_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.explicitgroup_id_seq', 1, false);


--
-- TOC entry 4387 (class 0 OID 0)
-- Dependencies: 296
-- Name: externaltool_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.externaltool_id_seq', 1, false);


--
-- TOC entry 4388 (class 0 OID 0)
-- Dependencies: 298
-- Name: externaltooltype_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.externaltooltype_id_seq', 1, false);


--
-- TOC entry 4389 (class 0 OID 0)
-- Dependencies: 303
-- Name: filemetadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.filemetadata_id_seq', 1120, true);


--
-- TOC entry 4390 (class 0 OID 0)
-- Dependencies: 306
-- Name: foreignmetadatafieldmapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.foreignmetadatafieldmapping_id_seq', 1, false);


--
-- TOC entry 4391 (class 0 OID 0)
-- Dependencies: 308
-- Name: foreignmetadataformatmapping_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.foreignmetadataformatmapping_id_seq', 1, false);


--
-- TOC entry 4392 (class 0 OID 0)
-- Dependencies: 310
-- Name: guestbook_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.guestbook_id_seq', 10, true);


--
-- TOC entry 4393 (class 0 OID 0)
-- Dependencies: 312
-- Name: guestbookresponse_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.guestbookresponse_id_seq', 1, false);


--
-- TOC entry 4394 (class 0 OID 0)
-- Dependencies: 314
-- Name: harvestingclient_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.harvestingclient_id_seq', 1, false);


--
-- TOC entry 4395 (class 0 OID 0)
-- Dependencies: 317
-- Name: ingestreport_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.ingestreport_id_seq', 1, false);


--
-- TOC entry 4396 (class 0 OID 0)
-- Dependencies: 319
-- Name: ingestrequest_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.ingestrequest_id_seq', 1, false);


--
-- TOC entry 4397 (class 0 OID 0)
-- Dependencies: 323
-- Name: metadatablock_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.metadatablock_id_seq', 8, true);


--
-- TOC entry 4398 (class 0 OID 0)
-- Dependencies: 325
-- Name: metric_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.metric_id_seq', 1, false);


--
-- TOC entry 4399 (class 0 OID 0)
-- Dependencies: 327
-- Name: oairecord_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.oairecord_id_seq', 1, false);


--
-- TOC entry 4400 (class 0 OID 0)
-- Dependencies: 329
-- Name: oaiset_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.oaiset_id_seq', 1, false);


--
-- TOC entry 4401 (class 0 OID 0)
-- Dependencies: 331
-- Name: oauth2tokendata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.oauth2tokendata_id_seq', 1, false);


--
-- TOC entry 4402 (class 0 OID 0)
-- Dependencies: 333
-- Name: passwordresetdata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.passwordresetdata_id_seq', 1, true);


--
-- TOC entry 4403 (class 0 OID 0)
-- Dependencies: 338
-- Name: roleassignment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.roleassignment_id_seq', 9985, true);


--
-- TOC entry 4404 (class 0 OID 0)
-- Dependencies: 340
-- Name: savedsearch_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.savedsearch_id_seq', 1, false);


--
-- TOC entry 4405 (class 0 OID 0)
-- Dependencies: 342
-- Name: savedsearchfilterquery_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.savedsearchfilterquery_id_seq', 1, false);


--
-- TOC entry 4406 (class 0 OID 0)
-- Dependencies: 345
-- Name: setting_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.setting_id_seq', 11, true);


--
-- TOC entry 4407 (class 0 OID 0)
-- Dependencies: 346
-- Name: setting_id_seq1; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.setting_id_seq1', 1, false);


--
-- TOC entry 4408 (class 0 OID 0)
-- Dependencies: 348
-- Name: shibgroup_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.shibgroup_id_seq', 1, false);


--
-- TOC entry 4409 (class 0 OID 0)
-- Dependencies: 350
-- Name: storagesite_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.storagesite_id_seq', 1, false);


--
-- TOC entry 4410 (class 0 OID 0)
-- Dependencies: 352
-- Name: summarystatistic_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.summarystatistic_id_seq', 1, false);


--
-- TOC entry 4411 (class 0 OID 0)
-- Dependencies: 354
-- Name: template_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.template_id_seq', 1, false);


--
-- TOC entry 4412 (class 0 OID 0)
-- Dependencies: 356
-- Name: termsofuseandaccess_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.termsofuseandaccess_id_seq', 9975, true);


--
-- TOC entry 4413 (class 0 OID 0)
-- Dependencies: 358
-- Name: userbannermessage_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.userbannermessage_id_seq', 1, false);


--
-- TOC entry 4414 (class 0 OID 0)
-- Dependencies: 360
-- Name: usernotification_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.usernotification_id_seq', 66, true);


--
-- TOC entry 4415 (class 0 OID 0)
-- Dependencies: 363
-- Name: vargroup_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.vargroup_id_seq', 1, false);


--
-- TOC entry 4416 (class 0 OID 0)
-- Dependencies: 365
-- Name: variablecategory_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.variablecategory_id_seq', 1, false);


--
-- TOC entry 4417 (class 0 OID 0)
-- Dependencies: 367
-- Name: variablemetadata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.variablemetadata_id_seq', 1, false);


--
-- TOC entry 4418 (class 0 OID 0)
-- Dependencies: 369
-- Name: variablerange_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.variablerange_id_seq', 1, false);


--
-- TOC entry 4419 (class 0 OID 0)
-- Dependencies: 371
-- Name: variablerangeitem_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.variablerangeitem_id_seq', 1, false);


--
-- TOC entry 4420 (class 0 OID 0)
-- Dependencies: 373
-- Name: workflow_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.workflow_id_seq', 1, false);


--
-- TOC entry 4421 (class 0 OID 0)
-- Dependencies: 375
-- Name: workflowcomment_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.workflowcomment_id_seq', 1, false);


--
-- TOC entry 4422 (class 0 OID 0)
-- Dependencies: 377
-- Name: workflowstepdata_id_seq; Type: SEQUENCE SET; Schema: public; Owner: dataverse
--

SELECT pg_catalog.setval('public.workflowstepdata_id_seq', 1, false);


--
-- TOC entry 3446 (class 2606 OID 17138)
-- Name: EJB__TIMER__TBL EJB__TIMER__TBL_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public."EJB__TIMER__TBL"
    ADD CONSTRAINT "EJB__TIMER__TBL_pkey" PRIMARY KEY ("TIMERID");


--
-- TOC entry 3448 (class 2606 OID 17140)
-- Name: actionlogrecord actionlogrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.actionlogrecord
    ADD CONSTRAINT actionlogrecord_pkey PRIMARY KEY (id);


--
-- TOC entry 3453 (class 2606 OID 17142)
-- Name: alternativepersistentidentifier alternativepersistentidentifier_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.alternativepersistentidentifier
    ADD CONSTRAINT alternativepersistentidentifier_pkey PRIMARY KEY (id);


--
-- TOC entry 3455 (class 2606 OID 17144)
-- Name: apitoken apitoken_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.apitoken
    ADD CONSTRAINT apitoken_pkey PRIMARY KEY (id);


--
-- TOC entry 3457 (class 2606 OID 17146)
-- Name: apitoken apitoken_tokenstring_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.apitoken
    ADD CONSTRAINT apitoken_tokenstring_key UNIQUE (tokenstring);


--
-- TOC entry 3460 (class 2606 OID 17148)
-- Name: authenticateduser authenticateduser_email_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticateduser
    ADD CONSTRAINT authenticateduser_email_key UNIQUE (email);


--
-- TOC entry 3462 (class 2606 OID 17150)
-- Name: authenticateduser authenticateduser_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticateduser
    ADD CONSTRAINT authenticateduser_pkey PRIMARY KEY (id);


--
-- TOC entry 3464 (class 2606 OID 17152)
-- Name: authenticateduser authenticateduser_useridentifier_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticateduser
    ADD CONSTRAINT authenticateduser_useridentifier_key UNIQUE (useridentifier);


--
-- TOC entry 3468 (class 2606 OID 17154)
-- Name: authenticateduserlookup authenticateduserlookup_authenticateduser_id_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticateduserlookup
    ADD CONSTRAINT authenticateduserlookup_authenticateduser_id_key UNIQUE (authenticateduser_id);


--
-- TOC entry 3470 (class 2606 OID 17156)
-- Name: authenticateduserlookup authenticateduserlookup_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticateduserlookup
    ADD CONSTRAINT authenticateduserlookup_pkey PRIMARY KEY (id);


--
-- TOC entry 3474 (class 2606 OID 17158)
-- Name: authenticationproviderrow authenticationproviderrow_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticationproviderrow
    ADD CONSTRAINT authenticationproviderrow_pkey PRIMARY KEY (id);


--
-- TOC entry 3477 (class 2606 OID 17160)
-- Name: auxiliaryfile auxiliaryfile_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.auxiliaryfile
    ADD CONSTRAINT auxiliaryfile_pkey PRIMARY KEY (id);


--
-- TOC entry 3479 (class 2606 OID 17162)
-- Name: bannermessage bannermessage_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.bannermessage
    ADD CONSTRAINT bannermessage_pkey PRIMARY KEY (id);


--
-- TOC entry 3481 (class 2606 OID 17164)
-- Name: bannermessagetext bannermessagetext_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.bannermessagetext
    ADD CONSTRAINT bannermessagetext_pkey PRIMARY KEY (id);


--
-- TOC entry 3483 (class 2606 OID 17166)
-- Name: builtinuser builtinuser_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.builtinuser
    ADD CONSTRAINT builtinuser_pkey PRIMARY KEY (id);


--
-- TOC entry 3485 (class 2606 OID 17168)
-- Name: builtinuser builtinuser_username_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.builtinuser
    ADD CONSTRAINT builtinuser_username_key UNIQUE (username);


--
-- TOC entry 3488 (class 2606 OID 17170)
-- Name: categorymetadata categorymetadata_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.categorymetadata
    ADD CONSTRAINT categorymetadata_pkey PRIMARY KEY (id);


--
-- TOC entry 3492 (class 2606 OID 17172)
-- Name: clientharvestrun clientharvestrun_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.clientharvestrun
    ADD CONSTRAINT clientharvestrun_pkey PRIMARY KEY (id);


--
-- TOC entry 3494 (class 2606 OID 17174)
-- Name: confirmemaildata confirmemaildata_authenticateduser_id_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.confirmemaildata
    ADD CONSTRAINT confirmemaildata_authenticateduser_id_key UNIQUE (authenticateduser_id);


--
-- TOC entry 3496 (class 2606 OID 17176)
-- Name: confirmemaildata confirmemaildata_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.confirmemaildata
    ADD CONSTRAINT confirmemaildata_pkey PRIMARY KEY (id);


--
-- TOC entry 3500 (class 2606 OID 17178)
-- Name: controlledvocabalternate controlledvocabalternate_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.controlledvocabalternate
    ADD CONSTRAINT controlledvocabalternate_pkey PRIMARY KEY (id);


--
-- TOC entry 3504 (class 2606 OID 17180)
-- Name: controlledvocabularyvalue controlledvocabularyvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.controlledvocabularyvalue
    ADD CONSTRAINT controlledvocabularyvalue_pkey PRIMARY KEY (id);


--
-- TOC entry 3508 (class 2606 OID 17182)
-- Name: customfieldmap customfieldmap_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customfieldmap
    ADD CONSTRAINT customfieldmap_pkey PRIMARY KEY (id);


--
-- TOC entry 3512 (class 2606 OID 17184)
-- Name: customquestion customquestion_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestion
    ADD CONSTRAINT customquestion_pkey PRIMARY KEY (id);


--
-- TOC entry 3515 (class 2606 OID 17186)
-- Name: customquestionresponse customquestionresponse_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestionresponse
    ADD CONSTRAINT customquestionresponse_pkey PRIMARY KEY (id);


--
-- TOC entry 3518 (class 2606 OID 17188)
-- Name: customquestionvalue customquestionvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestionvalue
    ADD CONSTRAINT customquestionvalue_pkey PRIMARY KEY (id);


--
-- TOC entry 3520 (class 2606 OID 17190)
-- Name: datafile datafile_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datafile
    ADD CONSTRAINT datafile_pkey PRIMARY KEY (id);


--
-- TOC entry 3526 (class 2606 OID 17192)
-- Name: datafilecategory datafilecategory_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datafilecategory
    ADD CONSTRAINT datafilecategory_pkey PRIMARY KEY (id);


--
-- TOC entry 3529 (class 2606 OID 17194)
-- Name: datafiletag datafiletag_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datafiletag
    ADD CONSTRAINT datafiletag_pkey PRIMARY KEY (id);


--
-- TOC entry 3532 (class 2606 OID 17196)
-- Name: dataset dataset_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataset
    ADD CONSTRAINT dataset_pkey PRIMARY KEY (id);


--
-- TOC entry 3536 (class 2606 OID 17198)
-- Name: datasetexternalcitations datasetexternalcitations_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetexternalcitations
    ADD CONSTRAINT datasetexternalcitations_pkey PRIMARY KEY (id);


--
-- TOC entry 3544 (class 2606 OID 17200)
-- Name: datasetfield_controlledvocabularyvalue datasetfield_controlledvocabularyvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfield_controlledvocabularyvalue
    ADD CONSTRAINT datasetfield_controlledvocabularyvalue_pkey PRIMARY KEY (datasetfield_id, controlledvocabularyvalues_id);


--
-- TOC entry 3538 (class 2606 OID 17202)
-- Name: datasetfield datasetfield_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfield
    ADD CONSTRAINT datasetfield_pkey PRIMARY KEY (id);


--
-- TOC entry 3548 (class 2606 OID 17204)
-- Name: datasetfieldcompoundvalue datasetfieldcompoundvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldcompoundvalue
    ADD CONSTRAINT datasetfieldcompoundvalue_pkey PRIMARY KEY (id);


--
-- TOC entry 3551 (class 2606 OID 17206)
-- Name: datasetfielddefaultvalue datasetfielddefaultvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfielddefaultvalue
    ADD CONSTRAINT datasetfielddefaultvalue_pkey PRIMARY KEY (id);


--
-- TOC entry 3557 (class 2606 OID 17208)
-- Name: datasetfieldtype datasetfieldtype_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldtype
    ADD CONSTRAINT datasetfieldtype_pkey PRIMARY KEY (id);


--
-- TOC entry 3561 (class 2606 OID 17210)
-- Name: datasetfieldvalue datasetfieldvalue_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldvalue
    ADD CONSTRAINT datasetfieldvalue_pkey PRIMARY KEY (id);


--
-- TOC entry 3564 (class 2606 OID 17212)
-- Name: datasetlinkingdataverse datasetlinkingdataverse_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetlinkingdataverse
    ADD CONSTRAINT datasetlinkingdataverse_pkey PRIMARY KEY (id);


--
-- TOC entry 3568 (class 2606 OID 17214)
-- Name: datasetlock datasetlock_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetlock
    ADD CONSTRAINT datasetlock_pkey PRIMARY KEY (id);


--
-- TOC entry 3572 (class 2606 OID 17216)
-- Name: datasetmetrics datasetmetrics_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetmetrics
    ADD CONSTRAINT datasetmetrics_pkey PRIMARY KEY (id);


--
-- TOC entry 3574 (class 2606 OID 17218)
-- Name: datasetversion datasetversion_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetversion
    ADD CONSTRAINT datasetversion_pkey PRIMARY KEY (id);


--
-- TOC entry 3580 (class 2606 OID 17220)
-- Name: datasetversionuser datasetversionuser_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetversionuser
    ADD CONSTRAINT datasetversionuser_pkey PRIMARY KEY (id);


--
-- TOC entry 3584 (class 2606 OID 17222)
-- Name: datatable datatable_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datatable
    ADD CONSTRAINT datatable_pkey PRIMARY KEY (id);


--
-- TOC entry 3587 (class 2606 OID 17224)
-- Name: datavariable datavariable_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datavariable
    ADD CONSTRAINT datavariable_pkey PRIMARY KEY (id);


--
-- TOC entry 3590 (class 2606 OID 17226)
-- Name: dataverse dataverse_alias_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse
    ADD CONSTRAINT dataverse_alias_key UNIQUE (alias);


--
-- TOC entry 3606 (class 2606 OID 17228)
-- Name: dataverse_citationdatasetfieldtypes dataverse_citationdatasetfieldtypes_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse_citationdatasetfieldtypes
    ADD CONSTRAINT dataverse_citationdatasetfieldtypes_pkey PRIMARY KEY (dataverse_id, citationdatasetfieldtype_id);


--
-- TOC entry 3608 (class 2606 OID 17230)
-- Name: dataverse_metadatablock dataverse_metadatablock_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse_metadatablock
    ADD CONSTRAINT dataverse_metadatablock_pkey PRIMARY KEY (dataverse_id, metadatablocks_id);


--
-- TOC entry 3593 (class 2606 OID 17232)
-- Name: dataverse dataverse_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse
    ADD CONSTRAINT dataverse_pkey PRIMARY KEY (id);


--
-- TOC entry 3610 (class 2606 OID 17234)
-- Name: dataversecontact dataversecontact_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversecontact
    ADD CONSTRAINT dataversecontact_pkey PRIMARY KEY (id);


--
-- TOC entry 3615 (class 2606 OID 17236)
-- Name: dataversefacet dataversefacet_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefacet
    ADD CONSTRAINT dataversefacet_pkey PRIMARY KEY (id);


--
-- TOC entry 3620 (class 2606 OID 17238)
-- Name: dataversefeatureddataverse dataversefeatureddataverse_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefeatureddataverse
    ADD CONSTRAINT dataversefeatureddataverse_pkey PRIMARY KEY (id);


--
-- TOC entry 3625 (class 2606 OID 17240)
-- Name: dataversefieldtypeinputlevel dataversefieldtypeinputlevel_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefieldtypeinputlevel
    ADD CONSTRAINT dataversefieldtypeinputlevel_pkey PRIMARY KEY (id);


--
-- TOC entry 3632 (class 2606 OID 17242)
-- Name: dataverselinkingdataverse dataverselinkingdataverse_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverselinkingdataverse
    ADD CONSTRAINT dataverselinkingdataverse_pkey PRIMARY KEY (id);


--
-- TOC entry 3636 (class 2606 OID 17244)
-- Name: dataverserole dataverserole_alias_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverserole
    ADD CONSTRAINT dataverserole_alias_key UNIQUE (alias);


--
-- TOC entry 3638 (class 2606 OID 17246)
-- Name: dataverserole dataverserole_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverserole
    ADD CONSTRAINT dataverserole_pkey PRIMARY KEY (id);


--
-- TOC entry 3643 (class 2606 OID 17248)
-- Name: dataversesubjects dataversesubjects_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversesubjects
    ADD CONSTRAINT dataversesubjects_pkey PRIMARY KEY (dataverse_id, controlledvocabularyvalue_id);


--
-- TOC entry 3645 (class 2606 OID 17250)
-- Name: dataversetheme dataversetheme_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversetheme
    ADD CONSTRAINT dataversetheme_pkey PRIMARY KEY (id);


--
-- TOC entry 3648 (class 2606 OID 17252)
-- Name: defaultvalueset defaultvalueset_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.defaultvalueset
    ADD CONSTRAINT defaultvalueset_pkey PRIMARY KEY (id);


--
-- TOC entry 3650 (class 2606 OID 17254)
-- Name: doidataciteregistercache doidataciteregistercache_doi_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.doidataciteregistercache
    ADD CONSTRAINT doidataciteregistercache_doi_key UNIQUE (doi);


--
-- TOC entry 3652 (class 2606 OID 17256)
-- Name: doidataciteregistercache doidataciteregistercache_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.doidataciteregistercache
    ADD CONSTRAINT doidataciteregistercache_pkey PRIMARY KEY (id);


--
-- TOC entry 3654 (class 2606 OID 17258)
-- Name: dvobject dvobject_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dvobject
    ADD CONSTRAINT dvobject_pkey PRIMARY KEY (id);


--
-- TOC entry 3670 (class 2606 OID 17260)
-- Name: explicitgroup_authenticateduser explicitgroup_authenticateduser_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup_authenticateduser
    ADD CONSTRAINT explicitgroup_authenticateduser_pkey PRIMARY KEY (explicitgroup_id, containedauthenticatedusers_id);


--
-- TOC entry 3672 (class 2606 OID 17262)
-- Name: explicitgroup_explicitgroup explicitgroup_explicitgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup_explicitgroup
    ADD CONSTRAINT explicitgroup_explicitgroup_pkey PRIMARY KEY (explicitgroup_id, containedexplicitgroups_id);


--
-- TOC entry 3664 (class 2606 OID 17264)
-- Name: explicitgroup explicitgroup_groupalias_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup
    ADD CONSTRAINT explicitgroup_groupalias_key UNIQUE (groupalias);


--
-- TOC entry 3666 (class 2606 OID 17266)
-- Name: explicitgroup explicitgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup
    ADD CONSTRAINT explicitgroup_pkey PRIMARY KEY (id);


--
-- TOC entry 3674 (class 2606 OID 17268)
-- Name: externaltool externaltool_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.externaltool
    ADD CONSTRAINT externaltool_pkey PRIMARY KEY (id);


--
-- TOC entry 3676 (class 2606 OID 17270)
-- Name: externaltooltype externaltooltype_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.externaltooltype
    ADD CONSTRAINT externaltooltype_pkey PRIMARY KEY (id);


--
-- TOC entry 3679 (class 2606 OID 17272)
-- Name: fileaccessrequests fileaccessrequests_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.fileaccessrequests
    ADD CONSTRAINT fileaccessrequests_pkey PRIMARY KEY (datafile_id, authenticated_user_id);


--
-- TOC entry 3681 (class 2606 OID 17274)
-- Name: filedownload filedownload_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.filedownload
    ADD CONSTRAINT filedownload_pkey PRIMARY KEY (guestbookresponse_id);


--
-- TOC entry 3687 (class 2606 OID 17276)
-- Name: filemetadata_datafilecategory filemetadata_datafilecategory_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.filemetadata_datafilecategory
    ADD CONSTRAINT filemetadata_datafilecategory_pkey PRIMARY KEY (filecategories_id, filemetadatas_id);


--
-- TOC entry 3683 (class 2606 OID 17278)
-- Name: filemetadata filemetadata_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.filemetadata
    ADD CONSTRAINT filemetadata_pkey PRIMARY KEY (id);


--
-- TOC entry 3691 (class 2606 OID 17280)
-- Name: flyway_schema_history flyway_schema_history_pk; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.flyway_schema_history
    ADD CONSTRAINT flyway_schema_history_pk PRIMARY KEY (installed_rank);


--
-- TOC entry 3694 (class 2606 OID 17282)
-- Name: foreignmetadatafieldmapping foreignmetadatafieldmapping_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.foreignmetadatafieldmapping
    ADD CONSTRAINT foreignmetadatafieldmapping_pkey PRIMARY KEY (id);


--
-- TOC entry 3701 (class 2606 OID 17284)
-- Name: foreignmetadataformatmapping foreignmetadataformatmapping_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.foreignmetadataformatmapping
    ADD CONSTRAINT foreignmetadataformatmapping_pkey PRIMARY KEY (id);


--
-- TOC entry 3704 (class 2606 OID 17286)
-- Name: guestbook guestbook_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbook
    ADD CONSTRAINT guestbook_pkey PRIMARY KEY (id);


--
-- TOC entry 3706 (class 2606 OID 17288)
-- Name: guestbookresponse guestbookresponse_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbookresponse
    ADD CONSTRAINT guestbookresponse_pkey PRIMARY KEY (id);


--
-- TOC entry 3711 (class 2606 OID 17290)
-- Name: harvestingclient harvestingclient_name_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.harvestingclient
    ADD CONSTRAINT harvestingclient_name_key UNIQUE (name);


--
-- TOC entry 3713 (class 2606 OID 17292)
-- Name: harvestingclient harvestingclient_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.harvestingclient
    ADD CONSTRAINT harvestingclient_pkey PRIMARY KEY (id);


--
-- TOC entry 3719 (class 2606 OID 17294)
-- Name: harvestingdataverseconfig harvestingdataverseconfig_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.harvestingdataverseconfig
    ADD CONSTRAINT harvestingdataverseconfig_pkey PRIMARY KEY (id);


--
-- TOC entry 3726 (class 2606 OID 17296)
-- Name: ingestreport ingestreport_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ingestreport
    ADD CONSTRAINT ingestreport_pkey PRIMARY KEY (id);


--
-- TOC entry 3729 (class 2606 OID 17298)
-- Name: ingestrequest ingestrequest_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ingestrequest
    ADD CONSTRAINT ingestrequest_pkey PRIMARY KEY (id);


--
-- TOC entry 3732 (class 2606 OID 17300)
-- Name: ipv4range ipv4range_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ipv4range
    ADD CONSTRAINT ipv4range_pkey PRIMARY KEY (id);


--
-- TOC entry 3735 (class 2606 OID 17302)
-- Name: ipv6range ipv6range_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ipv6range
    ADD CONSTRAINT ipv6range_pkey PRIMARY KEY (id);


--
-- TOC entry 3739 (class 2606 OID 17304)
-- Name: metadatablock metadatablock_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.metadatablock
    ADD CONSTRAINT metadatablock_pkey PRIMARY KEY (id);


--
-- TOC entry 3742 (class 2606 OID 17306)
-- Name: metric metric_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.metric
    ADD CONSTRAINT metric_pkey PRIMARY KEY (id);


--
-- TOC entry 3744 (class 2606 OID 17308)
-- Name: oairecord oairecord_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.oairecord
    ADD CONSTRAINT oairecord_pkey PRIMARY KEY (id);


--
-- TOC entry 3746 (class 2606 OID 17310)
-- Name: oaiset oaiset_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.oaiset
    ADD CONSTRAINT oaiset_pkey PRIMARY KEY (id);


--
-- TOC entry 3748 (class 2606 OID 17312)
-- Name: oauth2tokendata oauth2tokendata_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.oauth2tokendata
    ADD CONSTRAINT oauth2tokendata_pkey PRIMARY KEY (id);


--
-- TOC entry 3752 (class 2606 OID 17314)
-- Name: passwordresetdata passwordresetdata_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.passwordresetdata
    ADD CONSTRAINT passwordresetdata_pkey PRIMARY KEY (id);


--
-- TOC entry 3754 (class 2606 OID 17316)
-- Name: pendingworkflowinvocation pendingworkflowinvocation_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.pendingworkflowinvocation
    ADD CONSTRAINT pendingworkflowinvocation_pkey PRIMARY KEY (invocationid);


--
-- TOC entry 3757 (class 2606 OID 17318)
-- Name: persistedglobalgroup persistedglobalgroup_persistedgroupalias_key; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.persistedglobalgroup
    ADD CONSTRAINT persistedglobalgroup_persistedgroupalias_key UNIQUE (persistedgroupalias);


--
-- TOC entry 3759 (class 2606 OID 17320)
-- Name: persistedglobalgroup persistedglobalgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.persistedglobalgroup
    ADD CONSTRAINT persistedglobalgroup_pkey PRIMARY KEY (id);


--
-- TOC entry 3764 (class 2606 OID 17322)
-- Name: roleassignment roleassignment_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.roleassignment
    ADD CONSTRAINT roleassignment_pkey PRIMARY KEY (id);


--
-- TOC entry 3770 (class 2606 OID 17324)
-- Name: savedsearch savedsearch_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.savedsearch
    ADD CONSTRAINT savedsearch_pkey PRIMARY KEY (id);


--
-- TOC entry 3773 (class 2606 OID 17326)
-- Name: savedsearchfilterquery savedsearchfilterquery_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.savedsearchfilterquery
    ADD CONSTRAINT savedsearchfilterquery_pkey PRIMARY KEY (id);


--
-- TOC entry 3775 (class 2606 OID 17328)
-- Name: sequence sequence_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.sequence
    ADD CONSTRAINT sequence_pkey PRIMARY KEY (seq_name);


--
-- TOC entry 3777 (class 2606 OID 17330)
-- Name: setting setting_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.setting
    ADD CONSTRAINT setting_pkey PRIMARY KEY (id);


--
-- TOC entry 3780 (class 2606 OID 17332)
-- Name: shibgroup shibgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.shibgroup
    ADD CONSTRAINT shibgroup_pkey PRIMARY KEY (id);


--
-- TOC entry 3782 (class 2606 OID 17334)
-- Name: storagesite storagesite_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.storagesite
    ADD CONSTRAINT storagesite_pkey PRIMARY KEY (id);


--
-- TOC entry 3785 (class 2606 OID 17336)
-- Name: summarystatistic summarystatistic_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.summarystatistic
    ADD CONSTRAINT summarystatistic_pkey PRIMARY KEY (id);


--
-- TOC entry 3788 (class 2606 OID 17338)
-- Name: template template_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.template
    ADD CONSTRAINT template_pkey PRIMARY KEY (id);


--
-- TOC entry 3790 (class 2606 OID 17340)
-- Name: termsofuseandaccess termsofuseandaccess_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.termsofuseandaccess
    ADD CONSTRAINT termsofuseandaccess_pkey PRIMARY KEY (id);


--
-- TOC entry 3472 (class 2606 OID 17342)
-- Name: authenticateduserlookup unq_authenticateduserlookup_0; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticateduserlookup
    ADD CONSTRAINT unq_authenticateduserlookup_0 UNIQUE (persistentuserid, authenticationproviderid);


--
-- TOC entry 3578 (class 2606 OID 17344)
-- Name: datasetversion unq_datasetversion_0; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetversion
    ADD CONSTRAINT unq_datasetversion_0 UNIQUE (dataset_id, versionnumber, minorversionnumber);


--
-- TOC entry 3630 (class 2606 OID 17346)
-- Name: dataversefieldtypeinputlevel unq_dataversefieldtypeinputlevel_0; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefieldtypeinputlevel
    ADD CONSTRAINT unq_dataversefieldtypeinputlevel_0 UNIQUE (dataverse_id, datasetfieldtype_id);


--
-- TOC entry 3660 (class 2606 OID 17348)
-- Name: dvobject unq_dvobject_0; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dvobject
    ADD CONSTRAINT unq_dvobject_0 UNIQUE (authority, protocol, identifier);


--
-- TOC entry 3662 (class 2606 OID 17350)
-- Name: dvobject unq_dvobject_1; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dvobject
    ADD CONSTRAINT unq_dvobject_1 UNIQUE (owner_id, storageidentifier);


--
-- TOC entry 3699 (class 2606 OID 17352)
-- Name: foreignmetadatafieldmapping unq_foreignmetadatafieldmapping_0; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.foreignmetadatafieldmapping
    ADD CONSTRAINT unq_foreignmetadatafieldmapping_0 UNIQUE (foreignmetadataformatmapping_id, foreignfieldxpath);


--
-- TOC entry 3766 (class 2606 OID 17354)
-- Name: roleassignment unq_roleassignment_0; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.roleassignment
    ADD CONSTRAINT unq_roleassignment_0 UNIQUE (assigneeidentifier, role_id, definitionpoint_id);


--
-- TOC entry 3808 (class 2606 OID 17356)
-- Name: variablemetadata unq_variablemetadata_0; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablemetadata
    ADD CONSTRAINT unq_variablemetadata_0 UNIQUE (datavariable_id, filemetadata_id);


--
-- TOC entry 3792 (class 2606 OID 17358)
-- Name: userbannermessage userbannermessage_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.userbannermessage
    ADD CONSTRAINT userbannermessage_pkey PRIMARY KEY (id);


--
-- TOC entry 3795 (class 2606 OID 17360)
-- Name: usernotification usernotification_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.usernotification
    ADD CONSTRAINT usernotification_pkey PRIMARY KEY (id);


--
-- TOC entry 3800 (class 2606 OID 17362)
-- Name: vargroup_datavariable vargroup_datavariable_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.vargroup_datavariable
    ADD CONSTRAINT vargroup_datavariable_pkey PRIMARY KEY (vargroup_id, varsingroup_id);


--
-- TOC entry 3798 (class 2606 OID 17364)
-- Name: vargroup vargroup_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.vargroup
    ADD CONSTRAINT vargroup_pkey PRIMARY KEY (id);


--
-- TOC entry 3803 (class 2606 OID 17366)
-- Name: variablecategory variablecategory_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablecategory
    ADD CONSTRAINT variablecategory_pkey PRIMARY KEY (id);


--
-- TOC entry 3810 (class 2606 OID 17368)
-- Name: variablemetadata variablemetadata_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablemetadata
    ADD CONSTRAINT variablemetadata_pkey PRIMARY KEY (id);


--
-- TOC entry 3813 (class 2606 OID 17370)
-- Name: variablerange variablerange_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablerange
    ADD CONSTRAINT variablerange_pkey PRIMARY KEY (id);


--
-- TOC entry 3816 (class 2606 OID 17372)
-- Name: variablerangeitem variablerangeitem_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablerangeitem
    ADD CONSTRAINT variablerangeitem_pkey PRIMARY KEY (id);


--
-- TOC entry 3818 (class 2606 OID 17374)
-- Name: workflow workflow_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflow
    ADD CONSTRAINT workflow_pkey PRIMARY KEY (id);


--
-- TOC entry 3820 (class 2606 OID 17376)
-- Name: workflowcomment workflowcomment_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflowcomment
    ADD CONSTRAINT workflowcomment_pkey PRIMARY KEY (id);


--
-- TOC entry 3822 (class 2606 OID 17378)
-- Name: workflowstepdata workflowstepdata_pkey; Type: CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflowstepdata
    ADD CONSTRAINT workflowstepdata_pkey PRIMARY KEY (id);


--
-- TOC entry 3591 (class 1259 OID 17379)
-- Name: dataverse_alias_unique_idx; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE UNIQUE INDEX dataverse_alias_unique_idx ON public.dataverse USING btree (lower((alias)::text));


--
-- TOC entry 3692 (class 1259 OID 17380)
-- Name: flyway_schema_history_s_idx; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX flyway_schema_history_s_idx ON public.flyway_schema_history USING btree (success);


--
-- TOC entry 3449 (class 1259 OID 17381)
-- Name: index_actionlogrecord_actiontype; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_actionlogrecord_actiontype ON public.actionlogrecord USING btree (actiontype);


--
-- TOC entry 3450 (class 1259 OID 17382)
-- Name: index_actionlogrecord_starttime; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_actionlogrecord_starttime ON public.actionlogrecord USING btree (starttime);


--
-- TOC entry 3451 (class 1259 OID 17383)
-- Name: index_actionlogrecord_useridentifier; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_actionlogrecord_useridentifier ON public.actionlogrecord USING btree (useridentifier);


--
-- TOC entry 3458 (class 1259 OID 17384)
-- Name: index_apitoken_authenticateduser_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_apitoken_authenticateduser_id ON public.apitoken USING btree (authenticateduser_id);


--
-- TOC entry 3465 (class 1259 OID 17385)
-- Name: index_authenticateduser_lower_email; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE UNIQUE INDEX index_authenticateduser_lower_email ON public.authenticateduser USING btree (lower((email)::text));


--
-- TOC entry 3466 (class 1259 OID 17386)
-- Name: index_authenticateduser_lower_useridentifier; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE UNIQUE INDEX index_authenticateduser_lower_useridentifier ON public.authenticateduser USING btree (lower((useridentifier)::text));


--
-- TOC entry 3475 (class 1259 OID 17387)
-- Name: index_authenticationproviderrow_enabled; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_authenticationproviderrow_enabled ON public.authenticationproviderrow USING btree (enabled);


--
-- TOC entry 3486 (class 1259 OID 17388)
-- Name: index_builtinuser_username; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_builtinuser_username ON public.builtinuser USING btree (username);


--
-- TOC entry 3489 (class 1259 OID 17389)
-- Name: index_categorymetadata_category_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_categorymetadata_category_id ON public.categorymetadata USING btree (category_id);


--
-- TOC entry 3490 (class 1259 OID 17390)
-- Name: index_categorymetadata_variablemetadata_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_categorymetadata_variablemetadata_id ON public.categorymetadata USING btree (variablemetadata_id);


--
-- TOC entry 3497 (class 1259 OID 17391)
-- Name: index_confirmemaildata_authenticateduser_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_confirmemaildata_authenticateduser_id ON public.confirmemaildata USING btree (authenticateduser_id);


--
-- TOC entry 3498 (class 1259 OID 17392)
-- Name: index_confirmemaildata_token; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_confirmemaildata_token ON public.confirmemaildata USING btree (token);


--
-- TOC entry 3501 (class 1259 OID 17393)
-- Name: index_controlledvocabalternate_controlledvocabularyvalue_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_controlledvocabalternate_controlledvocabularyvalue_id ON public.controlledvocabalternate USING btree (controlledvocabularyvalue_id);


--
-- TOC entry 3502 (class 1259 OID 17394)
-- Name: index_controlledvocabalternate_datasetfieldtype_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_controlledvocabalternate_datasetfieldtype_id ON public.controlledvocabalternate USING btree (datasetfieldtype_id);


--
-- TOC entry 3505 (class 1259 OID 17395)
-- Name: index_controlledvocabularyvalue_datasetfieldtype_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_controlledvocabularyvalue_datasetfieldtype_id ON public.controlledvocabularyvalue USING btree (datasetfieldtype_id);


--
-- TOC entry 3506 (class 1259 OID 17396)
-- Name: index_controlledvocabularyvalue_displayorder; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_controlledvocabularyvalue_displayorder ON public.controlledvocabularyvalue USING btree (displayorder);


--
-- TOC entry 3509 (class 1259 OID 17397)
-- Name: index_customfieldmap_sourcedatasetfield; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_customfieldmap_sourcedatasetfield ON public.customfieldmap USING btree (sourcedatasetfield);


--
-- TOC entry 3510 (class 1259 OID 17398)
-- Name: index_customfieldmap_sourcetemplate; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_customfieldmap_sourcetemplate ON public.customfieldmap USING btree (sourcetemplate);


--
-- TOC entry 3513 (class 1259 OID 17399)
-- Name: index_customquestion_guestbook_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_customquestion_guestbook_id ON public.customquestion USING btree (guestbook_id);


--
-- TOC entry 3516 (class 1259 OID 17400)
-- Name: index_customquestionresponse_guestbookresponse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_customquestionresponse_guestbookresponse_id ON public.customquestionresponse USING btree (guestbookresponse_id);


--
-- TOC entry 3521 (class 1259 OID 17401)
-- Name: index_datafile_checksumvalue; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datafile_checksumvalue ON public.datafile USING btree (checksumvalue);


--
-- TOC entry 3522 (class 1259 OID 17402)
-- Name: index_datafile_contenttype; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datafile_contenttype ON public.datafile USING btree (contenttype);


--
-- TOC entry 3523 (class 1259 OID 17403)
-- Name: index_datafile_ingeststatus; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datafile_ingeststatus ON public.datafile USING btree (ingeststatus);


--
-- TOC entry 3524 (class 1259 OID 17404)
-- Name: index_datafile_restricted; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datafile_restricted ON public.datafile USING btree (restricted);


--
-- TOC entry 3527 (class 1259 OID 17405)
-- Name: index_datafilecategory_dataset_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datafilecategory_dataset_id ON public.datafilecategory USING btree (dataset_id);


--
-- TOC entry 3530 (class 1259 OID 17406)
-- Name: index_datafiletag_datafile_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datafiletag_datafile_id ON public.datafiletag USING btree (datafile_id);


--
-- TOC entry 3533 (class 1259 OID 17407)
-- Name: index_dataset_guestbook_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataset_guestbook_id ON public.dataset USING btree (guestbook_id);


--
-- TOC entry 3534 (class 1259 OID 17408)
-- Name: index_dataset_thumbnailfile_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataset_thumbnailfile_id ON public.dataset USING btree (thumbnailfile_id);


--
-- TOC entry 3545 (class 1259 OID 17409)
-- Name: index_datasetfield_controlledvocabularyvalue_controlledvocabula; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfield_controlledvocabularyvalue_controlledvocabula ON public.datasetfield_controlledvocabularyvalue USING btree (controlledvocabularyvalues_id);


--
-- TOC entry 3546 (class 1259 OID 17410)
-- Name: index_datasetfield_controlledvocabularyvalue_datasetfield_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfield_controlledvocabularyvalue_datasetfield_id ON public.datasetfield_controlledvocabularyvalue USING btree (datasetfield_id);


--
-- TOC entry 3539 (class 1259 OID 17411)
-- Name: index_datasetfield_datasetfieldtype_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfield_datasetfieldtype_id ON public.datasetfield USING btree (datasetfieldtype_id);


--
-- TOC entry 3540 (class 1259 OID 17412)
-- Name: index_datasetfield_datasetversion_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfield_datasetversion_id ON public.datasetfield USING btree (datasetversion_id);


--
-- TOC entry 3541 (class 1259 OID 17413)
-- Name: index_datasetfield_parentdatasetfieldcompoundvalue_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfield_parentdatasetfieldcompoundvalue_id ON public.datasetfield USING btree (parentdatasetfieldcompoundvalue_id);


--
-- TOC entry 3542 (class 1259 OID 17414)
-- Name: index_datasetfield_template_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfield_template_id ON public.datasetfield USING btree (template_id);


--
-- TOC entry 3549 (class 1259 OID 17415)
-- Name: index_datasetfieldcompoundvalue_parentdatasetfield_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfieldcompoundvalue_parentdatasetfield_id ON public.datasetfieldcompoundvalue USING btree (parentdatasetfield_id);


--
-- TOC entry 3552 (class 1259 OID 17416)
-- Name: index_datasetfielddefaultvalue_datasetfield_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfielddefaultvalue_datasetfield_id ON public.datasetfielddefaultvalue USING btree (datasetfield_id);


--
-- TOC entry 3553 (class 1259 OID 17417)
-- Name: index_datasetfielddefaultvalue_defaultvalueset_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfielddefaultvalue_defaultvalueset_id ON public.datasetfielddefaultvalue USING btree (defaultvalueset_id);


--
-- TOC entry 3554 (class 1259 OID 17418)
-- Name: index_datasetfielddefaultvalue_displayorder; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfielddefaultvalue_displayorder ON public.datasetfielddefaultvalue USING btree (displayorder);


--
-- TOC entry 3555 (class 1259 OID 17419)
-- Name: index_datasetfielddefaultvalue_parentdatasetfielddefaultvalue_i; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfielddefaultvalue_parentdatasetfielddefaultvalue_i ON public.datasetfielddefaultvalue USING btree (parentdatasetfielddefaultvalue_id);


--
-- TOC entry 3558 (class 1259 OID 17420)
-- Name: index_datasetfieldtype_metadatablock_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfieldtype_metadatablock_id ON public.datasetfieldtype USING btree (metadatablock_id);


--
-- TOC entry 3559 (class 1259 OID 17421)
-- Name: index_datasetfieldtype_parentdatasetfieldtype_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfieldtype_parentdatasetfieldtype_id ON public.datasetfieldtype USING btree (parentdatasetfieldtype_id);


--
-- TOC entry 3562 (class 1259 OID 17422)
-- Name: index_datasetfieldvalue_datasetfield_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetfieldvalue_datasetfield_id ON public.datasetfieldvalue USING btree (datasetfield_id);


--
-- TOC entry 3565 (class 1259 OID 17423)
-- Name: index_datasetlinkingdataverse_dataset_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetlinkingdataverse_dataset_id ON public.datasetlinkingdataverse USING btree (dataset_id);


--
-- TOC entry 3566 (class 1259 OID 17424)
-- Name: index_datasetlinkingdataverse_linkingdataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetlinkingdataverse_linkingdataverse_id ON public.datasetlinkingdataverse USING btree (linkingdataverse_id);


--
-- TOC entry 3569 (class 1259 OID 17425)
-- Name: index_datasetlock_dataset_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetlock_dataset_id ON public.datasetlock USING btree (dataset_id);


--
-- TOC entry 3570 (class 1259 OID 17426)
-- Name: index_datasetlock_user_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetlock_user_id ON public.datasetlock USING btree (user_id);


--
-- TOC entry 3575 (class 1259 OID 17427)
-- Name: index_datasetversion_dataset_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetversion_dataset_id ON public.datasetversion USING btree (dataset_id);


--
-- TOC entry 3581 (class 1259 OID 17428)
-- Name: index_datasetversionuser_authenticateduser_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetversionuser_authenticateduser_id ON public.datasetversionuser USING btree (authenticateduser_id);


--
-- TOC entry 3582 (class 1259 OID 17429)
-- Name: index_datasetversionuser_datasetversion_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datasetversionuser_datasetversion_id ON public.datasetversionuser USING btree (datasetversion_id);


--
-- TOC entry 3585 (class 1259 OID 17430)
-- Name: index_datatable_datafile_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datatable_datafile_id ON public.datatable USING btree (datafile_id);


--
-- TOC entry 3588 (class 1259 OID 17431)
-- Name: index_datavariable_datatable_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_datavariable_datatable_id ON public.datavariable USING btree (datatable_id);


--
-- TOC entry 3594 (class 1259 OID 17432)
-- Name: index_dataverse_affiliation; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_affiliation ON public.dataverse USING btree (affiliation);


--
-- TOC entry 3595 (class 1259 OID 17433)
-- Name: index_dataverse_alias; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_alias ON public.dataverse USING btree (alias);


--
-- TOC entry 3596 (class 1259 OID 17434)
-- Name: index_dataverse_dataversetype; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_dataversetype ON public.dataverse USING btree (dataversetype);


--
-- TOC entry 3597 (class 1259 OID 17435)
-- Name: index_dataverse_defaultcontributorrole_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_defaultcontributorrole_id ON public.dataverse USING btree (defaultcontributorrole_id);


--
-- TOC entry 3598 (class 1259 OID 17436)
-- Name: index_dataverse_defaulttemplate_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_defaulttemplate_id ON public.dataverse USING btree (defaulttemplate_id);


--
-- TOC entry 3599 (class 1259 OID 17437)
-- Name: index_dataverse_facetroot; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_facetroot ON public.dataverse USING btree (facetroot);


--
-- TOC entry 3600 (class 1259 OID 17438)
-- Name: index_dataverse_guestbookroot; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_guestbookroot ON public.dataverse USING btree (guestbookroot);


--
-- TOC entry 3601 (class 1259 OID 17439)
-- Name: index_dataverse_metadatablockroot; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_metadatablockroot ON public.dataverse USING btree (metadatablockroot);


--
-- TOC entry 3602 (class 1259 OID 17440)
-- Name: index_dataverse_permissionroot; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_permissionroot ON public.dataverse USING btree (permissionroot);


--
-- TOC entry 3603 (class 1259 OID 17441)
-- Name: index_dataverse_templateroot; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_templateroot ON public.dataverse USING btree (templateroot);


--
-- TOC entry 3604 (class 1259 OID 17442)
-- Name: index_dataverse_themeroot; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverse_themeroot ON public.dataverse USING btree (themeroot);


--
-- TOC entry 3611 (class 1259 OID 17443)
-- Name: index_dataversecontact_contactemail; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversecontact_contactemail ON public.dataversecontact USING btree (contactemail);


--
-- TOC entry 3612 (class 1259 OID 17444)
-- Name: index_dataversecontact_dataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversecontact_dataverse_id ON public.dataversecontact USING btree (dataverse_id);


--
-- TOC entry 3613 (class 1259 OID 17445)
-- Name: index_dataversecontact_displayorder; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversecontact_displayorder ON public.dataversecontact USING btree (displayorder);


--
-- TOC entry 3616 (class 1259 OID 17446)
-- Name: index_dataversefacet_datasetfieldtype_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversefacet_datasetfieldtype_id ON public.dataversefacet USING btree (datasetfieldtype_id);


--
-- TOC entry 3617 (class 1259 OID 17447)
-- Name: index_dataversefacet_dataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversefacet_dataverse_id ON public.dataversefacet USING btree (dataverse_id);


--
-- TOC entry 3618 (class 1259 OID 17448)
-- Name: index_dataversefacet_displayorder; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversefacet_displayorder ON public.dataversefacet USING btree (displayorder);


--
-- TOC entry 3621 (class 1259 OID 17449)
-- Name: index_dataversefeatureddataverse_dataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversefeatureddataverse_dataverse_id ON public.dataversefeatureddataverse USING btree (dataverse_id);


--
-- TOC entry 3622 (class 1259 OID 17450)
-- Name: index_dataversefeatureddataverse_displayorder; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversefeatureddataverse_displayorder ON public.dataversefeatureddataverse USING btree (displayorder);


--
-- TOC entry 3623 (class 1259 OID 17451)
-- Name: index_dataversefeatureddataverse_featureddataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversefeatureddataverse_featureddataverse_id ON public.dataversefeatureddataverse USING btree (featureddataverse_id);


--
-- TOC entry 3626 (class 1259 OID 17452)
-- Name: index_dataversefieldtypeinputlevel_datasetfieldtype_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversefieldtypeinputlevel_datasetfieldtype_id ON public.dataversefieldtypeinputlevel USING btree (datasetfieldtype_id);


--
-- TOC entry 3627 (class 1259 OID 17453)
-- Name: index_dataversefieldtypeinputlevel_dataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversefieldtypeinputlevel_dataverse_id ON public.dataversefieldtypeinputlevel USING btree (dataverse_id);


--
-- TOC entry 3628 (class 1259 OID 17454)
-- Name: index_dataversefieldtypeinputlevel_required; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversefieldtypeinputlevel_required ON public.dataversefieldtypeinputlevel USING btree (required);


--
-- TOC entry 3633 (class 1259 OID 17455)
-- Name: index_dataverselinkingdataverse_dataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverselinkingdataverse_dataverse_id ON public.dataverselinkingdataverse USING btree (dataverse_id);


--
-- TOC entry 3634 (class 1259 OID 17456)
-- Name: index_dataverselinkingdataverse_linkingdataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverselinkingdataverse_linkingdataverse_id ON public.dataverselinkingdataverse USING btree (linkingdataverse_id);


--
-- TOC entry 3639 (class 1259 OID 17457)
-- Name: index_dataverserole_alias; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverserole_alias ON public.dataverserole USING btree (alias);


--
-- TOC entry 3640 (class 1259 OID 17458)
-- Name: index_dataverserole_name; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverserole_name ON public.dataverserole USING btree (name);


--
-- TOC entry 3641 (class 1259 OID 17459)
-- Name: index_dataverserole_owner_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataverserole_owner_id ON public.dataverserole USING btree (owner_id);


--
-- TOC entry 3646 (class 1259 OID 17460)
-- Name: index_dataversetheme_dataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dataversetheme_dataverse_id ON public.dataversetheme USING btree (dataverse_id);


--
-- TOC entry 3655 (class 1259 OID 17461)
-- Name: index_dvobject_creator_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dvobject_creator_id ON public.dvobject USING btree (creator_id);


--
-- TOC entry 3656 (class 1259 OID 17462)
-- Name: index_dvobject_dtype; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dvobject_dtype ON public.dvobject USING btree (dtype);


--
-- TOC entry 3657 (class 1259 OID 17463)
-- Name: index_dvobject_owner_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dvobject_owner_id ON public.dvobject USING btree (owner_id);


--
-- TOC entry 3658 (class 1259 OID 17464)
-- Name: index_dvobject_releaseuser_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_dvobject_releaseuser_id ON public.dvobject USING btree (releaseuser_id);


--
-- TOC entry 3667 (class 1259 OID 17465)
-- Name: index_explicitgroup_groupaliasinowner; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_explicitgroup_groupaliasinowner ON public.explicitgroup USING btree (groupaliasinowner);


--
-- TOC entry 3668 (class 1259 OID 17466)
-- Name: index_explicitgroup_owner_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_explicitgroup_owner_id ON public.explicitgroup USING btree (owner_id);


--
-- TOC entry 3677 (class 1259 OID 17467)
-- Name: index_externaltooltype_externaltool_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_externaltooltype_externaltool_id ON public.externaltooltype USING btree (externaltool_id);


--
-- TOC entry 3684 (class 1259 OID 17468)
-- Name: index_filemetadata_datafile_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_filemetadata_datafile_id ON public.filemetadata USING btree (datafile_id);


--
-- TOC entry 3688 (class 1259 OID 17469)
-- Name: index_filemetadata_datafilecategory_filecategories_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_filemetadata_datafilecategory_filecategories_id ON public.filemetadata_datafilecategory USING btree (filecategories_id);


--
-- TOC entry 3689 (class 1259 OID 17470)
-- Name: index_filemetadata_datafilecategory_filemetadatas_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_filemetadata_datafilecategory_filemetadatas_id ON public.filemetadata_datafilecategory USING btree (filemetadatas_id);


--
-- TOC entry 3685 (class 1259 OID 17471)
-- Name: index_filemetadata_datasetversion_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_filemetadata_datasetversion_id ON public.filemetadata USING btree (datasetversion_id);


--
-- TOC entry 3695 (class 1259 OID 17472)
-- Name: index_foreignmetadatafieldmapping_foreignfieldxpath; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_foreignmetadatafieldmapping_foreignfieldxpath ON public.foreignmetadatafieldmapping USING btree (foreignfieldxpath);


--
-- TOC entry 3696 (class 1259 OID 17473)
-- Name: index_foreignmetadatafieldmapping_foreignmetadataformatmapping_; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_foreignmetadatafieldmapping_foreignmetadataformatmapping_ ON public.foreignmetadatafieldmapping USING btree (foreignmetadataformatmapping_id);


--
-- TOC entry 3697 (class 1259 OID 17474)
-- Name: index_foreignmetadatafieldmapping_parentfieldmapping_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_foreignmetadatafieldmapping_parentfieldmapping_id ON public.foreignmetadatafieldmapping USING btree (parentfieldmapping_id);


--
-- TOC entry 3702 (class 1259 OID 17475)
-- Name: index_foreignmetadataformatmapping_name; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_foreignmetadataformatmapping_name ON public.foreignmetadataformatmapping USING btree (name);


--
-- TOC entry 3707 (class 1259 OID 17476)
-- Name: index_guestbookresponse_datafile_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_guestbookresponse_datafile_id ON public.guestbookresponse USING btree (datafile_id);


--
-- TOC entry 3708 (class 1259 OID 17477)
-- Name: index_guestbookresponse_dataset_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_guestbookresponse_dataset_id ON public.guestbookresponse USING btree (dataset_id);


--
-- TOC entry 3709 (class 1259 OID 17478)
-- Name: index_guestbookresponse_guestbook_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_guestbookresponse_guestbook_id ON public.guestbookresponse USING btree (guestbook_id);


--
-- TOC entry 3714 (class 1259 OID 17479)
-- Name: index_harvestingclient_dataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_harvestingclient_dataverse_id ON public.harvestingclient USING btree (dataverse_id);


--
-- TOC entry 3715 (class 1259 OID 17480)
-- Name: index_harvestingclient_harvestingurl; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_harvestingclient_harvestingurl ON public.harvestingclient USING btree (harvestingurl);


--
-- TOC entry 3716 (class 1259 OID 17481)
-- Name: index_harvestingclient_harveststyle; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_harvestingclient_harveststyle ON public.harvestingclient USING btree (harveststyle);


--
-- TOC entry 3717 (class 1259 OID 17482)
-- Name: index_harvestingclient_harvesttype; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_harvestingclient_harvesttype ON public.harvestingclient USING btree (harvesttype);


--
-- TOC entry 3720 (class 1259 OID 17483)
-- Name: index_harvestingdataverseconfig_dataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_harvestingdataverseconfig_dataverse_id ON public.harvestingdataverseconfig USING btree (dataverse_id);


--
-- TOC entry 3721 (class 1259 OID 17484)
-- Name: index_harvestingdataverseconfig_harvestingurl; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_harvestingdataverseconfig_harvestingurl ON public.harvestingdataverseconfig USING btree (harvestingurl);


--
-- TOC entry 3722 (class 1259 OID 17485)
-- Name: index_harvestingdataverseconfig_harveststyle; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_harvestingdataverseconfig_harveststyle ON public.harvestingdataverseconfig USING btree (harveststyle);


--
-- TOC entry 3723 (class 1259 OID 17486)
-- Name: index_harvestingdataverseconfig_harvesttype; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_harvestingdataverseconfig_harvesttype ON public.harvestingdataverseconfig USING btree (harvesttype);


--
-- TOC entry 3724 (class 1259 OID 17487)
-- Name: index_ingestreport_datafile_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_ingestreport_datafile_id ON public.ingestreport USING btree (datafile_id);


--
-- TOC entry 3727 (class 1259 OID 17488)
-- Name: index_ingestrequest_datafile_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_ingestrequest_datafile_id ON public.ingestrequest USING btree (datafile_id);


--
-- TOC entry 3730 (class 1259 OID 17489)
-- Name: index_ipv4range_owner_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_ipv4range_owner_id ON public.ipv4range USING btree (owner_id);


--
-- TOC entry 3733 (class 1259 OID 17490)
-- Name: index_ipv6range_owner_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_ipv6range_owner_id ON public.ipv6range USING btree (owner_id);


--
-- TOC entry 3736 (class 1259 OID 17491)
-- Name: index_metadatablock_name; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_metadatablock_name ON public.metadatablock USING btree (name);


--
-- TOC entry 3737 (class 1259 OID 17492)
-- Name: index_metadatablock_owner_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_metadatablock_owner_id ON public.metadatablock USING btree (owner_id);


--
-- TOC entry 3740 (class 1259 OID 17493)
-- Name: index_metric_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_metric_id ON public.metric USING btree (id);


--
-- TOC entry 3749 (class 1259 OID 17494)
-- Name: index_passwordresetdata_builtinuser_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_passwordresetdata_builtinuser_id ON public.passwordresetdata USING btree (builtinuser_id);


--
-- TOC entry 3750 (class 1259 OID 17495)
-- Name: index_passwordresetdata_token; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_passwordresetdata_token ON public.passwordresetdata USING btree (token);


--
-- TOC entry 3755 (class 1259 OID 17496)
-- Name: index_persistedglobalgroup_dtype; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_persistedglobalgroup_dtype ON public.persistedglobalgroup USING btree (dtype);


--
-- TOC entry 3760 (class 1259 OID 17497)
-- Name: index_roleassignment_assigneeidentifier; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_roleassignment_assigneeidentifier ON public.roleassignment USING btree (assigneeidentifier);


--
-- TOC entry 3761 (class 1259 OID 17498)
-- Name: index_roleassignment_definitionpoint_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_roleassignment_definitionpoint_id ON public.roleassignment USING btree (definitionpoint_id);


--
-- TOC entry 3762 (class 1259 OID 17499)
-- Name: index_roleassignment_role_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_roleassignment_role_id ON public.roleassignment USING btree (role_id);


--
-- TOC entry 3767 (class 1259 OID 17500)
-- Name: index_savedsearch_creator_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_savedsearch_creator_id ON public.savedsearch USING btree (creator_id);


--
-- TOC entry 3768 (class 1259 OID 17501)
-- Name: index_savedsearch_definitionpoint_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_savedsearch_definitionpoint_id ON public.savedsearch USING btree (definitionpoint_id);


--
-- TOC entry 3771 (class 1259 OID 17502)
-- Name: index_savedsearchfilterquery_savedsearch_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_savedsearchfilterquery_savedsearch_id ON public.savedsearchfilterquery USING btree (savedsearch_id);


--
-- TOC entry 3783 (class 1259 OID 17503)
-- Name: index_summarystatistic_datavariable_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_summarystatistic_datavariable_id ON public.summarystatistic USING btree (datavariable_id);


--
-- TOC entry 3786 (class 1259 OID 17504)
-- Name: index_template_dataverse_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_template_dataverse_id ON public.template USING btree (dataverse_id);


--
-- TOC entry 3793 (class 1259 OID 17505)
-- Name: index_usernotification_user_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_usernotification_user_id ON public.usernotification USING btree (user_id);


--
-- TOC entry 3796 (class 1259 OID 17506)
-- Name: index_vargroup_filemetadata_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_vargroup_filemetadata_id ON public.vargroup USING btree (filemetadata_id);


--
-- TOC entry 3801 (class 1259 OID 17507)
-- Name: index_variablecategory_datavariable_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_variablecategory_datavariable_id ON public.variablecategory USING btree (datavariable_id);


--
-- TOC entry 3804 (class 1259 OID 17508)
-- Name: index_variablemetadata_datavariable_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_variablemetadata_datavariable_id ON public.variablemetadata USING btree (datavariable_id);


--
-- TOC entry 3805 (class 1259 OID 17509)
-- Name: index_variablemetadata_datavariable_id_filemetadata_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_variablemetadata_datavariable_id_filemetadata_id ON public.variablemetadata USING btree (datavariable_id, filemetadata_id);


--
-- TOC entry 3806 (class 1259 OID 17510)
-- Name: index_variablemetadata_filemetadata_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_variablemetadata_filemetadata_id ON public.variablemetadata USING btree (filemetadata_id);


--
-- TOC entry 3811 (class 1259 OID 17511)
-- Name: index_variablerange_datavariable_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_variablerange_datavariable_id ON public.variablerange USING btree (datavariable_id);


--
-- TOC entry 3814 (class 1259 OID 17512)
-- Name: index_variablerangeitem_datavariable_id; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE INDEX index_variablerangeitem_datavariable_id ON public.variablerangeitem USING btree (datavariable_id);


--
-- TOC entry 3576 (class 1259 OID 17513)
-- Name: one_draft_version_per_dataset; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE UNIQUE INDEX one_draft_version_per_dataset ON public.datasetversion USING btree (dataset_id) WHERE ((versionstate)::text = 'DRAFT'::text);


--
-- TOC entry 3778 (class 1259 OID 17514)
-- Name: unique_settings; Type: INDEX; Schema: public; Owner: dataverse
--

CREATE UNIQUE INDEX unique_settings ON public.setting USING btree (name, COALESCE(lang, ''::text));


--
-- TOC entry 3875 (class 2606 OID 17515)
-- Name: dataverse_citationdatasetfieldtypes dataverse_citationdatasetfieldtypes_citationdatasetfieldtype_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse_citationdatasetfieldtypes
    ADD CONSTRAINT dataverse_citationdatasetfieldtypes_citationdatasetfieldtype_id FOREIGN KEY (citationdatasetfieldtype_id) REFERENCES public.datasetfieldtype(id);


--
-- TOC entry 3852 (class 2606 OID 17520)
-- Name: datasetfield_controlledvocabularyvalue dtasetfieldcontrolledvocabularyvaluecntrolledvocabularyvaluesid; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfield_controlledvocabularyvalue
    ADD CONSTRAINT dtasetfieldcontrolledvocabularyvaluecntrolledvocabularyvaluesid FOREIGN KEY (controlledvocabularyvalues_id) REFERENCES public.controlledvocabularyvalue(id);


--
-- TOC entry 3896 (class 2606 OID 17525)
-- Name: explicitgroup_authenticateduser explicitgroup_authenticateduser_containedauthenticatedusers_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup_authenticateduser
    ADD CONSTRAINT explicitgroup_authenticateduser_containedauthenticatedusers_id FOREIGN KEY (containedauthenticatedusers_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3823 (class 2606 OID 17530)
-- Name: alternativepersistentidentifier fk_alternativepersistentidentifier_dvobject_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.alternativepersistentidentifier
    ADD CONSTRAINT fk_alternativepersistentidentifier_dvobject_id FOREIGN KEY (dvobject_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3824 (class 2606 OID 17535)
-- Name: apitoken fk_apitoken_authenticateduser_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.apitoken
    ADD CONSTRAINT fk_apitoken_authenticateduser_id FOREIGN KEY (authenticateduser_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3825 (class 2606 OID 17540)
-- Name: authenticateduserlookup fk_authenticateduserlookup_authenticateduser_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.authenticateduserlookup
    ADD CONSTRAINT fk_authenticateduserlookup_authenticateduser_id FOREIGN KEY (authenticateduser_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3826 (class 2606 OID 17545)
-- Name: auxiliaryfile fk_auxiliaryfile_datafile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.auxiliaryfile
    ADD CONSTRAINT fk_auxiliaryfile_datafile_id FOREIGN KEY (datafile_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3827 (class 2606 OID 17550)
-- Name: bannermessagetext fk_bannermessagetext_bannermessage_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.bannermessagetext
    ADD CONSTRAINT fk_bannermessagetext_bannermessage_id FOREIGN KEY (bannermessage_id) REFERENCES public.bannermessage(id);


--
-- TOC entry 3828 (class 2606 OID 17555)
-- Name: categorymetadata fk_categorymetadata_category_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.categorymetadata
    ADD CONSTRAINT fk_categorymetadata_category_id FOREIGN KEY (category_id) REFERENCES public.variablecategory(id);


--
-- TOC entry 3829 (class 2606 OID 17560)
-- Name: categorymetadata fk_categorymetadata_variablemetadata_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.categorymetadata
    ADD CONSTRAINT fk_categorymetadata_variablemetadata_id FOREIGN KEY (variablemetadata_id) REFERENCES public.variablemetadata(id);


--
-- TOC entry 3830 (class 2606 OID 17565)
-- Name: clientharvestrun fk_clientharvestrun_harvestingclient_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.clientharvestrun
    ADD CONSTRAINT fk_clientharvestrun_harvestingclient_id FOREIGN KEY (harvestingclient_id) REFERENCES public.harvestingclient(id);


--
-- TOC entry 3831 (class 2606 OID 17570)
-- Name: confirmemaildata fk_confirmemaildata_authenticateduser_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.confirmemaildata
    ADD CONSTRAINT fk_confirmemaildata_authenticateduser_id FOREIGN KEY (authenticateduser_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3832 (class 2606 OID 17575)
-- Name: controlledvocabalternate fk_controlledvocabalternate_controlledvocabularyvalue_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.controlledvocabalternate
    ADD CONSTRAINT fk_controlledvocabalternate_controlledvocabularyvalue_id FOREIGN KEY (controlledvocabularyvalue_id) REFERENCES public.controlledvocabularyvalue(id);


--
-- TOC entry 3833 (class 2606 OID 17580)
-- Name: controlledvocabalternate fk_controlledvocabalternate_datasetfieldtype_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.controlledvocabalternate
    ADD CONSTRAINT fk_controlledvocabalternate_datasetfieldtype_id FOREIGN KEY (datasetfieldtype_id) REFERENCES public.datasetfieldtype(id);


--
-- TOC entry 3834 (class 2606 OID 17585)
-- Name: controlledvocabularyvalue fk_controlledvocabularyvalue_datasetfieldtype_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.controlledvocabularyvalue
    ADD CONSTRAINT fk_controlledvocabularyvalue_datasetfieldtype_id FOREIGN KEY (datasetfieldtype_id) REFERENCES public.datasetfieldtype(id);


--
-- TOC entry 3835 (class 2606 OID 17590)
-- Name: customquestion fk_customquestion_guestbook_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestion
    ADD CONSTRAINT fk_customquestion_guestbook_id FOREIGN KEY (guestbook_id) REFERENCES public.guestbook(id);


--
-- TOC entry 3836 (class 2606 OID 17595)
-- Name: customquestionresponse fk_customquestionresponse_customquestion_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestionresponse
    ADD CONSTRAINT fk_customquestionresponse_customquestion_id FOREIGN KEY (customquestion_id) REFERENCES public.customquestion(id);


--
-- TOC entry 3837 (class 2606 OID 17600)
-- Name: customquestionresponse fk_customquestionresponse_guestbookresponse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestionresponse
    ADD CONSTRAINT fk_customquestionresponse_guestbookresponse_id FOREIGN KEY (guestbookresponse_id) REFERENCES public.guestbookresponse(id);


--
-- TOC entry 3838 (class 2606 OID 17605)
-- Name: customquestionvalue fk_customquestionvalue_customquestion_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.customquestionvalue
    ADD CONSTRAINT fk_customquestionvalue_customquestion_id FOREIGN KEY (customquestion_id) REFERENCES public.customquestion(id);


--
-- TOC entry 3839 (class 2606 OID 17610)
-- Name: datafile fk_datafile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datafile
    ADD CONSTRAINT fk_datafile_id FOREIGN KEY (id) REFERENCES public.dvobject(id);


--
-- TOC entry 3840 (class 2606 OID 17615)
-- Name: datafilecategory fk_datafilecategory_dataset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datafilecategory
    ADD CONSTRAINT fk_datafilecategory_dataset_id FOREIGN KEY (dataset_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3841 (class 2606 OID 17620)
-- Name: datafiletag fk_datafiletag_datafile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datafiletag
    ADD CONSTRAINT fk_datafiletag_datafile_id FOREIGN KEY (datafile_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3842 (class 2606 OID 17625)
-- Name: dataset fk_dataset_citationdatedatasetfieldtype_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataset
    ADD CONSTRAINT fk_dataset_citationdatedatasetfieldtype_id FOREIGN KEY (citationdatedatasetfieldtype_id) REFERENCES public.datasetfieldtype(id);


--
-- TOC entry 3843 (class 2606 OID 17630)
-- Name: dataset fk_dataset_guestbook_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataset
    ADD CONSTRAINT fk_dataset_guestbook_id FOREIGN KEY (guestbook_id) REFERENCES public.guestbook(id);


--
-- TOC entry 3844 (class 2606 OID 17635)
-- Name: dataset fk_dataset_harvestingclient_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataset
    ADD CONSTRAINT fk_dataset_harvestingclient_id FOREIGN KEY (harvestingclient_id) REFERENCES public.harvestingclient(id);


--
-- TOC entry 3845 (class 2606 OID 17640)
-- Name: dataset fk_dataset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataset
    ADD CONSTRAINT fk_dataset_id FOREIGN KEY (id) REFERENCES public.dvobject(id);


--
-- TOC entry 3846 (class 2606 OID 17645)
-- Name: dataset fk_dataset_thumbnailfile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataset
    ADD CONSTRAINT fk_dataset_thumbnailfile_id FOREIGN KEY (thumbnailfile_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3847 (class 2606 OID 17650)
-- Name: datasetexternalcitations fk_datasetexternalcitations_dataset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetexternalcitations
    ADD CONSTRAINT fk_datasetexternalcitations_dataset_id FOREIGN KEY (dataset_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3853 (class 2606 OID 17655)
-- Name: datasetfield_controlledvocabularyvalue fk_datasetfield_controlledvocabularyvalue_datasetfield_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfield_controlledvocabularyvalue
    ADD CONSTRAINT fk_datasetfield_controlledvocabularyvalue_datasetfield_id FOREIGN KEY (datasetfield_id) REFERENCES public.datasetfield(id);


--
-- TOC entry 3848 (class 2606 OID 17660)
-- Name: datasetfield fk_datasetfield_datasetfieldtype_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfield
    ADD CONSTRAINT fk_datasetfield_datasetfieldtype_id FOREIGN KEY (datasetfieldtype_id) REFERENCES public.datasetfieldtype(id);


--
-- TOC entry 3849 (class 2606 OID 17665)
-- Name: datasetfield fk_datasetfield_datasetversion_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfield
    ADD CONSTRAINT fk_datasetfield_datasetversion_id FOREIGN KEY (datasetversion_id) REFERENCES public.datasetversion(id);


--
-- TOC entry 3850 (class 2606 OID 17670)
-- Name: datasetfield fk_datasetfield_parentdatasetfieldcompoundvalue_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfield
    ADD CONSTRAINT fk_datasetfield_parentdatasetfieldcompoundvalue_id FOREIGN KEY (parentdatasetfieldcompoundvalue_id) REFERENCES public.datasetfieldcompoundvalue(id);


--
-- TOC entry 3851 (class 2606 OID 17675)
-- Name: datasetfield fk_datasetfield_template_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfield
    ADD CONSTRAINT fk_datasetfield_template_id FOREIGN KEY (template_id) REFERENCES public.template(id);


--
-- TOC entry 3854 (class 2606 OID 17680)
-- Name: datasetfieldcompoundvalue fk_datasetfieldcompoundvalue_parentdatasetfield_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldcompoundvalue
    ADD CONSTRAINT fk_datasetfieldcompoundvalue_parentdatasetfield_id FOREIGN KEY (parentdatasetfield_id) REFERENCES public.datasetfield(id);


--
-- TOC entry 3855 (class 2606 OID 17685)
-- Name: datasetfielddefaultvalue fk_datasetfielddefaultvalue_datasetfield_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfielddefaultvalue
    ADD CONSTRAINT fk_datasetfielddefaultvalue_datasetfield_id FOREIGN KEY (datasetfield_id) REFERENCES public.datasetfieldtype(id);


--
-- TOC entry 3856 (class 2606 OID 17690)
-- Name: datasetfielddefaultvalue fk_datasetfielddefaultvalue_defaultvalueset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfielddefaultvalue
    ADD CONSTRAINT fk_datasetfielddefaultvalue_defaultvalueset_id FOREIGN KEY (defaultvalueset_id) REFERENCES public.defaultvalueset(id);


--
-- TOC entry 3857 (class 2606 OID 17695)
-- Name: datasetfielddefaultvalue fk_datasetfielddefaultvalue_parentdatasetfielddefaultvalue_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfielddefaultvalue
    ADD CONSTRAINT fk_datasetfielddefaultvalue_parentdatasetfielddefaultvalue_id FOREIGN KEY (parentdatasetfielddefaultvalue_id) REFERENCES public.datasetfielddefaultvalue(id);


--
-- TOC entry 3858 (class 2606 OID 17700)
-- Name: datasetfieldtype fk_datasetfieldtype_metadatablock_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldtype
    ADD CONSTRAINT fk_datasetfieldtype_metadatablock_id FOREIGN KEY (metadatablock_id) REFERENCES public.metadatablock(id);


--
-- TOC entry 3859 (class 2606 OID 17705)
-- Name: datasetfieldtype fk_datasetfieldtype_parentdatasetfieldtype_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldtype
    ADD CONSTRAINT fk_datasetfieldtype_parentdatasetfieldtype_id FOREIGN KEY (parentdatasetfieldtype_id) REFERENCES public.datasetfieldtype(id);


--
-- TOC entry 3860 (class 2606 OID 17710)
-- Name: datasetfieldvalue fk_datasetfieldvalue_datasetfield_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetfieldvalue
    ADD CONSTRAINT fk_datasetfieldvalue_datasetfield_id FOREIGN KEY (datasetfield_id) REFERENCES public.datasetfield(id);


--
-- TOC entry 3861 (class 2606 OID 17715)
-- Name: datasetlinkingdataverse fk_datasetlinkingdataverse_dataset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetlinkingdataverse
    ADD CONSTRAINT fk_datasetlinkingdataverse_dataset_id FOREIGN KEY (dataset_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3862 (class 2606 OID 17720)
-- Name: datasetlinkingdataverse fk_datasetlinkingdataverse_linkingdataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetlinkingdataverse
    ADD CONSTRAINT fk_datasetlinkingdataverse_linkingdataverse_id FOREIGN KEY (linkingdataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3863 (class 2606 OID 17725)
-- Name: datasetlock fk_datasetlock_dataset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetlock
    ADD CONSTRAINT fk_datasetlock_dataset_id FOREIGN KEY (dataset_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3864 (class 2606 OID 17730)
-- Name: datasetlock fk_datasetlock_user_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetlock
    ADD CONSTRAINT fk_datasetlock_user_id FOREIGN KEY (user_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3865 (class 2606 OID 17735)
-- Name: datasetmetrics fk_datasetmetrics_dataset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetmetrics
    ADD CONSTRAINT fk_datasetmetrics_dataset_id FOREIGN KEY (dataset_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3866 (class 2606 OID 17740)
-- Name: datasetversion fk_datasetversion_dataset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetversion
    ADD CONSTRAINT fk_datasetversion_dataset_id FOREIGN KEY (dataset_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3867 (class 2606 OID 17745)
-- Name: datasetversion fk_datasetversion_termsofuseandaccess_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetversion
    ADD CONSTRAINT fk_datasetversion_termsofuseandaccess_id FOREIGN KEY (termsofuseandaccess_id) REFERENCES public.termsofuseandaccess(id);


--
-- TOC entry 3868 (class 2606 OID 17750)
-- Name: datasetversionuser fk_datasetversionuser_authenticateduser_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetversionuser
    ADD CONSTRAINT fk_datasetversionuser_authenticateduser_id FOREIGN KEY (authenticateduser_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3869 (class 2606 OID 17755)
-- Name: datasetversionuser fk_datasetversionuser_datasetversion_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datasetversionuser
    ADD CONSTRAINT fk_datasetversionuser_datasetversion_id FOREIGN KEY (datasetversion_id) REFERENCES public.datasetversion(id);


--
-- TOC entry 3870 (class 2606 OID 17760)
-- Name: datatable fk_datatable_datafile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datatable
    ADD CONSTRAINT fk_datatable_datafile_id FOREIGN KEY (datafile_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3871 (class 2606 OID 17765)
-- Name: datavariable fk_datavariable_datatable_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.datavariable
    ADD CONSTRAINT fk_datavariable_datatable_id FOREIGN KEY (datatable_id) REFERENCES public.datatable(id);


--
-- TOC entry 3876 (class 2606 OID 17770)
-- Name: dataverse_citationdatasetfieldtypes fk_dataverse_citationdatasetfieldtypes_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse_citationdatasetfieldtypes
    ADD CONSTRAINT fk_dataverse_citationdatasetfieldtypes_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3872 (class 2606 OID 17775)
-- Name: dataverse fk_dataverse_defaultcontributorrole_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse
    ADD CONSTRAINT fk_dataverse_defaultcontributorrole_id FOREIGN KEY (defaultcontributorrole_id) REFERENCES public.dataverserole(id);


--
-- TOC entry 3873 (class 2606 OID 17780)
-- Name: dataverse fk_dataverse_defaulttemplate_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse
    ADD CONSTRAINT fk_dataverse_defaulttemplate_id FOREIGN KEY (defaulttemplate_id) REFERENCES public.template(id);


--
-- TOC entry 3874 (class 2606 OID 17785)
-- Name: dataverse fk_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse
    ADD CONSTRAINT fk_dataverse_id FOREIGN KEY (id) REFERENCES public.dvobject(id);


--
-- TOC entry 3877 (class 2606 OID 17790)
-- Name: dataverse_metadatablock fk_dataverse_metadatablock_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse_metadatablock
    ADD CONSTRAINT fk_dataverse_metadatablock_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3878 (class 2606 OID 17795)
-- Name: dataverse_metadatablock fk_dataverse_metadatablock_metadatablocks_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverse_metadatablock
    ADD CONSTRAINT fk_dataverse_metadatablock_metadatablocks_id FOREIGN KEY (metadatablocks_id) REFERENCES public.metadatablock(id);


--
-- TOC entry 3879 (class 2606 OID 17800)
-- Name: dataversecontact fk_dataversecontact_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversecontact
    ADD CONSTRAINT fk_dataversecontact_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3880 (class 2606 OID 17805)
-- Name: dataversefacet fk_dataversefacet_datasetfieldtype_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefacet
    ADD CONSTRAINT fk_dataversefacet_datasetfieldtype_id FOREIGN KEY (datasetfieldtype_id) REFERENCES public.datasetfieldtype(id);


--
-- TOC entry 3881 (class 2606 OID 17810)
-- Name: dataversefacet fk_dataversefacet_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefacet
    ADD CONSTRAINT fk_dataversefacet_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3882 (class 2606 OID 17815)
-- Name: dataversefeatureddataverse fk_dataversefeatureddataverse_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefeatureddataverse
    ADD CONSTRAINT fk_dataversefeatureddataverse_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3883 (class 2606 OID 17820)
-- Name: dataversefeatureddataverse fk_dataversefeatureddataverse_featureddataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefeatureddataverse
    ADD CONSTRAINT fk_dataversefeatureddataverse_featureddataverse_id FOREIGN KEY (featureddataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3884 (class 2606 OID 17825)
-- Name: dataversefieldtypeinputlevel fk_dataversefieldtypeinputlevel_datasetfieldtype_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefieldtypeinputlevel
    ADD CONSTRAINT fk_dataversefieldtypeinputlevel_datasetfieldtype_id FOREIGN KEY (datasetfieldtype_id) REFERENCES public.datasetfieldtype(id);


--
-- TOC entry 3885 (class 2606 OID 17830)
-- Name: dataversefieldtypeinputlevel fk_dataversefieldtypeinputlevel_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversefieldtypeinputlevel
    ADD CONSTRAINT fk_dataversefieldtypeinputlevel_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3886 (class 2606 OID 17835)
-- Name: dataverselinkingdataverse fk_dataverselinkingdataverse_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverselinkingdataverse
    ADD CONSTRAINT fk_dataverselinkingdataverse_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3887 (class 2606 OID 17840)
-- Name: dataverselinkingdataverse fk_dataverselinkingdataverse_linkingdataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverselinkingdataverse
    ADD CONSTRAINT fk_dataverselinkingdataverse_linkingdataverse_id FOREIGN KEY (linkingdataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3888 (class 2606 OID 17845)
-- Name: dataverserole fk_dataverserole_owner_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataverserole
    ADD CONSTRAINT fk_dataverserole_owner_id FOREIGN KEY (owner_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3889 (class 2606 OID 17850)
-- Name: dataversesubjects fk_dataversesubjects_controlledvocabularyvalue_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversesubjects
    ADD CONSTRAINT fk_dataversesubjects_controlledvocabularyvalue_id FOREIGN KEY (controlledvocabularyvalue_id) REFERENCES public.controlledvocabularyvalue(id);


--
-- TOC entry 3890 (class 2606 OID 17855)
-- Name: dataversesubjects fk_dataversesubjects_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversesubjects
    ADD CONSTRAINT fk_dataversesubjects_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3891 (class 2606 OID 17860)
-- Name: dataversetheme fk_dataversetheme_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dataversetheme
    ADD CONSTRAINT fk_dataversetheme_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3892 (class 2606 OID 17865)
-- Name: dvobject fk_dvobject_creator_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dvobject
    ADD CONSTRAINT fk_dvobject_creator_id FOREIGN KEY (creator_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3893 (class 2606 OID 17870)
-- Name: dvobject fk_dvobject_owner_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dvobject
    ADD CONSTRAINT fk_dvobject_owner_id FOREIGN KEY (owner_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3894 (class 2606 OID 17875)
-- Name: dvobject fk_dvobject_releaseuser_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.dvobject
    ADD CONSTRAINT fk_dvobject_releaseuser_id FOREIGN KEY (releaseuser_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3897 (class 2606 OID 17880)
-- Name: explicitgroup_authenticateduser fk_explicitgroup_authenticateduser_explicitgroup_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup_authenticateduser
    ADD CONSTRAINT fk_explicitgroup_authenticateduser_explicitgroup_id FOREIGN KEY (explicitgroup_id) REFERENCES public.explicitgroup(id);


--
-- TOC entry 3898 (class 2606 OID 17885)
-- Name: explicitgroup_containedroleassignees fk_explicitgroup_containedroleassignees_explicitgroup_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup_containedroleassignees
    ADD CONSTRAINT fk_explicitgroup_containedroleassignees_explicitgroup_id FOREIGN KEY (explicitgroup_id) REFERENCES public.explicitgroup(id);


--
-- TOC entry 3899 (class 2606 OID 17890)
-- Name: explicitgroup_explicitgroup fk_explicitgroup_explicitgroup_containedexplicitgroups_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup_explicitgroup
    ADD CONSTRAINT fk_explicitgroup_explicitgroup_containedexplicitgroups_id FOREIGN KEY (containedexplicitgroups_id) REFERENCES public.explicitgroup(id);


--
-- TOC entry 3900 (class 2606 OID 17895)
-- Name: explicitgroup_explicitgroup fk_explicitgroup_explicitgroup_explicitgroup_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup_explicitgroup
    ADD CONSTRAINT fk_explicitgroup_explicitgroup_explicitgroup_id FOREIGN KEY (explicitgroup_id) REFERENCES public.explicitgroup(id);


--
-- TOC entry 3895 (class 2606 OID 17900)
-- Name: explicitgroup fk_explicitgroup_owner_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.explicitgroup
    ADD CONSTRAINT fk_explicitgroup_owner_id FOREIGN KEY (owner_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3901 (class 2606 OID 17905)
-- Name: externaltooltype fk_externaltooltype_externaltool_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.externaltooltype
    ADD CONSTRAINT fk_externaltooltype_externaltool_id FOREIGN KEY (externaltool_id) REFERENCES public.externaltool(id);


--
-- TOC entry 3902 (class 2606 OID 17910)
-- Name: fileaccessrequests fk_fileaccessrequests_authenticated_user_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.fileaccessrequests
    ADD CONSTRAINT fk_fileaccessrequests_authenticated_user_id FOREIGN KEY (authenticated_user_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3903 (class 2606 OID 17915)
-- Name: fileaccessrequests fk_fileaccessrequests_datafile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.fileaccessrequests
    ADD CONSTRAINT fk_fileaccessrequests_datafile_id FOREIGN KEY (datafile_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3904 (class 2606 OID 17920)
-- Name: filedownload fk_filedownload_guestbookresponse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.filedownload
    ADD CONSTRAINT fk_filedownload_guestbookresponse_id FOREIGN KEY (guestbookresponse_id) REFERENCES public.guestbookresponse(id);


--
-- TOC entry 3905 (class 2606 OID 17925)
-- Name: filemetadata fk_filemetadata_datafile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.filemetadata
    ADD CONSTRAINT fk_filemetadata_datafile_id FOREIGN KEY (datafile_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3907 (class 2606 OID 17930)
-- Name: filemetadata_datafilecategory fk_filemetadata_datafilecategory_filecategories_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.filemetadata_datafilecategory
    ADD CONSTRAINT fk_filemetadata_datafilecategory_filecategories_id FOREIGN KEY (filecategories_id) REFERENCES public.datafilecategory(id);


--
-- TOC entry 3908 (class 2606 OID 17935)
-- Name: filemetadata_datafilecategory fk_filemetadata_datafilecategory_filemetadatas_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.filemetadata_datafilecategory
    ADD CONSTRAINT fk_filemetadata_datafilecategory_filemetadatas_id FOREIGN KEY (filemetadatas_id) REFERENCES public.filemetadata(id);


--
-- TOC entry 3906 (class 2606 OID 17940)
-- Name: filemetadata fk_filemetadata_datasetversion_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.filemetadata
    ADD CONSTRAINT fk_filemetadata_datasetversion_id FOREIGN KEY (datasetversion_id) REFERENCES public.datasetversion(id);


--
-- TOC entry 3909 (class 2606 OID 17945)
-- Name: foreignmetadatafieldmapping fk_foreignmetadatafieldmapping_foreignmetadataformatmapping_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.foreignmetadatafieldmapping
    ADD CONSTRAINT fk_foreignmetadatafieldmapping_foreignmetadataformatmapping_id FOREIGN KEY (foreignmetadataformatmapping_id) REFERENCES public.foreignmetadataformatmapping(id);


--
-- TOC entry 3910 (class 2606 OID 17950)
-- Name: foreignmetadatafieldmapping fk_foreignmetadatafieldmapping_parentfieldmapping_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.foreignmetadatafieldmapping
    ADD CONSTRAINT fk_foreignmetadatafieldmapping_parentfieldmapping_id FOREIGN KEY (parentfieldmapping_id) REFERENCES public.foreignmetadatafieldmapping(id);


--
-- TOC entry 3911 (class 2606 OID 17955)
-- Name: guestbook fk_guestbook_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbook
    ADD CONSTRAINT fk_guestbook_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3912 (class 2606 OID 17960)
-- Name: guestbookresponse fk_guestbookresponse_authenticateduser_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbookresponse
    ADD CONSTRAINT fk_guestbookresponse_authenticateduser_id FOREIGN KEY (authenticateduser_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3913 (class 2606 OID 17965)
-- Name: guestbookresponse fk_guestbookresponse_datafile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbookresponse
    ADD CONSTRAINT fk_guestbookresponse_datafile_id FOREIGN KEY (datafile_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3914 (class 2606 OID 17970)
-- Name: guestbookresponse fk_guestbookresponse_dataset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbookresponse
    ADD CONSTRAINT fk_guestbookresponse_dataset_id FOREIGN KEY (dataset_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3915 (class 2606 OID 17975)
-- Name: guestbookresponse fk_guestbookresponse_datasetversion_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbookresponse
    ADD CONSTRAINT fk_guestbookresponse_datasetversion_id FOREIGN KEY (datasetversion_id) REFERENCES public.datasetversion(id);


--
-- TOC entry 3916 (class 2606 OID 17980)
-- Name: guestbookresponse fk_guestbookresponse_guestbook_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.guestbookresponse
    ADD CONSTRAINT fk_guestbookresponse_guestbook_id FOREIGN KEY (guestbook_id) REFERENCES public.guestbook(id);


--
-- TOC entry 3917 (class 2606 OID 17985)
-- Name: harvestingclient fk_harvestingclient_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.harvestingclient
    ADD CONSTRAINT fk_harvestingclient_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3918 (class 2606 OID 17990)
-- Name: harvestingdataverseconfig fk_harvestingdataverseconfig_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.harvestingdataverseconfig
    ADD CONSTRAINT fk_harvestingdataverseconfig_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3919 (class 2606 OID 17995)
-- Name: ingestreport fk_ingestreport_datafile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ingestreport
    ADD CONSTRAINT fk_ingestreport_datafile_id FOREIGN KEY (datafile_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3920 (class 2606 OID 18000)
-- Name: ingestrequest fk_ingestrequest_datafile_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ingestrequest
    ADD CONSTRAINT fk_ingestrequest_datafile_id FOREIGN KEY (datafile_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3921 (class 2606 OID 18005)
-- Name: ipv4range fk_ipv4range_owner_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ipv4range
    ADD CONSTRAINT fk_ipv4range_owner_id FOREIGN KEY (owner_id) REFERENCES public.persistedglobalgroup(id);


--
-- TOC entry 3922 (class 2606 OID 18010)
-- Name: ipv6range fk_ipv6range_owner_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.ipv6range
    ADD CONSTRAINT fk_ipv6range_owner_id FOREIGN KEY (owner_id) REFERENCES public.persistedglobalgroup(id);


--
-- TOC entry 3923 (class 2606 OID 18015)
-- Name: metadatablock fk_metadatablock_owner_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.metadatablock
    ADD CONSTRAINT fk_metadatablock_owner_id FOREIGN KEY (owner_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3924 (class 2606 OID 18020)
-- Name: metric fk_metric_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.metric
    ADD CONSTRAINT fk_metric_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dataverse(id);


--
-- TOC entry 3925 (class 2606 OID 18025)
-- Name: oauth2tokendata fk_oauth2tokendata_user_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.oauth2tokendata
    ADD CONSTRAINT fk_oauth2tokendata_user_id FOREIGN KEY (user_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3926 (class 2606 OID 18030)
-- Name: passwordresetdata fk_passwordresetdata_builtinuser_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.passwordresetdata
    ADD CONSTRAINT fk_passwordresetdata_builtinuser_id FOREIGN KEY (builtinuser_id) REFERENCES public.builtinuser(id);


--
-- TOC entry 3927 (class 2606 OID 18035)
-- Name: pendingworkflowinvocation fk_pendingworkflowinvocation_dataset_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.pendingworkflowinvocation
    ADD CONSTRAINT fk_pendingworkflowinvocation_dataset_id FOREIGN KEY (dataset_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3928 (class 2606 OID 18040)
-- Name: pendingworkflowinvocation fk_pendingworkflowinvocation_workflow_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.pendingworkflowinvocation
    ADD CONSTRAINT fk_pendingworkflowinvocation_workflow_id FOREIGN KEY (workflow_id) REFERENCES public.workflow(id);


--
-- TOC entry 3930 (class 2606 OID 18045)
-- Name: roleassignment fk_roleassignment_definitionpoint_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.roleassignment
    ADD CONSTRAINT fk_roleassignment_definitionpoint_id FOREIGN KEY (definitionpoint_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3931 (class 2606 OID 18050)
-- Name: roleassignment fk_roleassignment_role_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.roleassignment
    ADD CONSTRAINT fk_roleassignment_role_id FOREIGN KEY (role_id) REFERENCES public.dataverserole(id);


--
-- TOC entry 3932 (class 2606 OID 18055)
-- Name: savedsearch fk_savedsearch_creator_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.savedsearch
    ADD CONSTRAINT fk_savedsearch_creator_id FOREIGN KEY (creator_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3933 (class 2606 OID 18060)
-- Name: savedsearch fk_savedsearch_definitionpoint_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.savedsearch
    ADD CONSTRAINT fk_savedsearch_definitionpoint_id FOREIGN KEY (definitionpoint_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3934 (class 2606 OID 18065)
-- Name: savedsearchfilterquery fk_savedsearchfilterquery_savedsearch_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.savedsearchfilterquery
    ADD CONSTRAINT fk_savedsearchfilterquery_savedsearch_id FOREIGN KEY (savedsearch_id) REFERENCES public.savedsearch(id);


--
-- TOC entry 3935 (class 2606 OID 18070)
-- Name: summarystatistic fk_summarystatistic_datavariable_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.summarystatistic
    ADD CONSTRAINT fk_summarystatistic_datavariable_id FOREIGN KEY (datavariable_id) REFERENCES public.datavariable(id);


--
-- TOC entry 3936 (class 2606 OID 18075)
-- Name: template fk_template_dataverse_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.template
    ADD CONSTRAINT fk_template_dataverse_id FOREIGN KEY (dataverse_id) REFERENCES public.dvobject(id);


--
-- TOC entry 3937 (class 2606 OID 18080)
-- Name: template fk_template_termsofuseandaccess_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.template
    ADD CONSTRAINT fk_template_termsofuseandaccess_id FOREIGN KEY (termsofuseandaccess_id) REFERENCES public.termsofuseandaccess(id);


--
-- TOC entry 3938 (class 2606 OID 18085)
-- Name: userbannermessage fk_userbannermessage_bannermessage_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.userbannermessage
    ADD CONSTRAINT fk_userbannermessage_bannermessage_id FOREIGN KEY (bannermessage_id) REFERENCES public.bannermessage(id);


--
-- TOC entry 3939 (class 2606 OID 18090)
-- Name: userbannermessage fk_userbannermessage_user_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.userbannermessage
    ADD CONSTRAINT fk_userbannermessage_user_id FOREIGN KEY (user_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3940 (class 2606 OID 18095)
-- Name: usernotification fk_usernotification_requestor_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.usernotification
    ADD CONSTRAINT fk_usernotification_requestor_id FOREIGN KEY (requestor_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3941 (class 2606 OID 18100)
-- Name: usernotification fk_usernotification_user_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.usernotification
    ADD CONSTRAINT fk_usernotification_user_id FOREIGN KEY (user_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3943 (class 2606 OID 18105)
-- Name: vargroup_datavariable fk_vargroup_datavariable_vargroup_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.vargroup_datavariable
    ADD CONSTRAINT fk_vargroup_datavariable_vargroup_id FOREIGN KEY (vargroup_id) REFERENCES public.vargroup(id);


--
-- TOC entry 3944 (class 2606 OID 18110)
-- Name: vargroup_datavariable fk_vargroup_datavariable_varsingroup_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.vargroup_datavariable
    ADD CONSTRAINT fk_vargroup_datavariable_varsingroup_id FOREIGN KEY (varsingroup_id) REFERENCES public.datavariable(id);


--
-- TOC entry 3942 (class 2606 OID 18115)
-- Name: vargroup fk_vargroup_filemetadata_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.vargroup
    ADD CONSTRAINT fk_vargroup_filemetadata_id FOREIGN KEY (filemetadata_id) REFERENCES public.filemetadata(id);


--
-- TOC entry 3945 (class 2606 OID 18120)
-- Name: variablecategory fk_variablecategory_datavariable_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablecategory
    ADD CONSTRAINT fk_variablecategory_datavariable_id FOREIGN KEY (datavariable_id) REFERENCES public.datavariable(id);


--
-- TOC entry 3946 (class 2606 OID 18125)
-- Name: variablemetadata fk_variablemetadata_datavariable_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablemetadata
    ADD CONSTRAINT fk_variablemetadata_datavariable_id FOREIGN KEY (datavariable_id) REFERENCES public.datavariable(id);


--
-- TOC entry 3947 (class 2606 OID 18130)
-- Name: variablemetadata fk_variablemetadata_filemetadata_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablemetadata
    ADD CONSTRAINT fk_variablemetadata_filemetadata_id FOREIGN KEY (filemetadata_id) REFERENCES public.filemetadata(id);


--
-- TOC entry 3948 (class 2606 OID 18135)
-- Name: variablemetadata fk_variablemetadata_weightvariable_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablemetadata
    ADD CONSTRAINT fk_variablemetadata_weightvariable_id FOREIGN KEY (weightvariable_id) REFERENCES public.datavariable(id);


--
-- TOC entry 3949 (class 2606 OID 18140)
-- Name: variablerange fk_variablerange_datavariable_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablerange
    ADD CONSTRAINT fk_variablerange_datavariable_id FOREIGN KEY (datavariable_id) REFERENCES public.datavariable(id);


--
-- TOC entry 3950 (class 2606 OID 18145)
-- Name: variablerangeitem fk_variablerangeitem_datavariable_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.variablerangeitem
    ADD CONSTRAINT fk_variablerangeitem_datavariable_id FOREIGN KEY (datavariable_id) REFERENCES public.datavariable(id);


--
-- TOC entry 3951 (class 2606 OID 18150)
-- Name: workflowcomment fk_workflowcomment_authenticateduser_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflowcomment
    ADD CONSTRAINT fk_workflowcomment_authenticateduser_id FOREIGN KEY (authenticateduser_id) REFERENCES public.authenticateduser(id);


--
-- TOC entry 3952 (class 2606 OID 18155)
-- Name: workflowcomment fk_workflowcomment_datasetversion_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflowcomment
    ADD CONSTRAINT fk_workflowcomment_datasetversion_id FOREIGN KEY (datasetversion_id) REFERENCES public.datasetversion(id);


--
-- TOC entry 3953 (class 2606 OID 18160)
-- Name: workflowstepdata fk_workflowstepdata_parent_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflowstepdata
    ADD CONSTRAINT fk_workflowstepdata_parent_id FOREIGN KEY (parent_id) REFERENCES public.workflow(id);


--
-- TOC entry 3954 (class 2606 OID 18165)
-- Name: workflowstepdata_stepparameters fk_workflowstepdata_stepparameters_workflowstepdata_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflowstepdata_stepparameters
    ADD CONSTRAINT fk_workflowstepdata_stepparameters_workflowstepdata_id FOREIGN KEY (workflowstepdata_id) REFERENCES public.workflowstepdata(id);


--
-- TOC entry 3955 (class 2606 OID 18170)
-- Name: workflowstepdata_stepsettings fk_workflowstepdata_stepsettings_workflowstepdata_id; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.workflowstepdata_stepsettings
    ADD CONSTRAINT fk_workflowstepdata_stepsettings_workflowstepdata_id FOREIGN KEY (workflowstepdata_id) REFERENCES public.workflowstepdata(id);


--
-- TOC entry 3929 (class 2606 OID 18175)
-- Name: pendingworkflowinvocation_localdata pndngwrkflwinvocationlocaldatapndngwrkflwinvocationinvocationid; Type: FK CONSTRAINT; Schema: public; Owner: dataverse
--

ALTER TABLE ONLY public.pendingworkflowinvocation_localdata
    ADD CONSTRAINT pndngwrkflwinvocationlocaldatapndngwrkflwinvocationinvocationid FOREIGN KEY (pendingworkflowinvocation_invocationid) REFERENCES public.pendingworkflowinvocation(invocationid);


-- Completed on 2021-07-12 16:55:58

--
-- PostgreSQL database dump complete
--

