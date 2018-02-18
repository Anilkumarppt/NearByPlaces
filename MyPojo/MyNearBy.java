package com.example.dell.mapsusingretrofit.MyPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 13-02-2018.
 */

public class MyNearBy
{
    private List<Result> result=new ArrayList<Result>();

    private String[] html_attributions;

    private String status;

    public List<Result> getResult ()
    {
        return result;
    }

    public void setResult (List<Result> result)
    {
        this.result = result;
    }

    public String[] getHtml_attributions ()
    {
        return html_attributions;
    }

    public void setHtml_attributions (String[] html_attributions)
    {
        this.html_attributions = html_attributions;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [result = "+result+", html_attributions = "+html_attributions+", status = "+status+"]";
    }
}

