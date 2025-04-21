--------------------------------------------------------
--  File created - Monday-April-21-2025   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Procedure MANAGECUSTOMER
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "MANAGECUSTOMER" (
    p_Operation IN VARCHAR2,
    p_CustomerID IN NUMBER,
    p_Name IN VARCHAR2,
    p_Email IN VARCHAR2,
    p_Phone IN VARCHAR2
) AS
BEGIN
    IF p_Operation = 'CREATE' THEN
        INSERT INTO Customers (CustomerID, Name, Email, Phone)
        VALUES (Customers_Seq.NEXTVAL, p_Name, p_Email, p_Phone);
    ELSIF p_Operation = 'UPDATE' THEN
        UPDATE Customers
        SET Name = p_Name,
            Email = p_Email,
            Phone = p_Phone
        WHERE CustomerID = p_CustomerID;
    ELSIF p_Operation = 'DELETE' THEN
        DELETE FROM Customers
        WHERE CustomerID = p_CustomerID;
    ELSE
        RAISE_APPLICATION_ERROR(-20001, 'Invalid operation: ' || p_Operation);
    END IF;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20002, 'Error in ManageCustomer: ' || SQLERRM);
END ManageCustomer;

/
--------------------------------------------------------
--  DDL for Procedure MANAGECUSTOMERS
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "MANAGECUSTOMERS" (
    p_action IN VARCHAR2,
    p_CustomerID IN NUMBER DEFAULT NULL,
    p_Name IN VARCHAR2 DEFAULT NULL,
    p_Email IN VARCHAR2 DEFAULT NULL,
    p_Phone IN VARCHAR2 DEFAULT NULL,
    p_result OUT SYS_REFCURSOR
) AS
    v_customer_id NUMBER;
BEGIN
    IF UPPER(p_action) = 'CREATE' THEN
        SELECT customers_seq.NEXTVAL INTO v_customer_id FROM DUAL;
        INSERT INTO Customers (CustomerID, Name, Email, Phone)
        VALUES (v_customer_id, p_Name, p_Email, p_Phone);
        OPEN p_result FOR
        SELECT CustomerID, Name, Email, Phone
        FROM Customers
        WHERE CustomerID = v_customer_id;
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Customer created successfully with ID: ' || v_customer_id);

    ELSIF UPPER(p_action) = 'READ' THEN
        OPEN p_result FOR
        SELECT CustomerID, Name, Email, Phone
        FROM Customers
        WHERE CustomerID = NVL(p_CustomerID, CustomerID);

    ELSIF UPPER(p_action) = 'UPDATE' THEN
        UPDATE Customers
        SET Name = NVL(p_Name, Name),
            Email = NVL(p_Email, Email),
            Phone = NVL(p_Phone, Phone)
        WHERE CustomerID = p_CustomerID;
        IF SQL%ROWCOUNT > 0 THEN
            OPEN p_result FOR
            SELECT CustomerID, Name, Email, Phone
            FROM Customers
            WHERE CustomerID = p_CustomerID;
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Customer updated successfully.');
        ELSE
            OPEN p_result FOR SELECT * FROM Customers WHERE 1=0;
            DBMS_OUTPUT.PUT_LINE('No customer found with the provided CustomerID.');
        END IF;

    ELSIF UPPER(p_action) = 'DELETE' THEN
        DELETE FROM Customers
        WHERE CustomerID = p_CustomerID;
        IF SQL%ROWCOUNT > 0 THEN
            OPEN p_result FOR SELECT * FROM Customers WHERE 1=0;
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Customer deleted successfully.');
        ELSE
            OPEN p_result FOR SELECT * FROM Customers WHERE 1=0;
            DBMS_OUTPUT.PUT_LINE('No customer found with the provided CustomerID.');
        END IF;

    ELSE
        OPEN p_result FOR SELECT * FROM Customers WHERE 1=0;
        DBMS_OUTPUT.PUT_LINE('Invalid action. Please use CREATE, READ, UPDATE, or DELETE.');
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        OPEN p_result FOR SELECT * FROM Customers WHERE 1=0;
        RAISE;
