package quiztests.visvikis.giannis.quiztests;

import android.app.Dialog;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


/**
 * Created by ioannis on 27/2/2018.
 */

public class QuizDialog extends DialogFragment {


    private String correctAnswers;

    private QuizCommunication communication;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);

        Bundle args = getArguments();

        correctAnswers = args.getString(QuizCommunication.QUIZ_CORRECT_ANSWERS);

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        communication = (QuizCommunication) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.quiz_dialog, container, false);

        Dialog dialog = getDialog();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        AppCompatTextView correctAnswersTxtView = view.findViewById(R.id.quiz_result_txt);
        correctAnswersTxtView.setText(correctAnswers + " " + getString(R.string.correct));


        AppCompatButton quitButton = view.findViewById(R.id.quiz_quit);
        quitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communication.quitTheQuiz();
                QuizDialog.this.dismiss();
            }
        });

        AppCompatButton restartButton = view.findViewById(R.id.quiz_restart);
        restartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent restartQuizIntent = new Intent( getActivity(), QuizActivity.class );

                communication.quitTheQuiz();

                QuizDialog.this.dismiss();

                startActivity(restartQuizIntent);
            }
        });


        return view;
    }



    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();

        DisplayMetrics lDisplayMetrics = getResources().getDisplayMetrics();
        int widthPixels = lDisplayMetrics.widthPixels;
        int heightPixels = lDisplayMetrics.heightPixels;

        int newWidth, newHeight;


        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){

            newWidth =  widthPixels/2;
            newHeight = 7 * (heightPixels/10);
        }
        else{

            newWidth = 8 * (widthPixels/10);
            newHeight =  heightPixels/2;

        }

        dialog.getWindow().setLayout(newWidth, newHeight);

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        communication = null;
    }


}
