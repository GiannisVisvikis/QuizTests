package quiztests.visvikis.giannis.quiztests;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.AsyncTaskLoader;

import java.io.IOException;

/**
 * Created by ioannis on 26/2/2018.
 */

public class QuizImageLoader extends AsyncTaskLoader<Drawable> {

    private String pathToImage;


    public QuizImageLoader(Context context, String pathToImage) {
        super(context);

        this.pathToImage = pathToImage;

    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }



    @Override
    public Drawable loadInBackground() {

        Bitmap result;

        try {
            result = BitmapFactory.decodeStream(getContext().getAssets().open(pathToImage));
        }
        catch (IOException io){
            result = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.quiz_unknown);
        }

        return new BitmapDrawable(getContext().getResources(), result);
    }



}