END;

/

  GRANT EXECUTE ON "MANAGECUSTOMERS" TO PUBLIC;
--------------------------------------------------------
--  DDL for Procedure MANAGEMECHANIC
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "MANAGEMECHANIC" (
    Action IN VARCHAR2,
    MechanicID IN NUMBER DEFAULT NULL,
    Name IN VARCHAR2 DEFAULT NULL,
    ContactInfo IN VARCHAR2 DEFAULT NULL
)
AS
BEGIN
    CASE Action
        WHEN 'CREATE' THEN
            INSERT INTO Mechanic (MechanicID, Name, ContactInfo)
            VALUES (Mechanic_SEQ.NEXTVAL, Name, ContactInfo);
        WHEN 'READ' THEN
            DBMS_OUTPUT.PUT_LINE('Fetching Mechanic Data...');
            FOR rec IN (SELECT * FROM Mechanic WHERE MechanicID = MechanicID) LOOP
                DBMS_OUTPUT.PUT_LINE('MechanicID: ' || rec.MechanicID || ', Name: ' || rec.Name);
            END LOOP;
        WHEN 'UPDATE' THEN
            UPDATE Mechanic
            SET Name = Name, ContactInfo = ContactInfo
            WHERE MechanicID = MechanicID;
        WHEN 'DELETE' THEN
            DELETE FROM Mechanic WHERE MechanicID = MechanicID;
        ELSE
            DBMS_OUTPUT.PUT_LINE('Invalid Action Provided');
    END CASE;
END;

/
--------------------------------------------------------
--  DDL for Procedure MANAGEMECHANICS
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "MANAGEMECHANICS" (
    p_action IN VARCHAR2,
    p_MechanicID IN NUMBER DEFAULT NULL,
    p_Name IN VARCHAR2 DEFAULT NULL,
    p_ContactInfo IN VARCHAR2 DEFAULT NULL,
    p_result OUT SYS_REFCURSOR
) AS
    v_mechanic_id NUMBER;
BEGIN
    IF UPPER(p_action) = 'CREATE' THEN
        SELECT mechanic_seq.NEXTVAL INTO v_mechanic_id FROM DUAL;
        INSERT INTO Mechanic (MechanicID, Name, ContactInfo)
        VALUES (v_mechanic_id, p_Name, p_ContactInfo);
        OPEN p_result FOR
        SELECT MechanicID, Name, ContactInfo
        FROM Mechanic
        WHERE MechanicID = v_mechanic_id;
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Mechanic created successfully with ID: ' || v_mechanic_id);

    ELSIF UPPER(p_action) = 'READ' THEN
        OPEN p_result FOR
        SELECT MechanicID, Name, ContactInfo
        FROM Mechanic
        WHERE MechanicID = NVL(p_MechanicID, MechanicID);

    ELSIF UPPER(p_action) = 'UPDATE' THEN
        UPDATE Mechanic
        SET Name = NVL(p_Name, Name),
            ContactInfo = NVL(p_ContactInfo, ContactInfo)
        WHERE MechanicID = p_MechanicID;
        IF SQL%ROWCOUNT > 0 THEN
            OPEN p_result FOR
            SELECT MechanicID, Name, ContactInfo
            FROM Mechanic
            WHERE MechanicID = p_MechanicID;
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Mechanic updated successfully.');
        ELSE
            OPEN p_result FOR SELECT * FROM Mechanic WHERE 1=0;
            DBMS_OUTPUT.PUT_LINE('No mechanic found with the provided MechanicID.');
        END IF;

    ELSIF UPPER(p_action) = 'DELETE' THEN
        DELETE FROM Mechanic
        WHERE MechanicID = p_MechanicID;
        IF SQL%ROWCOUNT > 0 THEN
            OPEN p_result FOR SELECT * FROM Mechanic WHERE 1=0;
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Mechanic deleted successfully.');
        ELSE
            OPEN p_result FOR SELECT * FROM Mechanic WHERE 1=0;
            DBMS_OUTPUT.PUT_LINE('No mechanic found with the provided MechanicID.');
        END IF;

    ELSE
        OPEN p_result FOR SELECT * FROM Mechanic WHERE 1=0;
        DBMS_OUTPUT.PUT_LINE('Invalid action. Please use CREATE, READ, UPDATE, or DELETE.');
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        OPEN p_result FOR SELECT * FROM Mechanic WHERE 1=0;
        RAISE;
