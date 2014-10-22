package org.pieShare.pieShareAppFx.conrolExtensions;

import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import org.pieShare.pieShareAppFx.conrolExtensions.api.ITwoColumnListView;

/**
 *
 * @author Richard
 *
 * Inspired by: http://www.billmann.de/2013/07/03/javafx-custom-listcell/
 */
public class TwoColumnListView extends ListCell<ITwoColumnListView> {

	@Override
	public void updateItem(ITwoColumnListView entry, boolean empty) {
		super.updateItem(entry, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(null);

			// DO NOT CREATE INSTANCES IN THIS METHOD, THIS IS BAD!
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(4);
			// grid.setPadding(new Insets(0, 5, 0, 5));

			int i = 0;
			
			Node icon = entry.getFirstColumn();
			if (icon != null) {
				icon.getStyleClass().add("cache-list-icon");
				grid.add(icon, i, 0);//, 1, 2); 
				i++;
			}

			Node name = entry.getSecondColumn();
			if (name != null) {
				grid.add(name, i, 0);
			}
			
			setGraphic(grid);
		}
	}
}
