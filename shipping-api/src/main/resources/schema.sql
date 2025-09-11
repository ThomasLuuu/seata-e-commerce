CREATE TABLE IF NOT EXISTS s_shippings (
                                           ID BIGSERIAL PRIMARY KEY,
                                           USER_ID BIGINT NOT NULL,
                                           COST DECIMAL(15,2) NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    STATUS VARCHAR(20) NOT NULL,
    XID VARCHAR(128),
    BRANCH_ID BIGINT
    );

CREATE TABLE IF NOT EXISTS tcc_fence_log (
                                             XID VARCHAR(128) NOT NULL,
    BRANCH_ID BIGINT NOT NULL,
    ACTION_NAME VARCHAR(64) NOT NULL,
    STATUS SMALLINT NOT NULL, -- Changed TINYINT to SMALLINT for PostgreSQL
    GMT_CREATE TIMESTAMP NOT NULL,
    GMT_MODIFIED TIMESTAMP NOT NULL,
    PRIMARY KEY (XID, BRANCH_ID)
    );

-- Optional: Add comment for PostgreSQL
COMMENT ON COLUMN tcc_fence_log.STATUS IS '0:try, 1:commit, 2:rollback';