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
public interface IPieFileEntity extends IBaseEntity{

    boolean isSynched();

    void setSynched(boolean synched);

    String getAbsoluteWorkingPath();

    void setAbsoluteWorkingPath(String absoluteWorkingPath);

    byte[] getMd5();

    boolean isDeleted();

    void setDeleted(boolean deleted);

    void setMd5(byte[] md5);

    String getRelativeFilePath();

    void setRelativeFilePath(String relativeFilePath);

    String getFileName();

    void setFileName(String fileName);

    long getLastModified();

    void setLastModified(long lastModified);
}
