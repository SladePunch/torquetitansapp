--------------------------------------------------------
--  File created - Monday-April-21-2025   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Trigger CUSTOMERS_BI
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "CUSTOMERS_BI" 
BEFORE INSERT ON Customers
FOR EACH ROW
BEGIN
    SELECT Customers_SEQ.NEXTVAL INTO :NEW.CustomerID FROM DUAL;
END;

/
ALTER TRIGGER "CUSTOMERS_BI" ENABLE;
--------------------------------------------------------
--  DDL for Trigger DASHBOARD_BI
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "DASHBOARD_BI" 
BEFORE INSERT ON Dashboard
FOR EACH ROW
BEGIN
    SELECT Dashboard_SEQ.NEXTVAL INTO :NEW.ActivityID FROM DUAL;
END;

/
ALTER TRIGGER "DASHBOARD_BI" ENABLE;
--------------------------------------------------------
--  DDL for Trigger MAINTENANCESCHEDULE_BI
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "MAINTENANCESCHEDULE_BI" 
BEFORE INSERT ON MaintenanceSchedule
FOR EACH ROW
BEGIN
    SELECT MaintenanceSchedule_SEQ.NEXTVAL INTO :NEW.ScheduleID FROM DUAL;
END;

/
ALTER TRIGGER "MAINTENANCESCHEDULE_BI" ENABLE;
--------------------------------------------------------
--  DDL for Trigger MECHANIC_BI
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "MECHANIC_BI" 
BEFORE INSERT ON Mechanic
FOR EACH ROW
BEGIN
    SELECT Mechanic_SEQ.NEXTVAL INTO :NEW.MechanicID FROM DUAL;
END;

/
ALTER TRIGGER "MECHANIC_BI" ENABLE;
--------------------------------------------------------
--  DDL for Trigger REPORT_BI
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "REPORT_BI" 
BEFORE INSERT ON Report
FOR EACH ROW
BEGIN
    SELECT Report_SEQ.NEXTVAL INTO :NEW.ReportID FROM DUAL;
END;

/
ALTER TRIGGER "REPORT_BI" ENABLE;
--------------------------------------------------------
--  DDL for Trigger ROLE_BI
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "ROLE_BI" 
BEFORE INSERT ON Role
FOR EACH ROW
BEGIN
    SELECT Role_SEQ.NEXTVAL INTO :NEW.RoleID FROM DUAL;
END;

/
ALTER TRIGGER "ROLE_BI" ENABLE;
--------------------------------------------------------
--  DDL for Trigger SERVICERECORD_BI
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "SERVICERECORD_BI" 
BEFORE INSERT ON ServiceRecord
FOR EACH ROW
BEGIN
    SELECT ServiceRecord_SEQ.NEXTVAL INTO :NEW.ServiceID FROM DUAL;
END;

/
ALTER TRIGGER "SERVICERECORD_BI" ENABLE;
--------------------------------------------------------
--  DDL for Trigger USERS_BI
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "USERS_BI" 
BEFORE INSERT ON Users
FOR EACH ROW
BEGIN
    SELECT Users_SEQ.NEXTVAL INTO :NEW.UserID FROM DUAL;
END;

/
ALTER TRIGGER "USERS_BI" ENABLE;
--------------------------------------------------------
--  DDL for Trigger VEHICLE_BI
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TRIGGER "VEHICLE_BI" 
BEFORE INSERT ON Vehicle
FOR EACH ROW
BEGIN
    SELECT Vehicle_SEQ.NEXTVAL INTO :NEW.VehicleID FROM DUAL;
END;

/
ALTER TRIGGER "VEHICLE_BI" ENABLE;