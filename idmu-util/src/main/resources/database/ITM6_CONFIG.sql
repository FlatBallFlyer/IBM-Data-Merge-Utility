CREATE TABLE `TIA`.`ITM6_CONFIG` (
  `PROFILE` VARCHAR(255) NOT NULL 
, `AGENT` VARCHAR(20) NOT NULL 
, `SITNAME` VARCHAR(255) NOT NULL 
, `INTERVAL` VARCHAR(6) 
, `CRITERIA` VARCHAR(2000) 
, `SITINFO` VARCHAR(255) 
, `SEVERITY` VARCHAR(25) NOT NULL 
, `ENCODESIT` VARCHAR(32) 
, `LAST_UPDATE` DATE 
, `SCIM` VARCHAR(256) 
, `PRISEV` VARCHAR(20) 
, `PAGE` VARCHAR(256) 
, `CMD` VARCHAR(2000) 
, `AUTOSOPT` VARCHAR(40) 
, `LOGF` VARCHAR(256) 
, `REGEX` VARCHAR(256) 
, `HTID` VARCHAR(20) 
, `LOGFNAME` VARCHAR(40) 
, `EIF_CLASS` VARCHAR(100) 
, `ATTR` VARCHAR(256) 
, `THRESHOLD_1` VARCHAR(256) 
, `THRESHOLD_2` VARCHAR(256) 
, `PREENCODE` VARCHAR(1024) 
, `SF_ATTRIBUTE_NAME` VARCHAR(255) 
, `SF_SCRIPT_PATH_WITH_PARMS` VARCHAR(2000) 
, `SF_EXECUTION_FREQUENCY` VARCHAR(20) 
, `SF_OUTPUT_TYPE` VARCHAR(50) 
, `SF_FILTER_VALUE` VARCHAR(20) 
, `SF_FILTER_OPERATOR` VARCHAR(255) 
, `SF_CUSTOM_NAME` VARCHAR(1000) 
, `SF_IS_ACTIVE` VARCHAR(50) 
, `SF_TOKEN_LABELS` VARCHAR(1000) 
, `SF_TOKEN_SEPARATOR` VARCHAR(20) 
, `SF_KILL_AFTER_TIMEOUT` VARCHAR(20) 
, `SF_DISABLE_USE_AGENT_SYNC` VARCHAR(20) 
, `SF_TOKEN_TYPES` VARCHAR(20) 
, PRIMARY KEY 
  (
    `PROFILE` 
  , `AGENT` 
  , `SITNAME` 
  , `SEVERITY` 
  ));
