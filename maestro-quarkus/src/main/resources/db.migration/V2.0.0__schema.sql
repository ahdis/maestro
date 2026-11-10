create sequence test_run_session_seq start with 1 increment by 1;

create sequence test_suite_run_session_seq start with 1 increment by 1;

create table test_run_session (
    session_id varchar(255),
    expirationDateTime timestamp(6) with time zone,
    session TEXT,
    primary key (session_id)
);

create table test_suite_run_session (
    session_id varchar(255),
    expirationDateTime timestamp(6) with time zone,
    session TEXT,
    primary key (session_id)
);