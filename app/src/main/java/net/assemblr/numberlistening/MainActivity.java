package net.assemblr.numberlistening;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.RunnableFuture;


public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private boolean isAuto = false;
    private Timer mTimer = null;
    private Handler mHandler = null;

    private static final String TAG = "NumberL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        tts = new TextToSpeech(this, this);

        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                if (isAuto)
                    setNextNumber();
            }

            @Override
            public void onError(String s) {

            }
        });

        Button speechButton = (Button)findViewById(R.id.button_speech);
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speechText();
            }
        });

        Button nextButton = (Button)findViewById(R.id.button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNextNumber();
            }
        });

        Button autoButton = (Button)findViewById(R.id.button_auto);
        autoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button autoButton = (Button)findViewById(R.id.button_auto);

                stopTimer();

                if (!isAuto) {
                    isAuto = true;

                    autoButton.setTextColor(Color.GREEN);
                    mTimer = new Timer();
                    mTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    setNextNumber();
                                    speechText();
                                }
                            });
                        }
                    }, 0, 8000);
                } else {
                    isAuto = false;
                    autoButton.setTextColor(Color.BLACK);
                }
            }
        });

        setNextNumber();
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
        if (id == R.id.action_about) {
            Intent intent = new Intent(getApplication(), HelpActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInit(int status) {
        if (TextToSpeech.SUCCESS == status) {
            Locale locale = Locale.ENGLISH;
            if (tts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE) {
                tts.setLanguage(locale);
            } else {
                Log.d(TAG, "Error SetLocale");
            }
        } else {
            Log.d(TAG, "Error Init");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    private void setNextNumber() {
        Random r = new Random();
        Integer n = r.nextInt(Integer.MAX_VALUE);
        ((TextView)findViewById(R.id.number)).setText(String.format("%1$,3d", n));
    }

    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void speechText() {
        String string = ((TextView)findViewById(R.id.number)).getText().toString();
        if (string.length() > 0) {
            if (tts.isSpeaking()) {
                tts.stop();
            }
            tts.speak(string, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

}
