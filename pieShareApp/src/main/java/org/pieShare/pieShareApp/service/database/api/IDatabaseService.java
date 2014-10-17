/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareApp.service.database.api;

import java.util.ArrayList;
import org.pieShare.pieShareApp.model.PieUser;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;

/**
 *
 * @author Richard
 */
public interface IDatabaseService {
	void persistPieUser(PieUser service);
	PieUser getPieUser(String name);
	ArrayList<PieUser> findAllPieUsers();
}
