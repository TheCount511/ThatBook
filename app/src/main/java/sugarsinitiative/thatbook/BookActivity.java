package sugarsinitiative.thatbook;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wanderingcan.persistentsearch.PersistentSearchView;
import com.wanderingcan.persistentsearch.SearchMenuItem;

import java.util.ArrayList;
import java.util.List;

public class BookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Books>> {
    public static final String LOG_TAG = BookActivity.class.getName();
    private static final int VOICE_RECOGNITION_CODE = 9999;
    public String BOOKS_REQUEST_URL = "";
    PersistentSearchView persistentSearchView;
    private boolean mMicEnabled;
    String BOOKLIST = "booklist";
    //ArrayList<Books> books = new ArrayList<>();
    private BookDataAdapter adapter;
    private TextView emptyView;
    private ProgressBar loading;
    private ListView booksListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "starts loading app")  ;

        setContentView(R.layout.activity_book);


        //the following listView would hold the results that are sent
        booksListView = findViewById(R.id.list);

        //this view would be displayed if the listView is empty
        emptyView = findViewById(R.id.emptyView);

        //this would be called while the app is fetching the results
        loading = findViewById(R.id.loading_spinner);
        loading.setVisibility(View.GONE);

        adapter = new BookDataAdapter(this, -1);

        booksListView.setAdapter(adapter);


        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Books currentBook = adapter.getItem(position);
                Uri bookUrl = Uri.parse(currentBook.getmLink());

                //parse the string as a uri in the intent
                Intent intent = new Intent(Intent.ACTION_VIEW, bookUrl);
                startActivity(intent);
            }
        });

        mMicEnabled = isIntentAvailable(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH));

        persistentSearchView = findViewById(com.wanderingcan.persistentsearch.R.id.search_bar);

        persistentSearchView.setNavigationDrawable(null);

        persistentSearchView.setShowSearchMenu(true);
        persistentSearchView.setOnSearchListener(new PersistentSearchView.OnSearchListener() {
            @Override
            public void onSearchOpened() {
                //Called when the Searchbar is opened by the user or by something calling
                //persistentSearchView.openSearch();
                persistentSearchView.setNavigationDrawable(getDrawable(R.drawable.ic_arrow_back_black_24dp));
            }

            @Override
            public void onSearchClosed() {
                //Called when the searchbar is closed by the user or by something calling
                //persistentSearchView.closeSearch();
                persistentSearchView.setNavigationDrawable(null);
            }

            @Override
            public void onSearchCleared() {
                //Called when the searchbar has been cleared by the user by removing all
                //the text or hitting the clear button. This also will be called if
                //persistentSearchView.populateSearchText() is set with a null string or
                //an empty string
               // getLoaderManager().destroyLoader(1);
                    BOOKS_REQUEST_URL="";
            }

            @Override
            public void onSearchTermChanged(CharSequence term) {
                //Called when the text in the searchbar has been changed by the user or
                //by persistentSearchView.populateSearchText() with text passed in.
                //Best spot to handle giving suggestions to the user in the menu
                String query = term.toString();
                if (query.trim().equals("")){
                    BOOKS_REQUEST_URL=query;
                }
                else {
                    String resolvedQuery = term.toString().replaceAll(" ", "%20");
                    BOOKS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=" + resolvedQuery + "&maxResults=15";
                }
            }

            @Override
            public void onSearch(CharSequence text) {
                //Called when the user hits the IME Action Search on the keyboard to search
                //Here is the best spot to handle searches
               // getLoaderManager().destroyLoader(1);
                if (BOOKS_REQUEST_URL.trim().equals("")) {
                    Snackbar.make(persistentSearchView, R.string.empty_search, Snackbar.LENGTH_LONG).show();
                } else {
                    getLoaderManager().destroyLoader(1);
                    BookActions();
                }
            }
        });

        persistentSearchView.setOnMenuItemClickListener(new PersistentSearchView.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(SearchMenuItem item) {
                //Called when an Item in the SearchMenu is clicked, it passes in the
                //SearchMenuItem that was clicked

            }
        });

        persistentSearchView.setOnIconClickListener(new PersistentSearchView.OnIconClickListener() {
            @Override
            public void OnNavigationIconClick() {
                if (persistentSearchView.isSearchOpen()) {
                    persistentSearchView.closeSearch();
                }
            }

            @Override
            public void OnEndIconClick() {
                startVoiceRecognition();
            }
        });


        if(savedInstanceState != null) {
           Books[] books = (Books[]) savedInstanceState.getParcelableArray(BOOKLIST);
           adapter.addAll(books);
            Log.d(LOG_TAG, "trying to restore listview state..");
        }

    }


    //the following method handles activities related to the book array
    public void BookActions() {

        loading.setVisibility(View.VISIBLE);


        // /the emptyView is set to replace the listView if the array is empty
        booksListView.setEmptyView(emptyView);

        emptyView.setVisibility(View.GONE);

        //this initializes the network status method
        // and proceeds with running the app if there is a network else it handles it
        // by giving the user an appropriate feedback
        initialTestForNetwork();
    }

    //this closes the searchmenu when the back button is pressed
    @Override
    public void onBackPressed() {
        if (persistentSearchView.isSearchOpen()) {
            persistentSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VOICE_RECOGNITION_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            persistentSearchView.populateSearchText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    //This Method starts the voice recognition service
    private void startVoiceRecognition() {
        if (mMicEnabled) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                    this.getString(R.string.speak_now));
            startActivityForResult(intent, VOICE_RECOGNITION_CODE);
        }
    }


    //im not exactly sure what this method does for now but it works with the voice recognition service;
    private boolean isIntentAvailable(Intent intent) {
        PackageManager mgr = getPackageManager();
        if (mgr != null) {
            List<ResolveInfo> list = mgr.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }
        return false;
    }


    public void initialTestForNetwork() {

        /* This apps running and functionality starts with this method
         * and as explained in the onCreate method it checks if there is an active method
         * and proceeds with running the app as appropriate depending of the network status
         */

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (activeNetwork != null && activeNetwork.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid// because this activity implements the LoaderCallbacks interface).
            Log.i(LOG_TAG, "TEST: calling initLoader()...");
            getLoaderManager().initLoader(1, null, this).forceLoad();


        } else {

            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            loading.setVisibility(View.GONE);

            // Update empty state with no connection error message
           emptyView.setVisibility(View.VISIBLE);
            emptyView.setText(R.string.no_connection);

        }

        /* boolean isConnected = activeNetwork != null &&
         activeNetwork.isConnectedOrConnecting();
         return isConnected;**/
    }


    @Override
    public Loader<List<Books>> onCreateLoader(int id, Bundle args) {
        Log.i(LOG_TAG, "TEST: onCreateLoader() called...");
        return new BookListLoader(BookActivity.this, BOOKS_REQUEST_URL);
    }


    @Override
    public void onLoadFinished(Loader<List<Books>> loader, List<Books> data) {
        Log.i(LOG_TAG, "TEST: onLoadFinished() called...");


        //set the visibility of the loading progress bar to gone on finished loading
        loading.setVisibility(View.GONE);

        adapter.clear();

        //if there is a valid list of {@link Earthquake}s, then add them to the adapters data set. This will trigger the Listview to update.
        if (data != null && !data.isEmpty()) {
            adapter.addAll(data);
        }
        else{
            //set the emptyView text to the string resource on finished loading
            emptyView.setText(R.string.emptyArray);
        }

    }


    @Override
    public void onLoaderReset(Loader<List<Books>> loader) {
        //clear the adapter of the previous earthquake data
        Log.i(LOG_TAG, "TEST: onLoaderReset() called...");
       // getLoaderManager().destroyLoader(0);
        adapter.clear();
    }



    //this method saves the state to enable the app to restore the same state after minimizing
    //or rotating the screen
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Books[] books = new Books[adapter.getCount()];
        for (int i = 0; i < books.length; i++) {
            books[i] = adapter.getItem(i);
        }
        outState.putParcelableArray(BOOKLIST, books);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            Books[] books = (Books[]) savedInstanceState.getParcelableArray(BOOKLIST);
            adapter.addAll(books);
        }
        Log.d(LOG_TAG, "trying to restore listview state..");

    }
}




