package com.ort.smartacc;


import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecetaFragment extends Fragment {

    boolean error404=false;
    public final static String TAG = "recetaFragmentTag";
    public final static String KEY_ID_RECETA="key_id_receta";
    SQLiteConnector connector;

    static Bitmap image;

    public RecetaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        connector = (SQLiteConnector) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        Cursor c = connector.doQuery(SQLiteHelper.TABLES[0], null, "IDRecetas=" + getArguments().getInt(KEY_ID_RECETA), null, null, null, null);

        if (c.getCount() != 0) {
            error404 = false;
            // Inflate the layout for this fragment
            view = inflater.inflate(R.layout.fragment_receta, container, false);
            c.moveToNext();
            ((TextView) view.findViewById(R.id.receta_titulo)).setText(c.getString(c.getColumnIndex("Nombre")));
            ((TextView) view.findViewById(R.id.receta_texto_preparacion)).setText(c.getString(c.getColumnIndex("Texto")));
            //Setear la imagen
            if(image==null) {
                new LoadImageTask(getActivity(), (ImageView) view.findViewById(R.id.receta_foto), new LoadImageTask.OnReadyCallback() {
                    @Override
                    public void saveBitmap(Bitmap bitmap) {
                        image=bitmap;
                    }
                }).execute(Util.SERVER_URL + "celiaquia/" + c.getString(c.getColumnIndex("Imagen")));

            } else{
                ((ImageView)view.findViewById(R.id.receta_foto)).setImageBitmap(image);
            }
            //Poner los ingredientes
            Cursor ingredientes = connector.doRawQuery("SELECT ingredientes.IDIng, ingredientes.Nombre FROM ingredientes INNER JOIN ingrec ON ingredientes.IDIng=ingrec.IDIng WHERE ingrec.IDRecetas = " + getArguments().getInt(KEY_ID_RECETA) + " ORDER BY ingredientes.Nombre;", null);
            LinearLayout listaIngredientes = (LinearLayout) view.findViewById(R.id.receta_list_ingredientes);
            if(ingredientes.getCount()>0) {
                while (!ingredientes.isLast()) {
                    ingredientes.moveToNext();
                    Cursor datosIngredientes = connector.doQuery(SQLiteHelper.TABLES[4], null, "IDRecetas=ingrec.IDRecetas AND IDIng = ?", new String[]{ingredientes.getString(ingredientes.getColumnIndex("IDIng"))}, null, null, null);
                    datosIngredientes.moveToNext();
                    if (datosIngredientes.getInt(datosIngredientes.getColumnIndex("Cantidad")) == 0) {
                        TextView textView = new TextView(getActivity());
                        textView.setText(
                                String.format(
                                        getResources().getString(R.string.formato_ingrediente_sin_cantidad),
                                        datosIngredientes.getString(datosIngredientes.getColumnIndex("Unidad")),
                                        ingredientes.getString(ingredientes.getColumnIndex("Nombre"))));
                        listaIngredientes.addView(textView);
                    } else {
                        TextView textView = new TextView(getActivity());
                        textView.setText(
                                String.format(
                                        getResources().getString(R.string.formato_ingrediente_con_cantidad),
                                        datosIngredientes.getFloat(datosIngredientes.getColumnIndex("Cantidad")),
                                        datosIngredientes.getString(datosIngredientes.getColumnIndex("Unidad")),
                                        ingredientes.getString(ingredientes.getColumnIndex("Nombre"))));
                        listaIngredientes.addView(textView);
                    }
                }
            }
            //Poner los tags
            Cursor tags = connector.doRawQuery("SELECT * FROM tags INNER JOIN tagrec ON tags.IDTag=tagrec.IDTag WHERE tagrec.IDReceta = " + getArguments().getInt(KEY_ID_RECETA) + " ORDER BY tags.Nombre;", null);
            LinearLayout listaTags = (LinearLayout) view.findViewById(R.id.receta_list_categorias);
            if(tags.getCount()>0) {
                while (!tags.isLast()) {
                    tags.moveToNext();
                    TextView textView = new TextView(getActivity());
                    textView.setText(tags.getString(tags.getColumnIndex("Nombre")));
                    listaTags.addView(textView);
                }
            }
        } else {
            error404 = true;
            view = inflater.inflate(R.layout.error404, container, false);
        }
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
