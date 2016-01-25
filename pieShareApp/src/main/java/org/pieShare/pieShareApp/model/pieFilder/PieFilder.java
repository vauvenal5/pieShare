/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.pieFilder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import org.pieShare.pieShareApp.model.api.IBaseModel;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        File fileOrig = new File(relativePath);
        File fileForeign = new File(f.getRelativePath());

        try {
            if (!fileOrig.getCanonicalPath().equals(fileForeign.getCanonicalPath())) {
                return false;
            }
        } catch (IOException ex) {
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
		
		if (deleted != f.isDeleted()) {
			return false;
		}
		
		//todo-sv: for the time being I took lastModified out of the equation
		//due to a bug in android preventing us from setting the right time
		//https://code.google.com/p/android/issues/detail?id=18624
		//if the bug does not get fixed we need to implement a special
		//file service for android which will allow us to fake the
		//lastmodified for our core logic
		//--> could even be a good idea to work in general with our own mod time

        /*if (this.lastModified != f.lastModified) {
            return false;
        }*/

        return this.equalsParas(f);
    }

    @Override
    public int compareTo(Object o) {
        PieFilder f = (PieFilder) o;

        if (this.equals(f)) {
            return 0;
        }

//        if (this.equalsParas(f) && (this.lastModified > f.lastModified)) {
//            return 1;
//        }

        return -1;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(relativePath);
        hash = 79 * hash + Objects.hashCode(name);
        //hash = 79 * hash + (int) (this.lastModified ^ (this.lastModified >>> 32));
        hash = 79 * hash + (deleted ? 1 : 0);
        return hash;
    }
}
