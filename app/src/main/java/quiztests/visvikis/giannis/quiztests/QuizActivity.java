package quiztests.visvikis.giannis.quiztests;


import com.google.android.gms.ads.InterstitialAd;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class QuizActivity extends AppCompatActivity {
    // Remove the below line after defining your own ad unit ID.
    private static final String TOAST_TEXT = "Test ads are being shown. "
            + "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID.";

    private InterstitialAd mInterstitialAd;


    private final String INDEX_TAG = "INDEX_TAG";


    private final String QUIZ_DATABASE_NAME = "quiz_database.db";
    private final String TO_CHECK_QUIZ_DATABASE_NAME = "new_quiz_database.db";

    private final int QUIZ_LOADER_TASK_ID = 1;
    private final int AD_LOADER_TASK_ID = 2;
    private final int IMAGE_LOADER_TASK_ID = 3;

    private boolean quizStarted;

    //ArrayLists tags
    private final String CORRECT_ANSWERS_TAG = "CORRECT_ANSWERS";
    private final String FALSE_ANSWERS_1_TAG = "WRONG_ANSWERS_1";
    private final String FALSE_ANSWERS_2_TAG = "WRONG_ANSWERS_2";
    private final String FALSE_ANSWERS_3_TAG = "WRONG_ANSWERS_3";
    private final String QUESTIONS_TAG = "QUESTIONS";
    private final String INFO_LINKS_TAG = "INFO_LINKS";
    private final String FILE_PATHS_TAG = "FILE_PATHS";
    private final String HOW_MANY_CORRECTS_TAG = "HOW_MANY_CORRECTS_SO_FAR";

    private final String QUIZ_STARTED_TAG = "QUIZ_STARTED";

    private final String CORRECT_PLACE_TAG = "CORRECT_PLACE";

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


    private ImageView quizImage;
    private AppCompatTextView quizQuestion;
    private AppCompatTextView quizCounterTxt;
    private AppCompatButton answerButton1;
    private AppCompatButton answerButton2;
    private AppCompatButton answerButton3;
    private AppCompatButton answerButton4;

    private LinearLayout moreInfoLayout;

    private HashMap<String, AppCompatButton> correctAnswerPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_activity);


        correctAnswerPlace = new HashMap<>();


        //Get references to views
        ImageView quizImage = findViewById(R.id.ad_image_view);

        quizQuestion = findViewById(R.id.quiz_question_text);

        quizCounterTxt = findViewById(R.id.counter_textview);

        moreInfoLayout = findViewById(R.id.more_info);

        answerButton1 = findViewById(R.id.answer_button_1);

        answerButton2 = findViewById(R.id.answer_button_2);

        answerButton3 = findViewById(R.id.answer_button_3);

        answerButton4 = findViewById(R.id.answer_button_4);



        if(savedInstanceState != null){

            //retrieve state

            quizStarted = savedInstanceState.getBoolean(QUIZ_STARTED_TAG);

            questionIndex = savedInstanceState.getInt(INDEX_TAG);
            correctAnswers = savedInstanceState.getInt(HOW_MANY_CORRECTS_TAG);

            questionsList = savedInstanceState.getStringArrayList(QUESTIONS_TAG);
            filePathsList = savedInstanceState.getStringArrayList(FILE_PATHS_TAG);
            infoLinksList = savedInstanceState.getStringArrayList(INFO_LINKS_TAG);
            correctAnswersList = savedInstanceState.getStringArrayList(CORRECT_ANSWERS_TAG);
            falseAnswersList1 = savedInstanceState.getStringArrayList(FALSE_ANSWERS_1_TAG);
            falseAnswersList2 = savedInstanceState.getStringArrayList(FALSE_ANSWERS_2_TAG);
            falseAnswersList3 = savedInstanceState.getStringArrayList(FALSE_ANSWERS_3_TAG);

            setupTheQuiz();
        }
        else {

            quizStarted = false;
            questionIndex = 0;
            correctAnswers = 0;

            //CHECK IF THE DATABASE IS CREATED. IF YES CHECK IF THERE ARE ANY UPDATES PENDING AND LOAD THE QUIZ
            checkForUpdates();

        }



        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        loadInterstitial();

        if(!quizStarted)
            initiateNewQuiz();


        read and make a navigate back button

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

        outState.putBoolean(QUIZ_STARTED_TAG, quizStarted);

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

        getSupportLoaderManager().initLoader(AD_LOADER_TASK_ID, null,
                            new android.support.v4.app.LoaderManager.LoaderCallbacks<InterstitialAd>() {
                                @Override
                                public android.support.v4.content.Loader<InterstitialAd> onCreateLoader(int id, Bundle args) {
                                    return new InterstitialAdLoader(getApplicationContext());
                                }

                                @Override
                                public void onLoadFinished(android.support.v4.content.Loader<InterstitialAd> loader, InterstitialAd data) {
                                    mInterstitialAd = data;

                                }

                                @Override
                                public void onLoaderReset(android.support.v4.content.Loader<InterstitialAd> loader) {

                                }
                            });


    }


    /**
     *
     * Called after the database check is performed. Triggers the AsyncTaskLoader that accesses the database picking random questions
     *
     */
    private void initiateNewQuiz(){

        getSupportLoaderManager().initLoader(QUIZ_LOADER_TASK_ID, null,
                                new android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<QuizQuestion>>() {
                                    @Override
                                    public android.support.v4.content.Loader<ArrayList<QuizQuestion>> onCreateLoader(int id, Bundle args) {
                                        return new FeedTheQuizTaskLoader(getApplicationContext(), QUIZ_DATABASE_NAME);
                                    }

                                    @Override
                                    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<QuizQuestion>> loader, ArrayList<QuizQuestion> data) {
                                        quizStarted = true;


                                        for( QuizQuestion question : data){

                                            questionsList.add(question.getQuestion());
                                            filePathsList.add(question.getAssetPath());
                                            infoLinksList.add(question.getLink());
                                            correctAnswersList.add(question.getCorrectAnswer());
                                            falseAnswersList1.add(question.getWrongAnswer1());
                                            falseAnswersList2.add(question.getWrongAnswer2());
                                            falseAnswersList3.add(question.getWrongAnswer3());

                                            setupTheQuiz();
                                        }

                                    }

                                    @Override
                                    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<QuizQuestion>> loader) {

                                    }
                                });


    }



    /**
     * Either set the quiz up for the first question, or restore it after an orientation change to the prior ,
     * or setup the next question after an answer.
     * Depends on the value of questionIndex
     */
    private void setupTheQuiz() {

        AppCompatButton[] buttons = new AppCompatButton[]{answerButton1, answerButton2, answerButton3, answerButton4};

        //select a button at random, remember which was it and place the correct answer on it
        Random random = new Random();
        int correctAnswerIndex = random.nextInt(buttons.length);
        final AppCompatButton chosenButton = buttons[correctAnswerIndex];
        correctAnswerPlace.put(CORRECT_PLACE_TAG, chosenButton);
        chosenButton.setText(correctAnswersList.get(questionIndex));
        chosenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chosenButton.setBackgroundColor(Color.GREEN);
                correctAnswers ++;

                try{
                    Thread.sleep(2000);
                }
                catch (InterruptedException ie){
                    Log.e("INTERRUPTED_EXC", ie.getMessage());
                }

                if(questionIndex < totalQuestions - 1) {
                    questionIndex++;
                    setupTheQuiz();
                }
                else{
                    make a dialog for restart or quit
                }

            }
        });


        //put the false answers on the other buttons
        ArrayList<AppCompatButton> notChosenButtons = new ArrayList<>();
        ArrayList[] wrongAnswers = new ArrayList[]{falseAnswersList1, falseAnswersList2, falseAnswersList3};

        //collect all buttons that do not have the right answer
        for(AppCompatButton button : buttons){
            if (button != correctAnswerPlace.get(CORRECT_PLACE_TAG)) //if it is not the one that contains the correct answer
                notChosenButtons.add(button);
        }


        //not chosen buttons and wrongAnswers must have the same length
        for (int falseIndex=0; falseIndex<wrongAnswers.length; falseIndex++){

            final AppCompatButton notChosenButton = notChosenButtons.get(falseIndex);
            ArrayList<String> falseAnswersList = wrongAnswers[falseIndex];
            String falseAnswer = falseAnswersList.get(questionIndex);
            notChosenButton.setText(falseAnswer);

            notChosenButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notChosenButton.setBackgroundColor(Color.RED);
                    moreInfoLayout.setVisibility(View.VISIBLE);

                    if(questionIndex < totalQuestions - 1) {
                        make a button in order to procceed to the next question
                        increase the index and call the setup again
                    }
                    else {
                        make a dialog for restart or quit quiz
                    }
                }
            });
        }



        //setUp the Loader for the question Image
        getSupportLoaderManager().initLoader(IMAGE_LOADER_TASK_ID, null, new LoaderManager.LoaderCallbacks<Bitmap>() {
            @Override
            public Loader<Bitmap> onCreateLoader(int id, Bundle args) {

                String pathToImage = filePathsList.get(questionIndex);
                return new QuizImageLoader(getApplicationContext(), pathToImage);
            }

            @Override
            public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
                quizImage.setImageBitmap(data);
            }

            @Override
            public void onLoaderReset(Loader<Bitmap> loader) {

            }
        });



        //setup the question
        quizQuestion.setText(questionsList.get(questionIndex));

        //setup the counter over the image
        quizCounterTxt.setText(questionIndex + 1 + "/" + totalQuestions);

        //set up the link
        moreInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = infoLinksList.get(questionIndex);
                Intent moreInfoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(moreInfoIntent);
            }
        });



        //the quizIndex is incremented with every answer inside an onClickListener
    }





    /**
     * Temporarily copy the database file from the assets folder and check if any table holds more rows than the old one
     *
     * @return either the existing sqlite database or the updated one if exists
     *
     */
    private void checkForUpdates(){

        //access the old database (or create it for the first time)

        SQLiteDatabase quizDatabase = openOrCreateDatabase(QUIZ_DATABASE_NAME, MODE_PRIVATE, null);

        //temporarily copy the sqlite.db file from assets in the storage and check for any updates

        File newDatabaseFile = new File("/data/data/" + getPackageName() +"/databases/" + TO_CHECK_QUIZ_DATABASE_NAME);

        try{

            BufferedInputStream inputStream = new BufferedInputStream(getAssets().open("databases/the_quiz.db"));

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


        long oldDatabaseTables = DatabaseUtils.longForQuery(quizDatabase, "select count(name) from sqlite_master where type = ?", new String[]{"table"});


        long oldDriversTableCount, oldConstructorTableCount, oldCircuitsTableCount, oldFiguresTableCount,
                oldHelmetsTableCount, oldCarsTableCount;


        if(oldDatabaseTables > 1){ //there is always at least one upon creation

           //access the old database
           oldDriversTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from drivers_table;", null);
           oldConstructorTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from constructors_table;", null);
           oldCircuitsTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from circuits_table;", null);
           oldFiguresTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from figures_table;", null);
           oldCarsTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from cars_table;", null);
           oldHelmetsTableCount = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from helmets_table;", null);

        }
        else {

            oldDriversTableCount = 0;
            oldConstructorTableCount = 0;
            oldCircuitsTableCount = 0;
            oldFiguresTableCount = 0;
            oldHelmetsTableCount = 0;
            oldCarsTableCount = 0;
        }

        Log.e("OLD QUIZ DRIVERS", oldDriversTableCount + "");
        Log.e("OLD QUIZ CONSTRUCTORS", oldConstructorTableCount + "");
        Log.e("OLD QUIZ CIRCUITS", oldCircuitsTableCount + "");
        Log.e("OLD QUIZ FIGURES", oldFiguresTableCount + "");
        Log.e("OLD QUIZ HELMETS", oldHelmetsTableCount + "");
        Log.e("OLD QUIZ CARS", oldCarsTableCount + "");


        //access the new database that was created
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

            quizDatabase.close();

            String oldDatabasePath = quizDatabase.getPath();
            File oldDatabaseFile = new File(oldDatabasePath);

            if(oldDatabaseFile.exists()) {
                oldDatabaseFile.delete();
                Log.e("OLD_DATA", " Deleted " + oldDatabaseFile.getAbsolutePath());
                //rename the file to the former
                newDatabaseFile.renameTo(new File(oldDatabasePath));
                Log.e("NEW_DATA", "New data renamed to " + newDatabaseFile.getAbsolutePath());
            }

        }
        else {

            //no updates found. Erase the new db file and return the existing one
            Log.e("NO _DB_UPDATE", "No DB update found in assets");

            if(newDatabaseFile != null && newDatabaseFile.exists()) {
                newDatabaseFile.delete();
                Log.e("NEW_DATA", "New data deleted");
            }

        }
        

    }




}
