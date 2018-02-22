package quiztests.visvikis.giannis.quiztests;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.database.Cursor;
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
import java.sql.SQLInput;
import java.util.ArrayList;


public class QuizActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private InterstitialAd mInterstitialAd;


    private SQLiteDatabase quizDatabase;

    private final String INDEX_TAG = "INDEX_TAG";
    private final String QUIZ_DATABASE_NAME = "quiz_database.db";
    private final String TO_CHECK_QUIZ_DATABASE_NAME = "new_quiz_database.db";

    private final int QUIZ_LOADER_TASK_ID = 1;

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

        LinearLayout moreInfoLayout = findViewById(R.id.more_info);

        AppCompatButton answerButton1 = findViewById(R.id.answer_button_1);

        AppCompatButton answerButton2 = findViewById(R.id.answer_button_2);

        AppCompatButton answerButton3 = findViewById(R.id.answer_button_3);

        AppCompatButton answerButton4 = findViewById(R.id.answer_button_4);

        quizDatabase = openOrCreateDatabase(QUIZ_DATABASE_NAME, MODE_PRIVATE, null);


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
            questionIndex = 0;
            correctAnswers = 0;

            //TODO CHECK IF THE DATABASE IS CREATED. IF YES CHECK IF THERE ARE ANY UPDATES PENDING
            prepareTheQuizDatabase();


            //TODO when the task loader is finished, take each QuizQuestion object and fill in the ArrayLists

            DO WORK HERE

        }



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

                Toast.makeText(getApplicationContext(), "Add Failed to load", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdClosed() {

                Toast.makeText(getApplicationContext(), "Ad was closed", Toast.LENGTH_LONG).show();
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


    /**
     * Calls for a check in the quiz database for any updates or to create it if the database does not exist
     * and then call to initiates the AsyncTaskHolder to set up the quiz
     */
    private void prepareTheQuizDatabase(){

        quizDatabase = checkForUpdates();

        initiateNewQuiz();
    }


    /**
     * Temporarily copy the database file from the assets folder and check if any table holds more rows than the old one
     *
     * @return either the existing sqlite database or the updated one if exists
     *
     */
    private SQLiteDatabase checkForUpdates(){

        //access the old database (or create it for the first time)

        SQLiteDatabase quizDatabase = openOrCreateDatabase(QUIZ_DATABASE_NAME, MODE_PRIVATE, null);

        //temporarily copy the sqlite.db file from assets in the storage and check for any updates

        File newDatabaseFile = new File("/data/data/" + getPackageName() +"/databases/" + TO_CHECK_QUIZ_DATABASE_NAME);

        try{

            BufferedInputStream inputStream = new BufferedInputStream(getAssets().open("databases/quizdatabase.db"));

            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(newDatabaseFile));

            int b;

            while ( (b = inputStream.read()) != -1 ){

                outputStream.write(b);

            }

            inputStream.close();
            outputStream.close();

            Log.e("QUIZ_ACT", "NEW DATABASE FILE COPIED");

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


        //access the old database
        long oldDriversTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from drivers_table;", null);
        long oldConstructorTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from constructors_table;", null);
        long oldCircuitsTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from circuits_table;", null);
        long oldFiguresTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from figures_table;", null);
        long oldCarsTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from cars_table;", null);
        long oldHelmetsTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from helmets_table;", null);

        Log.e("OLD QUIZ DRIVERS", oldDriversTableCount + "");
        Log.e("OLD QUIZ CONSTRUCTORS", oldConstructorTableCount + "");
        Log.e("OLD QUIZ CIRCUITS", oldCircuitsTableCount + "");
        Log.e("OLD QUIZ FIGURES", oldFiguresTableCount + "");
        Log.e("OLD QUIZ HELMETS", oldHelmetsTableCount + "");
        Log.e("OLD QUIZ CARS", oldCarsTableCount + "");


        //access the new database
        SQLiteDatabase newDataBase = openOrCreateDatabase(TO_CHECK_QUIZ_DATABASE_NAME, MODE_PRIVATE, null);

        long newDriversTableCount = DatabaseUtils.longForQuery(newDataBase, "select count(*) from drivers_table;", null);
        long newConstructorTableCount = DatabaseUtils.longForQuery(newDataBase, "select count(*) from constructors_table;", null);
        long newCircuitsTableCount = DatabaseUtils.longForQuery(newDataBase, "select count(*) from circuits_table;", null);
        long newFiguresTableCount = DatabaseUtils.longForQuery(newDataBase, "select count(*) from figures_table;", null);
        long newCarsTableCount = DatabaseUtils.longForQuery(newDataBase, "select count(*) from cars_table;", null);
        long newHelmetsTableCount = DatabaseUtils.longForQuery(newDataBase, "select count(*) from helmets_table;", null);

        Log.e("NEW QUIZ DRIVERS", newDriversTableCount + "");
        Log.e("NEW QUIZ CONSTRUCTORS", newConstructorTableCount + "");
        Log.e("NEW QUIZ CIRCUITS", newCircuitsTableCount + "");
        Log.e("NEW QUIZ FIGURES", newFiguresTableCount + "");
        Log.e("NEW QUIZ HELMETS", newHelmetsTableCount + "");
        Log.e("NEW QUIZ CARS", newCarsTableCount + "");



        //compare and see if there is any update
        Log.e("QUIZ_ACT", "COMPARING DATABASES");

        if(newDriversTableCount > oldDriversTableCount
                || newConstructorTableCount > oldConstructorTableCount
                    || newCircuitsTableCount > oldCircuitsTableCount
                        ||newFiguresTableCount > oldFiguresTableCount
                            || newCarsTableCount > oldCarsTableCount
                                || newHelmetsTableCount > oldHelmetsTableCount){


            Log.e("DB UPDATE FOUND", "DB update found");

            //the file in the assets folder is an update
            //erase the old database file and return the new one

            String oldDatabasePath = quizDatabase.getPath();
            quizDatabase.close();

            File oldDatabaseFile = new File(oldDatabasePath);

            if(oldDatabaseFile.exists())
                oldDatabaseFile.delete();

            return newDataBase;

        }
        else {

            //no updates found. Erase the new db file and return the existing one

            if(newDatabaseFile != null && newDatabaseFile.exists())
                newDatabaseFile.delete();

            return quizDatabase;
        }


    }




}
