package gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * @author Björn Ho
 */
public class MainController implements Initializable {
	/**
	 * You can interact with an individual tab page if you need it. (Uncomment
	 * if necessary).
	 */
	// @FXML private GridPane ribbonTab;
	// @FXML private TabPane tabPane;
	@FXML private VBox verticalBox;

	/**
	 * Right now this MainController is empty but perhaps there will be 
	 * additions later on. Keeping it for now.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {

	}
	
	 public void importNew(final ActionEvent e) {
		 System.out.println("importing");
		 final FileChooser fileExplorer = new FileChooser();
		 fileExplorer.getExtensionFilters().addAll(new ExtensionFilter("gfa files", "*.gfa"));
		 File file = fileExplorer.showOpenDialog(verticalBox.getScene().getWindow());
		 if (file != null) {
             //openFile(file);
			 System.out.println(file.getName());
         }
	 }
}
