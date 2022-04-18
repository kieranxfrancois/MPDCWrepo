// Kieran Francois, S1835773, MPD CW TRI B 2021/22
package org.me.gcu.kfmpdcws1835773;

public class futureRoadworksClass
{
    private String aTitle;
    private String aDescription;
    private String aLink;
    private String aPubDate;

    public futureRoadworksClass()
    {
        aTitle = "";
        aDescription = "";
        aLink = "";
    }

//constructor derived from the pull parser course content
    public futureRoadworksClass(String abolt, String awasher, String anut)
    {
        aTitle = abolt;
        aDescription = awasher;
        aLink = anut;
    }

//standard getters and setters
    public void setTitle(String title)
    {
        aTitle = title;
    }

    public void setDescription(String description)
    {
        aDescription = description;
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
        return aDescription;
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