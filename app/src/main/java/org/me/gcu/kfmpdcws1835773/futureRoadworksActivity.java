// Kieran Francois, S1835773, MPD CW TRI B 2021/22
//pretty much the current roadworks activity class with functionality to search for the date
package org.me.gcu.kfmpdcws1835773;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ExpandableListView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class futureRoadworksActivity extends AppCompatActivity implements View.OnClickListener {


    private Date startDate;
    private Date endDate;
    private Date searchDate;
    private DatePickerDialog.OnDateSetListener dateListener;
    private TextView dateSelection;
    private SearchView searchView;
    private String dateSearch;
    private String roadSearch;
    private String urlSource;
    private String kfString;
    private String kfTask;
    private String kfMain;

    LinkedList <futureRoadworksClass> List = null;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listHead;
    HashMap<String, List<String>> listChild;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_roadworks);

        roadSearch = "";
        dateSearch = "";
        urlSource = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";

        List  = new LinkedList<futureRoadworksClass>();

        searchView = (SearchView)findViewById(R.id.roadSearch);
        dateSelection = (TextView)findViewById(R.id.dateSearch);
        expListView = (ExpandableListView) findViewById(R.id.ExpandableList);

        listHead = new ArrayList<String>();
        listChild = new HashMap<String, List<String>>();

        prepData();

        listAdapter = new ExpandableListAdapter(this, listHead, listChild);
        expListView.setAdapter(listAdapter);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                roadSearch = query;
                startProgress();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                if(newText.length() == 0)
                {
                    roadSearch = newText;
                    startProgress();
                }
                return false;
            }
        });

        dateSelection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Calendar calender = Calendar.getInstance();
                int year = calender.get(Calendar.YEAR);
                int month = calender.get(Calendar.MONTH);
                int day = calender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(futureRoadworksActivity.this,
                        android.R.style.Theme_Holo,
                        dateListener,
                        year,
                        month,
                        day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }

        });


        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                month = month + 1;

                dateSelection.setText(String.format("%02d", dayOfMonth) + " " + GetMonthByNumber(month, true) + " " + year);

                dateSearch = String.format("%02d", dayOfMonth) + " " + GetMonthByNumber(month, true) + " " + year;

                String tempDate = String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month) + "/" + year;
                try {
                    searchDate =new SimpleDateFormat("dd/mm/yyyy").parse(tempDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startProgress();
            }
        };
    }

//found this in an online resource
    private String GetMonthByNumber(int monthNumber, boolean monthSlang)
    {
        String month;

        if (monthNumber == 1)
        {
            if(monthSlang)
                return "Jan";
            else
                return "January";
        }
        else if (monthNumber == 2)
        {
            if(monthSlang)
                return "Feb";
            else
                return "February";
        }
        else if (monthNumber == 3)
        {
            if(monthSlang)
                return "Mar";
            else
                return "March";
        }
        else if (monthNumber == 4)
        {
            if(monthSlang)
                return "Apr";
            else
                return "April";
        }
        else if (monthNumber == 5)
        {
            return "May";
        }
        else if (monthNumber == 6)
        {
            if(monthSlang)
                return "Jun";
            else
                return "June";
        }
        else if (monthNumber == 7)
        {
            if(monthSlang)
                return "Jul";
            else
                return "July";
        }
        else if (monthNumber == 8)
        {
            if(monthSlang)
                return "Aug";
            else
                return "August";
        }
        else if (monthNumber == 9)
        {
            if(monthSlang)
                return "Sep";
            else
                return "September";
        }
        else if (monthNumber == 10)
        {
            if(monthSlang)
                return "Oct";
            else
                return "October";
        }
        else if (monthNumber == 11)
        {
            if(monthSlang)
                return "Nov";
            else
                return "November";
        }
        else if (monthNumber == 12)
        {
            if(monthSlang)
                return "Dec";
            else
                return "December";
        }
        return "Error";
    }

    private void prepData() {
        if(List != null)
        {
            listHead.clear();
            listChild.clear();

            for (int i = 0; i < List.size(); i++)
            {
                listHead.add(List.get(i).getTitle());

                List<String> listItems = new ArrayList<String>();
                kfString = List.get(i).getDescription();
                kfString.replaceAll("<br />", " ");
                Log.e("Description", kfString);
                listItems.add(List.get(i).getDescription());
                listItems.add(List.get(i).getPubDate());

                listChild.put(listHead.get(i), listItems);
                expListView.setAdapter(listAdapter);
            }
        }
        expListView.setAdapter(listAdapter);
    }

