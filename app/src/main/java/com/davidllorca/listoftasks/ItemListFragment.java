package com.davidllorca.listoftasks;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by David Llorca <davidllorcabaron@gmail.com> on 7/14/14.
 */
public class ItemListFragment extends Fragment {

    // Actions
    public static final int NEW_ITEM = 1;
    public static final int EDIT_ITEM = 2;
    public static final int SHOW_ITEM = 3;

    // Views
    private TextView titleTv;
    private ListView list;
    private TaskAdapter adapter;

    // Element selected
    private int lastRowSelected = 0;
    public static DataBaseHelper mDbHelper = null;



    public ItemListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Show menu
        setHasOptionsMenu(true);

        View rootView = inflater.inflate(R.layout.fragment_item_list, container, false);
        // Reference ListView
        titleTv = (TextView) rootView.findViewById(R.id.title);
        list = (ListView) rootView.findViewById(R.id.list);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Open database
        mDbHelper = new DataBaseHelper(getActivity());
        fillData();

        registerForContextMenu(list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(),ItemActivity.class);
                int rowId = (Integer) getView().findViewById(R.id.row_importance).getTag();
                intent.putExtra(DataBaseHelper.SL_ID, rowId);
                intent.putExtra("action", SHOW_ITEM);
                startActivityForResult(intent, SHOW_ITEM);
            }
        });
    }

    private void fillData() {
        // Open database
        mDbHelper.open();
        // Get all entries of table
        Cursor itemCursor = mDbHelper.getItems();
        ListEntry item = null;
        ArrayList<ListEntry> resultList = new ArrayList<ListEntry>();
        // Process result
        while (itemCursor.moveToNext()) {
            int id = itemCursor.getInt(itemCursor.getColumnIndex(DataBaseHelper.SL_ID));
            String task = itemCursor.getString(itemCursor.getColumnIndex(DataBaseHelper.SL_ITEM));
            String place = itemCursor.getString(itemCursor.getColumnIndex(DataBaseHelper.SL_PLACE));
            String description = itemCursor.getString(itemCursor.getColumnIndex(DataBaseHelper.SL_DESCRIPTION));
            int importance = itemCursor.getInt(itemCursor.getColumnIndex(DataBaseHelper.SL_IMPORTANCE));
            // Create a new object
            item = new ListEntry();
            item.setId(id);
            item.setTask(task);
            item.setPlace(place);
            item.setDescription(description);
            item.setImportance(importance);
            // Add new objects in List
            resultList.add(item);
        }
        // Close database
        itemCursor.close();
        mDbHelper.close();

        // Create Adapter
        adapter = new TaskAdapter(getActivity(), R.layout.row_list, resultList, getActivity().getLayoutInflater());
        list.setAdapter(adapter);
    }

    private void deleteEntry() {
        mDbHelper.open();
        mDbHelper.deleteItem(((ListEntry) list.getAdapter().getItem(lastRowSelected)).getId());
        mDbHelper.close();
        // Refresh data
        fillData();
    }

    /**
     * Show toast message.
     *
     * @param message, string id from resources.
     */
    private void showMessage(int message) {
        Toast.makeText(getActivity(), getResources().getString(message), Toast.LENGTH_SHORT).show();
    }

    /*
        MENU OVERFLOW
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_item_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.new_item:
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                startActivityForResult(intent, NEW_ITEM);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
        CONTEXT MENU -> longclick on item list.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo delW = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        lastRowSelected = delW.position;

        switch (item.getItemId()){
            case R.id.delete_item:
                new AlertDialog.Builder(getActivity()).setTitle(getActivity().getString(R.string.deleteItem)).setMessage(R.string.confirmDeleteItem).setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEntry();
                    }
                }).setNegativeButton(android.R.string.cancel, null).show();
                return true;
            case R.id.edit_item:
                Intent intent = new Intent(getActivity(), ItemActivity.class);
                intent.putExtra(DataBaseHelper.SL_ID, ((ListEntry) list.getAdapter().getItem(lastRowSelected)).getId());
                startActivityForResult(intent, EDIT_ITEM);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == EDIT_ITEM || requestCode == NEW_ITEM) {
            if (resultCode == Activity.RESULT_OK) {
                fillData();
            }
        }
    }

    /**
     * Model of List adapter.
     */
    private static class TaskAdapter extends ArrayAdapter<ListEntry> {

        // Variables
        private LayoutInflater mInflater;
        private List<ListEntry> mObjects;

        // Constructor
        public TaskAdapter(Context context, int resource, List<ListEntry> mObjects, LayoutInflater mInflater) {
            super(context, resource, mObjects);
            this.mInflater = mInflater;
            this.mObjects = mObjects;
        }

        static class ViewHolder{
            TextView place;
            TextView task;
            ImageView importance;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            // Create holder
            if(row == null){
                // Inflate view item list
                row = mInflater.inflate(R.layout.row_list, null);
                ViewHolder holder = new ViewHolder();
                holder.place = (TextView) row.findViewById(R.id.row_place);
                holder.task = (TextView) row.findViewById(R.id.row_item);
                holder.importance = (ImageView) row.findViewById(R.id.row_importance);
                row.setTag(holder);
            }
            // Fill data
            ListEntry listEntry = mObjects.get(position);
            ViewHolder holder = (ViewHolder)row.getTag();

            holder.place.setText(listEntry.getPlace());
            holder.task.setText(listEntry.getTask());
            holder.importance.setTag(new Integer(listEntry.getId()));
                switch (listEntry.getImportance()) {
                    case 1:
                        holder.importance.setImageResource(R.mipmap.ic_green);
                        break;
                    case 2:
                        holder.importance.setImageResource(R.mipmap.ic_yellow);
                        break;
                    default:
                        holder.importance.setImageResource(R.mipmap.ic_red);
                        break;
                }
            return row;
        }
    }
}
