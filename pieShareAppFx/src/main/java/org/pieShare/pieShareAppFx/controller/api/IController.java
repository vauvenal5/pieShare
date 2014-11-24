/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareAppFx.controller.api;

import java.io.IOException;
import javafx.fxml.Initializable;
import javafx.scene.Node;

/**
 *
 * @author Richard
 */
public interface IController extends Initializable {

	Node getControl() throws IOException;
}
