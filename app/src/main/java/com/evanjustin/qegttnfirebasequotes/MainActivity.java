package com.evanjustin.qegttnfirebasequotes;

import android.content.Intent;
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

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private FirebaseDatabase fbdb; // sorry.
    private DatabaseReference fbdbref;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser firebaseUser;
    private ListView category_listview;
    private Quote[] quotes;
    private Quote last_quote;
    // ArrayLists to hold the quotes
    private ArrayList<Quote> all_quotes; // all the quotes in the firebase db
    private ArrayList<Quote> war_time_quotes; // quotes in the wartime category.
    private ArrayList<Quote> horror_quotes; // quotes in the horror category.
    private ArrayList<Quote> oregairu_quotes; // quotes from oregairu category.
    private ArrayList<Quote> shakespeare_quotes; //quotes from the shakespeare category.
    private ArrayList<Quote> philosophy_quotes; // quotes from the philosophy category.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init firebaseauth and get all quotes from database.
        firebaseauthenticator();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
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

                quoteIntent.putExtra("quoteObject", q);
                startActivity(quoteIntent);

            }
        });

    }

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

//        firebaseAuth.signInWithEmailAndPassword(getString(R.string.firebase_username),getString(R.string.firebase_password))
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if(task.isSuccessful()) {
//                            Log.d("FB", "signed into firebase");
//                            //get the json object from the database and return it as an arraylist of quotes.
//                            readFirebaseDatabase(fbdbref);
//                        }
//                        if(!task.isSuccessful()){
//                            Log.w("FB","Sign in with Email",task.getException());
//                        }
//                    }
//                });
//        firebaseAuth.signInWithEmailAndPassword(getString(R.string.firebase_username),getString(R.string.firebase_password));
        readFirebaseDatabase(fbdbref);

    }

    /**
     * reads from the firebase database and stores all the quotes in the all_quotes variable.
     * Organizes the quotes based on categories
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
                sort_all_quotes(all_quotes);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("FB CANCELLED", databaseError.getMessage());
            }
        });

    }

//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        firebaseAuth.signOut();
//    }
//
//    @Override
//    public void onPause(){
//        super.onPause();
//        firebaseAuth.signOut();
//    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quotes, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            // Handle the about action
        } else if (id == R.id.nav_last_run) {

        } else if (id == R.id.nav_random) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
