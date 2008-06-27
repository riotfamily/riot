package org.riotfamily.revolt.dialect;

import org.riotfamily.revolt.Script;
import org.riotfamily.revolt.definition.Index;
import org.riotfamily.revolt.support.TypeMap;

public class OracleDialect extends Sql92Dialect {

    protected void registerTypes() {
        registerType(TypeMap.BIT, "NUMBER(1,0)");
        registerType(TypeMap.TINYINT, "NUMBER(3,0)");
        registerType(TypeMap.SMALLINT, "NUMBER(5,0)");
        registerType(TypeMap.INTEGER, "NUMBER(10,0)");
        registerType(TypeMap.BIGINT, "NUMBER(19,0)");
        registerType(TypeMap.FLOAT, "FLOAT");
        registerType(TypeMap.REAL, "REAL");
        registerType(TypeMap.DOUBLE, "DOUBLE PRECISION");
        registerType(TypeMap.NUMERIC, "NUMERIC");
        registerType(TypeMap.DECIMAL, "NUMERIC");
        registerType(TypeMap.CHAR, "CHAR", true);
        registerType(TypeMap.VARCHAR, "VARCHAR", true);
        registerType(TypeMap.LONGVARCHAR, "CLOB");
        registerType(TypeMap.DATE, "DATE");
        registerType(TypeMap.TIME, "TIME");
        registerType(TypeMap.TIMESTAMP, "TIMESTAMP");
        registerType(TypeMap.BINARY, "RAW", true);
        registerType(TypeMap.VARBINARY, "RAW", true);
        registerType(TypeMap.LONGVARBINARY, "LONG RAW", true);
        registerType(TypeMap.BLOB, "BLOB");
        registerType(TypeMap.CLOB, "CLOB");
    }

    public Script createAutoIncrementSequence(String name) {
        return new Script("CREATE SEQUENCE").append(name);
    }

    public Script createIndex(String table, Index index) {
        Script sql = new Script("CREATE INDEX").append(index.getName())
                .append("ON").append(quote(table));

        addColumnNames(sql, index.getColumns());
        return sql;
    }

    public boolean supports(String databaseProductName, int majorVersion,
            int minorVersion) {
        
        return "Oracle".equals(databaseProductName) && majorVersion >= 10;
    }

    public Script renameTable(String name, String renameTo) {
        return alterTable(name).append("RENAME TO").append(quote(renameTo));
    }
}
