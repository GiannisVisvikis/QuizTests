package quiztests.visvikis.giannis.quiztests;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

/**
 * Created by ioannis on 26/2/2018.
 */

public class QuizImageLoader extends AsyncTaskLoader<Bitmap> {

    private String pathToImage;


    public QuizImageLoader(Context context, String pathToImage) {
        super(context);

        this.pathToImage = pathToImage;

    }



    @Override
    public Bitmap loadInBackground() {

        Bitmap result;

        try {
            result = BitmapFactory.decodeStream(getContext().getAssets().open(pathToImage));
        }
        catch (IOException io){
            result = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.quiz_unknown);
        }

        return result;
    }



}
