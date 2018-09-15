package sugarsinitiative.thatbook;

import android.os.Parcel;
import android.os.Parcelable;

public class Books implements Parcelable{

    /**
     * This holds the thumbnail of the book
     */
    private String mThumbnail;


    /**
     * This holds the title of the book
     */
    private String mTitle;

    /**
     * This holds the names of the author(s) of the book
     */
    private String mAuthors;


    /**
     * This holds the Language of the book
     */
    private String mLanguage;

    /**
     * This holds the web link to the book
     */
    private String mLink;


    /**
     * @param thumbnail this holds the url link to each book image
     * @param title    this is an identifier for the title of the book
     * @param language this is the language in which the book is written
     * @param link     this is a web link to the book
     */
    public Books(String thumbnail,String title, String authors, String language, String link) {
       mThumbnail = thumbnail;
        mTitle = title;
       mAuthors = authors;
        mLanguage = language;
        mLink = link;
    }

    protected Books (Parcel in){
        mThumbnail = in.readString();
        mTitle = in.readString();;
        mAuthors = in.readString();;
        mLanguage = in.readString();;
        mLink = in.readString();;
    }


    public static final Creator<Books> CREATOR = new Creator<Books>() {
        @Override
        public Books createFromParcel(Parcel in) {
            return new Books(in);
        }


        @Override
        public Books[] newArray(int size) {
            return new Books[size];
        }
    };


    //this method is used to get the url link for each book image

    public String getmThumbnail() {
        return mThumbnail;
    }


    //this method is used to get the title
    public String getmTitle() {
        return mTitle;
    }

    public String getmAuthors() {
        return mAuthors;
    }

    //this method is used to get the language
    public String getmLanguage() {
        return mLanguage;
    }

    //this method is used to get the web link
    public String getmLink() {
        return mLink;
    }


    /**
     *
     * @return describeContents
     *
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mThumbnail);
        parcel.writeString(mTitle);
        parcel.writeString(mAuthors);
        parcel.writeString(mLanguage);
        parcel.writeString(mLink);
    }



}