//get the dates from the description for use when searching
    private boolean CheckDescDate(String desc)
    {
        if(desc.contains("Start Date"))
        {
            String startDate = desc.substring(desc.indexOf(","), desc.indexOf(">"));
            String temp1= desc.substring(desc.indexOf(">"));
            String endDate = temp1.substring(temp1.indexOf(","));
            String tempStartDate = "";
            String tempEndDate = "";
            for(int iCount = 8; iCount < startDate.length(); iCount++)
            {
                if(startDate.charAt(iCount) == ' ')
                {
                    tempStartDate = startDate.substring(2, 8) + " " + startDate.charAt(iCount + 1) + startDate.charAt(iCount + 2) + startDate.charAt(iCount + 3) + startDate.charAt(iCount + 4);
                    break;
                }
            }
            for(int iCount = 8; iCount < endDate.length(); iCount++)
            {
                if(endDate.charAt(iCount) == ' ')
                {
                    tempEndDate = endDate.substring(2, 8) + " " + endDate.charAt(iCount + 1) + endDate.charAt(iCount + 2) + endDate.charAt(iCount + 3) + endDate.charAt(iCount + 4);
                    break;
                }
            }
            try {
                SimpleDateFormat formatter=new SimpleDateFormat("dd MMM yyyy");
                this.startDate = formatter.parse(tempStartDate);
                this.endDate = formatter.parse(tempEndDate);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
            if(searchDate != null)
            {
                //Check if the selected date by the user is contained in the range
                if ((searchDate.compareTo(this.startDate) >= 0) && (searchDate.compareTo(this.endDate) <= 0)) {
                    Log.e("Dates", "TRUE");
                    return true;
                } else {
                    Log.e("Dates", "FALSE");
                    return false;
                }
            }
        }
        return false;
    }

//I only put this in to trigger the on click listener and now nothing happens until you click. but I am sure worse code has been written...
    public void onClick(View aview) { startProgress(); }

//create start progress method that will be used throughout
    public void startProgress()
    {
        kfTask = "";
        new Thread(new Task(urlSource)).start();
    }

    private class Task implements Runnable
    {
        private String url;
        public Task(String aurl)
        {
            url = aurl;
        }
        @Override
        public void run()
        {
            URL aurl;
            URLConnection yc;
            BufferedReader in = null;
            String inputLine = "";
            try
            {
                aurl = new URL(url);
                yc = aurl.openConnection();
                in = new BufferedReader(new InputStreamReader(yc.getInputStream()));

                while ((inputLine = in.readLine()) != null)
                {
                    kfTask = kfTask + inputLine;
                }
                in.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            futureRoadworksActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    kfMain = kfTask;
                    if(List != null)
                        List.clear();
                    List = parseDataPlannedWorks(kfMain);
                    prepData();
                }
            });
        }
    }

//Pull parser derived from the PullParser3 lab
    private LinkedList<futureRoadworksClass> parseDataPlannedWorks(String dataToParse)
    {
        futureRoadworksClass Widget = null;
        boolean bStart = false;
        boolean bSkip = false;

        try
        {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput( new StringReader( dataToParse ) );
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT)
            {
                switch(eventType)
                {
                    case XmlPullParser.START_TAG:

                        // Check which Tag we have
                        if (xpp.getName().equalsIgnoreCase("channel"))
                        {
                            List  = new LinkedList<futureRoadworksClass>();
                        }
                        else if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            Widget = new futureRoadworksClass();
                            bStart = true;
                        }
                        else if(bStart)
                        {
                            if (xpp.getName().equalsIgnoreCase("title"))
                            {
                                String temp = xpp.nextText();
                                if(roadSearch.length() > 0)
                                {
                                    if(!temp.contains(roadSearch))
                                    {
                                        bSkip = true;
                                    }
                                }
                                Widget.setTitle(temp);
                            }
                            else if (xpp.getName().equalsIgnoreCase("description"))
                            {
                                String temp = xpp.nextText();
                                Widget.setDescription(temp);
                                CheckDescDate(temp);

                            }
                            else if (xpp.getName().equalsIgnoreCase("link"))
                            {
                                String temp = xpp.nextText();
                                Widget.setLink(temp);
                            }

                            else if (xpp.getName().equalsIgnoreCase("pubDate"))
                            {
                                String temp = xpp.nextText();
                                if(dateSearch.length() > 0)
                                {
                                    if(!temp.contains(dateSearch) && !CheckDescDate(Widget.getDescription()))
                                    {
                                        bSkip = true;
                                    }
                                }
                                Widget.setPubDate(temp);
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            if(bSkip == false)
                            {
                                List.add(Widget);
                            }
                            bSkip = false;
                        }
                        else
                        if (xpp.getName().equalsIgnoreCase("channel"))
                        {
                            int size;
                            size = List.size();
                        }

                        break;
                }
                eventType = xpp.next();
            }
        }
        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException ae1)
        {
            Log.e("MyTag","IO error during parsing");
        }
        Log.e("MyTag","End document");
        return List;
    }
}
//futureRoadworksActivity end