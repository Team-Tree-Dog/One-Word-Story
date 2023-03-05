/*
  Create DB
  */
CREATE DATABASE onewordstory
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1
    IS_TEMPLATE = False;

/*
 Stories table
 */
CREATE TABLE public.stories
(
    story_id bigserial NOT NULL,
    story text NOT NULL,
    publish_unix_timestamp_utc_seconds bigint NOT NULL,
    num_likes bigint NOT NULL,
    PRIMARY KEY (story_id)
)
WITH (
    OIDS = FALSE
);

/*
 Authors table
 */

CREATE TABLE public.authors
(
    author_entry_id bigserial NOT NULL,
    story_id bigint NOT NULL,
    author_name text NOT NULL,
    PRIMARY KEY (author_entry_id)
)
WITH (
    OIDS = FALSE
);

/*
  Comments table
 */
CREATE TABLE public.comments
(
    comment_id bigserial NOT NULL,
    story_id bigint NOT NULL,
    display_name text NOT NULL,
    content text NOT NULL,
    PRIMARY KEY (comment_id)
)
    WITH (
        OIDS = FALSE
    );

/*
    Title suggestions table
*/
CREATE TABLE public.titles
(
    suggestion_id bigserial NOT NULL,
    story_id bigint NOT NULL,
    title text NOT NULL,
    upvotes bigint NOT NULL,
    PRIMARY KEY (suggestion_id)
)
    WITH (
        OIDS = FALSE
    );
