

package quiztests.visvikis.giannis.quiztests;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


/**
 *
 * Created by ioannis on 23/02/2018.
 * Will be used to load the interstitial ads asynchronously
 *
 */




public class InterstitialAdLoader extends AsyncTaskLoader<InterstitialAd> {



    public InterstitialAdLoader(Context context) {
        super(context);



    }




    @Override
    public InterstitialAd loadInBackground(){



        return newInterstitialAd();
    }




    private InterstitialAd newInterstitialAd() {

        InterstitialAd interstitialAd = new InterstitialAd(getContext());
        interstitialAd.setAdUnitId(getContext().getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                Toast.makeText(getContext(), "Ad Loaded", Toast.LENGTH_SHORT).show();

            }


            @Override
            public void onAdFailedToLoad(int errorCode) {

                Toast.makeText(getContext(), "Add Failed to load", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAdClosed() {

                Toast.makeText(getContext(), "Ad was closed", Toast.LENGTH_LONG).show();
            }

        });


        AdRequest adRequest = new AdRequest.Builder()
                .setRequestAgent("android_studio:ad_template").build();
        interstitialAd.loadAd(adRequest);

        return interstitialAd;
    }

}
