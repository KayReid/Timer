package edu.stlawu.stopwatch;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // Define variables for our views
    private TextView tv_count = null;
    private Button bt_start = null;
    private Button bt_stop = null;
    private Button bt_reset = null;
    private Timer t = null;
    private Counter ctr = null; //Timertask

    // audio variables
    private AudioAttributes aa = null;
    private SoundPool soundPool = null;
    private int bloopSound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize views
        this.tv_count = findViewById(R.id.tv_count);
        this.bt_start = findViewById(R.id.bt_start);
        this.bt_stop = findViewById(R.id.bt_stop);
        this.bt_reset = findViewById(R.id.bt_reset);

        // start button enables timer
        this.bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_start.setEnabled(false);
                bt_stop.setEnabled(true);
                bt_reset.setEnabled(true);
                resume();
            }
        });

        // stop button
        this.bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_stop.setEnabled(false);
                bt_start.setEnabled(true);
                bt_start.setText("Resume");
                bt_reset.setEnabled(true);
                getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply();
                ctr.cancel();
            }
        });


        // reset button
        this.bt_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_start.setEnabled(true);
                bt_start.setText("Start");
                bt_stop.setEnabled(false);
                // reset count
                getPreferences(MODE_PRIVATE).edit().putInt("COUNT", 0).apply();
                ctr.cancel();
                // set text view back to zero
                MainActivity.this.tv_count.setText("00:00.0");
            }
        }

        );

        /*

        // long ass chain for sound effect
        this.aa = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).setUsage(AudioAttributes.USAGE_GAME).build();
        this.soundPool = new SoundPool.Builder().setMaxStreams(1).setAudioAttributes(aa).build();
        this.bloopSound = this.soundPool.load(this, R.raw.bloop, 1);

        // connecting the sound and animation to the counter
        this.tv_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(bloopSound,1f, 1f, 1, 0, 1f);

                // animation
                Animator anim = AnimatorInflater.loadAnimator(MainActivity.this, R.animator.counter);
                anim.setTarget(tv_count);
                anim.start();
            }
        });

        */
    }

    @Override
    protected void onStart() {
        super.onStart();

        // reload the count from a previous run, if first time running, start at 0
        // getint needs a default value
        int count = getPreferences(MODE_PRIVATE).getInt("COUNT", 0);

        this.tv_count.setText(String.format("%02d:%02d.%d", count / 600, (count / 10) % 60, count % 10));
        System.out.println(count);

        // create timer
        this.t = new Timer();

        // factory method: an example of a design pattern
        Toast.makeText(this, "Stopwatch has started", Toast.LENGTH_LONG).show();


    }

    public void resume() {
        int count = getPreferences(MODE_PRIVATE).getInt("COUNT", 0);
        ctr = new Counter();
        this.ctr.count = count;
        t.scheduleAtFixedRate(ctr, 0, 100);
    }

    public void set_display(){
        this.tv_count.setText(String.format("%02d:%02d.%d", ctr.count / 600, (ctr.count / 10 ) % 60, ctr.count % 10));

    }

    @Override
    protected void onPause() {
        // removes view
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // saves count to reopen when app is created
        getPreferences(MODE_PRIVATE).edit().putInt("COUNT", ctr.count).apply();
    }

    class Counter extends TimerTask{
        private int count = 0;

        @Override
        public void run() {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        set_display();
                        count++;
                }
            });
        }
    }
}
;