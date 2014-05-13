/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.model;

import org.pieShare.pieTools.pieUtilities.model.EncryptedPassword;

/**
 *
 * @author Svetoslav
 */
public class PieUser {
    private EncryptedPassword password;
    private String userName;
    private String cloudName;
    private boolean isLoggedIn = false;

    public boolean isIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public EncryptedPassword getPassword() {
        return password;
    }

    public void setPassword(EncryptedPassword password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        this.cloudName = userName;
    }

    public String getCloudName() {
        return cloudName;
    }
}
