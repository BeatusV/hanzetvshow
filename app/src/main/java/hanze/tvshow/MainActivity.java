package hanze.tvshow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {
    public static String EXTRA_MESSAGE = "Extra_Message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplaySearch.class);
        EditText search = (EditText) findViewById(R.id.editText);
        intent.putExtra(EXTRA_MESSAGE, search.getText().toString());
        startActivity(intent);
    }


}
