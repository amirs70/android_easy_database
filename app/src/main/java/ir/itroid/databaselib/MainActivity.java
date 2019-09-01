package ir.itroid.databaselib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Date;
import java.util.Random;

import ir.itroid.easydatabase.Database;
import ir.itroid.easydatabase.ResultStructur;

import static ir.itroid.databaselib.App.DBG;

public class MainActivity extends AppCompatActivity {

    long last_id;
    private String[] hash = new String[]{"kajsdnkasjdvnjksnv", "29q34e2n3jrfjfnkqj", "czxczdfmoinaq3wrw903fuej9v"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*findViewById(R.id.create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                ResultStructur resultStructur = new Database("task")
                        .addArgs("domain", "itroid.ir")
                        .addArgs("hash", hash[r.nextInt(2)])
                        .addArgs("ts", getTime())
                        .create();
                last_id = resultStructur.id;
                Log.e(DBG, "create: " + last_id);
            }
        });

        findViewById(R.id.read).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                ResultStructur resultStructur = new Database("task")
                        .addArgs("ts", getTime())
                        .update();
                Log.e(DBG, "getTime: " + getTime());
                Log.e(DBG, "update: " + resultStructur.stat);
            }
        });

        findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                ResultStructur resultStructur = new Database("task")
                        .delete();
                Log.e(DBG, "delete: " + resultStructur.stat);
            }
        });*/
        Random r = new Random();
        ResultStructur resultStructur = new Database("task")
                .addWhere("domain;=;itroid.ir", "hash;LIKE;adsd")
                .addWhere("hash;!=;few")
                .withQuery(true)
                .read();
        Log.e(DBG, "read: " + resultStructur.toString());
    }

    private long getTime() {
        return new Date().getTime() / 1000;
    }
}
