/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.historyService;

import org.pieShare.pieShareApp.model.pieFile.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IHistoryService {
	void syncPieFileWithDb(PieFile pieFile);
}
