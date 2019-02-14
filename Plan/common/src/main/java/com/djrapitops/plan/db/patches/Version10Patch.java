/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.db.patches;

import com.djrapitops.plan.api.exceptions.database.DBInitException;
import com.djrapitops.plan.api.exceptions.database.DBOpException;
import com.djrapitops.plan.db.SQLDB;
import com.djrapitops.plan.db.sql.tables.*;

import java.util.Optional;

import static com.djrapitops.plan.db.sql.parsing.Sql.FROM;

public class Version10Patch extends Patch {

    private Integer serverID;

    public Version10Patch(SQLDB db) {
        super(db);
    }

    @Override
    public boolean hasBeenApplied() {
        return !hasTable("plan_gamemodetimes");
    }

    @Override
    protected void applyPatch() {
        try {
            Optional<Integer> fetchedServerID = db.getServerTable().getServerID(getServerUUID());
            if (!fetchedServerID.isPresent()) {
                throw new IllegalStateException("Server UUID was not registered, try rebooting the plugin.");
            }
            serverID = fetchedServerID.get();
            alterTablesToV10();
        } catch (DBInitException e) {
            throw new DBOpException(e.getMessage(), e);
        }
    }

    public void alterTablesToV10() throws DBInitException {
        copyCommandUsage();

        copyTPS();

        dropTable(UserInfoTable.TABLE_NAME);
        copyUsers();

        dropTable(GeoInfoTable.TABLE_NAME);
        execute(GeoInfoTable.createTableSQL(dbType));
        dropTable(WorldTimesTable.TABLE_NAME);
        dropTable(WorldTable.TABLE_NAME);
        execute(WorldTable.createTableSQL(dbType));
        execute(WorldTimesTable.createTableSQL(dbType));

        dropTable("plan_gamemodetimes");
        dropTable("temp_nicks");
        dropTable("temp_kills");
        dropTable("temp_users");
    }

    private void copyUsers() throws DBInitException {
        String tempTableName = "temp_users";
        renameTable(UsersTable.TABLE_NAME, tempTableName);

        String tempNickTableName = "temp_nicks";
        renameTable(NicknamesTable.TABLE_NAME, tempNickTableName);

        String tempKillsTableName = "temp_kills";
        renameTable(KillsTable.TABLE_NAME, tempKillsTableName);

        execute(UsersTable.createTableSQL(dbType));
        execute(NicknamesTable.createTableSQL(dbType));
        dropTable(SessionsTable.TABLE_NAME);
        execute(SessionsTable.createTableSQL(dbType));
        execute(KillsTable.createTableSQL(dbType));

        execute(UserInfoTable.createTableSQL(dbType));

        String statement = "INSERT INTO plan_users " +
                "(id, uuid, registered, name)" +
                " SELECT id, uuid, registered, name" +
                FROM + tempTableName;
        execute(statement);
        statement = "INSERT INTO plan_user_info " +
                "(user_id, registered, opped, banned, server_id)" +
                " SELECT id, registered, opped, banned, '" + serverID + "'" +
                FROM + tempTableName;
        execute(statement);
        statement = "INSERT INTO plan_nicknames " +
                "(user_id, nickname, server_id)" +
                " SELECT user_id, nickname, '" + serverID + "'" +
                FROM + tempNickTableName;
        execute(statement);
        statement = "INSERT INTO plan_kills " +
                "(killer_id, victim_id, weapon, date, session_id)" +
                " SELECT killer_id, victim_id, weapon, date, '0'" +
                FROM + tempKillsTableName;
        execute(statement);
    }

    private void copyCommandUsage() throws DBInitException {
        String tempTableName = "temp_cmdusg";

        renameTable("plan_commandusages", tempTableName);

        execute(CommandUseTable.createTableSQL(dbType));

        String statement = "INSERT INTO plan_commandusages " +
                "(command, times_used, server_id)" +
                " SELECT command, times_used, '" + serverID + "'" +
                FROM + tempTableName;
        execute(statement);

        dropTable(tempTableName);
    }

    private void copyTPS() throws DBInitException {
        String tempTableName = "temp_tps";
        TPSTable tpsTable = db.getTpsTable();

        renameTable(tpsTable.toString(), tempTableName);

        execute(TPSTable.createTableSQL(dbType));

        String statement = "INSERT INTO plan_tps " +
                "(date, tps, players_online, cpu_usage, ram_usage, entities, chunks_loaded, server_id)" +
                " SELECT date, tps, players_online, cpu_usage, ram_usage, entities, chunks_loaded, '" + serverID + "'" +
                FROM + tempTableName;
        execute(statement);

        dropTable(tempTableName);
    }
}
