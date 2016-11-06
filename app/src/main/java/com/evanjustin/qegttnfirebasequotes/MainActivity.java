package com.evanjustin.qegttnfirebasequotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Random;

/**
 * Main Activity Class.
 * Created by Evan G, and Thanh Tung Nguyen.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    // private class variables.
    private DrawerLayout drawer;
    private FirebaseDatabase fbdb; // sorry.
    private DatabaseReference fbdbref;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser firebaseUser;
    private ListView category_listview;
    // ArrayLists to hold the quotes
    private ArrayList<Quote> all_quotes; // all the quotes in the firebase db
    private ArrayList<Quote> war_time_quotes; // quotes in the wartime category.
    private ArrayList<Quote> horror_quotes; // quotes in the horror category.
    private ArrayList<Quote> oregairu_quotes; // quotes from oregairu category.
    private ArrayList<Quote> shakespeare_quotes; //quotes from the shakespeare category.
    private ArrayList<Quote> philosophy_quotes; // quotes from the philosophy category.

    SharedPreferences prefs;
    SharedPreferences.Editor editor;


    /**
     * Initilizes the activity.
     * Calls the authentication for the user to use the Firebase Database.
     * Initilizes the ListView and adapter for the categories
     * sets listener for the ListView's onItemPressed --> calls generates random quote based on the
     * category selected and fires the QuoteActivty activity.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle("Categories");
        setContentView(R.layout.activity_main);
        all_quotes = AllQuotes.all_quotes;

        prefs = getSharedPreferences("Quotes",MODE_PRIVATE);
        editor = prefs.edit();


        //init firebaseauth and get all quotes from database.
        firebaseauthenticator();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // initialize the drawer
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        // Set up the adapter for the category list view.
        QuoteCategoryAdapter category_adapter = new QuoteCategoryAdapter(this, R.layout.category_listview, getResources().getStringArray(R.array.categories));
        category_listview = (ListView)findViewById(R.id.quote_cat_list);
        category_listview.setAdapter(category_adapter);

        /**
         * Handles the click events of the ListView.
         * Randomly generates(selects) a quote from the categories list view
         * and passes it to the QuoteActivity.
         */
        category_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                startQuoteActivity(index);
            }
        });

    }

    /**
     * Private helper method used to generate a random quote to send to the QuoteActivity
     * based on the position clicked in the list view.
     * @param i
     */
    private void startQuoteActivity(int i) {

        Random rnd = new Random();
        int rng;
        Quote q = new Quote();
        Intent quoteIntent = new Intent(MainActivity.this,QuoteActivity.class);
        switch(i){
            case 0:
                rng = rnd.nextInt(war_time_quotes.size());
                q = war_time_quotes.get(rng);
                break;
            case 1:
                rng = rnd.nextInt(horror_quotes.size());
                q = horror_quotes.get(rng);
                break;
            case 2:
                rng = rnd.nextInt(oregairu_quotes.size());
                q = oregairu_quotes.get(rng);
                break;
            case 3:
                rng = rnd.nextInt(shakespeare_quotes.size());
                q = shakespeare_quotes.get(rng);
                break;
            case 4:
                rng = rnd.nextInt(philosophy_quotes.size());
                q = philosophy_quotes.get(rng);
        }
        // Special thanks to Muhammad Aamir Ali from StackOverflow for showing me how to do this.
        Gson gson = new Gson(); // create Google Javascript notation object to save an object in shared prefs
        String json = gson.toJson(q);
        editor.putString("last_run", json);
        editor.commit();
        Log.d("QUOTES", "writing quote to Gson object...");

        quoteIntent.putExtra("quoteObject", q);
        startActivity(quoteIntent);

    }

    /**
     * Private helper method to sort all the provided quotes into their specific catetgories,
     * which are as class variables.
     * @param quotes contains all the quotes retrieved from the firebase database.
     */
    private void sort_all_quotes(ArrayList<Quote> quotes) {
        Log.d("Quotes", "sorting quotes...");
        Log.d("Quotes::sort", String.valueOf(quotes.size()));
        war_time_quotes = new ArrayList<>(5);
        horror_quotes = new ArrayList<>(5);
        oregairu_quotes = new ArrayList<>(5);
        shakespeare_quotes = new ArrayList<>(5);
        philosophy_quotes = new ArrayList<>(5);
        for(Quote q : quotes){
            if(q.getCategory().equals("War Time"))
                war_time_quotes.add(q);
            else if(q.getCategory().equals("Horror"))
                horror_quotes.add(q);
            else if(q.getCategory().equals("Oregairu"))
                oregairu_quotes.add(q);
            else if(q.getCategory().equals("Shakespeare"))
                shakespeare_quotes.add(q);
            else if(q.getCategory().equals("Philosophy"))
                philosophy_quotes.add(q);
            }
    }

    /**
     * Initilizes and authenticates the user to use the firebase realtime database.
     */
    private void firebaseauthenticator(){
        Log.d("Quotes", "Entered firebaseauthenticator");
        fbdb = FirebaseDatabase.getInstance();
        fbdbref = fbdb.getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseAuth.signInWithEmailAndPassword(getString(R.string.firebase_username),getString(R.string.firebase_password));
        readFirebaseDatabase(fbdbref);

    }

    /**
     * reads from the firebase database and stores all the quotes in the all_quotes variable.
     * calls the sort_all_quotes helper method to organizes the quotes based on categories
     * @param ref
     * @return
     */
    private void readFirebaseDatabase(DatabaseReference ref){
        final ArrayList<Quote> quotes = new ArrayList<>(25);
        ref.child("quotes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Quote q = new Quote();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    quotes.add(ds.getValue(Quote.class));
                    Log.d("Quotes", "added quote");
                    Log.d("Quotes", String.valueOf(quotes.size()));
                }
                all_quotes = quotes;
                AllQuotes.all_quotes = quotes;
                sort_all_quotes(all_quotes);

            }

            /**
             * incase of error, log the failure message.
             * @param databaseError
             */
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("FB CANCELLED", databaseError.getMessage());
            }
        });

    }

    /**
     * When the activity is killed by the user or the OS, logout the user from firebase.
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        firebaseAuth.signOut();
    }

    /**
     * Makes sure to close the navigation drawer on back press, if it is open. Otherwise, call super.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * creates the hamburger menu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quotes, menu);
        return true;
    }

    /**
     * handles the option menu selection
     * calls the appropriate helper method follow through with the action.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case(R.id.aboutMenu):
                onAboutMenu();
                return true;
            case(R.id.randomMenu):
                onRandomMenu();
                return true;
            case(R.id.lastRunMenu):
                onLastRunMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handles the navigation drawer navigation touches.
     * Calls the appropriate helper methods to do the actions.
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            // Handle the about action
            onAboutMenu();
        } else if (id == R.id.nav_last_run) {
            onLastRunMenu();

        } else if (id == R.id.nav_random) {
            onRandomMenu();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Handles the About menu item for nav drawer and hamburger menu
     * Stats the AboutActivity Activity.
     */
    private void onAboutMenu() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    /**
     * Handles the on random menu option.
     * Calls the startQuoteActivity and passes it a random index between 1 and 5 to be used as
     * random category. That method then fires the QuoteActivity.
     */
    private void onRandomMenu() {
        Random rnd = new Random();
        int index = rnd.nextInt(5);
        startQuoteActivity(index);
    }

    /**
     * reads quote from shared preferences and send it to the QuoteActivity
     */
    private void onLastRunMenu() {
        Gson gson = new Gson();
        if(prefs.contains("last_run")) {
            String json = prefs.getString("last_run", "");
            Quote q = gson.fromJson(json, Quote.class);
            Intent i = new Intent(MainActivity.this, QuoteActivity.class);
            i.putExtra("quoteObject", q);
            startActivity(i);
        }


    }

}
