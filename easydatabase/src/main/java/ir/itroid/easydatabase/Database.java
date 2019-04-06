package ir.itroid.easydatabase;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    protected String table;
    protected int perPage = 30;
    protected int offset = 0;
    protected String sort = "DESC";
    protected ContentValues args = new ContentValues();
    protected String orderBy = "`id`";
    protected String query = null;
    protected ArrayList<ArrayList<WhereStructur>> where = new ArrayList<>();
    protected boolean withQuery;

    public Database(String table) {
        this.table = table;
    }

    public Database setPerPage(int perPage) {
        this.perPage = perPage;
        return this;
    }

    public Database withQuery(boolean withQuery) {
        this.withQuery = withQuery;
        return this;
    }

    public Database setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public Database setSort(String sort) {
        this.sort = sort;
        return this;
    }

    public Database setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public Database setQuery(String query) {
        this.query = query;
        return this;
    }

    public Database addArgs(String key, Long value) {
        this.args.put(key, value);
        return this;
    }

    public Database addArgs(String key, int value) {
        this.args.put(key, value);
        return this;
    }

    public Database addArgs(String key, Float value) {
        this.args.put(key, value);
        return this;
    }

    public Database addArgs(String key, String value) {
        this.args.put(key, value);
        return this;
    }

    public Database addArgs(String key, Double value) {
        this.args.put(key, value);
        return this;
    }

    public Database addArgs(String key, boolean value) {
        this.args.put(key, value);
        return this;
    }

    private boolean isInt(String value) {
        try {
            int i = Integer.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isLong(String value) {
        try {
            long i = Long.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isDouble(String value) {
        try {
            double i = Double.valueOf(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isBoolean(String value) {
        String lower = value.toLowerCase();
        return lower.equals("true") || lower.equals("false");
    }

    private boolean isNull(String value) {
        String lower = value.toLowerCase();
        return lower.equals("null");
    }

    private boolean isStringString(String value) {
        return !isDouble(value) && !isLong(value) && !isBoolean(value) && !isInt(value) && !isNull(value);
    }

    public Database addWhere(String... wheres) {
        int ln = wheres.length;
        ArrayList<WhereStructur> where = new ArrayList<>();
        for (String w : wheres) {
            String[] mWhereSplit = w.split(";");
            WhereStructur whereStructur = new WhereStructur();
            whereStructur.key = mWhereSplit[0];
            whereStructur.value = mWhereSplit[mWhereSplit.length - 1];
            w = w
                    .replace(whereStructur.key + ";", "")
                    .replace(";" + whereStructur.value, "");
            whereStructur.action = w;
            where.add(whereStructur);
        }
        this.where.add(where);
        return this;
    }

    private String getTrueValue(String value) {
        if (isStringString(value)) {
            value = "\"" + value + "\"";
        }
        return value;
    }

    private String parseWhere(WhereStructur whereStructur) {
        return "`" + whereStructur.key + "` " + whereStructur.action + " " + getTrueValue(whereStructur.value);
    }

    protected String getWhere() {
        StringBuilder out_where = new StringBuilder();
        int andWhereSize = where.size();
        boolean isOutFirst = true;
        boolean isInnerFirst = true;
        for (ArrayList<WhereStructur> andWhere : where) {
            if (isOutFirst) {
                isOutFirst = false;
            } else {
                out_where.append(" AND ");
            }
            out_where.append("(");
            for (WhereStructur orWhere : andWhere) {
                if (isInnerFirst) {
                    isInnerFirst = false;
                } else {
                    out_where.append(" OR ");
                }
                out_where.append(parseWhere(orWhere));
            }
            out_where.append(")");
        }
        return out_where.toString();
    }

    private String getAdditional(boolean whitAdditional) {
        if (!whitAdditional) return "";
        return " ORDER BY " + this.orderBy + " " + this.sort + " "
                + (this.perPage == -1 ? "" : "LIMIT " + this.perPage + " OFFSET " + this.offset);
    }

    protected String getSelectQuery(boolean whitAdditional) {
        String w = getWhere();
        return "SELECT * FROM `" + table +
                (w.equals("") ? "" : "` WHERE " + getWhere()) +
                getAdditional(whitAdditional);
    }

    protected double getCountOfAll() {
        Cursor cursor;
        cursor = DatabaseCore.DB.rawQuery(getSelectQuery(false), null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    protected HashMap<String, Object> extract(Cursor cursor) {
        HashMap<String, Object> result = new HashMap<>();
        String[] cols = cursor.getColumnNames();
        int colsSize = cols.length;
        for (String currentCol : cols) {
            int index = cursor.getColumnIndex(currentCol);
            switch (cursor.getType(index)) {
                case Cursor.FIELD_TYPE_NULL:
                    result.put(currentCol, null);
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    result.put(currentCol, cursor.getInt(index));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    result.put(currentCol, cursor.getFloat(index));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    result.put(currentCol, cursor.getString(index));
                    break;
                case Cursor.FIELD_TYPE_BLOB:
                    result.put(currentCol, cursor.getBlob(index));
                    break;
            }
        }
        return result;
    }

    public ResultStructur create() {
        ResultStructur resultStructur = new ResultStructur();
        resultStructur.id = DatabaseCore.DB.insert(table, null, this.args);
        resultStructur.stat = resultStructur.id > 0;
        return resultStructur;
    }

    public ResultStructur read() {

        String query = getSelectQuery(true);

        int count = 0;
        ResultStructur resultStructur = new ResultStructur();
        ArrayList<HashMap<String, Object>> result = new ArrayList<>();

        try (Cursor cursor = DatabaseCore.DB.rawQuery(query, null)) {
            while (cursor.moveToNext()) {
                result.add(extract(cursor));
            }
            count = cursor.getCount();
        } catch (SQLiteException e) {
            resultStructur.err = e.getMessage();
        }

        resultStructur.count = count;
        resultStructur.stat = resultStructur.count > 0;
        resultStructur.num_rows = getCountOfAll();
        resultStructur.pages = (int) Math.ceil(getCountOfAll() / resultStructur.count);
        resultStructur.results = resultStructur.stat ? result : null;
        if (withQuery) resultStructur.query = query;

        return resultStructur;
    }

    public ResultStructur one() {
        setPerPage(-1);
        ResultStructur structur = read();
        if (structur.stat) {
            structur.pages = 1;
            structur.num_rows = 1;
            structur.result = structur.results.get(0);
            structur.results = null;
        }
        return structur;
    }

    public ResultStructur update() {
        ResultStructur resultStructur = new ResultStructur();
        resultStructur.stat = DatabaseCore.DB.update(table, this.args, getWhere(), null) > 0;
        return resultStructur;
    }

    public ResultStructur delete() {
        ResultStructur resultStructur = new ResultStructur();
        resultStructur.stat = DatabaseCore.DB.delete(table, getWhere(), null) > 0;
        return resultStructur;
    }

    private class WhereStructur {
        public String key, action, value;
    }

}
