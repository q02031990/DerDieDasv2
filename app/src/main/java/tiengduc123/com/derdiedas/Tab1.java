package tiengduc123.com.derdiedas;

/**
 * Created by Dell on 1/2/2016.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tiengduc123.com.derdiedas.R;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab1 extends Fragment {

    ListView lv;
    EditText tv;
    String keySearch;
    DatabaseHelper db;
    Button btnAddWoerter;

    ArrayList<woeter> _Cursor = null;

    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;


    // expandListView
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<woeter> listDataHeader;
    HashMap<woeter, List<String>> listDataChild;
    View v;
    //private String file_url = "http://tiengduc123.com/app/Data/data.php";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab_1,container,false);
        expListView = (ExpandableListView) v.findViewById(R.id.lvExp);
        tv = (EditText) v.findViewById(R.id.txtSearch);
        db = new DatabaseHelper(getContext());
        btnAddWoerter = (Button) v.findViewById(R.id.btnAddWoerter);

        new napDulieuLenListView().execute(keySearch);

        tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                keySearch = tv.getText().toString();
                new napDulieuLenListView().execute(keySearch);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

                // TODO Auto-generated method stub
            }

            boolean doubleBackToExitPressedOnce = false;

            @Override
            public void afterTextChanged(Editable s) {
                SystemClock.sleep(100);
                if (_Cursor.size() != 0) {
                    btnAddWoerter.setVisibility(View.INVISIBLE);
                    expListView.setVisibility(View.VISIBLE);
                } else {
                    expListView.setVisibility(View.INVISIBLE);
                    btnAddWoerter.setText("Add '" + keySearch + "' to your Database");
                    btnAddWoerter.setVisibility(View.VISIBLE);
                }
            }
        });

        btnAddWoerter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), addWoerter.class);
                keySearch = tv.getText().toString();
                it.putExtra("word", keySearch);
                getContext().startActivity(it);
            }
        });
        return v;
    }

    /*public String getKeySearch(){
        Bundle bu = this.getIntent().getExtras();
        String id = "-1";
        if (bu != null) {
            id = bu.getString("KeySearch");
            return id;
        }
        return "-1";
    }*/

    @Override
    public void onResume() {
        super.onResume();
        //Toast.makeText(this.getContext(),"resume 1",Toast.LENGTH_LONG).show();
    }

    class napDulieuLenListView extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            //request toi database de lay du lieu
            //_Cursor = db.searchWord(params[0]);

            if(params[0]==""){
                _Cursor = db.ShowHistorySearch();
            }else{
                _Cursor = db.searchWord(params[0]);
            }
            listDataHeader = new ArrayList<woeter>();//Root Node
            listDataChild = new HashMap<woeter, List<String>>();

            // Adding note data
            int i=0;
            for(woeter item : _Cursor){
                listDataHeader.add(item);
                List<String> childItem = new ArrayList<String>();
                childItem.add(item.woeter);
                listDataChild.put(listDataHeader.get(i), childItem); // Header, Child data
                i++;
            }
            return "" ;// doc noi dung url tra ve string
        }


        protected void onPostExecute(String s) {
            try {

                //expandeListView
                // get the listview
                //expListView = (ExpandableListView) v.findViewById(R.id.lvExp);

                // preparing list data
                // nap du lieu len list View
                //prepareListData();

                listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

                // setting list adapter
                expListView.setAdapter(listAdapter);

                // Listview Group click listener
                expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v,
                                                int groupPosition, long id) {
                        // Toast.makeText(getApplicationContext(),
                        // "Group Clicked " + listDataHeader.get(groupPosition),
                        // Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

                // Listview Group expanded listener
                expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                    @Override
                    public void onGroupExpand(final int groupPosition) {
                        final woeter w = _Cursor.get(groupPosition);
                        final DatabaseHelper db = new DatabaseHelper(getContext());


                        if (w.definition == null || w.definition == "") {
                            /*if (!isOnline()) {
                                Toast.makeText(getContext(), "To get definition this Word. You have to Connect to Internet", Toast.LENGTH_LONG).show();
                                return ;
                            }*/

                            new Thread(new Runnable() {
                                public void run() {
                                    w.definition = db.readFileFromInternet(w.woeter);
                                    listAdapter.setChildViewData(groupPosition, w); // sua list Adapter
                                    /*Context context = getContext();
                                    Tab2 _tab2 = new Tab2();
                                    _tab2.listAdapter.add(w);*/
                                    //listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);

                                    /*boolean resualt = */
                                    db.updateData(w.ID, w.artikel, w.woeter, w.woeter, w.definition,w.plural);
                                    /*if (resualt) {
                                        //Toast.makeText(getApplicationContext(), "You need to connect to the Internet", Toast.LENGTH_LONG).show();
                                    }*/
                                    LayoutInflater infalInflater = (LayoutInflater) getContext()
                                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                    View convertView = infalInflater.inflate(R.layout.list_item_mean_word, null);
                                    TextView definition = (TextView) convertView.findViewById(R.id.txtDefinition);
                                    definition.setText("" + w.definition);
                              }}).start();
                        }
                    }
                });

                // Listview Group collasped listener
                expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                    @Override
                    public void onGroupCollapse(int groupPosition) {
                        /*Toast.makeText(getContext(),
                                listDataHeader.get(groupPosition) + " Collapsed",
                                Toast.LENGTH_SHORT).show();*/

                    }
                });

                // Listview on child click listener
                expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v,
                                                int groupPosition, int childPosition, long id) {
                        // TODO Auto-generated method stub
                        /*Toast.makeText(
                                getContext(),
                                listDataHeader.get(groupPosition)
                                        + " : "
                                        + listDataChild.get(
                                        listDataHeader.get(groupPosition)).get(
                                        childPosition), Toast.LENGTH_SHORT)
                                .show();*/
                        return false;
                    }
                });
            }catch (Exception ex){

            }

        }
    }

    private boolean isOnline()
    {
        try
        {
            ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo().isConnectedOrConnecting();
        }
        catch (Exception e)
        {
            return false;
        }
    }

}