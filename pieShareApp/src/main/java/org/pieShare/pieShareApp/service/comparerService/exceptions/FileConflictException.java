/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.comparerService.exceptions;

import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Richard
 */
public class FileConflictException extends Exception {

	private PieFile pieFile;

	public FileConflictException(String message) {
		super(message);
	}
	
	public FileConflictException(String message, PieFile pieFile) {
		super(message);
		this.pieFile = pieFile;
	}

	public void setPieFile(PieFile pieFile) {
		this.pieFile = pieFile;
	}

	public PieFile getPieFile() {
		return this.pieFile;
	}
}
