/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller.api;

import javafx.scene.Node;
import javafx.scene.control.Label;

/**
 *
 * @author Richard
 */
public interface ITwoColumnListViewItem {

	Node getSecondColumn();

	Node getFirstColumn();

	default Object getObject() {
		return null;
	};
	
	IController getController();
}
