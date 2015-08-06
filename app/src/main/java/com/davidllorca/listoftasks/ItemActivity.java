package com.davidllorca.listoftasks;

import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class ItemActivity extends ActionBarActivity {

    // Views
    TextView item;
    TextView place;
    TextView description;
    TextView importance;
    //Id entry
    Integer rowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get extras, id and requestcode
        Bundle extras = getIntent().getExtras();
        rowId = (savedInstanceState == null) ? null : (Integer) savedInstanceState.getSerializable(DataBaseHelper.SL_ID);
        if (rowId == null) {
            rowId = extras != null ? extras.getInt(DataBaseHelper.SL_ID) : null;
        }

        if (extras != null && extras.getInt("action") == ItemListFragment.SHOW_ITEM) {
            setContentView(R.layout.detail_item);
        } else {
            setContentView(R.layout.activity_item);

            Button btnSave = (Button) findViewById(R.id.add);
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // onActivityForResult in ItemListFragment
                    setResult(RESULT_OK);
                    saveData();
                    finish();
                }
            });
        }

        // Views
        item = (TextView) findViewById(R.id.item);
        place = (TextView) findViewById(R.id.place);
        description = (TextView) findViewById(R.id.description);
        importance = (TextView) findViewById(R.id.importance);
        TableRow tr = (TableRow) findViewById(R.id.idRow);
        if (rowId != null) {
            tr.setVisibility(View.VISIBLE);
            populateFieldsFromDB();
        } else {
            tr.setVisibility(View.GONE);
        }

    }

    /**
     * Get data from task and show them in each tag.
     */
    private void populateFieldsFromDB() {
        ItemListFragment.mDbHelper.open();
        Cursor c = ItemListFragment.mDbHelper.getItem(rowId.intValue());
        if (c.moveToFirst()) {
            // If column doesn't exist throw exception
            item.setText(c.getString(c.getColumnIndexOrThrow(DataBaseHelper.SL_ITEM)));
            // Get by column name
            place.setText(c.getString(c.getColumnIndex(DataBaseHelper.SL_PLACE)));
            // Get by column number
            description.setText(c.getString(2));
            importance.setText(Integer.toString(c.getInt(3)));
            TextView id = (TextView) findViewById(R.id.identificator);
            id.setText(Integer.toString(c.getInt(4)));
        }
        c.close();
        ItemListFragment.mDbHelper.close();
    }

    /**
     * Insert a new task in database.
     */
    protected void saveData() {
        // Get data.
        String itemText = item.getText().toString();
        String placeText = place.getText().toString();
        String descriptionText = description.getText().toString();
        String importanceText = importance.getText().toString();

        //Insert data
        // Open database
        ItemListFragment.mDbHelper.open();
        if (rowId == null) {
            // Insert
            ItemListFragment.mDbHelper.insertItem(itemText, placeText, descriptionText, Integer.parseInt(importanceText));
        } else {
            // Update
            TextView tv = (TextView) findViewById(R.id.identificator);
            String ident = tv.getText().toString();
            ItemListFragment.mDbHelper.updateItem(Integer.parseInt(ident), itemText, placeText, descriptionText, Integer.parseInt(importanceText));
        }
        // Close connection
        ItemListFragment.mDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item, menu);
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

        return super.onOptionsItemSelected(item);
    }

    /**
     * Show toast message.
     *
     * @param message, string id from resources.
     */
    private void showMessage(int message) {
        Toast.makeText(this, getResources().getString(message), Toast.LENGTH_SHORT).show();
    }
}
