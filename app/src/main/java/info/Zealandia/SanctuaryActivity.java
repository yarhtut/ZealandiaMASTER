package info.Zealandia;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import info.Zealandia.adapter.BirdAdapter;
import info.Zealandia.app.AppController;
import info.Zealandia.app.CacheHelper;

import info.Zealandia.dbhelper.SQLiteHandler;
import info.Zealandia.dbhelper.SessionManager;
import info.Zealandia.model.SanctuaryView;
import info.Zealandia.R;
/**
 * Created by 21104216 on 2/04/2015.
 */
public class SanctuaryActivity extends ActionBarActivity {
  // private static final String TAG = MainActivity.class.getSimpleName();
  //  private static final String url = "http://yar.cloudns.org/SlimApi/api/list/all?mobile=1";
    private ProgressDialog pDialog;
    private List<SanctuaryView> birdList = new ArrayList<SanctuaryView>();
    private ListView listView;
    private BirdAdapter adapter;


    private SQLiteHandler db;
   // private ActivitySQLiteHandler activityDb;
    private SessionManager session;

    public Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sanctuary);

        toolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // School Activity Database SqLite database handler
       // activityDb = new ActivitySQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        listView = (ListView) findViewById(R.id.list);
        birdList = CacheHelper.getInstance().updateTabFromJSON("active");

        adapter = new BirdAdapter(this, birdList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);

        // Showing progress dialog before making http request
        //pDialog.setMessage("Loading...");
        //  pDialog.show();

        adapter.notifyDataSetChanged();



         listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


        @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


            TextView catId = (TextView) view.findViewById(R.id.textViewID);
            int _catId = Integer.parseInt(catId.getText().toString());



            // Add it to the DB and re-draw the ListView
            db.insectCategoriesId(_catId);
            String CLICKED = db.getUpdateClicked(_catId);
           Toast.makeText(SanctuaryActivity.this,CLICKED, Toast.LENGTH_SHORT).show();
        }
        });

        }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
      //  if(id == android.R.id.home){
       //     NavUtils.navigateUpFromSameTask(this);
       // }

        return super.onOptionsItemSelected(item);
    }
    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(SanctuaryActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
