package com.example.sanofrank.comicsappback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //bottoni
        Button btnadd = (Button) findViewById(R.id.aggiungi);
        Button btnview = (Button) findViewById(R.id.visualizza);
        //btnadd listner
        btnadd.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v){
            Intent add = new Intent(MainActivity.this, Aggiungi.class);
            startActivity(add);
            }
        });

        //btnview listner
        btnview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent add = new Intent(MainActivity.this, Visualizza.class);
                startActivity(add);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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


}
