package sugarsinitiative.thatbook;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class BookListLoader extends AsyncTaskLoader<List<Books>> {
    List<Books> result;
    private String urls;
    private static final String LOG_TAG = BookActivity.class.getName();

    public BookListLoader(Context context, String url){
        super(context);
        urls = url;
    }

    @Override
    protected void onStartLoading(){
        android.util.Log.i(LOG_TAG, "TEST: onStartLoading() called...");
        forceLoad();
    }

    @Override
    public List<Books> loadInBackground(){
        android.util.Log.i(LOG_TAG, "TEST: loadInBackground() called...");
        if(urls ==null){
            return null;
        }
        result = QueryUtils.fetchBookData(urls);
        android.util.Log.i(LOG_TAG, "TEST: loadInBackground() called..."+urls);

        return result;
    }
}
