package tiengduc123.com.derdiedas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import tiengduc123.com.derdiedas.R;
// Add this to the header of your file:
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;

public class MainActivity extends ActionBarActivity {

    private int notificationId = 1;
    private NotificationManager notificationManager;



    // Declaring Your View and Variable
    int pageSelected =0;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence Titles[]={"Normen","History","Game" };
    //String[] Titles = getResources().getStringArray(R.array.tabs);
    int Numboftabs =3;

    // File url to download
    private String file_url = "http://tiengduc123.com/app/Data/data.php";
    private String DB_PATH = "/data/data/tiengduc123.com.derdiedas/databases/Derdiedas.db";
    private String URL_share = "https://play.google.com/store/apps/details?id=tiengduc123.com.derdiedas";

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;

    final static private String PREF_KEY_SHORTCUT_ADDED = "PREF_KEY_SHORTCUT_ADDED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Creating The Toolbar and setting it as the Toolbar for the activity
        FacebookSdk.sdkInitialize(getApplicationContext());
        // Initialize the SDK before executing any other operations,
        // especially, if you're using Facebook UI elements.

        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();

        //creatNotification(Calendar.getInstance().getTimeInMillis(),"Co thong bao moi");
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());

        //Create ShortcutIcon
        createShortcutIcon();

        if(!db.checkDBExit()){
            if(!isOnline()){
                Toast.makeText(getApplicationContext(),"Need connect to the Internet", Toast.LENGTH_LONG).show();
                return;
            }
            //Toast.makeText(getApplicationContext(),"Please wait, Dictionary have been downloaded ...", Toast.LENGTH_SHORT).show();
            new DownloadFileFromURL().execute(file_url + "?p=" + getCurrentPhone());
        }

            // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
            adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);

            // Assigning ViewPager View and setting the adapter
            pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(adapter);

            // Assiging the Sliding Tab Layout View
            tabs = (SlidingTabLayout) findViewById(R.id.tabs);
            tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

            // Setting Custom Color for the Scroll bar indicator of the Tab View
            tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
                @Override
                public int getIndicatorColor(int position) {
                    return getResources().getColor(R.color.tabsScrollColor);
                }
            });
        /*tabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

            // Setting the ViewPager For the SlidingTabsLayout
            tabs.setViewPager(pager);

            ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener(){
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    pageSelected = position;
                    if(pageSelected == 1){

//                        Tab1 tab1 = new Tab1();
//                        tab1.tv.clearFocus();
                        //tab2.onResume();
                        //Toast.makeText(getApplicationContext(),"Hide keybroad", Toast.LENGTH_SHORT).show();
                    }
                }
            };
            tabs.setOnPageChangeListener(pageChangeListener);
            pager.setCurrentItem(pageSelected);
            pager.setAdapter(adapter); //Set your FragmentPagerAdapter
    }

    public String getCurrentPhone(){
        TelephonyManager TM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        // IMEI No.
                String imeiNo = "";//TM.getDeviceId();

        // IMSI No.
                String imsiNo = "";//TM.getSubscriberId();

        // SIM Serial No.
                String simSerialNo  = "";//TM.getSimSerialNumber();
        String str = imeiNo + "|" + imsiNo + "|" + simSerialNo;
        return str;
    }
    private boolean isOnline()
    {
        try
        {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);//Menu Resource, Menu
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.MenuUpdateDatabase:
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                //Toast.makeText(getApplicationContext(),"Yes Clicked", Toast.LENGTH_LONG).show();
                                new DownloadFileFromURL().execute(file_url);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                //Toast.makeText(getApplicationContext(),"No", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Do You Want to Update Database. Your words will be deleted. You cann't restore").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;


            case R.id.MenuShare:
                shareForFriend();
                /*Toast.makeText(getApplicationContext(),"This is MenuShare", Toast.LENGTH_LONG).show();*/
                break;

            case R.id.MenuUpdate:
                Toast.makeText(getApplicationContext(), "The version is newest", Toast.LENGTH_LONG).show();
                break;

            case R.id.MenuAddWord:
                // chuyen man hinh
                Intent it = new Intent(MainActivity.this, addWoerter.class);
                startActivity(it);
                break;

            case R.id.MenuAbout:
                AlertDialog.Builder builder_MenuAbout = new AlertDialog.Builder(this);
                builder_MenuAbout.setTitle("About Us");
                builder_MenuAbout.setMessage(Html.fromHtml("<p>See more at, \n <a href=\"http://Tiengduc123.com\">Http://www.Tiengduc123.com</a>.</p>"));
                AlertDialog alert = builder_MenuAbout.create();
                alert.show();
        }

        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();
        }
        return false;
    }

    private void creatNotification(long when, String Data){

        //khơi tạo biến quản lý notification
        String serName= Context.NOTIFICATION_SERVICE;
        notificationManager= (NotificationManager)getSystemService(serName);
        //khởi tạo các icon, thông báo  và thời gian để hiển thị cùng.
        int icon = R.drawable.icon;
        String tickerText= "Có thông báo mới";
        //long when = System.currentTimeMillis();
        Notification notification= new Notification(icon, tickerText, when);
        String title= "android.vn";
        String text= "@thanhlong90.it: click vào đây để xem chi tiết ^^!";
        //Thiết lập xem chi tiết thông báo
        Intent intent= new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("extendedText", text);
        intent.putExtra("extendedTitle", title);
        PendingIntent launchIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        //notification(getApplicationContext(), title, text, launchIntent);
        //kích hoạt notification
        notificationId= 1;
        notificationManager.notify(notificationId, notification);
        /*String notiContent = "Noi dung thong bao";
        String notiTitle = "Thong bao moi";

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),R.drawable.icon);
        int smallIcon = R.drawable.icon;

        String NotiData = "Noi dung 2" + Data;
        Intent it = new Intent(getApplicationContext(), MainActivity.class);
        it.putExtra("NotiData",NotiData );

        it.setData(Uri.parse("content://" + when));
        PendingIntent pIt = PendingIntent.getActivity(getApplicationContext(), 0, it, Intent.FLAG_ACTIVITY_NEW_TASK);
        NotificationManager notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        builder = new NotificationCompat.Builder((getApplicationContext()));
        builder.setWhen(when)
                .setContentText(notiContent)
                .setContentTitle(notiTitle)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true)
                .setTicker(notiTitle)
                .setLargeIcon(largeIcon)
                .setDefaults(Notification.DEFAULT_LIGHTS |
                            Notification.DEFAULT_SOUND |
                                Notification.DEFAULT_VIBRATE
                            )
                .setContentIntent(pIt);
        Notification notification = builder.build();

        notiManager.notify((int)when, notification);


*/
    }

    private void createShortcutIcon() {

        // Checking if ShortCut was already added
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        boolean shortCutWasAlreadyAdded = sharedPreferences.getBoolean(PREF_KEY_SHORTCUT_ADDED, false);
        if (shortCutWasAlreadyAdded) return;

        Intent shortcutIntent = new Intent(getApplicationContext(), MainActivity.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Der Die Das Suchen");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(), R.mipmap.ic_launcher));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);

        // Remembering that ShortCut was already added
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_KEY_SHORTCUT_ADDED, true);
        editor.commit();
    }

    public void shareForFriend() {
        String message = "Der Die Das Suchen.<br />" + URL_share;
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(share, "Share to your Friend"));
    }

    /**
     * Showing Dialog
     * */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                OutputStream output = new FileOutputStream(DB_PATH);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }
            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            //ChuyenManHinh();
        }

    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();

        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}