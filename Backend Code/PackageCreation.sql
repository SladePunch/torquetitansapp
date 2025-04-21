--------------------------------------------------------
--  File created - Monday-April-21-2025   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package CUSTOMER_PKG
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE "CUSTOMER_PKG" AS
    PROCEDURE Get_All_Customers (
        p_Result OUT SYS_REFCURSOR
    );
END Customer_Pkg;

/
GRANT EXECUTE ON "MANAGECUSTOMERS" TO PUBLIC;
--------------------------------------------------------
--  DDL for Package SERVICERECORD_PKG
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE PACKAGE "SERVICERECORD_PKG" AS
    PROCEDURE Get_Assigned_Tasks (
        p_MechanicID IN NUMBER,
        p_Result OUT SYS_REFCURSOR
    );

    PROCEDURE Get_Recent_Services (
        p_MaxRecords IN NUMBER,
        p_Result OUT SYS_REFCURSOR
    );

    PROCEDURE Get_Service_Records_By_Vehicle (
        p_VehicleID IN NUMBER,
        p_Result OUT SYS_REFCURSOR
    );
END ServiceRecord_Pkg;

/