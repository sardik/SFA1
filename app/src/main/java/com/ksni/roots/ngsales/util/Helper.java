package com.ksni.roots.ngsales.util;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ksni.roots.ngsales.EnerlifeWebRequest;
import com.ksni.roots.ngsales.MainActivity;
import com.ksni.roots.ngsales.NgantriInformation;
import com.ksni.roots.ngsales.R;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by #roots on 21/08/2015.
 */



public class Helper {

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return s;
        }
        return out;
    }
    public static String strRepeat(int length,String kal,char karakter){
        int rep = length - kal.length();
        String buff = "";
        for(int i=0;i<rep;i++){
            buff +=karakter;
        }

        return buff+kal;
    }

    public static Date getDateByStr(String strDate){
        SimpleDateFormat tgl = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return tgl.parse(strDate);
        }catch(Exception ex){
            return null;
        }
    }

    public static String getFormatDate(Date tgl,String format){
        SimpleDateFormat so8601Format = new SimpleDateFormat(format);
        return so8601Format.format(tgl);
    }

    public static String getDDMMYYYYFromMysqlFormat(String tgl,String separator){
        //yyyy-MM-dd
        return tgl.substring(8,10)+separator+
               tgl.substring(5,7)+separator+
               tgl.substring(0,4);
    }


    public static String getDayByDate(String strDate){

        String ret = "";
        Date tanggal = null;

        SimpleDateFormat tgl = new SimpleDateFormat("yyyy-MM-dd");

        try {
            tanggal = tgl.parse(strDate);
        }catch(Exception ex){

        }

        Calendar c = Calendar.getInstance();
        c.setTime(tanggal);

        int result = c.get(Calendar.DAY_OF_WEEK);
        switch (result) {
            case Calendar.MONDAY:ret    = "(Sen)";break;
            case Calendar.TUESDAY:ret   = "(Sel)";break;
            case Calendar.WEDNESDAY:ret = "(Rab)";break;
            case Calendar.THURSDAY:ret  = "(Kam)";break;
            case Calendar.FRIDAY:ret    = "(Jum)";break;
            case Calendar.SATURDAY:ret  = "(Sab)";break;
            case Calendar.SUNDAY:ret    = "(Mgg)";break;
        }

        return ret;
    }

    public static double getDouble2Digit(double value){
        String strVal = String.format("%.2f", value);
        return Double.parseDouble(strVal.replace(",","."));
    }

    public static String getStrTime(String val){
        String buff ="";
        if (val!=null){
            if(val.trim().length()>0){
                buff = val.substring(11,19);
            }
        }
        return buff;
    }

    public static void notifyQueue(Context ctx){
        boolean isActiveQueue = NgantriInformation.isExistActive(ctx.getApplicationContext());
        //Log.e("ActiveQueque", "is : " + isActiveQueue); //DELSOON
        boolean isRunningService = NgantriInformation.isServiceRunning(EnerlifeWebRequest.class, ctx.getApplicationContext());
        //Log.e("RunningService", "is : " + isRunningService); //DELSOON

        if((!isActiveQueue && !isRunningService )) { // if not false
            Intent msgIntent = new Intent(ctx.getApplicationContext(), EnerlifeWebRequest.class);
            msgIntent.putExtra("init", "1");
            ctx.startService(msgIntent);
            //Log.e("CHECK_RELEASE","CHECK_RELEASE");
        }else if(isActiveQueue && !isRunningService) { // if false dan true
            Intent msgIntent = new Intent(ctx.getApplicationContext(), EnerlifeWebRequest.class);
            msgIntent.putExtra("init", "1");
            msgIntent.putExtra("forceActive", "1");
            ctx.startService(msgIntent);
            //Log.e("FORCE_ACTIVE","FORCE_ACTIVE");
        }else if(isActiveQueue && isRunningService) { //Updated By Obbie 6 Mei 2017

        }
    }


    public static float getBatteryLevel(Context ctx) {
        Intent batteryIntent = ctx.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);


        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

    public static boolean isServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public static int rotate(File ff) {
        try
        {

            ExifInterface exif = new ExifInterface(ff.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
            { return 270; }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
            { return 180; }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
            { return 90; }
            return 0;

        } catch (FileNotFoundException e)
        {
            //e.printStackTrace();
        } catch (IOException e)
        {
            //e.printStackTrace();
        }
        return 0;
    }

    public static Bitmap rotateImage(Bitmap bmp,float degree){
        Bitmap takenImage = bmp;

        Matrix m = new Matrix();
        m.postRotate( degree );

        takenImage = Bitmap.createBitmap(takenImage,
                0, 0, takenImage.getWidth(), takenImage.getHeight(),
                m, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        takenImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return takenImage;
        //byte[] byteFormat = stream.toByteArray();

    }

    public static void rotateImage(String fileName,float degree){
        Bitmap takenImage = BitmapFactory.decodeFile(fileName);

        File f = new File(fileName);

        Matrix m = new Matrix();
        //m.postRotate( Helper.rotate(f) );
        m.postRotate( degree );

        takenImage = Bitmap.createBitmap(takenImage,
                0, 0, takenImage.getWidth(), takenImage.getHeight(),
                m, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        takenImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();


        OutputStream fOut=null;
        try {
            fOut = new BufferedOutputStream(new FileOutputStream(fileName));
            fOut.write(byteFormat);
            fOut.flush();
            fOut.close();

        } catch (Exception e) {
            Log.e("xx",e.toString());
        }

    }


    public static void resizePhoto(String fileName,int width,int height){


        Bitmap bMap= BitmapFactory.decodeFile(fileName);
        Bitmap bitmap = Bitmap.createScaledBitmap(bMap, width, height, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] byteFormat = stream.toByteArray();


        OutputStream fOut=null;
        try {
            fOut = new BufferedOutputStream(new FileOutputStream(fileName));
            fOut.write(byteFormat);
            fOut.flush();
            fOut.close();

        } catch (Exception e) {
                Log.e("xx",e.toString());
        }

/*
        File f = new File(fileName);
        Matrix m = new Matrix();
        m.postRotate( rotate(f) );

        bitmap = Bitmap.createBitmap(bitmap,
                0, 0, bitmap.getWidth(), bitmap.getHeight(),
                m, true);
*/


    }


    public static Bitmap getStrResizePhoto(Bitmap bMap,int width,int height){
        Bitmap bitmap = Bitmap.createScaledBitmap(bMap, width, height, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        return bitmap;
    }

    public static String getStrResizePhotoBase64(Bitmap bMap,int width,int height){

        Bitmap bitmap = Bitmap.createScaledBitmap(bMap, width, height, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;

    }

    public static String getStrResizePhotoBase64(Bitmap bMap,int width,int height,Bitmap.CompressFormat cf,int quality){

        Bitmap bitmap = Bitmap.createScaledBitmap(bMap, width, height, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(cf, quality, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;

    }


    public static String getStrResizePhotoBase64(String fileName,int width,int height){

        Bitmap bMap= BitmapFactory.decodeFile(fileName);
        Bitmap bitmap = Bitmap.createScaledBitmap(bMap, width, height, false);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;

    }

    public  static void showToast(Context ctx,String msg){
        Toast t = Toast.makeText(ctx, msg, Toast.LENGTH_LONG);
        //TextView tt = (TextView)t.getView().findViewById(android.R.id.message);
        //tt.setShadowLayer(0,0,0, Color.parseColor("#BB000000"));
        //t.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        t.show();

    }
    public static String getEnvironment(Context ctx, String key,String defaultValue){
        SharedPreferences session = ctx.getSharedPreferences("ngsales", 0);
        SharedPreferences.Editor e = session.edit();
        return session.getString("key",defaultValue);
    }

    public static void setEnvironment(Context ctx, String key,String value){
        SharedPreferences session = ctx.getSharedPreferences("ngsales", 0);
        SharedPreferences.Editor e = session.edit();
        e.putString(key, value);
        e.commit();
    }


    public static boolean isNullEditText(EditText t){
        if (t.getText().toString().length()==0)
             return true;
        else
            return false;
    }

    public static boolean isEmpty(String t){
        if (t == null){
            return true;
        }else if (t.length()==0){
            return true;
        }
        return false;

    }

    public static String getExternalPath(){
        File direct = new File(Environment.getExternalStorageDirectory() + "/Enerlife");
        if(!direct.exists()) {
            direct.mkdir();
        }
        return direct.getPath();

    }

    public static String getCreateExternalPath(String path){
        File direct = new File(Environment.getExternalStorageDirectory() + "/"+path);
        if(!direct.exists()) {
            direct.mkdir();
        }
        return direct.getPath();

    }
    public static File getOutputMediaPhotoFileCall(String outlet){
        File mediaStorageDir;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Enerlife");
        } else {
            mediaStorageDir = Environment.getDownloadCacheDirectory();
        }

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Enerlife", "failed to create directory");
                return null;
            }
        }
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + java.io.File.separator + outlet+"_" + getCurrentDateTime("yyyyMMdd") + ".jpg");

        return mediaFile;
    }

    public static File getOutputMediaPhotoFile(){
            File mediaStorageDir;

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Enerlife");
            } else {
              mediaStorageDir = Environment.getDownloadCacheDirectory();
            }

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Enerlife", "failed to create directory");
                    return null;
                }
            }

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + java.io.File.separator + "IMG_" + timeStamp + ".jpg");

            return mediaFile;
        }



    public static String getEncodeImageBitmap(Bitmap bm){
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 4;
        options.inPurgeable = true;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.JPEG,40,baos);

        byte [] byteImage_photo = baos.toByteArray();
        //ba1 = Base64.encodeBytes(byteImage_photo);

        String encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
        return encodedImage;

    }

    public static String getEncodeImageOutlet(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    public static void deleteFile(String path){
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
        }catch(Exception x){

        }
    }

    public static Bitmap getDecodeImage(String data){
        byte[] byteFormat = Base64.decode(data, Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteFormat, 0, byteFormat.length);
        return bitmap;
    }

    public static String getEncodeImageOutlet(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;

    }

    public static String getEncodeImage(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        //Bitmap bitmap = BitmapFactory.decodeFile(path);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 40, stream);
        byte[] byteFormat = stream.toByteArray();
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
        /*BitmapFactory.Options options = new BitmapFactory.Options();

        options.inSampleSize = 4;
        options.inPurgeable = true;
        Bitmap bm = BitmapFactory.decodeFile(path,options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        bm.compress(Bitmap.CompressFormat.PNG,0,baos);

        byte [] byteImage_photo = baos.toByteArray();
        //ba1 = Base64.encodeBytes(byteImage_photo);


        String encodedImage = Base64.encodeToString(byteImage_photo, Base64.DEFAULT);
        return encodedImage;
        */

    }

    private boolean haveNetworkConnection(Context ctx) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
  public static boolean isOnline(Context ctx){
      ConnectivityManager cm =
              (ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

      NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
      boolean isConnected = activeNetwork != null &&
              activeNetwork.isConnectedOrConnecting();
      return isConnected;
  }


    public static long getTimeElapsedMinute(String tglAwal,String tglAkhir){
        SimpleDateFormat tgl = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long selisih = 0;
        try {
            Date awal = tgl.parse(tglAwal);
            Date akhir = tgl.parse(tglAkhir);
            selisih = (akhir.getTime()-awal.getTime()) / ( 1000 );

            Log.e("selisih",String.valueOf(akhir.getTime()-awal.getTime()));

        }catch(Exception e){
            Log.e("getTimeElapsedMinute",e.toString());
        }
        return selisih;
    }

    public static boolean isValidDate(String serverDate){
        return getCurrentDate().equals(serverDate);
    }

    public static String getIMEI(Context ctx){
         TelephonyManager telephoneMgr = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephoneMgr!=null) {
            if (telephoneMgr.getDeviceId() != null)
                return telephoneMgr.getDeviceId();
            else
                return "";
        }
        else
            return "";
    }
    public static void msgbox(Context ctx, String message,String title) {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ctx);
        dlgAlert.setTitle(title);
        dlgAlert.setMessage(message);
        dlgAlert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //finish();
            }
        });
        dlgAlert.setCancelable(true);
        dlgAlert.create().show();
    }

    public static String getBulat(int num){
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        format.setGroupingUsed(false);
        return format.format(num);
    }

    public static String getBulat(double num){
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        format.setGroupingUsed(false);
        return format.format(num);
    }

    public static String getFormatCurrency(double num){
        DecimalFormat formatted = new DecimalFormat("#,###,###,###,##0");
        return  formatted.format(num);
    }

    public static String getFormatCurrencyWithDigit(double num){
        DecimalFormat formatted = new DecimalFormat("#,###,###,###,##0.00");
        return  formatted.format(num);
    }


    public static String getStrDateFromDate(int year,int month,int day){

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date tanggal = cal.getTime();

        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        String cur = iso8601Format.format(tanggal);
        return cur;
    }


    public static String getStrDateFromDate(Date tgl){
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        String cur = iso8601Format.format(tgl);
        return cur;
    }

    public static String getCurrentDateTime(){
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String cur = iso8601Format.format(new Date());
        return cur;
    }

    public static String getCurrentDateTime(String format){
        SimpleDateFormat iso8601Format = new SimpleDateFormat(format);
        String cur = iso8601Format.format(new Date());
        return cur;
    }
    public static String getCurrentDate(){
        SimpleDateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd");
        String cur = iso8601Format.format(new Date());
        return cur;
    }

    public static boolean isNumber(String num){
            try {
                Integer.parseInt(num);
                return true;
            } catch (NumberFormatException nfe) {}
            return false;
    }

    public static int toInt(String num){
        if (isNumber((num)))
            return Integer.parseInt(num);
        else
            return  0;
    }

    public static String getInfoTime(String tglAwal,String tglAkhir,long dur){
        String buff = "";
        SimpleDateFormat tgl = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long selisih = 0;
        try {
            Date awal = tgl.parse(tglAwal);
            Date akhir = tgl.parse(tglAkhir);
            selisih =    (akhir.getTime()-awal.getTime()) / ( 1000 );
            //selisih = selisih - (dur  );


            long jam = selisih / 3600;
            long menit = (selisih % 3600) / 60;
            long detik = ((selisih % 3600)) % 60;


            if (jam>0) buff = String.valueOf(jam) + "h ";
            if (menit>0) buff += String.valueOf(menit) + "m ";
            if (detik>0) buff += String.valueOf(detik) + "s";


        }catch(Exception ex){}


            return buff;

    }

    public static String getNullString(String val){
        if (val==null)
            return "";
        else
            return val;
    }

    public static String getInfoTimeFromSecond(long selisih){
        String buff = "";

        try {
            long jam = selisih / 3600;
            long menit = (selisih % 3600) / 60;
            long detik = ((selisih % 3600)) % 60;

            if (jam>0) buff = String.valueOf(jam) + "h ";
            if (menit>0) buff += String.valueOf(menit) + "m ";
            if (detik>0) buff += String.valueOf(detik) + "s";


        }catch(Exception ex){}


        return buff;

    }

    public static String getStrResource(Context ctx,int id){
        return ctx.getResources().getString(id);
    }

}


