package org.pieShare.pieShareAppFx.conrolExtensions;


import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.pieShare.pieShareAppFx.preferences.api.IPreferencesEntry;

/**
 *
 * @author Richard
 * 
 * Inspired by: http://www.billmann.de/2013/07/03/javafx-custom-listcell/
*/
public class PreferencesListViewItems extends ListCell<IPreferencesEntry> {
    @Override
    public void updateItem(IPreferencesEntry entry, boolean empty) {
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
 
            Label icon = entry.getIconLabel();
            //icon.setFont(Font.font("FontAwesome", FontWeight.BOLD, 24));
            icon.getStyleClass().add("cache-list-icon");
            grid.add(icon, 0, 0);//, 1, 2);            
 
            Label name = entry.getTextLabel();
            //name.getStyleClass().add("cache-list-name");
            grid.add(name, 1, 0);
 
            /*
            if (CacheUtils.hasUserFoundCache(cache, new Long(3906456))) {
                JavaFXUtils.addClasses(this, CACHE_LIST_FOUND_CLASS);
                JavaFXUtils.removeClasses(this, CACHE_LIST_NOT_FOUND_CLASS);
            } else {
                JavaFXUtils.addClasses(this, CACHE_LIST_NOT_FOUND_CLASS);
                JavaFXUtils.removeClasses(this, CACHE_LIST_FOUND_CLASS);
            }
			*/
            setGraphic(grid);
        }
    }
}