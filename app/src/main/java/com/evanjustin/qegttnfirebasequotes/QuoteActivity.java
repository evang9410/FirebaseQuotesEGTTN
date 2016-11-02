package com.evanjustin.qegttnfirebasequotes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Thanh Tung Nguyen, Evan Glicakis
 */
public class QuoteActivity extends AppCompatActivity {

    private final static String TAG = "QuoteActivity";

    private Quote quoteInfo;
    TextView attributed;
    TextView ref;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

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

}
