package org.starloco.locos.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Result {
    public final Connection connection;
    public final Statement statement;
    public final ResultSet resultSet;

    Result(Connection connection, Statement statement, ResultSet resultSet) {
        this.connection = connection;
        this.statement = statement;
        this.resultSet = resultSet;
    }

    void close() throws SQLException {
        if(this.resultSet != null && !this.resultSet.isClosed())
            this.resultSet.close();
        if(this.statement != null && !this.statement.isClosed())
            this.statement.close();
        if(this.connection != null && !this.connection.isClosed())
            this.connection.close();
    }

    boolean isClosed() throws SQLException {
        return this.resultSet != null && this.resultSet.isClosed() && this.connection != null && this.connection.isClosed();
    }
}