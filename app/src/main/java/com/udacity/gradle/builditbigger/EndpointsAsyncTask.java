package com.udacity.gradle.builditbigger;

import android.os.AsyncTask;

import com.example.iruler.myapplication.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Created by iRuler on 25/8/15.
 */
public class EndpointsAsyncTask extends AsyncTask<MainActivity, Void, String> {
    public static String ASYNCTASK_NOT_OK = "com.udacity.gradle.builditbigger.endpointasynctask.not_ok";
    private static MyApi myApiService = null;
    private MainActivity mainActivity;
    private EndpointsAsyncTaskListener mListener = null;


    @Override
    protected String doInBackground(MainActivity... params) {
        if(myApiService == null) {  // Only do this once
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver

            myApiService = builder.build();
        }

        mainActivity = params[0];
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            return myApiService.tellJoke().execute().getJoke();
        } catch (IOException e) {
            return e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if(this.mListener != null){
            if(result.equals("")){
                this.mListener.onComplete(ASYNCTASK_NOT_OK);
            }else {
                this.mListener.onComplete(result);
            }
        }
        if(mainActivity != null) {
            mainActivity.hideRefreshLayoutSwipeProgress();
            mainActivity.showJoke(result);
        }
    }

    @Override
    protected void onCancelled(){
        if(this.mListener != null){
            this.mListener.onComplete(ASYNCTASK_NOT_OK);
        }
    }

    public EndpointsAsyncTask setListener(EndpointsAsyncTaskListener listener) {
        this.mListener = listener;
        return this;
    }

    public static interface EndpointsAsyncTaskListener {
        public void onComplete(String result);
    }
}
