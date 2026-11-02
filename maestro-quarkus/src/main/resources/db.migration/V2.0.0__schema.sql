create sequence test_run_session_seq start with 1 increment by 1;

create sequence test_suite_run_session_seq start with 1 increment by 1;

create table test_run_session (
                                     id integer not null,
                                     session_id varchar(255),
                                     session TEXT,
                                     primary key (id)
);

create table test_suite_run_session (
                                    id integer not null,
                                    session_id varchar(255),
                                    session TEXT,
                                    primary key (id)
);