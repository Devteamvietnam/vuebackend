CREATE TABLE "user" (
    id uuid NOT NULL PRIMARY KEY,
    username character varying(200) NOT NULL,
    email character varying(200),
	password character varying(200),
	fullname character varying(200),
	firstname character varying(200),
	lastname character varying(200),
	phonenumber character varying(20),
	gender character varying(5),
	address character varying(200),
	city character varying(50),
	job character varying(50),
	birthdate character varying(20),
	roles character varying(200),
	level character varying(20)
);

CREATE TABLE user_image (
    id uuid NOT NULL PRIMARY KEY,
    file_name character varying(4096),
    file_type character varying(256),
    user_id uuid NOT NULL REFERENCES "user" (id),
    data BLOB 
 );


CREATE TABLE faq_subject (
    id uuid NOT NULL PRIMARY KEY,
    name character varying(200) NOT NULL,
    kr_name character varying(200),
    note text
);

CREATE TABLE faq_category (
    id uuid NOT NULL PRIMARY KEY,
    name character varying(200) NOT NULL,
    kr_name character varying(200),
    subject_id uuid NOT NULL REFERENCES faq_subject (id),
    note text
);

CREATE TABLE faq_product (
    id uuid NOT NULL PRIMARY KEY,
    name character varying(200) NOT NULL,
    kr_name character varying(200),
    category_id uuid NOT NULL REFERENCES faq_category (id),
    note text
);

CREATE TABLE faq (
    id uuid NOT NULL PRIMARY KEY,
    title character varying(200) NOT NULL,
    content text,
    status boolean,
    level character varying(20),
    product_id uuid NOT NULL REFERENCES faq_product (id),
    created_date DATE
);

CREATE TABLE faq_image (
    id uuid NOT NULL PRIMARY KEY,
    file_name character varying(4096),
    file_type character varying(128),
    faq_id uuid NOT NULL REFERENCES faq (id),
    data BLOB 
 );

CREATE TABLE keyword (
    id uuid NOT NULL PRIMARY KEY,
    keyword character varying(200) NOT NULL,
    note text,
    created_date DATE
);

CREATE TABLE faq_keyword (
	faq_id uuid NOT NULL REFERENCES faq (id),
	keyword_id uuid NOT NULL REFERENCES keyword (id)
);




CREATE TABLE event_category (
	id uuid NOT NULL PRIMARY KEY,
	category_name character varying(4096),
	payment_type character varying(4096),
	start_datetime TIMESTAMP,
	end_datetime TIMESTAMP
);

CREATE TABLE event (
	id uuid NOT NULL PRIMARY KEY,
	category_id uuid NOT NULL REFERENCES event_category (id),
	event_name character varying(256),
	place character varying(256),
	lecturer character varying(4096),
	max_participant integer,
	current_participant integer,
	start_datetime TIMESTAMP,
	end_datetime TIMESTAMP,
	start_apply_datetime TIMESTAMP,
	end_apply_datetime TIMESTAMP,
	description text
);

CREATE TABLE event_image (
    id uuid NOT NULL PRIMARY KEY,
    file_name character varying(4096),
    file_type character varying(128),
    event_id uuid NOT NULL REFERENCES event (id),
    image_type character varying(256),
    data BLOB 
 );

CREATE TABLE event_category_image (
    id uuid NOT NULL PRIMARY KEY,
    file_name character varying(4096),
    file_type character varying(128),
    category_id uuid NOT NULL REFERENCES event_category (id),
    data BLOB 
 );

CREATE TABLE event_additional_info (
	id uuid NOT NULL PRIMARY KEY,
	question character varying(4096),
	required boolean,
	event_id uuid NOT NULL REFERENCES event (id)
);

CREATE TABLE event_survey (
	id uuid NOT NULL PRIMARY KEY,
	question character varying(4096),
	event_id uuid NOT NULL REFERENCES event (id)	
);

CREATE TABLE event_survey_option (
    id uuid NOT NULL PRIMARY KEY,
    event_survey_id uuid NOT NULL REFERENCES event_survey (id),
    option character varying(256),
    poll integer
);

CREATE TABLE role (
	id uuid NOT NULL PRIMARY KEY,
	name character varying(20)
);

CREATE TABLE user_role (
	user_id uuid NOT NULL REFERENCES "user" (id),
	role_id uuid NOT NULL REFERENCES role (id)
);

CREATE TABLE applicant (
	id uuid NOT NULL PRIMARY KEY,
	user_id uuid NOT NULL REFERENCES "user" (id),
	event_id uuid NOT NULL REFERENCES event (id),
	applied_date TIMESTAMP,
	state character varying(20),
	number_of_tickets integer
);

CREATE TABLE applicant_additionalinfo_answer (
	id uuid NOT NULL PRIMARY KEY,
	applicant_id uuid NOT NULL REFERENCES applicant (id),
	question character varying(4096),
	answer character varying(4096),
	required boolean
);

CREATE TABLE applicant_survey_answer (
	id uuid NOT NULL PRIMARY KEY,
	applicant_id uuid NOT NULL REFERENCES applicant (id),
	question character varying(4096),
	answer character varying(4096)
);




CREATE TABLE product_category (
	id uuid NOT NULL PRIMARY KEY,
	colour character varying(20),
	name character varying(256)
);

CREATE TABLE product (
	id uuid NOT NULL PRIMARY KEY,
	category_id uuid NOT NULL REFERENCES product_category (id),
	name character varying(256),
	unit character varying(256),
	category character varying(256),
	amount integer,
	schedule integer,
	times_per_day integer,
	duration_in_days integer
);

CREATE TABLE product_image (
    id uuid NOT NULL PRIMARY KEY,
    file_name character varying(4096),
    file_type character varying(128),
    product_id uuid NOT NULL REFERENCES product (id),
    data BLOB 
 );

CREATE TABLE product_user_settings (
	id uuid NOT NULL PRIMARY KEY,
	product_id uuid NOT NULL REFERENCES product (id),
	user_id uuid NOT NULL REFERENCES "user" (id),
	started_date TIMESTAMP,
	schedule integer, -- 1 is everyday, 2 is every other day, 3 is every 3 days
	time1 TIMESTAMP,
	time2 TIMESTAMP,
	time3 TIMESTAMP,
	duration_in_days integer,
	enable_alarm boolean,
	note text
);

CREATE TABLE product_use_history (
	id uuid NOT NULL PRIMARY KEY,
	product_id uuid NOT NULL REFERENCES product (id),
	user_id uuid NOT NULL REFERENCES "user" (id),
	recorded_date TIMESTAMP,
	recorded_time1 TIMESTAMP,
	status1 boolean,
	recorded_time2 TIMESTAMP,
	status2 boolean,
	recorded_time3 TIMESTAMP,
	status3 boolean
);

CREATE TABLE user_preference (
	id uuid NOT NULL PRIMARY KEY,
	user_id uuid NOT NULL REFERENCES "user" (id),
	preference_type character varying(20),
	preference_data text
)