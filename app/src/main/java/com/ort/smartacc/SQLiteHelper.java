package com.ort.smartacc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class SQLiteHelper extends SQLiteOpenHelper{

    /**
     * Nombre de la base de datos
     */
    public static String DB_NAME = "smartacc";
    /**
     * Nombres de las tablas
     */
    public static String[] TABLES = {"recetas", "tags", "ingredientes", "tagrec", "ingrec","lugares"};
    /**
     * Strings para crear las tablas
     * Los valores son int, excepto:Texto, Imagen, Nombre, Unidad (son String) y Cantidad (es float)
     */
    static String[] CREATE_TABLES ={"CREATE TABLE IF NOT EXISTS `recetas` (" +
            "  `IDRecetas` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "  `Texto` varchar(2048) NOT NULL," +
            "  `Imagen` varchar(80) NOT NULL," +
            "  `Nombre` varchar(80) NOT NULL," +
            "  `TiempoPrep` tinyint(4) NOT NULL," +
            "  `Puntaje` tinyint(4) NOT NULL" +
            ");",
            "CREATE TABLE IF NOT EXISTS `tags` (" +
            "  `IDTag` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "  `Nombre` varchar(20) NOT NULL" +
            ");",
            "CREATE TABLE IF NOT EXISTS `ingredientes` (" +
            "  `IDIng` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "  `Nombre` varchar(40) NOT NULL" +
            ");",
            "CREATE TABLE IF NOT EXISTS `tagrec` (" +
            "  `IDReceta` INTEGER NOT NULL," +
            "  `IDTag` INTEGER NOT NULL," +
            "  `IDTagrec` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
            ");",
            "CREATE TABLE IF NOT EXISTS `ingrec` (" +
            "  `IDIng` INTEGER NOT NULL," +
            "  `IDRecetas` INTEGER NOT NULL," +
            "  `Cantidad` float NOT NULL," +
            "  `Unidad` varchar(30) NOT NULL," +
            "  `IDIngrec` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT" +
            ");",
            "CREATE TABLE IF NOT EXISTS `lugares` (" +
            "  `idLugar` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
            "  `lat` REAL NOT NULL," +
            "  `lon` REAL NOT NULL," +
            "  `name` varchar(20) NOT NULL," +
            "  `address` varchar(128) NOT NULL," +
            "  `description` varchar(512) NOT NULL" +
            ")"};
    /**
     * Context usado para distintas acciones.
     */
    Context context;
    public SQLiteHelper(Context context, int version) {
        super(context, DB_NAME, null, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String CREATE_TABLE : CREATE_TABLES) {
            //Creo cada tabla
            db.execSQL(CREATE_TABLE);
        }
        update(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i=0;i<TABLES.length;i++){
            //Elimino las viejas tablas y las recreo vacias
            db.execSQL("DROP TABLE IF EXISTS " + TABLES[i]);
            db.execSQL(CREATE_TABLES[i]);
        }
        update(db);
    }
    void update(final SQLiteDatabase db){
        //Actualizo cada tabla
        for(final String TABLE : TABLES) {
            //Mando un requerimiento para conseguir esa tabla
            new RequestTask(context, new RequestTask.OnReadyCallback() {
                @Override
                public void onReady(String response) {
                    try{
                        if (response != null) {
                            //Parseo la respuesta en JSON, formando un array de rows
                            JSONArray array = new JSONArray(response);

                            //Recorro el array
                            for (int i = 0; i < array.length(); i++) {
                                //Consigo cada row, la cual va a tener varios pares de nombre:valor
                                JSONObject objRecetas = array.getJSONObject(i);
                                //El ContentValues se usa para insertar las columnas
                                ContentValues values = new ContentValues();
                                //Iterator para recorrer las columnas de la row
                                Iterator<String> it = objRecetas.keys();

                                //Recorro todos los pares de nombre:valor
                                while (it.hasNext()) {
                                    String name = it.next();

                                    switch (name) {
                                        case "Texto":
                                        case "Imagen":
                                        case "Nombre":
                                        case "Unidad":
                                        case "name":
                                        case "address":
                                        case "description":
                                            values.put(name, objRecetas.getString(name));
                                            break;
                                        case "Cantidad":
                                        case "lat":
                                        case "lon":
                                            values.put(name, objRecetas.getDouble(name));
                                            break;
                                        default:
                                            values.put(name, objRecetas.getInt(name));
                                    }
                                }
                                db.insert(TABLE, null, values);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).execute(Util.SERVER_URL+"smartacc/json/" + TABLE + ".php");
        }
    }
    public static int getVersion(Context context){
        SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath(SQLiteHelper.DB_NAME).getPath(), null,
                SQLiteDatabase.OPEN_READONLY);
        int r = db.getVersion();
        db.close();
        return r;
    }
}
