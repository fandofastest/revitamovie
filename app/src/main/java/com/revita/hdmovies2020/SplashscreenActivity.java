package com.revita.hdmovies2020;

import android.Manifest;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.ixidev.gdpr.GDPRChecker;
import com.revita.hdmovies2020.revita_utl.ApiResources;
import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;
import com.startapp.android.publish.adsCommon.adListeners.AdDisplayListener;

import org.json.JSONException;
import org.json.JSONObject;

import static com.revita.hdmovies2020.revita_utl.MyAppClass.getContext;


public class SplashscreenActivity extends AppCompatActivity {

    private int SPLASH_TIME = 2000;

    private final String TAG = MainActivity.class.getSimpleName();
    InterstitialAd fanInterstitialAd;
    private com.google.android.gms.ads.InterstitialAd mInterstitialAd;
    ProgressBar progressBar;

    LinearLayout progresly;
    Button button;
    private String fanInterid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_splashscreen);
        progresly=findViewById(R.id.llProgressBar);

        button=findViewById(R.id.startbut);
        progressBar=findViewById(R.id.progressbar1);


        getAdDetails(new ApiResources().getAdDetails());
        getStatusapp(new ApiResources().getInfoApp());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE}, 2909);
            } else {
            }
        } else {
        }

        //Toast.makeText(SplashscreenActivity.this, "login:"+ isLogedIn(), Toast.LENGTH_SHORT).show();
//        Thread timer = new Thread() {
//            public void run() {
//                try {
//                    sleep(SPLASH_TIME);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    if (isLogedIn()) {
//                        startActivity(new Intent(SplashscreenActivity.this,MainActivity.class));
//                        finish();
//                    } else {
//                        if (!Constants.IS_LOGIN_MANDATORY) {
//                            Intent intent = new Intent(SplashscreenActivity.this,MainActivity.class);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                            startActivity(intent);
//
//                        } else {
//                            startActivity(new Intent(SplashscreenActivity.this,LoginActivity.class));
//                        }
//                        finish();
//                    }
//
//                }
//            }
//        };
//        timer.start();

    }

    public boolean isLogedIn() {
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);
        return preferences.getBoolean("status", false);

    }

    public void loadinter (String inter){

        fanInterstitialAd = new InterstitialAd(this,inter);

        fanInterstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {

                progresly.setVisibility(View.GONE);
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                progresly.setVisibility(View.GONE);

                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");

                Intent intent = new Intent(SplashscreenActivity.this,MainActivity.class);
                startActivity(intent);
            }

            @Override
            public void onError(Ad ad, AdError adError) {

                loadinteradmob(ApiResources.adMobInterstitialId);


                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                progresly.setVisibility(View.GONE);

                fanInterstitialAd.show();



                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad

            }

            @Override
            public void onAdClicked(Ad ad) {
                progresly.setVisibility(View.GONE);

                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        });

        fanInterstitialAd.loadAd();
    }


    private void getAdDetails(String url){



        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject jsonObject=response.getJSONObject("startapp");
                    ApiResources.startappid = jsonObject.getString("startappid");
                    ApiResources.startappstatus = jsonObject.getString("startappstatus");

                    StartAppSDK.init(getContext(), ApiResources.startappid, true);
                    StartAppSDK.setUserConsent (getContext(),
                            "pas",
                            System.currentTimeMillis(),
                            true);
                    StartAppAd.disableSplash();

                } catch (JSONException e) {
                    Log.e("json", "ERROR");


                    e.printStackTrace();
                }


                try {
                    JSONObject jsonObject=response.getJSONObject("admob");

                    ApiResources.admobstatus = jsonObject.getString("status");
                    ApiResources.adMobBannerId = jsonObject.getString("admob_banner_ads_id");
                    ApiResources.adMobInterstitialId = jsonObject.getString("admob_interstitial_ads_id");
                    ApiResources.adMobPublisherId = jsonObject.getString("admob_publisher_id");

//                    interadmob=jsonObject.getString("admob_interstitial_ads_id");

//                    Toast.makeText(getContext(),"coba"+interadmob,Toast.LENGTH_LONG).show();

                    new GDPRChecker()
                            .withContext(SplashscreenActivity.this)
                            .withPrivacyUrl(Config.TERMS_URL) // your privacy url
                            .withPublisherIds(ApiResources.adMobPublisherId) // your admob account Publisher id
                            .withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
                            .check();




                } catch (JSONException e) {
                    Log.e("json", "ERROR");

                    e.printStackTrace();
                }


                try {
                    JSONObject jsonObject=response.getJSONObject("fan");

                    ApiResources.fanadStatus = jsonObject.getString("status");
                    ApiResources.fanBannerid = jsonObject.getString("fan_banner");
                    fanInterid = jsonObject.getString("fan_inters");

//                    Toast.makeText(getContext(),ApiResources.fanadStatus+ApiResources.fanBannerid+ApiResources.fanInterid , Toast.LENGTH_LONG).show();
//
//
                    if (!fanInterid.equals("")){
                        button.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadinter(fanInterid);
                                progresly.setVisibility(View.VISIBLE);
                            }
                        });
                    }