END;

/
--------------------------------------------------------
--  DDL for Procedure MANAGESERVICERECORD
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "MANAGESERVICERECORD" (
    p_Operation IN VARCHAR2,
    p_ServiceID IN NUMBER,
    p_VehicleID IN NUMBER,
    p_ServiceDate IN DATE,
    p_Description IN VARCHAR2,
    p_MechanicID IN NUMBER,
    p_Status IN VARCHAR2
) AS
BEGIN
    IF p_Operation = 'CREATE' THEN
        INSERT INTO ServiceRecord (ServiceID, VehicleID, MechanicID, Description, ServiceDate, Status)
        VALUES (ServiceRecord_Seq.NEXTVAL, p_VehicleID, p_MechanicID, p_Description, p_ServiceDate, p_Status);
    ELSIF p_Operation = 'UPDATE' THEN
        UPDATE ServiceRecord
        SET VehicleID = p_VehicleID,
            ServiceDate = p_ServiceDate,
            Description = p_Description,
            MechanicID = p_MechanicID,
            Status = p_Status
        WHERE ServiceID = p_ServiceID;
    ELSIF p_Operation = 'DELETE' THEN
        DELETE FROM ServiceRecord
        WHERE ServiceID = p_ServiceID;
    ELSE
        RAISE_APPLICATION_ERROR(-20001, 'Invalid operation: ' || p_Operation);
    END IF;
    COMMIT;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE_APPLICATION_ERROR(-20002, 'Error in ManageServiceRecord: ' || SQLERRM);
END ManageServiceRecord;

/
--------------------------------------------------------
--  DDL for Procedure MANAGEUSER
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "MANAGEUSER" (
    Action IN VARCHAR2,
    UserID IN NUMBER DEFAULT NULL,
    Username IN VARCHAR2 DEFAULT NULL,
    Password IN VARCHAR2 DEFAULT NULL,
    Email IN VARCHAR2 DEFAULT NULL,
    RoleID IN NUMBER DEFAULT NULL
)
AS
BEGIN
    CASE Action
        WHEN 'CREATE' THEN
            INSERT INTO Users (UserID, Username, Password, Email, RoleID)
            VALUES (Users_SEQ.NEXTVAL, Username, Password, Email, RoleID);
        WHEN 'READ' THEN
            DBMS_OUTPUT.PUT_LINE('Fetching User Data...');
            FOR rec IN (SELECT * FROM Users WHERE UserID = UserID) LOOP
                DBMS_OUTPUT.PUT_LINE('UserID: ' || rec.UserID || ', Username: ' || rec.Username);
            END LOOP;
        WHEN 'UPDATE' THEN
            UPDATE Users
            SET Username = Username, Password = Password, Email = Email, RoleID = RoleID
            WHERE UserID = UserID;
        WHEN 'DELETE' THEN
            DELETE FROM Users WHERE UserID = UserID;
        ELSE
            DBMS_OUTPUT.PUT_LINE('Invalid Action Provided');
    END�CASE;
END;

