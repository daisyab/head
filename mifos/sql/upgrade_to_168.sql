DROP TABLE SYSTEM_CONFIGURATION;

-- this row was CollectionSheetHelper.daysInAdvance
-- (value now stored in applicationConfiguration.default.properties)
UPDATE CONFIG_KEY_VALUE_INTEGER
   SET CONFIGURATION_KEY = 'x',
       CONFIGURATION_VALUE = 0
 WHERE CONFIGURATION_ID = 1;

UPDATE DATABASE_VERSION SET DATABASE_VERSION = 168 WHERE DATABASE_VERSION = 167;
