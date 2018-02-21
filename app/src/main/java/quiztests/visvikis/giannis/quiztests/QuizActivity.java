package quiztests.visvikis.giannis.quiztests;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;


public class QuizActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private InterstitialAd mInterstitialAd;


    SQLiteDatabase quizDatabase;

    private final String INDEX_TAG = "INDEX_TAG";
    private final String QUIZ_DATABASE_NAME = "quiz_database.db";
    private final String TO_CHECK_QUIZ_DATABASE_NAME = "new_quiz_database.db";

    //ArrayLists tags
    private final String CORRECT_ANSWERS_TAG = "CORRECT_ANSWERS";
    private final String FALSE_ANSWERS_1_TAG = "WRONG_ANSWERS_1";
    private final String FALSE_ANSWERS_2_TAG = "WRONG_ANSWERS_2";
    private final String FALSE_ANSWERS_3_TAG = "WRONG_ANSWERS_3";
    private final String QUESTIONS_TAG = "QUESTIONS";
    private final String INFO_LINKS_TAG = "INFO_LINKS";
    private final String FILE_PATHS_TAG = "FILE_PATHS";
    private  final String HOW_MANY_CORRECTS_TAG = "HOW_MANY_CORRECTS_SO_FAR";

    private final int totalQuestions = 15;

    private int correctAnswers;
    private int questionIndex;

    private ArrayList<String> questionsList;
    private ArrayList<String> filePathsList;
    private ArrayList<String> infoLinksList;
    private ArrayList<String> correctAnswersList;
    private ArrayList<String> falseAnswersList1;
    private ArrayList<String> falseAnswersList2;
    private ArrayList<String> falseAnswersList3;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);


        //Get references to views
        ImageView quizImage = findViewById(R.id.ad_image_view);

        AppCompatTextView quizQuestion = findViewById(R.id.quiz_question_text);

        LinearLayout moreInfoLayout = findViewById(R.id.more_info)

        AppCompatButton answerButton1 = findViewById(R.id.answer_button_1);

        AppCompatButton answerButton2 = findViewById(R.id.answer_button_2);

        AppCompatButton answerButton3 = findViewById(R.id.answer_button_3);

        AppCompatButton answerButton4 = findViewById(R.id.answer_button_4);


        if(savedInstanceState != null){
            //retrieve state

            questionIndex = savedInstanceState.getInt(INDEX_TAG);
            correctAnswers = savedInstanceState.getInt(HOW_MANY_CORRECTS_TAG);

            questionsList = savedInstanceState.getStringArrayList(QUESTIONS_TAG);
            filePathsList = savedInstanceState.getStringArrayList(FILE_PATHS_TAG);
            infoLinksList = savedInstanceState.getStringArrayList(INFO_LINKS_TAG);
            correctAnswersList = savedInstanceState.getStringArrayList(CORRECT_ANSWERS_TAG);
            falseAnswersList1 = savedInstanceState.getStringArrayList(FALSE_ANSWERS_1_TAG);
            falseAnswersList2 = savedInstanceState.getStringArrayList(FALSE_ANSWERS_2_TAG);
            falseAnswersList3 = savedInstanceState.getStringArrayList(FALSE_ANSWERS_3_TAG);

        }
        else {

            //Check if database file in assets holds more entries than the one stored in /data/data/package/databases/...
            //Delete the old database file if exists and copy the new into the data bases place in the app and then select
            //questions


            questionIndex = 0;
            correctAnswers = 0;

        }


        quizDatabase = openOrCreateDatabase(QUIZ_DATABASE_NAME, MODE_PRIVATE, null);


        //CHECK IF THE DATABASE IS CREATED. IF YES CHECK IF THERE ARE ANY UPDATES PENDING

        ee if there are more than one tables ... then there is a database in phone storage



        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = newInterstitialAd();
        loadInterstitial();


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


        outState.putInt(INDEX_TAG,questionIndex);
        outState.putInt(HOW_MANY_CORRECTS_TAG,correctAnswers);

        outState.putStringArrayList(QUESTIONS_TAG, questionsList);
        outState.putStringArrayList(FILE_PATHS_TAG, filePathsList);
        outState.putStringArrayList(INFO_LINKS_TAG, infoLinksList);

        outState.putStringArrayList(CORRECT_ANSWERS_TAG, correctAnswersList);
        outState.putStringArrayList(FALSE_ANSWERS_1_TAG, falseAnswersList1);
        outState.putStringArrayList(FALSE_ANSWERS_2_TAG, falseAnswersList2);
        outState.putStringArrayList(FALSE_ANSWERS_3_TAG, falseAnswersList3);



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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    private InterstitialAd newInterstitialAd() {

        InterstitialAd interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                Toast.makeText(getApplicationContext(), "Ad Loaded", Toast.LENGTH_SHORT).show();

            }


            @Override
            public void onAdFailedToLoad(int errorCode) {

                Toast.makeText(QuizActivity.this, "Add Failed to load", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdClosed() {


            }

        });
        return interstitialAd;
    }



    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            loadInterstitial();
        }
    }


    private void loadInterstitial() {
        //Initialize and load the ad.
        mInterstitialAd = newInterstitialAd();

        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        mInterstitialAd.loadAd(adRequest);
    }





    private void initiateNewQuiz(){

    }



    private void checkTheQuizDatabase(){


        long numTables = DatabaseUtils.longForQuery(quizDatabase,"select * from sqlite_master where type = 'table'", null);


        try{

            BufferedInputStream inputStream = new BufferedInputStream(getAssets().open("databases/quizdatabase.db"));

            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(
                    new File("/data/data/" + getPackageName() +"/databases/" + TO_CHECK_QUIZ_DATABASE_NAME)));

            int b;

            while ( (b = inputStream.read()) != -1 ){

                outputStream.write(b);

            }

            inputStream.close();
            outputStream.close();

        }
        catch (FileNotFoundException fnf){
            //No new File found, therefore there is no update present
            Toast.makeText(this, "NO DATABASE FILE FOUND IN ASSETS", Toast.LENGTH_SHORT).show();
            Log.e("QUIZ_ACT_FNF_Exc", fnf.getMessage());
        }
        catch (IOException ioe){
            Toast.makeText(this, "IOException, see log", Toast.LENGTH_SHORT).show();
            Log.e("QUIZ_ACT_COPY_DATABS", ioe.getMessage());
        }


    }





}