//                    else{
//
//                        Intent intent = new Intent(getContext(),MainActivity.class);
//                        startActivity(intent);
//
//
//                    }











                } catch (JSONException e) {
                    Log.e("json", "ERROR");
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", String.valueOf(error));


            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);





    }


    public void loadinteradmob(String inter){

        mInterstitialAd = new com.google.android.gms.ads.InterstitialAd(this);
        mInterstitialAd.setAdUnitId(inter);
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                progresly.setVisibility(View.GONE);

                mInterstitialAd.show();

                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                StartAppAd startAppAd = new StartAppAd(getContext());
                startAppAd.showAd(new AdDisplayListener() {
                    @Override
                    public void adHidden(com.startapp.android.publish.adsCommon.Ad ad) {
                        progresly.setVisibility(View.GONE);

                        Intent intent = new Intent(SplashscreenActivity.this,MainActivity.class);

                        startActivity(intent);

                    }

                    @Override
                    public void adDisplayed(com.startapp.android.publish.adsCommon.Ad ad) {

                    }

                    @Override
                    public void adClicked(com.startapp.android.publish.adsCommon.Ad ad) {
                        progresly.setVisibility(View.GONE);

                        Intent intent = new Intent(SplashscreenActivity.this,MainActivity.class);

                        startActivity(intent);

                    }

                    @Override
                    public void adNotDisplayed(com.startapp.android.publish.adsCommon.Ad ad) {

                        progresly.setVisibility(View.GONE);

                        Intent intent = new Intent(SplashscreenActivity.this,MainActivity.class);

                        startActivity(intent);

                    }
                })   ;


//                final Handler   handler = new Handler();
//
//                final Runnable r = new Runnable() {
//                    public void run() {
//                        StartAppAd.showAd(getContext());
//
//                        layutprogressbar.setVisibility(View.GONE);
//
//                        Intent intent = new Intent(SplashscreenActivity.this,MainActivity.class);
//
//                        startActivity(intent);
//                    }
//                };
//
//                handler.postDelayed(r, 3000);



                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                progresly.setVisibility(View.GONE);

                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                progresly.setVisibility(View.GONE);


                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                progresly.setVisibility(View.GONE);

                Intent intent = new Intent(SplashscreenActivity.this,MainActivity.class);

                startActivity(intent);

                // Code to be executed when the interstitial ad is closed.
            }
        });
    }


    private void getStatusapp(String url){

        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {


                try {
                    JSONObject jsonObject=response.getJSONObject("statusapp");

                    ApiResources.statusapp = jsonObject.getString("status");
                    String apk = jsonObject.getString("apk");

                    if (ApiResources.statusapp.equals("0")){

                        Intent intent=new Intent(getContext(), UpdateActivity.class);
                        intent.putExtra("apk",apk);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);


                        Toast.makeText(getApplicationContext(),"APPNOTFOUND" , Toast.LENGTH_LONG).show();



                    }

                    JSONObject jsonObject1=response.getJSONObject("notifapp");

                    ApiResources.statusnotif = jsonObject1.getString("statusnotif");
                    ApiResources.judulstatus = jsonObject1.getString("judulstatus");
                    ApiResources.pesan = jsonObject1.getString("pesan");
                    ApiResources.foto = jsonObject1.getString("foto");
                    ApiResources.icon = jsonObject1.getString("icon");
                    ApiResources.apknew = jsonObject1.getString("apk");





                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        Volley.newRequestQueue(getContext()).add(jsonObjectRequest);


    }




}
