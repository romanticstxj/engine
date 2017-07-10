package com.madhouse.configuration;

import java.util.List;

public class Redis {
	public class Config {
		private String passwd;
		private String host;
		private int port;
		private int db;

		public String getPasswd() {
			return passwd;
		}

		public void setPasswd(String passwd) {
			this.passwd = passwd;
		}

		public String getHost() {
			return host;
		}

		public void setHost(String host) {
			this.host = host;
		}

		public int getPort() {
			return port;
		}

		public void setPort(int port) {
			this.port = port;
		}

		public int getDb() {
			return db;
		}

		public void setDb(int db) {
			this.db = db;
		}
	}

	private Config master;

	private Config slave;

	public Config getMaster() {
		return master;
	}

	public void setMaster(Config master) {
		this.master = master;
	}

	public Config getSlave() {
		return slave;
	}

	public void setSlave(Config slave) {
		this.slave = slave;
	}

	public Redis(Config master, Config slave) {
		super();
		this.master = master;
		this.slave = slave;
	}

	public Redis() {
		super();
		// TODO 自动生成的构造函数存根
	}

}