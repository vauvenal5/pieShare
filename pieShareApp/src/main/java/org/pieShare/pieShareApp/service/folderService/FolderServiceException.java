/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.folderService;

/**
 *
 * @author daniela
 */
public class FolderServiceException extends Exception {

    /**
     * Creates a new instance of <code>FolderServiceException</code> without
     * detail message.
     */
    public FolderServiceException() {
    }

    /**
     * Constructs an instance of <code>FolderServiceException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public FolderServiceException(String msg) {
        super(msg);
    }
}
