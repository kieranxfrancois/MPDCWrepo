// Kieran Francois, S1835773, MPD CW TRI B 2021/22
package org.me.gcu.kfmpdcws1835773;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class liveIncidentsActivity extends AppCompatActivity implements View.OnClickListener
{
    private String kfString;
    private String urlSource;
    private String kfMain;
    private String kfTask;

    LinkedList <liveIncidentsClass> List = null;

    //calling the variables linked to the incident list
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listHead;
    HashMap<String, List<String>> listChild;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_incidents);

        urlSource = "https://trafficscotland.org/rss/feeds/currentincidents.aspx"; //first URL source being used for the current incidents on traffic scotland
        List  = new LinkedList<liveIncidentsClass>();

        expListView = (ExpandableListView) findViewById(R.id.ExpandableList);
        listHead = new ArrayList<String>();
        listChild = new HashMap<String, List<String>>();

        prepData();

        listAdapter = new ExpandableListAdapter(this, listHead, listChild);
        expListView.setAdapter(listAdapter);

        Bundle extras = getIntent().getExtras();
        if(extras !=null) {
            String value = extras.getString("KEY");
        }
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

            liveIncidentsActivity.this.runOnUiThread(new Runnable()
            {
                public void run() {

                    kfMain = kfTask;

                    if(List != null)
                        List.clear();

                    List = parseDataIncidents(kfMain);

                    prepData();
                }
            });
        }
    }

    public void onClick(View aview)
    {
        startProgress();
    }

    public void startProgress()
    {
        kfTask = "";
        new Thread(new Task(urlSource)).start();
    }

//Pull parser derived from the PullParser3 lab
    private LinkedList<liveIncidentsClass> parseDataIncidents(String dataToParse)
    {
        liveIncidentsClass Widget = null;
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
                            List  = new LinkedList<liveIncidentsClass>();
                        }
                        else if (xpp.getName().equalsIgnoreCase("item"))
                        {
                            Widget = new liveIncidentsClass();
                            bStart = true;
                        }
                        else if(bStart)
                        {
                            if (xpp.getName().equalsIgnoreCase("title"))
                            {
                                String temp = xpp.nextText();
                                Widget.setTitle(temp);
                            }
                            else if (xpp.getName().equalsIgnoreCase("description"))
                            {
                                String temp = xpp.nextText();
                                Widget.setDescription(temp);

                            }
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
//liveIncidentsActivity end