package com.evanjustin.qegttnfirebasequotes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.Random;

/**
 * Created by Thanh Tung Nguyen, Evan Glicakis
 */
public class QuoteActivity extends AppCompatActivity {

    private final static String TAG = "QuoteActivity";

    private Quote quoteInfo;
    TextView attributed;
    TextView ref;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        prefs = getSharedPreferences("Quotes",MODE_PRIVATE);

        // get intent extras and place it into quoteInfo field
        Intent i = getIntent();
        quoteInfo = (Quote)i.getParcelableExtra("quoteObject");
        // populate text views with whats in quoteInfo object
        fillViews();

        attributed = (TextView)findViewById(R.id.attributedName);
        attributed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNameClick();
            }
        });

        ref = (TextView)findViewById(R.id.refLink);
        ref.setText(quoteInfo.getReference());
        ref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefLink();
            }
        });



    }

    private void fillViews() {

        TextView categoryTV = (TextView)findViewById(R.id.categoryText);
        TextView quoteTV = (TextView)findViewById(R.id.quoteText);
        TextView attributedTV = (TextView)findViewById(R.id.attributedName);
        TextView dateTV = (TextView)findViewById(R.id.dateAdded);

        categoryTV.setText(quoteInfo.getCategory());
        quoteTV.setText("\"" + quoteInfo.getQuote() + "\"");
        attributedTV.setText(quoteInfo.getAttributed());
        dateTV.setText(quoteInfo.getDate());
    }

    public void onRefLink() {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(quoteInfo.getReference()));
        startActivity(browserIntent);
    }

    public void onNameClick() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(quoteInfo.getBlurb())
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), null);
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.quotes, menu);
        return true;
    }

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

    private void onAboutMenu() {
        Intent i = new Intent(this, AboutActivity.class);
        startActivity(i);
    }

    private void onRandomMenu() {
        Intent i = new Intent(this, QuoteActivity.class);
        Random rnd = new Random();
        int rng = rnd.nextInt(AllQuotes.all_quotes.size());
        Quote q = AllQuotes.all_quotes.get(rng);
        Log.d("QA",q.toString());
        i.putExtra("quoteObject", q);
        startActivity(i);

//        SharedPreferences.Editor editor = prefs.edit();
//        Gson gson = new Gson(); // create Google Javascript notation object to save an object in shared prefs
//        String json = gson.toJson(q);
//        editor.putString("last_run", json);
//        editor.commit();
//        Log.d("QUOTES", "writing quote to Gson object...");
    }

    private void onLastRunMenu() {
        Gson gson = new Gson();
        if(prefs.contains("last_run")) {
            String json = prefs.getString("last_run", "");
            Quote q = gson.fromJson(json, Quote.class);
            Intent i = new Intent(this, QuoteActivity.class);
            i.putExtra("quoteObject", q);
            startActivity(i);
        }

    }

}
