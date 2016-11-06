package com.evanjustin.qegttnfirebasequotes;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by 1432581 on 10/26/2016.
 */
public class Quote implements Parcelable {
    private String attributed; // name of the person who said the quote
    private String blurb; // piece of information about the person who said the quote
    private String quote; // the text itself of the quote
    private String category;
    private String reference;
    private String date;

    /**
     * Default empty param constructor, used primary by the firebase to create the objects from the
     * database.
     */
    public Quote(){}

    /**
     * Constructor for the Quote Object
     * @param attributed
     * @param blurb
     * @param quote
     * @param category
     * @param reference
     * @param date
     */
    public Quote(String attributed, String blurb, String quote, String category, String reference, String date) {
        this.attributed = attributed;
        this.blurb = blurb;
        this.quote = quote;
        this.category = category;
        this.reference = reference;
        this.date = date;
    }

    // Mutators for the Quote Object

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAttributed() {
        return attributed;
    }

    public void setAttributed(String attributed) {
        this.attributed = attributed;
    }

    public String getBlurb() {
        return blurb;
    }

    public void setBlurb(String blurb) {
        this.blurb = blurb;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    @Override
    public String toString() {
        return this.quote +" - " + this.attributed + " " + this.date;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Methods used by the Parcable interface to have a class be parcable, which allows it to be
     * passed throughout intents.
     * Special thanks to Dallas Gutauckis for the http://www.parcelabler.com website which easily
     * Generates a parcable object.
     * Android studio does have plugins for this too
     * See answer #2 of this thread:
     * http://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
     * @param in
     */
    protected Quote(Parcel in) {
        attributed = in.readString();
        blurb = in.readString();
        quote = in.readString();
        category = in.readString();
        reference = in.readString();
        date = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(attributed);
        dest.writeString(blurb);
        dest.writeString(quote);
        dest.writeString(category);
        dest.writeString(reference);
        dest.writeString(date);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Quote> CREATOR = new Parcelable.Creator<Quote>() {
        @Override
        public Quote createFromParcel(Parcel in) {
            return new Quote(in);
        }

        @Override
        public Quote[] newArray(int size) {
            return new Quote[size];
        }
    };
}