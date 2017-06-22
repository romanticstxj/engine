package com.madhouse.configuration;

import java.util.List;

public class WebApp
{
    private String domain;
    
    private String impression;
    
    private String click;
    
    private List<Bid> bids;
    
    public String getDomain()
    {
        return domain;
    }
    
    public void setDomain(String domain)
    {
        this.domain = domain;
    }
    
    public List<Bid> getBids()
    {
        return bids;
    }
    
    public void setBids(List<Bid> bids)
    {
        this.bids = bids;
    }
    
    public String getImpression()
    {
        return impression;
    }
    
    public void setImpression(String impression)
    {
        this.impression = impression;
    }
    
    public String getClick()
    {
        return click;
    }
    
    public void setClick(String click)
    {
        this.click = click;
    }
    
    public WebApp(String domain, String impression, String click, List<Bid> bids)
    {
        super();
        this.domain = domain;
        this.impression = impression;
        this.click = click;
        this.bids = bids;
    }
    
    public WebApp()
    {
        super();
        // TODO 自动生成的构造函数存根
    }
}