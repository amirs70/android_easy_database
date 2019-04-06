package ir.itroid.easydatabase;

public class DatabaseTableQuery {

    private StringBuilder query = new StringBuilder();
    private String table;

    public DatabaseTableQuery(String table) {
        this.table = table;
    }

    public DatabaseTableQuery createTableIfNotExists() {
        query.append("CREATE TABLE IF NOT EXISTS ")
                .append("`")
                .append(table)
                .append("`")
                .append(" (`id` ")
                .append(TABLE_TYPES.INTEGER)
                .append(" PRIMARY KEY AUTOINCREMENT NOT NULL");
        return this;
    }

    public DatabaseTableQuery addColumn(String name, TABLE_TYPES type, Boolean canNull, Object defaultValue) {

        String defaultV = "";
        boolean isNullDefault = String.valueOf(defaultValue).toLowerCase().equals("null");
        if (!(defaultValue == null || (isNullDefault && !canNull))) {
            defaultV = " DEFAULT " + (isNullDefault ? "null" : defaultValue);
        }

        query.append(", `")
                .append(name)
                .append("` ")
                .append(type)
                .append(canNull ? " NULL " : " NOT NULL")
                .append(defaultV);
        return this;
    }

    private String sameModifyColumnQuery(String name, TABLE_TYPES type, String after, Boolean canNull, Object defaultValue) {
        String defaultV = "";
        if (defaultValue != null) {
            boolean isNullDefault = String.valueOf(defaultValue).toLowerCase().equals("null");
            defaultV = " DEFAULT " + (isNullDefault ? "null" :
                    (defaultValue instanceof String ? "\"" + defaultValue + "\"" : defaultValue));
        }
        return "`" + name + "` " + type + (canNull != null && canNull ? " NULL" : " NOT NULL") +
                (after != null ? " AFTER `" + after + "`" : "") +
                defaultV;
    }

    public String createColumn(String name, TABLE_TYPES type, String after, Boolean canNull, Object defaultValue) {
        return "ALTER TABLE `" + table + "` ADD " + sameModifyColumnQuery(name, type, after, canNull, defaultValue);
    }

    public String getQuery() {
        return query.append(")").toString();
    }

}
