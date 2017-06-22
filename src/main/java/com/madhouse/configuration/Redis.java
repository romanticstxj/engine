package com.madhouse.configuration;

import java.util.List;

public class Redis
{
    
    private Master master;
    
    private Slave slave;
    
    public Master getMaster()
    {
        return master;
    }
    
    public void setMaster(Master master)
    {
        this.master = master;
    }
    
    public Slave getSlave()
    {
        return slave;
    }
    
    public void setSlave(Slave slave)
    {
        this.slave = slave;
    }
    
    public Redis(Master master, Slave slave)
    {
        super();
        this.master = master;
        this.slave = slave;
    }
    
    public Redis()
    {
        super();
        // TODO 自动生成的构造函数存根
    }
    
}