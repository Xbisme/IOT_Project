package xbisme.iot_project.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import xbisme.iot_project.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }
}