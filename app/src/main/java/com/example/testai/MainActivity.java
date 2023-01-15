package com.example.testai;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText inputText;
    private TextView outputText;
    private Button submit_button;
    private String apiKey = "INSERT API KEY HERE";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputText = findViewById(R.id.input_text);
        outputText = findViewById(R.id.output_text);
        submit_button = findViewById(R.id.submit_button);
        progressBar = findViewById(R.id.progress_bar);

        submit_button.setOnClickListener(view -> {
            String text = inputText.getText().toString();
            Log.i("OpenAIOnclick", inputText.getText().toString());
            new OpenAiRequest().execute(text);

        });
    }

    private class OpenAiRequest extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            submit_button.setVisibility(View.GONE);
        }


        @Override
        protected String doInBackground(String... params) {
            String text = params[0];
            String urlString = "https://api.openai.com/v1/completions";
            //String json = "{\"prompt\":\"" + text + "\",\"model\":\"text-davinci-003\",\"temperature\":0.5}";
            String json = "{\"prompt\":\"" + text + "\",\"model\":\"text-davinci-003\",\"temperature\":0.5,\"max_tokens\":2048}";

            String result = "";

            Log.i("OpenAIdoInBackgroundtext", text);
            Log.i("OpenAIdoInBackgroundurlString", urlString);
            Log.i("OpenAIdoInBackgroundjson", json);
            Log.i("OpenAIdoInBackgroundresult", result);



            try {
                Log.i("OpenAIdoInBackground", "sono nel try di doinbackground");
                URL url = new URL(urlString);
                Log.i("OpenAItrydoInBackground", String.valueOf(url));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                Log.i("OpenAItrydoInBackground", String.valueOf(connection));
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                Log.i("OpenAIORequestProperty", String.valueOf(connection.getRequestProperties()));

                if(apiKey.trim().length()==0){
                    Log.i("OpenAIOApyKeyif", "apiKey empty or whitespace");
                }else{
                    Log.i("OpenAIOApyKeyelse", apiKey);
                }
                Log.i("OpenAIOApyKey", apiKey);
                connection.setDoOutput(true);
                Log.i("OpenAItrydoInBackgroundConnection2", String.valueOf(connection));

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(json);
                wr.flush();
                wr.close();

                int responseCode = connection.getResponseCode();
                Log.i("OpenAIOResponse", String.valueOf(responseCode));

                if (responseCode == 200) {
                    Log.i("OpenAIOResponse200", "entrato nel response code 200");
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parsing response JSON to get the generated text
                    JSONObject jsonObject = new JSONObject(response.toString());
                    Log.i("jsonObject", String.valueOf(jsonObject));
                    JSONArray jsonArray = jsonObject.getJSONArray("choices");
                    Log.i("jsonArray", String.valueOf(jsonArray));
                    JSONObject firstChoice = jsonArray.getJSONObject(0);
                    Log.i("jsonArray", String.valueOf(firstChoice));
                    result = firstChoice.getString("text");
                    Log.i("resultdopojsonarray", result);
                }
            } catch (IOException | JSONException e) {
                Log.i("OpenAIOResponse200", "entrato nel catch di response code 200");
                e.printStackTrace();
            }
            Log.i("OpenAIresulttornato", result);
            return result;

        }



        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            submit_button.setVisibility(View.VISIBLE);
            Log.i("OpenAIOonPostExecute", "entrato in onPostExecute");
            outputText.setText(result);
            Log.i("OpenAI", result);
        }

    }
}