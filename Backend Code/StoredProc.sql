REM INSERTING into EXPORT_TABLE
SET DEFINE OFF;
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','PROCEDURE GetAssignedTasks (
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','    p_MechanicID IN NUMBER,
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','    p_Result OUT SYS_REFCURSOR
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS',') AS
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','BEGIN
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','    OPEN p_Result FOR
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','        SELECT 
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','            sr.ServiceID,
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','            sr.Description,
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','            v.Plate AS Vehicle,
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','            c.FirstName || '' '' || c.LastName AS Customer,
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','            sr.ServiceType,
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','            sr.ServiceDate,
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','            sr.Status
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','        FROM ServiceRecord sr
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','        JOIN Vehicle v ON sr.VehicleID = v.VehicleID
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','        JOIN Customers c ON v.CustomerID = c.CustomerID
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','        WHERE (p_MechanicID = 0 OR sr.MechanicID = p_MechanicID)
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','        AND sr.Status IN (''Pending'', ''Ongoing'')
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','        ORDER BY sr.ServiceDate;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETASSIGNEDTASKS','END GetAssignedTasks;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','PROCEDURE GetMostRequestedService (
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','    p_ServiceName OUT VARCHAR2
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE',') AS
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','BEGIN
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','    SELECT ServiceType
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','    INTO p_ServiceName
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','    FROM ServiceRecord
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','    GROUP BY ServiceType
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','    ORDER BY COUNT(*) DESC
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','    FETCH FIRST 1 ROW ONLY;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','EXCEPTION
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','    WHEN NO_DATA_FOUND THEN
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','        p_ServiceName := ''N/A'';
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETMOSTREQUESTEDSERVICE','END GetMostRequestedService;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETTOTALSERVICESCOMPLETED','PROCEDURE GetTotalServicesCompleted (
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETTOTALSERVICESCOMPLETED','    p_Total OUT NUMBER
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETTOTALSERVICESCOMPLETED',') AS
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETTOTALSERVICESCOMPLETED','BEGIN
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETTOTALSERVICESCOMPLETED','    SELECT COUNT(*)
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETTOTALSERVICESCOMPLETED','    INTO p_Total
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETTOTALSERVICESCOMPLETED','    FROM ServiceRecord
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETTOTALSERVICESCOMPLETED','    WHERE Status = ''Completed'';
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('GETTOTALSERVICESCOMPLETED','END GetTotalServicesCompleted;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','PROCEDURE UpdateServiceStatus (
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    p_ServiceID IN NUMBER,
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    p_NewStatus IN VARCHAR2
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS',') AS
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    v_Count NUMBER;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','BEGIN
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    UPDATE ServiceRecord
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    SET Status = p_NewStatus
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    WHERE ServiceID = p_ServiceID;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    SELECT COUNT(*) INTO v_Count
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    FROM ServiceRecord
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    WHERE ServiceID = p_ServiceID;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    IF v_Count = 0 THEN
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','        RAISE_APPLICATION_ERROR(-20001, ''Service record not found'');
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    END IF;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    COMMIT;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','EXCEPTION
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','    WHEN OTHERS THEN
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','        ROLLBACK;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','        RAISE;
');
Insert into EXPORT_TABLE (NAME,TEXT) values ('UPDATESERVICESTATUS','END UpdateServiceStatus;
');
