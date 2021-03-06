package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final int MAX_TWEET=140;
    public static final String TAG= "ComposeActivity";
    EditText etCompose;
    Button btnTweet;
    TextView tvCharCount;
    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        client=TwitterApp.getRestClient(this);
        etCompose=findViewById(R.id.etCompose);
        btnTweet=findViewById(R.id.btnTweet);
        tvCharCount = findViewById(R.id.tvCharCount);
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                int characterCount=etCompose.length();
                characterCount=MAX_TWEET-characterCount;
                String showString=String.valueOf(characterCount);
                tvCharCount.setText("Remaining characters:"+showString);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tweetContent=etCompose.getText().toString();
                if (tweetContent.isEmpty()){
                    return;
                }
                if (tweetContent.length()>MAX_TWEET){
                    Toast.makeText(ComposeActivity.this,"Sorry your tweet is too long", Toast.LENGTH_LONG).show();

                }
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(TAG, "onSuccess to publish Tweet");
                        try {
                            Tweet tweet= Tweet.fromJson(json.jsonObject);
                            Log.i(TAG, "published tweet says"+tweet.body);
                            Intent intent= new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(TAG, "on failure to publish tweet", throwable);
                    }
                });
            }

        });
    }
}