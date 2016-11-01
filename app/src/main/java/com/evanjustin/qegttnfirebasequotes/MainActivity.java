package com.evanjustin.qegttnfirebasequotes;

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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawer;
    private FirebaseDatabase fbdb; // sorry.
    private DatabaseReference fbdbref;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ListView category_listview;
    private Quote[] quotes;
    private Quote last_quote;
    // ArrayLists to hold the quotes
    private ArrayList<Quote> all_quotes; // all the quotes in the firebase db
    private ArrayList<Quote> war_time_quotes; // quotes in the wartime category.
    private ArrayList<Quote> horror_quotes; // quotes in the horror category.


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init firebaseauth and get all quotes from database.
        firebaseauthenticator();

        //Sort the quotes into their respective categories.
        sort_all_quotes();

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

    }

    private void sort_all_quotes() {
        for(Quote q : all_quotes){
            switch(q.getCategory()){
                case "War Time":
                    war_time_quotes.add(q);
                    break;
                case "Horror":
                    horror_quotes.add(q);
                    break;
            }
        }
    }

    /**
     * Initilizes and authenticates the user to use the firebase realtime database.
     */
    private void firebaseauthenticator(){
        fbdb = FirebaseDatabase.getInstance();
        fbdbref = fbdb.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(getString(R.string.firebase_username),getString(R.string.firebase_password))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d("FB", "signed into firebase");
                            //get the json object from the database and return it as an arraylist of quotes.

                        }
                    }
                });
        readFirebaseDatabase(fbdbref);
    }

    /**
     * reads from the firebase database and returns an array of quote objects.
     * @param ref
     * @return
     */
    private void readFirebaseDatabase(DatabaseReference ref){
        all_quotes = new ArrayList<>(25);
        ref.child("quotes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Quote q = new Quote();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    q = ds.getValue(Quote.class);
                    Log.i("FB Quote", "adding quote to list: " + q.toString());
                    all_quotes.add(q);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        firebaseAuth.signOut();
    }

    @Override
    public void onPause(){
        super.onPause();
        firebaseAuth.signOut();
    }

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