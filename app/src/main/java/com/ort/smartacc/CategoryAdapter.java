package com.ort.smartacc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Usuario on 10/10/2015.
 */
class Category{
    String name;
    boolean selected = false;

    public Category(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}

public class CategoryAdapter extends BaseExpandableListAdapter {

    private List<Category> categoryList;
    private Context context;

    public CategoryAdapter(List<Category> categoryList, Context context) {
        this.categoryList = categoryList;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return categoryList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return 1;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return categoryList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_parent, parent, false);
        }
        TextView parentTextView = (TextView) convertView.findViewById(R.id.parentName);
        parentTextView.setText("Categories");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Category child = (Category) getChild(groupPosition, childPosition);

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_single, parent, false);
        }
        TextView childTextView = (TextView) convertView.findViewById(R.id.childName);
        childTextView.setText(child.getName());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class CategoryHolder{
        public TextView categoryName;
        public CheckBox chkBox;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        CategoryHolder holder = new CategoryHolder();

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_single, null);

            holder.categoryName = (TextView) v.findViewById(R.id.childName);
            holder.chkBox = (CheckBox) v.findViewById(R.id.chk_box);

            //holder.chkBox.setOnCheckedChangeListener(FragmentSearch);
        }else{
            holder = (CategoryHolder) v.getTag();
        }

        Category c = categoryList.get(position);
        holder.categoryName.setText(c.getName());
        holder.chkBox.setTag(c);

        return v;
    }
}
