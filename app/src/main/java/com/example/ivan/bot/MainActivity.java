package com.example.ivan.bot;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private TextToSpeech ttobj;
    private int CTE=0;
    private ArrayList<String> lista = new ArrayList<>();
    private ArrayList<Boolean> who = new ArrayList<>();
    private Adaptador adp;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lista.add("-");
        who.add(false);
        adp = new Adaptador(this,lista,who);
        lv = (ListView)findViewById(R.id.listView);
        lv.setAdapter(adp);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

//        Intent intent = new Intent();
//        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
//        startActivityForResult(intent, CTE);


        ttobj=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    ttobj.setLanguage(Locale.UK);
                }
            }
        });
        ttobj.setLanguage(new Locale("es", "ES"));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                escuchar();
            }
        });


    }

    public void aniadir(String list, boolean wh){
        lista.add(list);
        who.add(wh);
        adp = new Adaptador(this, lista,who);
        lv.setAdapter(adp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CTE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                if(lista.get(0).compareTo("-")==0){
                    lista.remove(0);
                    who.remove(0);
                }

                lista.add(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
                who.add(false);
                adp = new Adaptador(this,lista,who);
                lv.setAdapter(adp);
            } else {
                Log.v("añ", data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS).toString());
                lista.add(data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS).get(0));
                who.add(false);


                adp = new Adaptador(this,lista,who);
                lv.setAdapter(adp);

                Enviar(data.getStringArrayListExtra(
                        RecognizerIntent.EXTRA_RESULTS).get(0));

//                Intent intent = new Intent();
//                intent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
//                startActivity(intent);
            }
        }

    }

    public void Enviar(String aux) {
        Hebra a = new Hebra();
        a.execute(aux);
    }
    public void comprobar(){

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

    public void onPause(){
        if(ttobj !=null){
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onPause();
    }
    public void escuchar(){
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "es-ES");
        i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla ahora");
        i.putExtra(RecognizerIntent.
                        EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                3000);
        startActivityForResult(i, CTE);

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = ttobj.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {

                ttobj.setLanguage(new Locale("es", "ES"));
                ttobj.setLanguage(Locale.getDefault());
                ttobj.isLanguageAvailable(Locale.US);
                ttobj.isLanguageAvailable(new Locale("spa", "ESP"));
                //tts.setPitch(50); //tono
                //tts.setSpeechRate(200); //velocidad
                ttobj.speak(lista.get(lista.size()-1), TextToSpeech.QUEUE_FLUSH, null, null); //api 21
                ttobj.speak(lista.get(lista.size()-1), TextToSpeech.QUEUE_FLUSH, null);
                //TextToSpeech.QUEUE_FLUSH
                //TextToSpeech.QUEUE_ADD
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private class Hebra extends AsyncTask<String, Void, String> {
        private String s="";
        private ChatterBotFactory factory;
        private ChatterBot bot1;
        private ChatterBotSession bot1session;
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... params) {

                try {
                    s = bot1session.think(params[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                ttobj.setLanguage(new Locale("es", "ES"));
                ttobj.setLanguage(Locale.getDefault());
                ttobj.isLanguageAvailable(Locale.US);
                ttobj.isLanguageAvailable(new Locale("spa", "ESP"));
                //tts.setPitch(50); //tono
                //tts.setSpeechRate(200); //velocidad
                ttobj.speak(s, TextToSpeech.QUEUE_FLUSH, null, null); //api 21
                ttobj.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                //TextToSpeech.QUEUE_FLUSH
                //TextToSpeech.QUEUE_ADD

            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            aniadir(s, true);

        }

        @Override
        protected void onPreExecute() {

            factory = new ChatterBotFactory();


            try {
                bot1 = factory.create(ChatterBotType.CLEVERBOT);
            } catch (Exception e) {
                e.printStackTrace();
            }

            bot1session = bot1.createSession();





        }

        @Override
        protected void onProgressUpdate(Void... values) {


        }
    }
}
