/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.pieFilder;

import java.util.Arrays;
import java.util.Objects;
import org.pieShare.pieShareApp.model.api.IBaseModel;
import java.util.UUID;
/**
 * Abstract PieFilder object used by PieFile and PieFolder
 *
 * @author daniela
 */
public abstract class PieFilder implements IBaseModel, Comparable<Object> {

    private String id; 
	private String relativePath;
	private String name;
	private boolean deleted;
	protected long lastModified;

	public PieFilder() {
		this.deleted = false;
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRelativePath() {
		return relativePath;
	}

	//relative path + name
	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}
	protected boolean equalsParas(PieFilder f) {
		if (!name.equals(f.getName())) {
			return false;
		}

		if (!relativePath.equals(f.getRelativePath())) {
			return false;
		}

		if (deleted != f.isDeleted()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PieFilder)) {
			return false;
		}

		PieFilder f = (PieFilder) o;

		if (this.lastModified != f.lastModified) {
			return false;
		}

		return this.equalsParas(f);
	}
	
	@Override
	public int compareTo(Object o) {
		PieFilder f = (PieFilder)o;
		
		if(this.equals(f)) {
			return 0;
		}
		
		if(this.equalsParas(f) && (this.lastModified > f.lastModified)) {
			return 1;
		}
		
		return -1;
	}
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(relativePath);
		hash = 79 * hash + Objects.hashCode(name);
		hash = 79 * hash + (int) (this.lastModified ^ (this.lastModified >>> 32));
		hash = 79 * hash + (deleted ? 1 : 0);
		return hash;
	}
}
