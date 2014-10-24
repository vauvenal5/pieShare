/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.utils;

/**
 *
 * @author Svetoslav
 */
public class ShouldNeverHappenError extends Error {

	public ShouldNeverHappenError(String msg, Throwable ex) {
		super(ex);
	}
}