/
--------------------------------------------------------
--  DDL for Procedure MANAGEVEHICLES
--------------------------------------------------------
set define off;

  CREATE OR REPLACE EDITIONABLE PROCEDURE "MANAGEVEHICLES" (
    p_action IN VARCHAR2,
    p_VehicleID IN NUMBER DEFAULT NULL,
    p_Make IN VARCHAR2 DEFAULT NULL,
    p_Model IN VARCHAR2 DEFAULT NULL,
    p_Year IN NUMBER DEFAULT NULL,
    p_LicensePlate IN VARCHAR2 DEFAULT NULL,
    p_OwnerID IN NUMBER DEFAULT NULL,
    p_result OUT SYS_REFCURSOR
) AS
    v_vehicle_id NUMBER;
BEGIN
    IF UPPER(p_action) = 'CREATE' THEN
        SELECT vehicle_seq.NEXTVAL INTO v_vehicle_id FROM DUAL;
        INSERT INTO Vehicle (VehicleID, Make, Model, Year, LicensePlate, OwnerID)
        VALUES (v_vehicle_id, p_Make, p_Model, p_Year, p_LicensePlate, p_OwnerID);
        OPEN p_result FOR
        SELECT VehicleID, Make, Model, Year, LicensePlate, OwnerID
        FROM Vehicle
        WHERE VehicleID = v_vehicle_id;
        COMMIT;
        DBMS_OUTPUT.PUT_LINE('Vehicle created successfully with ID: ' || v_vehicle_id);

    ELSIF UPPER(p_action) = 'READ' THEN
        OPEN p_result FOR
        SELECT VehicleID, Make, Model, Year, LicensePlate, OwnerID
        FROM Vehicle
        WHERE VehicleID = NVL(p_VehicleID, VehicleID);

    ELSIF UPPER(p_action) = 'READ_BY_OWNER' THEN
        OPEN p_result FOR
        SELECT VehicleID, Make, Model, Year, LicensePlate, OwnerID
        FROM Vehicle
        WHERE OwnerID = p_OwnerID;

    ELSIF UPPER(p_action) = 'UPDATE' THEN
        UPDATE Vehicle
        SET Make = NVL(p_Make, Make),
            Model = NVL(p_Model, Model),
            Year = NVL(p_Year, Year),
            LicensePlate = NVL(p_LicensePlate, LicensePlate),
            OwnerID = NVL(p_OwnerID, OwnerID)
        WHERE VehicleID = p_VehicleID;
        IF SQL%ROWCOUNT > 0 THEN
            OPEN p_result FOR
            SELECT VehicleID, Make, Model, Year, LicensePlate, OwnerID
            FROM Vehicle
            WHERE VehicleID = p_VehicleID;
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Vehicle updated successfully.');
        ELSE
            OPEN p_result FOR SELECT * FROM Vehicle WHERE 1=0;
            DBMS_OUTPUT.PUT_LINE('No vehicle found with the provided VehicleID.');
        END IF;

    ELSIF UPPER(p_action) = 'DELETE' THEN
        DELETE FROM Vehicle
        WHERE VehicleID = p_VehicleID;
        IF SQL%ROWCOUNT > 0 THEN
            OPEN p_result FOR SELECT * FROM Vehicle WHERE 1=0;
            COMMIT;
            DBMS_OUTPUT.PUT_LINE('Vehicle deleted successfully.');
        ELSE
            OPEN p_result FOR SELECT * FROM Vehicle WHERE 1=0;
            DBMS_OUTPUT.PUT_LINE('No vehicle found with the provided VehicleID.');
        END IF;

    ELSE
        OPEN p_result FOR SELECT * FROM Vehicle WHERE 1=0;
        DBMS_OUTPUT.PUT_LINE('Invalid action. Please use CREATE, READ, READ_BY_OWNER, UPDATE, or DELETE.');
    END IF;
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        DBMS_OUTPUT.PUT_LINE('Error: ' || SQLERRM);
        OPEN p_result FOR SELECT * FROM Vehicle WHERE 1=0;
        RAISE;
END;

/
