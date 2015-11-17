/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.model.entities.api;

/**
 *
 * @author richy
 */
public interface IConfigurationEntity {

    IPieUserEntity getPieUserEntity();

    void setPieUserEntity(IPieUserEntity pieUserEntity);

    String getWorkingDir();

    void setWorkingDir(String workingDir);

    String getTmpDir();

    void setTmpDir(String tmpDir);

    String getPwdFile();

    void setPwdFile(String pwdFile);

    String getUser();

    void setUser(String user);
}
