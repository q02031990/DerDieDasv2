package tiengduc123.com.derdiedas;

/**
 * Created by Dell on 1/2/2016.
 */
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tiengduc123.com.derdiedas.R;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab2 extends Fragment {

    ListView lv;
    EditText tv;
    String keySearch;
    DatabaseHelper db;

    // expandListView
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<woeter> listDataHeader;
    HashMap<woeter, List<String>> listDataChild;
    View v;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.tab_2,container,false);
        expListView = (ExpandableListView) v.findViewById(R.id.lvExpTab2);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        //new napDulieuLenListView().execute(keySearch);
        onResume();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
                mSwipeRefreshLayout.setRefreshing(false);
            }

            private void refreshContent() {
                new napDulieuLenListView().execute(keySearch);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();


        db = new DatabaseHelper(getContext());
        new napDulieuLenListView().execute(keySearch);
        //Toast.makeText(this.getContext(),"Swipe to redload History",Toast.LENGTH_LONG).show();
    }

    class napDulieuLenListView extends AsyncTask<String, Integer, String> {

        ArrayList<woeter> _Cursor;
        @Override
        protected String doInBackground(String... params) {
            //request toi database de lay du lieu
            _Cursor = db.ShowHistorySearch();

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
                            if (!isOnline()) {
                                Toast.makeText(getContext(), "To get definition this Word. You have to Connect to Internet", Toast.LENGTH_LONG).show();
                                return ;
                            }

                            new Thread(new Runnable() {
                                public void run() {
                                    w.definition = db.readFileFromInternet(w.woeter);
                                    listAdapter.setChildViewData(groupPosition, w); // sua list Adapter
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
                        Toast.makeText(
                                getContext(),
                                listDataHeader.get(groupPosition)
                                        + " : "
                                        + listDataChild.get(
                                        listDataHeader.get(groupPosition)).get(
                                        childPosition), Toast.LENGTH_SHORT)
                                .show();
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