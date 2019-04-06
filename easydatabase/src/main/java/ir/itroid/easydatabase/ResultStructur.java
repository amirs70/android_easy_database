package ir.itroid.easydatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultStructur {
    public boolean stat;
    public ArrayList<HashMap<String, Object>> results;
    public HashMap<String, Object> result;
    public int count;
    public double num_rows;
    public int pages;
    public long id;
    public String err = "";
    public String query;
}
