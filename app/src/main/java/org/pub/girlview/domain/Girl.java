package org.pub.girlview.domain;

import java.io.Serializable;

public class Girl implements Serializable {
	private String name;
	private String src;
	private String href;
	private String desc;

	public Girl() {
		super();
	}

	public Girl(String name, String src, String href, String desc) {
		super();
		this.name = name;
		this.src = src;
		this.href = href;
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
