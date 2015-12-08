/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.service.historyService;

import java.util.List;
import org.pieShare.pieShareApp.model.pieFilder.PieFile;

/**
 *
 * @author Svetoslav
 */
public interface IHistoryService {
	void syncPieFileWithDb(PieFile pieFile);
	PieFile syncDeleteToHistory(PieFile file);
	List<PieFile> syncLocalPieFilesWithHistory();
}
