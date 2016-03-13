package tiengduc123.com.derdiedas;

/**
 * Created by Dell on 1/2/2016.
 */
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import tiengduc123.com.derdiedas.R;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<woeter> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<woeter, List<String>> _listDataChild;
    private HashMap<String, String> mMapStuff =new HashMap<String, String>();

    public ExpandableListAdapter(Context context, List<woeter> listDataHeader,
                                 HashMap<woeter, List<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    public void setChildViewData(int groupPosition, woeter value){
        _listDataHeader.set(groupPosition, value); // Header, Child data
    }

    public void add(woeter value){
        _listDataHeader.add(value);
        _listDataChild.put(value, null);
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        //final String childText = (String) getChild(groupPosition, childPosition);
        final LayoutInflater infalInflater = (LayoutInflater) this._context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = infalInflater.inflate(R.layout.list_item_mean_word, null);
        }

        final woeter p1 = _listDataHeader.get(groupPosition);
        TextView definition1 = (TextView) convertView.findViewById(R.id.txtDefinition);
        TextView txtChiaNomen = (TextView) convertView.findViewById(R.id.txtChiaNomen);

        //set definition
        definition1.setText(p1.definition);

        // luu id woerter va history_Search
        DatabaseHelper db = new DatabaseHelper(convertView.getContext());
        db.insertDataToHistory(p1.ID);

        String _nominativ = "";
        String _akkusativ= "";
        String _dativ= "";
        String _nominativ1 = "";
        String _akkusativ1= "";
        String _dativ1= "";
        if (p1.definition != null || p1.definition != ""){
                definition1.setText("" + p1.definition);
                if(p1.artikel == "der"){
                    _nominativ  = "<b>er</b>";
                    _akkusativ  = "<b>en</b>";
                    _dativ      = "<b>em</b>";
                    _nominativ1  = "<b>er</b>";
                    _akkusativ1  = "<b>en</b>";
                    _dativ1      = "<b>em</b>";
                }else if(p1.artikel == "die"){
                    _nominativ  = "<b>ie</b>";
                    _akkusativ  = "<b>ie</b>";
                    _dativ      = "<b>er</b>";
                    _nominativ1  = "<b>e</b>";
                    _akkusativ1  = "<b>e</b>";
                    _dativ1     = "<b>er</b>";
                }else{
                    _nominativ  = "<b>as</b>";
                    _akkusativ  = "<b>as</b>";
                    _dativ      = "<b>em</b>";
                    _nominativ1  = "<b>es</b>";
                    _akkusativ1  = "<b>es</b>";
                    _dativ1      = "<b>em</b>";
                }

                txtChiaNomen.setText(Html.fromHtml(
                                "<b style='color: green;'><u>1.Nominativ</u></b>" +
                                "<p>    d"+ _nominativ + " " + p1.woeter+ " | ein"+ _nominativ1 + " " + p1.woeter+ "</p>" +
                                "<b style='color:  blue;'><u>2.Akkusativ</u></b>" +
                                "<p>    d"+ _akkusativ + " " + p1.woeter+ " | ein"+ _akkusativ1 + " " + p1.woeter+ "</p>" +
                                "<b style='color:red ;'><u>3.Dativ</u></b>" +
                                "<p>    d"+ _dativ + " " + p1.woeter+ " | ein"+ _dativ1 + " " + p1.woeter+ "</p>"
                ));


            ImageView imgwiki = (ImageView)convertView.findViewById(R.id.imgwiki);
            imgwiki.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    _context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://de.wikipedia.org/wiki/" + p1.woeter)));
                }
            });

            ImageView imgGoogleTranslate = (ImageView)convertView.findViewById(R.id.imgGoogleTranslate);
            imgGoogleTranslate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    _context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://translate.google.com/#de/vi/" + p1.woeter)));
                }
            });

            ImageView imgeBingTranslate = (ImageView)convertView.findViewById(R.id.imgeGoogleImage);
            imgeBingTranslate.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    _context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.google.de/search?tbm=isch&q=" + p1.woeter)));
                }
            });

            ImageView imageDictcc = (ImageView)convertView.findViewById(R.id.imageDictcc);
            imageDictcc.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    _context.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("http://www.dict.cc/?s=" + p1.woeter)));
                }
            });

            ImageView imgLoangoai = (ImageView)convertView.findViewById(R.id.imgLoangoai);
            imgLoangoai.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    //load
                    SpeakWord(p1.artikel);
                    SpeakWord(p1.woeter);
                }
            });
        }

        // button click edit
        TextView tv = (TextView) convertView.findViewById(R.id.txtEdit);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(_context, addWoerter.class);
                it.putExtra("id", p1.ID);
                _context.startActivity(it);
            }
        });


        // button click edit
        TextView txtDelete = (TextView) convertView.findViewById(R.id.txtDelete);
        txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHelper db = new DatabaseHelper(_context);
                if(db.DeleteWord(p1.ID)){
                    Toast.makeText(_context,"This word '" + p1.woeter +"' have been deleted", Toast.LENGTH_SHORT).show();
                    Intent it = new Intent(_context, MainActivity.class);
                    _context.startActivity(it);
                }
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1 /*this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size()*/;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        //String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }
        //nap du lieu
        woeter p = _listDataHeader.get(groupPosition);

        if (p != null) {
            // Anh xa + Gan gia tri
            TextView txtArtikel = (TextView) convertView.findViewById(R.id.txtArtikel);
            txtArtikel.setText(groupPosition + 1 + "." + p.artikel);

            TextView txtwoeter = (TextView) convertView.findViewById(R.id.txtWoeter);
            if(p.woeter.length() >15){
                txtwoeter.setTextSize(20);
            }
            txtwoeter.setText(" " + p.woeter + " ");
            TextView txtplural = (TextView) convertView.findViewById(R.id.txtPlural);
            txtplural.setText(p.plural);
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
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
        //mp.stop();
    }
}