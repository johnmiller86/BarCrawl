package com.example.crawlapp.barcrawl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class LoadCrawlActivity extends AppCompatActivity {

    // UI Components
    private ListView listView;
    private ArrayList<String> crawls;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_crawl);

        // Linking UI Components
        listView = (ListView) findViewById(R.id.listView2);
        crawls = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, crawls);
        listView.setAdapter(arrayAdapter);
        registerForContextMenu(listView);
        fillListView();
    }

    // Context Menu -- to select crawl from the ListView
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.context_menu_load, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){

            // Remove was clicked
            case R.id.select:

                // MapsActivity Intent
                Intent intent = new Intent(this, MapsActivity.class);
                intent.putExtra("CRAWL", crawls.get(info.position));

                // Launching MapsActivity
                startActivity(intent);
                return true;

            default:
                // Do nothing user cancelled
                return super.onContextItemSelected(item);
        }
    }

    private void fillListView(){

        CrawlRepo crawlRepo = new CrawlRepo();
        UserRepo userRepo = new UserRepo();

        int userId = userRepo.getUserId(LoginActivity.user.getUsername());

        // Ensuring userId was retrieved successfully
        if (userId == -1){
            Toast.makeText(this, "An internal error occurred :(", Toast.LENGTH_SHORT).show();
        }
        else {
            // Setting userId
            LoginActivity.user.setUserId(userId);

            ArrayList<String> tempList = crawlRepo.getCrawlList(LoginActivity.user.getUserId());

            for (String string : tempList){
                crawls.add(string);
            }

            // Updating ListView
            arrayAdapter.notifyDataSetChanged();
        }

        if (crawls.size() == 0){
            Toast.makeText(this, "You have not yet created any crawls!!", Toast.LENGTH_SHORT).show();
        }
    }
}
