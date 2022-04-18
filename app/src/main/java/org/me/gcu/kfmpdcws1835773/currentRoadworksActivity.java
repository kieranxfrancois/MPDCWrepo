// Kieran Francois, S1835773, MPD CW TRI B 2021/22
//very similar to liveIncidentsActivity with the addition of searching for specific roads or keywords

package org.me.gcu.kfmpdcws1835773;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import androidx.appcompat.app.AppCompatActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class currentRoadworksActivity extends AppCompatActivity implements View.OnClickListener
{

    private SearchView searchView;
    private String kfTask;
    private String kfMain;
    private String kfString;
    private String roadSearch;
    private String urlSource;
    private Date startDate;
    private Date endDate;

    LinkedList <currentRoadworksClass> List = null;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listHead;
    HashMap<String, List<String>> listChild;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roadworks);

        roadSearch = "";
        urlSource = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
        List  = new LinkedList<currentRoadworksClass>();
        searchView = (SearchView)findViewById(R.id.filterByRoadName);
        expListView = (ExpandableListView) findViewById(R.id.ExpandableList);
        listHead = new ArrayList<String>();
        listChild = new HashMap<String, List<String>>();
        listAdapter = new ExpandableListAdapter(this, listHead, listChild);
        expListView.setAdapter(listAdapter);

        prepData();

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            String value = extras.getString("KEY");
        }
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

//need to implement the on group expand listener to update the data when clicked
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {}
        });
        startProgress();
    }

    //need to implement the on click so the app runs
    public void onClick(View aview)
    {
        startProgress();
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
                Log.e("Description", kfString);
                listItems.add(List.get(i).getDescription());
                listItems.add(List.get(i).getPubDate());
                listChild.put(listHead.get(i), listItems);
                expListView.setAdapter(listAdapter);
            }
        }
        expListView.setAdapter(listAdapter);
    }

    private boolean CheckDateInside(String description)
    {
        if(description.contains("Start Date"))
        {
            String startDate = description.substring(description.indexOf(","), description.indexOf(">"));
            String temp1= description.substring(description.indexOf(">"));
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
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long diff = this.endDate.getTime() - this.startDate.getTime();
        }
        return false;
    }
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
            currentRoadworksActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {
                    kfMain = kfTask;
                    if(List != null)
                        List.clear();
                    List = parseCurrentRoadWorks(kfMain);
                    prepData();
                }
            });
        }
    }

//Pull parser derived from the PullParser3 lab
    private LinkedList<currentRoadworksClass> parseCurrentRoadWorks(String dataToParse)
    {
        currentRoadworksClass Widget = null;
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
                        if (xpp.getName().equalsIgnoreCase("channel"))
                        {
                            List  = new LinkedList<currentRoadworksClass>();
                        }
                        else if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            Widget = new currentRoadworksClass();
                            bStart = true;
                        }
                        else if(bStart)
                        {
                            if (xpp.getName().equalsIgnoreCase("title"))
                            {
                                String temp = xpp.nextText();
                                //check if text is in search bar
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
                                CheckDateInside(temp);
                            }

//                            Why is this not removing broken linebreak tag ??????????????
//                            else if (xpp.getName().equalsIgnoreCase("description") && !xpp.getText().contains("\n"))
//                            {
//                                kfString = xpp.nextText();
//                                kfString = kfString.replaceAll("<br />", " ");
//                            }

                            else if (xpp.getName().equalsIgnoreCase("link"))
                            {
                                String temp = xpp.nextText();
                                Widget.setLink(temp);
                            }
                            else if (xpp.getName().equalsIgnoreCase("pubDate"))
                            {
                                String temp = xpp.nextText();
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
//currentRoadworksActivity end