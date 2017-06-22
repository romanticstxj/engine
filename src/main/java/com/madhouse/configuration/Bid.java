package com.madhouse.configuration;

public class Bid
{
    private int type;
    
    private String path;
    
    private String apiClass;
    
    public int getType()
    {
        return type;
    }
    
    public void setType(int type)
    {
        this.type = type;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public void setPath(String path)
    {
        this.path = path;
    }
    
    public Bid(int type, String path)
    {
        super();
        this.type = type;
        this.path = path;
    }
    
    public String getApiClass()
    {
        return apiClass;
    }

    public void setApiClass(String apiClass)
    {
        this.apiClass = apiClass;
    }

    

    public Bid()
    {
        super();
        // TODO 自动生成的构造函数存根
    }
    
}