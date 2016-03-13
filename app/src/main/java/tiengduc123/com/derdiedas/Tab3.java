package tiengduc123.com.derdiedas;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import tiengduc123.com.derdiedas.R;

/**
 * Created by Dell on 1/2/2016.
 */
public class Tab3  extends Fragment {

    Button btnDer;
    Button btnDie;
    Button btnDas;
    ImageView imgLoa;
    //ProgressBar progressBar;
    TextView txtId;
    TextView txtWoerter;
    ArrayList<woeter> arrayWoerter;
    DatabaseHelper db;
    Random randomGenerator;
    int progressStatus = 1;
    int anworten;
    int richtigeAnworten = 0;
    int fascheAnworten = 0;
    int AnzahlFragen =0;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_3,container,false);
        btnDer = (Button) v.findViewById(R.id.btnDer);
        btnDie = (Button) v.findViewById(R.id.btnDie);
        btnDas = (Button) v.findViewById(R.id.btnDas);
        txtId = (TextView) v.findViewById(R.id.txtId);
        txtWoerter = (TextView) v.findViewById(R.id.txtWoerter);
        imgLoa = (ImageView) v.findViewById(R.id.imgLoa);

        db = new DatabaseHelper(getContext());
        arrayWoerter = db.getWordForGame();
        AnzahlFragen = arrayWoerter.size();

        anyItem();
        btnDer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check("der");
            }
        });
        btnDie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check("die");
            }
        });
        btnDas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check("das");
            }
        });
        imgLoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpeakWord(txtWoerter.getText().toString());
                /*Toast.makeText(getContext(),"Loa,dsdsdsds",Toast.LENGTH_SHORT).show();*/
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(this.getContext(),"resume 3",Toast.LENGTH_LONG).show();
    }

    public void SpeakWord(String str){
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();
        try {
            mp.setDataSource("http://tiengduc123.com/wp-content/plugins/dict-search/1.php?lang=de&word=" + str);
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void check(String artikel){

        txtId.setText(progressStatus + "/" + AnzahlFragen);
        woeter w = anyItem();
        if(w.artikel == artikel) {
            Toast.makeText(this.getContext(), "Richtig", Toast.LENGTH_SHORT).show();
            //txtWoerter.setText(arrayWoerter.get(anworten).artikel + " " + arrayWoerter.get(anworten).woeter);
            richtigeAnworten ++;
        }else {
            Toast.makeText(this.getContext(), "Falsch", Toast.LENGTH_SHORT).show();
            //txtWoerter.setText(arrayWoerter.get(anworten).artikel + " " + arrayWoerter.get(anworten).woeter);
            fascheAnworten ++;
        }

        if(arrayWoerter.size() == 2){
            Toast.makeText(getContext(),"Fertig gemacht, show Dialog", Toast.LENGTH_SHORT).show();
            return;
        }
        if(progressStatus == (AnzahlFragen-1)){
            progressStatus = 0;
        }
        txtWoerter.setText(arrayWoerter.get(progressStatus).woeter);
    }

    public woeter anyItem()
    {
        txtId.setText(progressStatus + "/" + AnzahlFragen);
        randomGenerator = new Random();
        int index = progressStatus;//randomGenerator.nextInt(arrayWoerter.size());
        txtWoerter.setText(arrayWoerter.get(index).woeter);
        woeter item = arrayWoerter.get(index);
        //arrayWoerter.remove(index);
        progressStatus++;
        return item;
    }
}
