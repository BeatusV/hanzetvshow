package hanze.tvshow;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DisplaySearch  extends FragmentActivity implements DownloadCallback{
    private TextView mDatatext;
    private NetworkFragment mNetworkFragment;
    private boolean mDownloading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_search);
        mDatatext = (TextView) findViewById(R.id.resultView);
        mNetworkFragment = NetworkFragment.getInstance(getFragmentManager(), "https://api.tvmaze.com/search/shows?q=girls");
    }

    public void sendRequest(View view){
        startDownload();
    }
    private void startDownload(){
        if (!mDownloading && mNetworkFragment != null){
            mNetworkFragment.startDownload();
            mDownloading = true;
        }
    }
    @Override
    public void updateFromDownload(String result) {
        if(result != null) {
            mDatatext.setText(result);
        } else {
            mDatatext.setText("Connection error");
        }
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo;
    }


    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) {
        switch(progressCode) {
            // You can add UI behavior for progress updates here.
            case Progress.ERROR:
                break;
            case Progress.CONNECT_SUCCESS:
                break;
            case Progress.GET_INPUT_STREAM_SUCCESS:
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
                mDatatext.setText("" + percentComplete + "%");
                break;
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                break;
        }
    }

    @Override
    public void finishDownloading() {
        mDownloading = false;
        if (mNetworkFragment != null){
            mNetworkFragment.cancelDownload();
        }
    }

}
