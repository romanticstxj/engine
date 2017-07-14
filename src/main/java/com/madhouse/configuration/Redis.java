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
	private int maxIdle;
	private int minIdle;
	private int maxTotal;

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

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
}