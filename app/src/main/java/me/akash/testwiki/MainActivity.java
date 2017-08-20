package me.akash.testwiki;

import android.app.ProgressDialog;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    EditText search_text;
    TextView search_res;
    private static final int DATA_FETCHED = 0;
    private static final int DATA_FETCH_FAILED = 1;
    private TextToSpeech tts;
    private Button btnSpeak;
    private Button stop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //define elem
        tts = new TextToSpeech(this, this);
        btnSpeak = (Button) findViewById(R.id.speech);

        stop = (Button) findViewById(R.id.speechstop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tts != null) {
                    tts.stop();
        }
    }
});
        // button on click event
        btnSpeak.setOnClickListener(new View.OnClickListener() {

@Override
public void onClick(View arg0) {
        speakOut();
        }

        });
        search_text = (EditText) findViewById(R.id.edit_text);
        search_res = (TextView) findViewById(R.id.text_view);
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(search_text.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Field can not be emty", Toast.LENGTH_SHORT).show();
                } else {
                    String url = getUrl();
                    initJSONcall(url);

                }
            }
        });
    }


    private void updateUI(int dataFetched, Object response) {
        switch (dataFetched) {
            case DATA_FETCHED:
                CardView cv = (CardView) findViewById(R.id.card);
                cv.setVisibility(View.VISIBLE);
                String data = getParsedData(response);
                search_res.setText(data);
                break;
            case DATA_FETCH_FAILED:
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private String getParsedData(Object response) {
        String result = null;
        try {
            String key = null;
            JSONObject json = new JSONObject(response.toString());
            JSONObject query = json.getJSONObject("query");
            JSONObject pages = query.getJSONObject("pages");
            Iterator<String> iter = pages.keys();
            while (iter.hasNext()) {
                key = iter.next();
            }
            JSONObject res = pages.getJSONObject(key);
            result = res.getString("extract");
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "err", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    private void initJSONcall(String url) {
        CustomObjectRequest objReq = new CustomObjectRequest(Request.Method.GET, url, null, new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                updateUI(DATA_FETCHED, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateUI(DATA_FETCH_FAILED, null);
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(objReq);

    }


    private String getUrl() {
        String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=";
        String editString = search_text.getText().toString();
        String[] splited = editString.split("\\s+");
        for (int i = 0; i < splited.length - 1; i++) {
            url = url + splited[i] + "%20";
        }
        url = url + splited[splited.length - 1];
        return url;
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            } else {
                btnSpeak.setEnabled(true);
                speakOut();
            }

        } else {
        }

    }

    private void speakOut() {

        String text = search_res.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
