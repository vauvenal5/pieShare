/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.pieFilder;

import org.pieShare.pieShareApp.model.api.IBaseModel;

/**
 * PieFolder a implementation of the general PieFilder, represents a Folder
 * (with special Pie attributes)
 *
 * @author daniela
 */
public class PieFolder extends PieFilder implements IBaseModel {

    public PieFolder() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PieFolder)) {
            //not even a PieFolder object
            return false;
        }

        PieFolder f = (PieFolder) o;

        if (this.getName().equalsIgnoreCase(f.getName())
                && this.getRelativePath().equalsIgnoreCase(f.getRelativePath())
                && this.isDeleted() == f.isDeleted()) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = this.getId().hashCode() + this.getName().hashCode() + this.getRelativePath().hashCode();
        return hash;
    }

}
