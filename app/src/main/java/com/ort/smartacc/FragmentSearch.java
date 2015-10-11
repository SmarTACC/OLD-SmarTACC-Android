package com.ort.smartacc;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Usuario on 10/10/2015.
 */
public class FragmentSearch extends android.support.v4.app.Fragment /*implements android.widget.CompoundButton.OnCheckedChangeListener*/{

    ExpandableListView listView;
    ArrayList<Category> categoryList;
    CategoryAdapter categoryAdapter;

    View view;

    public FragmentSearch(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        listView = (ExpandableListView) view.findViewById(R.id.elvCategories);
        displayCategoryList();
        return view;
    }

    private void displayCategoryList(){
        categoryList = new ArrayList<Category>();

        //Hago un request al servidor para conseguir la version de la DB
        RequestTask versionTask= (RequestTask) new RequestTask().execute(SQLiteHelper.SERVER_URL + "version.php");

        try {
            String serverVersion = versionTask.get();
            SQLiteHelper helper;
            SQLiteDatabase db = null;
            //Si la version es null es porque hubo internet, uso el numero de version que ya tenia
            if(serverVersion!=null){
                helper = new SQLiteHelper(getActivity().getApplicationContext(), Integer.parseInt(serverVersion));
                //No encontre otra forma de actualizar que haciendo un get
                //Se va a actualizar si la version del server es mayor a la de la DB local
                db = helper.getReadableDatabase();
            }

            String[] col ={"Nombre"};
            Cursor c = null;
            if (db != null) {
                c = db.query(SQLiteHelper.TABLES[2],col,null,null,null,null,null);
            }
            while(!c.isLast()){
                c.moveToNext();
                for(int i =0; i<c.getColumnCount();i++){
                    categoryList.add(new Category("" + c.getString(i)));
                }
            }
            c.close();

            categoryAdapter = new CategoryAdapter(categoryList, getActivity().getApplicationContext());
            listView.setAdapter(categoryAdapter);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

   /* @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = listView.getPositionForView(buttonView);
        if (pos != ListView.INVALID_POSITION){
            Category c = categoryList.get(pos);
            c.setSelected(isChecked);
        }
    }*/
}
