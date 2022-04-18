// Kieran Francois, S1835773, MPD CW TRI B 2021/22
package org.me.gcu.kfmpdcws1835773;

public class liveIncidentsClass
{
    private String aTitle;
    private String aDesc;
    private String aLink;
    private String aPubDate;

    public liveIncidentsClass()
    {
        aTitle = "";
        aDesc = "";
        aLink = "";
    }

//constructor derived from the pull parser course content
    public liveIncidentsClass(String abolt, String awasher, String anut)
    {
        aTitle = abolt;
        aDesc = awasher;
        aLink = anut;
    }

//standard getters and setters
    public void setTitle(String title)
    {
        aTitle = title;
    }

    public void setDescription(String description)
    {
        aDesc = description;
    }

    public void setLink(String link)
    {
        aLink = link;
    }

    public void setPubDate(String pubDate)
    {
        aPubDate = pubDate;
    }

    public String getTitle()
    {
        return aTitle;
    }

    public String getDescription()
    {
        return aDesc;
    }

    public String getLink()
    {
        return aLink;
    }

    public String getPubDate()
    {
        return aPubDate;
    }
}
//liveIncidentsClass end