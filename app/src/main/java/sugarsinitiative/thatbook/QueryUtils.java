package sugarsinitiative.thatbook;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static List<Books> extractBooks(String booksListJSON) {
        if (TextUtils.isEmpty(booksListJSON)) {
            return null;
        }

        List<Books> booksList = new ArrayList<>();


        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            //this traverses the json response to its root folder
            JSONObject jsonRootObject = new JSONObject(booksListJSON);


            //this traverses to an array in the root folder called items
            JSONArray bookListArray = jsonRootObject.optJSONArray("items");

            //this iterates for the number of items in the array called "items"
            for (int i = 0; i < bookListArray.length(); i++) {


                //the object current book is used to represent each item corresponding to what the value of i is at that time
                JSONObject currentBook = bookListArray.getJSONObject(i);

                //this gets the object in the array called volume info
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                //this gets the object in which the image links are stored
                JSONObject imageLinks = volumeInfo.optJSONObject("imageLinks");
                String thumbnail ;
                if (imageLinks == null) {
                    thumbnail = "";
                } else {
                    //this gets the image link in the imageLink object in an item called "smallThumbnail"
                    thumbnail = imageLinks.getString("smallThumbnail");
                }

                //this gets a string from volume info called "title"
                String title = volumeInfo.getString("title");


                //this gets the array in which the authors adapter is stored
                String noAuthor = "Author not given";
                String authorsNames = "  ";

                JSONArray authorsListArray = volumeInfo.optJSONArray("authors");



                if (volumeInfo.isNull("authors")) {
                    authorsNames = noAuthor;
                } else {
                    for (int j = 0; j < authorsListArray.length(); j++) {

                        if (j == 0) {
                            authorsNames = authorsListArray.getString(j);
                        } else
                            authorsNames = authorsNames + " , " + authorsListArray.getString(j);
                    }
                }

                //this gets a string from volume info called "language"
                String language = volumeInfo.getString("language");

                //this accesses the object called "accessInfo" that is in the "items" array
                JSONObject accessInfo = currentBook.getJSONObject("accessInfo");

                //this gets the string value for the item "webReaderLink" in the accessInfo Object
                String webLink = accessInfo.getString("webReaderLink");

                Books book = new Books(thumbnail, title, authorsNames, language, webLink);
                booksList.add(book);
                Log.i("QueryUtils", "showing the authors" + authorsNames);

            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        // Return the list of books
        return booksList;
    }

    /**
     * Query the Google books dataset and return an {@link List<Books>} object to represent a single book.
     */
    public static List<Books> fetchBookData(String requestUrl) {
        android.util.Log.i(LOG_TAG, "TEST: fetchBookData called...");

        try {
            Thread.sleep(9000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }


        // Return the {@link Books}
        return extractBooks(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Error with creating URL ", e);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";


        if (url == null) {
            return jsonResponse;
        }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                }
                else {
                Log.e(LOG_TAG, "error response code:" + urlConnection.getResponseCode());
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "problem receiving the Booklist JSON results:", e);

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }

        return jsonResponse;
    }


    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


}
