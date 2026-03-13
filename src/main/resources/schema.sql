CREATE TABLE employees
(
    id bigint PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    hire_date date NOT NULL,
    name varchar(50) NOT NULL,
    email varchar(100) UNIQUE NOT NULL,
    employee_number varchar(100) UNIQUE NOT NULL,
    position varchar(50) NOT NULL,
    work_status varchar(50) NOT NULL,
    department_id bigint NOT NULL,
    profile_id bigint UNIQUE
);

-- departmnet_id fk 설정
ALTER TABLE employees
    ADD CONSTRAINT fk_department_id
        FOREIGN KEY (department_id)
            REFERENCES departments (id)
            ON DELETE CASCADE;

-- profile_id fk 설정
ALTER TABLE employees
    ADD CONSTRAINT fk_profile_id
        FOREIGN KEY (profile_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL;

CREATE TABLE departments
(
    id bigint PRIMARY KEY,
    created_at timestamptz NOT NULL,
    updated_at timestamptz,
    name varchar(100) UNIQUE NOT NULL,
    description varchar(255),
    established_date date NOT NULL
);

CREATE TABLE employee_histories
(
    id bigint PRIMARY_KEY,
    created_at timestamptz NOT NULL,
    change_type varchar(50) NOT NULL,
    ip_address varchar(50) NOT NULL,
    memo varchar(255),
    employee_number varchar(100) NOT NULL,
    employee_id bigint
);
-- employee_id fk 설정
ALTER TABLE employee_histories
    ADD CONSTRAINT fk_employee_id
        FOREIGN KEY (employee_id)
            REFERENCES employees (id)
            ON DELETE SET NULL;

CREATE TABLE employee_history_details
(
    id bigint PRIMARY KEY,
    created_at timestamptz NOT NULL,
    property varchar(100) NOT NULL,
    before_value varchar(100),
    after_value varchar(100),
    history_id bigint NOT NULL
);
-- history_id fk 설정
ALTER TABLE employee_history_details
    ADD CONSTRAINT fk_history_id
        FOREIGN KEY (history_id)
            REFERENCES employee_histories (id)
            ON DELETE CASCADE;

CREATE TABLE backups
(
    id bigint PRIMARY KEY,
    ip_address varchar(50) NOT NULL,
    backup_status varchar(50) NOT NULL,
    started_at timestamptz NOT NULL,
    ended_at timestamptz,
    file_id bigint
);
-- file_id fk 설정
ALTER TABLE backups
    ADD CONSTRAINT fk_file_id
        FOREIGN KEY (file_id)
            REFERENCES binary_contents (id)
            ON DELETE SET NULL;

CREATE TABLE binary_contents
(
    id bigint PRIMARY KEY,
    created_at timestamptz NOT NULL,
    file_name varchar(255) NOT NULL,
    size bigint NOT NULL,
    content_type varchar(100) NOT NULL,
    path varchar(255) NOT NULL
);