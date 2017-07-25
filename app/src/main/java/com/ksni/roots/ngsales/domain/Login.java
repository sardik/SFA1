package com.ksni.roots.ngsales.domain;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Random;

import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.R;
import com.ksni.roots.ngsales.model.Settings;
import com.ksni.roots.ngsales.util.FontsOverride;
import com.ksni.roots.ngsales.util.Helper;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends Activity {
    //public static final     String BASE_URL         = "http://10.1.200.21/request";
    //public static final     String BASE_URL         = "http://10.1.50.126/sfa/public/request";
    //public static final     String BASE_URL         = "http://10.1.50.166/sfa/public/request";
    private ProgressDialog progressDialog;
    private EditText tUser;
    private EditText tPassword;

    private String token;
    private String company;
    private String barcode;
    private String multi_dist;
    private String sls;
    private int sls_type;
    private String sls_name;
    private int branch;
    private String zone;

    private CheckBox cbTrial;
    public static String baseUrl = MainActivity.BASE_URL;

    private GradientBackgroundPainter gradientBackgroundPainter;
    private BackgroundPainter backgroundPainter;

    private void loadSettings(){
        Settings settingan = new Settings(this);
        settingan.loadInfo();

        baseUrl = settingan.server;

        SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
        SharedPreferences.Editor e = session.edit();

        e.putString("CUR_SERVER", settingan.server);
        e.putString("CUR_USER", settingan.last_login);
        e.putString("CUR_COMPANY", settingan.company);
        e.putString("CUR_BARCODE_NUMBER", settingan.barcode_number);
        e.putString("CUR_MULTI_DIST", settingan.multi_dist);
        e.putString("CUR_SLS", settingan.salesman);
        e.putString("CUR_SLS_NAME", settingan.salesman_name);
        e.putInt("CUR_SLS_TYPE", settingan.salesman_type);

        e.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        super.onCreate(savedInstanceState);

        //try{
          //  DBManager dm= DBManager.getInstance(getApplicationContext());
            //dm.database().execSQL("ALTER TABLE settings ADD COLUMN barcode_number TEXT");
        //} catch(Exception x){Log.e("settings",x.toString());}

        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Titillium-Light.otf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/Titillium-Light.otf");
        FontsOverride.setDefaultFont(this, "SANS", "fonts/Titillium-Bold.otf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/Titillium-Bold.otf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Titillium-Bold.otf");

        //DBManager dm =  DBManager.getInstance(getApplicationContext());
        //Settings.init(Login.this,"","","");
        //dm.database().execSQL("update settings set is_login=1,WORK_START='10:10'");

        if (Settings.isLogin(this)){
            loadSettings();

            startActivity(new Intent(Login.this, MainActivity.class));
            finish();
        }else {

            setContentView(R.layout.ui_login);

            cbTrial = (CheckBox) findViewById(R.id.checkBoxTrial);

            // ---------- Perubahan 2 warna ------------------- //
//            final View targetView = findViewById(R.id.bg_test);
//
//            BackgroundPainter backgroundPainter = new BackgroundPainter();
//            int color1 = getApplicationContext().getColor(R.color.color1);
//            int color2 = getApplicationContext().getColor(R.color.color2);
//            backgroundPainter.animate(targetView, color1, color2);
            // ---------- End Perubahan 2 warna ------------------- //

            // ---------- Perubahan 2 warna gradien ------------------- //
            final View backgroundImage = findViewById(R.id.bg_test);

            final int[] drawables = new int[4];
            drawables[0] = R.drawable.bg_login_gradien_1;
            drawables[1] = R.drawable.bg_login_gradien_2;
            drawables[2] = R.drawable.bg_login_gradien_3;
            drawables[3] = R.drawable.bg_login_gradien_4;
            gradientBackgroundPainter = new GradientBackgroundPainter(backgroundImage, drawables);
            gradientBackgroundPainter.start();
            // ---------- Perubahan 2 warna gradien ------------------- //


            tUser = (EditText) findViewById(R.id.tUser);
            tPassword = (EditText) findViewById(R.id.tPassword);

            final Button btn = (Button) findViewById(R.id.btnLogin);
            btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (tUser.getText().toString().length() == 0 || tPassword.getText().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), Helper.getStrResource(Login.this,R.string.login_blank), Toast.LENGTH_SHORT).show();
                    } else {
                        if (Settings.isLastLogin(getApplicationContext(), tUser.getText().toString()))
                            doLogin(tUser.getText().toString(), tPassword.getText().toString());
                        else
                            Toast.makeText(getApplicationContext(), Helper.getStrResource(Login.this,R.string.login_switch_restrict), Toast.LENGTH_LONG).show();
                    }


                }
            });


            tPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        btn.performClick();
                    }
                    return false;
                }
            });
        }
    }

    private void doLogin(final String user,String password){

        InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(tPassword.getWindowToken(), 0);
        //keyboard.showSoftInput(tPassword, 0);

        new  AsyncTask<String, Void, String>(){
            @Override
            protected void onPreExecute() {

                if (cbTrial.isChecked()) {
                    baseUrl = MainActivity.BASE_URL_TRIAL;
                } else {
                    baseUrl = MainActivity.BASE_URL_PRODUCTION;
                }

                progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("Loading...");
                progressDialog.setIndeterminate(false);
                progressDialog.setCancelable(false);
                progressDialog.show();

            }

            @Override
            protected String doInBackground(String... params) {
                String result = "0";
                try {
                    URL url = new URL (baseUrl);
                    Log.e("URL", baseUrl);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setUseCaches(false);
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setConnectTimeout(15 * 1000);
                    urlConnection.setReadTimeout(15 * 1000);
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("charset", "utf-8");
                    urlConnection.setRequestMethod("POST");

                    DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());

                    //Buat Object
                    JSONObject jsonReady = new JSONObject();
                    jsonReady.put("command","login");
                    jsonReady.put("imei", Helper.getIMEI(Login.this));
                    jsonReady.put("username",params[0]);
                    jsonReady.put("password", params[1]);

                    wr.writeBytes("data=" + jsonReady.toString());
                    Log.e("JSLOG",jsonReady.toString());

                    wr.flush();
                    wr.close();

                    int statusCode = urlConnection.getResponseCode();

                    if (statusCode == 200) {

                        BufferedReader r = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = r.readLine()) != null) {
                            response.append(line);
                        }

                        JSONObject res = new JSONObject(response.toString());
                        token = res.optString("token");
                        company = res.optString("company_id");
                        multi_dist = res.optString("multi_dist");
                        sls = res.optString("salesman_id");
                        sls_name = res.optString("fullname");
                        branch = res.optInt("branch_id");
                        zone = res.optString("zone_id");
                        sls_type = res.optInt("salesman_team_id");

                        if(res.optString("barcode_number")!=null)
                            barcode =  res.optString("barcode_number");
                        else
                            barcode =  "";

//                        Log.e("Branch",String.valueOf(branch));
//                        Log.e("Barcode Number",res.optString("barcode_number"));
//                        Log.e("Zone",res.optString("zone_id"));
//                        Log.e("Sales Type",String.valueOf(sls_type));

                        if(sls_type==1)// 1 = to, 2 = canvas
                            sls_type = 0;
                        else
                            sls_type = 1;

                        if (res.optString("success").equals("true")) {
                            SharedPreferences session = getApplicationContext().getSharedPreferences("ngsales", 0);
                            SharedPreferences.Editor e = session.edit();
                            e.putString("CUR_COMPANY", company);
                            e.putInt("CUR_BRANCH", branch);
                            e.putString("CUR_ZONE", zone);
                            e.putString("CUR_SLS", sls);
                            e.putString("CUR_USER", params[0]);
                            e.putString("CUR_TOKEN", token);
                            e.putString("CUR_SLS_NAME", sls_name.toUpperCase());
                            e.putInt("CUR_SLS_TYPE", sls_type);
                            e.commit();
                            result = "1";
                        }else{
                            if (res.optString("error").trim().length()>0) {
                                result = res.optString("error");
                            }
                        }
                    }
                } catch (MalformedURLException e) {
                    Log.e("MalformedURLException", e.toString());
                    result = "99";
                } catch (SocketException e) {
                    Log.e("SocketException", e.toString());
                    result = "99";
                } catch (SocketTimeoutException e) {
                    Log.e("SocketTimeoutException", e.toString());
                    result = "99";
                } catch (ProtocolException e) {
                    Log.e("ProtocolException", e.toString());
                    result = "99";
                } catch (IOException e) {
                    Log.e("IOException", e.toString());
                    result = "99";
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                    result = "99";
                } catch (Exception e) {
                    Log.e("Exception", e.toString());
                    result = "99";
                }

                return result;
            }

            @Override
            protected void onPostExecute(String result) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    gradientBackgroundPainter.stop();
                }

                if (result.equals("1")) {
                    Settings.login(Login.this, tUser.getText().toString().toLowerCase(), baseUrl, sls, sls_name, company, branch, zone, sls_type, multi_dist, barcode);
                    Intent in = new Intent(Login.this, MainActivity.class);
                    //in.putExtra("BASE_URL", baseUrl);
                    startActivity(in);
                    finish();
                } else if (result.equals("99")) {
                    //Save ke db table LogError
                    Toast.makeText(getApplicationContext(),Helper.getStrResource(Login.this,R.string.connection_error), Toast.LENGTH_SHORT).show();
                } else if (result.equals("0")) {
                    Toast.makeText(getApplicationContext(),Helper.getStrResource(Login.this,R.string.login_invalid), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();
                }

            }


        }.execute(user,password);

    }

    public class BackgroundPainter {

        private static final int MIN = 800;
        private static final int MAX = 1500;

        private final Random random;

        public BackgroundPainter() {
            random = new Random();
        }

        public void animate(@NonNull final View target, @ColorInt final int color1,
                            @ColorInt final int color2) {

            final ValueAnimator valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), color1, color2);

            valueAnimator.setDuration(randInt(MIN, MAX));

            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override public void onAnimationUpdate(ValueAnimator animation) {
                    target.setBackgroundColor((int) animation.getAnimatedValue());
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) {
                    //reverse animation
                    animate(target, color2, color1);
                }
            });

            valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
            valueAnimator.start();
        }

        private int randInt(int min, int max) {
            return random.nextInt((max - min) + 1) + min;
        }
    }

    public class GradientBackgroundPainter {

        private static final int MIN = 4000;
        private static final int MAX = 5000;

        private final Random random;
        private final android.os.Handler handler;
        private final View target;
        private final int[] drawables;
        private final Context context;

        public GradientBackgroundPainter(@NonNull View target, int[] drawables) {
            this.target = target;
            this.drawables = drawables;
            random = new Random();
            handler = new android.os.Handler();
            context = target.getContext().getApplicationContext();
        }

        private void animate(final int firstDrawable, int secondDrawable, final int duration) {
            if (secondDrawable >= drawables.length) {
                secondDrawable = 0;
            }
            final Drawable first = ContextCompat.getDrawable(context, drawables[firstDrawable]);
            final Drawable second = ContextCompat.getDrawable(context, drawables[secondDrawable]);

            final TransitionDrawable transitionDrawable =
                    new TransitionDrawable(new Drawable[] { first, second });

            target.setBackgroundDrawable(transitionDrawable);

            transitionDrawable.setCrossFadeEnabled(false);

            transitionDrawable.startTransition(duration);

            final int localSecondDrawable = secondDrawable;
            handler.postDelayed(new Runnable() {
                @Override public void run() {
                    animate(localSecondDrawable, localSecondDrawable + 1, randInt(MIN, MAX));
                }
            }, duration);
        }

        public void start() {
            final int duration = randInt(MIN, MAX);
            animate(0, 1, duration);
        }

        public void stop() {
            handler.removeCallbacksAndMessages(null);
        }

        private int randInt(int min, int max) {
            return random.nextInt((max - min) + 1) + min;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        gradientBackgroundPainter.stop();
    }
}

