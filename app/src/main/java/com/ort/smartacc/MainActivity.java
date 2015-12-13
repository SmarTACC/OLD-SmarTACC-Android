package com.ort.smartacc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                    SQLiteConnector{

    NavigationDrawerFragment mNavigationDrawerFragment;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /**
     * Instancia en modo solo lectura de la base de datos.
     * Hay que verificar que no sea null!
     * Un ejemplo básico de una consulta (SELECT Cantidad FROM tagrec)
         PARA SQL PURO USAR .rawQuery

         String[] col ={"Cantidad"};
         Cursor c = dataBase.query(SQLiteHelper.TABLES[4],col,null,null,null,null,null);
         while(!c.isLast()){
         c.moveToNext();
         for(int i =0; i<c.getColumnCount();i++){
         TextView tv = new TextView(this);
         tv.setText(""+c.getFloat(i));
         layout.addView(tv);
         }
         }
         c.close();
     */
    SQLiteDatabase dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.ort.smartacc.R.layout.activity_drawer);

        /*
          Fragment managing the behaviors, interactions and presentation of the navigation drawer.
         */
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                com.ort.smartacc.R.id.navigation_drawer,
                (DrawerLayout) findViewById(com.ort.smartacc.R.id.drawer_layout));

        if(savedInstanceState==null) {
            //Hago un request al servidor para conseguir la version de la DB
            new RequestTask(this, new RequestTask.OnReadyCallback() {
                @Override
                public void onReady(String response) {
                    dataBase = new SQLiteHelper(MainActivity.this,
                            (response != null) ?
                                    Integer.parseInt(response) :
                                    SQLiteHelper.getVersion(MainActivity.this)).getReadableDatabase();
                    Toast.makeText(MainActivity.this, "Database ready", Toast.LENGTH_SHORT).show();
                    /*
                    //Para cargar el fragment del mapa, hacerlo asi:
                    SupportMapFragment mapFragment = new SupportMapFragment();
                    getSupportFragmentManager().beginTransaction().add(R.id.container,mapFragment).commit();
                    mapFragment.getMapAsync(new MapCallback(MainActivity.this));
                    */
                    RecetaFragment recetas = new RecetaFragment();
                    Bundle bundle = new Bundle();
                    bundle.putInt(RecetaFragment.KEY_ID_RECETA, 131);
                    recetas.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.container, recetas, RecetaFragment.TAG).addToBackStack(RecetaFragment.TAG).commit();
                }
            }).execute(Util.SERVER_URL + "smartacc/json/version.php");
        } else{
            dataBase = new SQLiteHelper(MainActivity.this,SQLiteHelper.getVersion(MainActivity.this)).getReadableDatabase();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //TODO: comente esto porque por alguna razon, cuando giras 2 veces el celular provoca que el container se cambie
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(com.ort.smartacc.R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();*/
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(com.ort.smartacc.R.string.title_section1);
                break;
            case 2:
                mTitle = getString(com.ort.smartacc.R.string.title_section2);
                break;
            case 3:
                mTitle = getString(com.ort.smartacc.R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(com.ort.smartacc.R.menu.drawer, menu);
            //restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.ort.smartacc.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Cursor doRawQuery(String sql, String[] args) {
        return dataBase.rawQuery(sql, args);
    }
    @Override
    public Cursor doQuery(String table, String[] columns, String selection,
                          String[] selectionArgs, String groupBy, String having,
                          String orderBy) {
        return dataBase.query(table, columns,selection,selectionArgs,groupBy,having,orderBy);
    }

}
