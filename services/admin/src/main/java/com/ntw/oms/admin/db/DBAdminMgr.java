//////////////////////////////////////////////////////////////////////////////
// Copyright 2020 Anurag Yadav (anurag.yadav@newtechways.com)               //
//                                                                          //
// Licensed under the Apache License, Version 2.0 (the "License");          //
// you may not use this file except in compliance with the License.         //
// You may obtain a copy of the License at                                  //
//                                                                          //
//     http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                          //
// Unless required by applicable law or agreed to in writing, software      //
// distributed under the License is distributed on an "AS IS" BASIS,        //
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. //
// See the License for the specific language governing permissions and      //
// limitations under the License.                                           //
//////////////////////////////////////////////////////////////////////////////

package com.ntw.oms.admin.db;

import com.ntw.common.status.DatabaseStatus;
import com.ntw.common.status.ServiceStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by anurag on 23/05/19.
 */
@Component
public class DBAdminMgr {

    private static final Logger logger = LoggerFactory.getLogger(DBAdminMgr.class);

    @Autowired(required = false)
    @Qualifier("cqlAdmin")
    private DBAdmin cqlAdmin;

    @Autowired(required = false)
    @Qualifier("sqlAdmin")
    private DBAdmin sqlAdmin;

    @Autowired
    private Environment environment;

    public DBAdminMgr() {
    }

    private boolean manageDB(String dbTypeParam) {
        String dbType = environment.getProperty("database.type");
        if (dbType == null || dbType.length() == 0 || dbType.equals("ALL"))
            return true;
        return dbType.equals(dbTypeParam) ? true : false;
    }

    private DBAdmin getDBAdmin(String dbType) {
        if (dbType.equals("CQL")) {
            return cqlAdmin;
        } else if (dbType.equals("SQL")) {
            return sqlAdmin;
        }
        return null;
    }

    public DatabaseStatus getDBStatus(String dbType) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Calcutta"));
        String db = dbType.equals("SQL") ? "Postgres" : dbType.equals("CQL") ?
                "Cassandra" : "Unknown";
        DatabaseStatus dbStatus = new DatabaseStatus(db);
        DBAdmin dbAdmin = getDBAdmin(dbType);
        if (manageDB(dbType) && dbAdmin != null) {
            dbStatus.setConnection(dbAdmin.getConnection());
            try {
                Date dateTime = dbAdmin.getDateTime();
                cal.setTime(dateTime);
                logger.debug("Fetched data from DB = {}", dateFormat.format(cal.getTime()));
                dbStatus.setDatabaseTime(dateFormat.format(cal.getTime()));
            } catch (Exception e) {
                logger.error("Unable to fetch data from {} DB", dbType);
                dbStatus.setDatabaseTime("DB not reachable: "+e.getMessage());
                return dbStatus;
            }
            return dbStatus;
        }
        return dbStatus;
    }

}
