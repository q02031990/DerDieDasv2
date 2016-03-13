package tiengduc123.com.derdiedas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import tiengduc123.com.derdiedas.R;

public class addWoerter extends AppCompatActivity {

    Button btn;
    TextView txtArtikel;
    TextView txtWoeter;
    EditText txtDefinition;
    TextView txtPlural;
    TextView txtID;
    DatabaseHelper db;
    Button btnBack;
    String id = "-1";
    String word = null;
    Toolbar toolbar;

    private static char[] SOURCE_CHARACTERS = { 'Ä', 'ä', 'Ö', 'ö', 'Ü', 'ü',
            'ß' };

    // Mang cac ky tu thay the khong dau
    private static char[] DESTINATION_CHARACTERS = { 'a', 'a', 'o', 'o', 'u',
            'u', 's' };

    /**
     * Bo dau 1 ky tu
     *
     * @param ch
     * @return
     */
    public static char removeAccent(char ch) {
        int index = Arrays.binarySearch(SOURCE_CHARACTERS, ch);
        if (index >= 0) {
            ch = DESTINATION_CHARACTERS[index];
        }
        return ch;
    }

    /**
     * Bo dau 1 chuoi
     *
     * @param s
     * @return
     */
    public static String removeAccent(String s) {
        StringBuilder sb = new StringBuilder(s);
        for (int i = 0; i < sb.length(); i++) {
            sb.setCharAt(i, removeAccent(sb.charAt(i)));
        }
        return sb.toString();
    }

    public String getID(){
        Bundle bu = getIntent().getExtras();
        String id = "-1";
        if (bu != null) {
             id = bu.getString("id");
            return id;
        }
        return "-1";
    }

    public String getWord(){
        Bundle bu = getIntent().getExtras();
        if (bu != null) {
            word = bu.getString("word");
        }
        return word;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_woerter);

        //hien thi nut back tren header
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtArtikel = (TextView) findViewById(R.id.txtArtikel);
        txtWoeter = (TextView) findViewById(R.id.txtWoeter);
        txtDefinition = (EditText) findViewById(R.id.txtDefinition);
        txtID = (TextView) findViewById(R.id.txtID);
        txtPlural = (TextView) findViewById(R.id.txtPlural);

        btn = (Button) findViewById(R.id.btnAddWord);

        db = new DatabaseHelper(this);
        id = getID();
        word = getWord();
        if(word != null){
            txtWoeter.setText(word);
        }else{
            new NapDuLieuLenLayout().execute(id);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (id == "-1" || id == null) {// tao moi du lieu

                    try {

                        String artikel = txtArtikel.getText().toString().trim();
                        String Woeter = toTitleCase(txtWoeter.getText().toString()).trim();
                        String definition = txtDefinition.getText().toString().trim();
                        String plural = txtPlural.getText().toString().trim();
                        String idArtikel = "1";

                        if(artikel== ""){
                            Toast.makeText(getApplicationContext(),"What is Artikel of the Noun", Toast.LENGTH_SHORT);
                            return;
                        }

                        /*if (artikel=="der") {
                            idArtikel = "1";
                        } else if (artikel == "die") {
                            idArtikel = "2";
                        } else if(artikel =="das") {
                            idArtikel = "4";
                        }
*/
                        switch(artikel){
                            case "der" :
                                idArtikel ="1";
                                break;
                            case "die" :
                                idArtikel ="2";
                                break;
                            case "das" :
                                idArtikel ="4";
                                break;
                        }


                        String Woeter_suchen = removeAccent(txtWoeter.getText().toString());
                        if (db.insertData(idArtikel, Woeter, Woeter_suchen, definition,plural)) {
                            Toast.makeText(getApplicationContext(), "added your Word", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(addWoerter.this, MainActivity.class);
                            startActivity(it);
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Could not add this Word", Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                } else {// neu ko thi sua lai du lieu da co

                    try {

                        String artikel = txtArtikel.getText().toString();
                        String Woeter = toTitleCase(txtWoeter.getText().toString());
                        String definition = txtDefinition.getText().toString();
                        String plural = txtPlural.getText().toString();

                        if (artikel.equalsIgnoreCase("der")) {
                            artikel = "1";
                        } else if (artikel.equalsIgnoreCase("die")) {
                            artikel = "2";
                        } else {
                            artikel = "4";
                        }


                        String Woeter_suchen = removeAccent(txtWoeter.getText().toString());
                        if (db.updateData(id, artikel, Woeter, Woeter_suchen, definition, plural)) {
                            Toast.makeText(getApplicationContext(), "saved your Word", Toast.LENGTH_SHORT).show();
                            Intent it = new Intent(addWoerter.this, MainActivity.class);
                            startActivity(it);
                            return;
                        }
                        Toast.makeText(getApplicationContext(), "Could not save this Word", Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
                }


            }
        });

        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent it = new Intent(addWoerter.this, MainActivity.class);
        startActivity(it);
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
                                //new DownloadFileFromURL().execute(file_url);
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
                //shareForFriend();
                /*Toast.makeText(getApplicationContext(),"This is MenuShare", Toast.LENGTH_LONG).show();*/
                break;

            case R.id.MenuUpdate:
                Toast.makeText(getApplicationContext(), "The version is newest", Toast.LENGTH_LONG).show();
                break;

            case R.id.MenuAddWord:
                // chuyen man hinh
                Intent it = new Intent(this, addWoerter.class);
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

    class NapDuLieuLenLayout extends AsyncTask<String, Integer, String> {

        woeter a;
        @Override
        protected String doInBackground(String... params) {

            //request toi database de lay du lieu
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            a = db.searchWordByID(params[0]);
            return "" ;// doc noi dung url tra ve string
        }


        protected void onPostExecute(String s) {
            try {
                txtArtikel.setText(a.artikel);
                txtWoeter.setText(a.woeter);
                txtDefinition.setText(a.definition);
                txtID.setText(a.ID);
                txtPlural.setText(a.plural);

                btn.setText("Save to Database");

                txtDefinition.requestFocus();
                txtDefinition.setSelection(txtDefinition.getText().length());

            }catch (Exception ex){

            }
        }
    }

    public static String toTitleCase(String givenString) {
        String[] arr = givenString.split(" ");
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < arr.length; i++) {
            sb.append(Character.toUpperCase(arr[i].charAt(0)))
                    .append(arr[i].substring(1)).append(" ");
        }
        return sb.toString().trim();
    }
}
