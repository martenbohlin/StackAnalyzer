package se.mju.stackanalyzer.ui;

import static java.lang.System.out;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;

public class MenuController implements Initializable {
	@FXML
	private MenuBar menuBar;

	@FXML
	private void handleOpen(final ActionEvent event) {
		provideAboutFunctionality();
	}

	@FXML
	private void handleExit(final ActionEvent event) {
		System.exit(0);
	}


	/**
	 * Perform functionality associated with "About" menu selection or CTRL-A.
	 */
	private void provideAboutFunctionality() {
		out.println("You clicked on About!");
	}

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {
		menuBar.setFocusTraversable(true);
	}
}