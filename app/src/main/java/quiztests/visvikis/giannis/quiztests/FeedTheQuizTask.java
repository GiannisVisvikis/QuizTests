package quiztests.visvikis.giannis.quiztests;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by giannis on 21/2/2018.
 */



public class FeedTheQuizTask extends AsyncTaskLoader<ArrayList<QuizQuestion>> {


    private Context context;
    private String quizDatabaseName;



    public FeedTheQuizTask(Context context, String quizDatabaseName) {
        super(context);

        this.context = context;
        this.quizDatabaseName = quizDatabaseName;
    }



    /*

        IMPORTANT

        Make sure you have checked there is a database installed in the app before calling this loader

     */



    @Override
    public ArrayList<QuizQuestion> loadInBackground() {


        ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();

        SQLiteDatabase quizDatabase = context.openOrCreateDatabase(quizDatabaseName, Context.MODE_PRIVATE, null);

        //get two questions about helmets
        Cursor helmetsCursor = getRandomEntries(quizDatabase, "helmets_table", 2);
        addQuestions(quizQuestions, helmetsCursor);

        //get two questions about figures
        Cursor figuresCursor = getRandomEntries(quizDatabase, "figures_table", 2);
        addQuestions(quizQuestions, figuresCursor);


        //get wwo questions about cars
        Cursor carsCursor = getRandomEntries(quizDatabase, "cas_table", 2);
        procceed

        return quizQuestions;

    }



    /**
     *
     * Query the selected table_name in the quizDatabase and get howMany discrete rows in a Cursor object
     *
     * @param quizDatabase
     * @param tableName
     * @param howMany
     * @return a Cursor object containing howMany random discrete rows of results. No duplicates.
     */
    private Cursor getRandomEntries(SQLiteDatabase quizDatabase, String tableName, int howMany){

        HashMap<Integer, Integer> trackSelected = new HashMap<>();
        Random random = new Random();


        long allEntries = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from " + tableName + ";", null);

        String selectionRange = "(";

        int counter = 0;
        int randomRow;

        while (counter < howMany){

            randomRow = 1 + random.nextInt( (int) allEntries );

            while (trackSelected.get(randomRow) != null){ //don't get duplicate rows, retry until you get a not selected row
                //System.out.println( randomRow + " is a duplicate");
                randomRow = 1 + random.nextInt( (int) allEntries );
            }

            trackSelected.put(randomRow, 1);

            //System.out.println(randomRow + " selected");

            counter++;

            selectionRange += randomRow + ",";
        }

        String finalSelection = selectionRange.substring(0, selectionRange.length() - 1) + ");"; //get rid of the last comma and close parenthesis

        return quizDatabase.rawQuery("select link, photo_path, question, answer, false_1, false_2, false_3 from " + tableName + " where id in " + finalSelection, null);

    }


    /**
     *
     * Transform each Cursor row to a QuizQuestion object and add it to the results of the Task
     *
     * @param result
     * @param dataResults
     */
    private void addQuestions(ArrayList<QuizQuestion> result, Cursor dataResults){

        if(dataResults.moveToFirst()){

            do{

                String link = dataResults.getString(0);
                String assetPath = dataResults.getString(1);
                String question = dataResults.getString(2);
                String correct = dataResults.getString(3);
                String false1 = dataResults.getString(4);
                String false2 = dataResults.getString(5);
                String false3 = dataResults.getString(6);

                QuizQuestion quizQuestion = new QuizQuestion(link, assetPath, question, correct, false1, false2, false3);

                result.add(quizQuestion);

            }while (dataResults.moveToNext());


        }


    }


}
