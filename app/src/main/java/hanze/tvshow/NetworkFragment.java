package hanze.tvshow;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Created by Gebruiker on 18-9-2017.
 */

public class NetworkFragment extends Fragment {
    public static final String TAG = "NetworkFragment";
    private static final String URL_KEY = "UrlKey";
    private DownloadCallback mCallback;
    private DownloadTask mDownloadTask;
    private String mUrlString;


    public static NetworkFragment getInstance(FragmentManager fragmentManager, String url) {
        NetworkFragment networkFragment = (NetworkFragment) fragmentManager
                .findFragmentByTag(NetworkFragment.TAG);
        if (networkFragment == null) {
            networkFragment = new NetworkFragment();
            Bundle args = new Bundle();
            args.putString(URL_KEY, url);
            networkFragment.setArguments(args);
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit();
        }
        return networkFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mUrlString = getArguments().getString(URL_KEY);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (DownloadCallback)context;
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
    @Override
    public void onDestroy() {
        cancelDownload();
        super.onDestroy();
    }
    public void startDownload() {
        cancelDownload();
        mDownloadTask = new DownloadTask();
        mDownloadTask.execute(mUrlString);
    }
    public void cancelDownload() {
        if(mDownloadTask != null) {
            mDownloadTask.cancel(true);
            mDownloadTask = null;
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, DownloadTask.Result> {
        class Result {
            public String mResultValue;
            public Exception mException;

            public Result(String resultValue) {
                mResultValue = resultValue;
            }

            public Result(Exception exception) {
                mException = exception;
            }
        }

        @Override
        protected void onPreExecute() {
            if (mCallback != null) {
                NetworkInfo networkInfo = mCallback.getActiveNetworkInfo();
                if (networkInfo == null || !networkInfo.isConnected() ||
                        (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                                && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                    mCallback.updateFromDownload(null);
                    cancel(true);
                }
            }
        }

        @Override
        protected Result doInBackground(String... urls) {
            Result result = null;
            if(!isCancelled() && urls != null && urls.length > 0) {
                String urlString = urls[0];
                try {
                    URL url = new URL(urlString);
                    String resultString = downloadUrl(url);
                    if (resultString != null) {
                        result = new Result(resultString);
                    } else {
                        throw new IOException("No response recieved");
                    }
                } catch(Exception e) {
                    result = new Result(e);
                }
            }
            return result;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if(values.length >= 2) {
                mCallback.onProgressUpdate(values[0], values[1]);
            }
        }

        @Override
        protected void onPostExecute(Result result) {
            if(result != null && mCallback !=null) {
                if(result.mException != null) {
                    mCallback.updateFromDownload(result.mException.getMessage());
                } else if (result.mResultValue != null) {
                    mCallback.updateFromDownload(result.mResultValue);
                }
                mCallback.finishDownloading();
            }
        }
        @Override
        protected void onCancelled(Result result) {
        }

        private String downloadUrl(URL url) throws IOException {
            InputStream stream = null;
            HttpsURLConnection connection = null;
            String result = null;

            try {
                connection = (HttpsURLConnection) url.openConnection();
                connection.setReadTimeout(3000);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                publishProgress(DownloadCallback.Progress.CONNECT_SUCCESS);
                int responseCode = connection.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw new IOException("Http error code: " + responseCode);
                }

                stream = connection.getInputStream();
                publishProgress(DownloadCallback.Progress.GET_INPUT_STREAM_SUCCESS, 0);;
                if (stream != null){
                    result = readStream(stream, 500);
                    publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_SUCCESS, 0);
                }
            } finally {
                 if (stream != null) {
                     stream.close();
                 }
                 if (connection != null) {
                     connection.disconnect();
                 }
            }
            return result;
        }

        private String readStream(InputStream stream, int maxLength) throws IOException {
            String result = null;
            InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[maxLength];
            int numChars = 0;
            int readSize = 0;
            while (numChars < maxLength && readSize != -1){
                numChars += readSize;
                int pct = (100 * numChars) / maxLength;
                publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS, pct);
                readSize = reader.read(buffer, numChars, buffer.length - numChars);
            }
            if (numChars != -1){
                numChars = Math.min(numChars, maxLength);
                result = new  String(buffer, 0 , numChars);
            }

            return result;
        }
    }
}



