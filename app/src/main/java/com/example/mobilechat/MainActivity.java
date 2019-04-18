package com.example.mobilechat;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button btnPost;
    EditText etxtPostMsg;
    TextView txtReceivedPosts;

    String username;

    Handler myFetchPostsHandler;

    EncDecrypt encDecrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        btnPost = findViewById(R.id.btnPost);
        etxtPostMsg = findViewById(R.id.etxtPostMsg);

        txtReceivedPosts = findViewById(R.id.txtReceivedPosts);
        // needed to enable scrolling of TextView, along with changes in layout xml
        txtReceivedPosts.setMovementMethod(new ScrollingMovementMethod());

        encDecrypt = new EncDecrypt();

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myMsg", "Post button pressed");

                username = "Mjölnir";

                String message = etxtPostMsg.getText().toString();

                Log.d("myMsg", "\nusername = " + username + "\nmessage is: " + message);

                String encryptedMessage = "encmsg: " + encDecrypt.encrypt(message);

                // create PostTask object, run method. This does it in separate thread.
                PostTask postTask = new PostTask();
                postTask.execute(username, encryptedMessage);

                // hide keyboard after typing.
                if (getCurrentFocus() != null) {
                    getCurrentFocus().clearFocus();

                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

                // clear the text after posted.
                etxtPostMsg.setText("");
            } // end on click

        });

        myFetchPostsHandler = new Handler();
        myFetchPostsHandler.postDelayed(myFetchPostTimerRunnable, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Log.d("myMsg", "onCreateOptionsMenu...");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // happens when settings on top of screen is selected
        Log.d("myMsg", "onOptionsItemSelected...");

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

    // android.os.AsyncTask<Params, Progress, Result?
// Params: The type of parameters sent to the task upon execution
// Progress: The type of progress units published during the background computation
// Result: The type of result of background computation
    public class PostTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("myMsg", "PostTask AsyncTask");
            //if no msg to post, then nothing to do
            //verify size of params.
            if (params.length == 0) {
                return null;
            }
            Log.d("myMsg", "Username = " + params[0]);
            Log.d("myMsg", "message = " + params[1]);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //contains response as string
            String response = null;

            try {
                //Construct the URL for message to post to database,
                // No parameters
                // http://testchat.ridgewater.net/postmessageandroid.php?
                final String BASE_URL = "http://testchat.ridgewater.net/postmessageandroid.php?";
                final String USERNAME_PARAM = "txtUserName";
                final String CHATMESSAGE_PARAM = "txtChatMessage";

                // This builds :
                // http://testchat.ridgewater.net/postmessageandroid.php?txtUserName=username&txtChatMessage=message
                Uri builtUri = Uri.parse(BASE_URL).buildUpon().appendQueryParameter(USERNAME_PARAM,
                        params[0]).appendQueryParameter(CHATMESSAGE_PARAM, params[1]).build();

                URL url = new URL(builtUri.toString());

                Log.d("myMsg", "url = " + builtUri);

                urlConnection = (HttpURLConnection) url.openConnection();
                Log.d("myMsg", "connection open finish");
                urlConnection.setRequestMethod("GET");
                Log.d("myMsg", "setRequestMethod(GET) finish");
                urlConnection.connect();
                Log.d("myMsg", "Connection was made. urlConnection = " + urlConnection);


                //Read input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // nothing to do
                    response = "No input string";
                }

                reader = new BufferedReader(new InputStreamReader((inputStream)));
                String line;
                Boolean endOfFile = false;

                while (!endOfFile) {
                    line = reader.readLine();
                    // checking if null is sufficient
                    if (line == null) {
                        endOfFile = true;
                    } else {
                        buffer.append(line + "\n");
                    }
                }

                if (buffer.length() == 0) {
                    response = "Empty string";
                }
                response = buffer.toString();

                Log.d("myMsg", "response:\n  " + response);

            } catch (IOException e) {
                Log.d("myMsg", "Error= " + e);
                response = null;

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    Log.d("myMsg", "Connection closed.");
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.d("myMsg", "Error closing stream: " + e);
                    }
                }
            } // end try/catch/finally block


            return null;
        } // end do in background method
    }

    // run this method at a regular interval
    private Runnable myFetchPostTimerRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("myRecMsg", "myFetchPostsTimerRunnable...");

            // do something
            FetchPostsTask fetchPostsTask = new FetchPostsTask();
            fetchPostsTask.execute();


            //Reset timer to run again
            myFetchPostsHandler.postDelayed(myFetchPostTimerRunnable, 1000);
        }

    };

    // android.os.AsyncTask<Params, Progress, Result?
// Params: The type of parameters sent to the task upon execution
// Progress: The type of progress units published during the background computation
// Result: The type of result of background computation
    public class FetchPostsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.d("myRecMsg", "FetchPostsTask AsyncTask");

            // Declare outside of try/catch block so they can be closed in finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //contains raw received posts receivedPosts as string
            String receivedPosts = null;

            try {
                //Construct the URL for message to be received from database,
                // No parameters
                // http://testchat.ridgewater.net/postmessageandroid.php?
                final String BASE_URL = "http://testchat.ridgewater.net/getpostsandroid.php?";

                // This builds :
                // http://testchat.ridgewater.net/getpostsandroid.php?
                Uri builtUri = Uri.parse(BASE_URL).buildUpon().build();

                URL url = new URL(builtUri.toString());

                Log.d("myRecMsg", "url = " + builtUri);

                // Create request to open connection
                urlConnection = (HttpURLConnection) url.openConnection();
                Log.d("myRecMsg", "connection open finish");
                urlConnection.setRequestMethod("GET");
                Log.d("myRecMsg", "setRequestMethod(GET) finish");
                urlConnection.connect();
                Log.d("myRecMsg", "Connection was made. urlConnection = " + urlConnection);


                //Read input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // nothing to do
                    receivedPosts = "No input string";
                }

                reader = new BufferedReader(new InputStreamReader((inputStream)));
                String line;
                Boolean endOfFile = false;

                while (!endOfFile) {
                    line = reader.readLine();
                    // checking if null is sufficient
                    if (line == null) {
                        endOfFile = true;
                    } else {
                        // check if beginningo of line contains encmsg
                        if (line.startsWith("encmsg: ")) {
                            line= line.replace("encmsg: ", "");
                            line = encDecrypt.decrypt(line);
                        }
                        buffer.append(line + "\n");
                    }
                }

                if (buffer.length() == 0) {
                    receivedPosts = "Empty string";
                }
                receivedPosts = buffer.toString();

                Log.d("myRecMsg", "receivedPosts:\n  " + receivedPosts);

            } catch (IOException e) {
                Log.d("myRecMsg", "Error= " + e);
                receivedPosts = "IO Exception - Network Failure?";

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                    Log.d("myRecMsg", "Connection closed.");
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.d("myRecMsg", "Error closing stream: " + e);
                    }
                }
            } // end try/catch/finally block

            return receivedPosts;

        } // end DoInBackground

        @Override
        protected void onPostExecute(String result) {
            Log.d("myRecMsg", "*** in onPostExecute");
            if (result != null) {
                txtReceivedPosts.setText(result);
            }
        }
    } // end FetchPostsTask
} // end Main