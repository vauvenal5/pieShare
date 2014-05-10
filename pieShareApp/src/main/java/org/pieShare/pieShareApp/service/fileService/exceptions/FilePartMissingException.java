/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.fileService.exceptions;

/**
 *
 * @author richy
 */
public class FilePartMissingException extends Exception
{
    public FilePartMissingException(String Message)
    {
        super(Message);
    }
}
