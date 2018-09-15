package sugarsinitiative.thatbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BookDataAdapter extends ArrayAdapter<Books>{





    public BookDataAdapter(Context context, int resource /**ArrayList<Books> books**/){
        super(context, resource/**0,**/ /**books**/);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_layout, parent, false);

        }

        String bookThumbnail;
        String bookTitle;
        String authorsNames;
        String bookLanguage;


        //get the data item at this position
        Books currentBook = getItem(position);



        ImageView bookThumbnailView = convertView.findViewById(R.id.thumbnail);
        bookThumbnail = currentBook.getmThumbnail();
        if (bookThumbnail.trim().equals("")) {
            Picasso.get()
                    .load(R.drawable.no_image_available)
                    .resize(50, 50)
                    .centerCrop()
                    .into(bookThumbnailView);
        }else {
            Picasso.get()
                    .load(bookThumbnail)
                    .resize(50, 50)
                    .centerCrop()
                    .into(bookThumbnailView);}



        TextView bookTitleView = convertView.findViewById(R.id.bookTitle);
        bookTitle = currentBook.getmTitle();
        bookTitleView.setText(bookTitle);

        TextView authorsNamesView = convertView.findViewById(R.id.authorsNames);
        authorsNames = currentBook.getmAuthors();
        String by = getContext().getString(R.string.by);

        if (authorsNames == "Author not given"){
            authorsNamesView.setText(authorsNames);
        }
        else{
        authorsNamesView.setText(by.concat(" "+authorsNames));}

        TextView language = convertView.findViewById(R.id.language);
        bookLanguage = currentBook.getmLanguage();
        language.setText(bookLanguage);

        return convertView;
    }




}

