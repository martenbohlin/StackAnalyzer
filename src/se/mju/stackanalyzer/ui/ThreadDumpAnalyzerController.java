package se.mju.stackanalyzer.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import se.mju.stackanalyzer.ThreadDumpParser;
import se.mju.stackanalyzer.model.ThreadState;
import se.mju.stackanalyzer.util.StopWatch;

public class ThreadDumpAnalyzerController {
	@FXML private BorderPane root;
	@FXML private BorderPane stacksParent;
	private StackpaneController stacksPaneController;

	@FXML
	private void handleOpen(final ActionEvent event) throws FileNotFoundException, IOException {
		File file = new FileChooser().showOpenDialog(root.getScene().getWindow());
		if (file != null) {
			open(file);
		}
	}
	
	@FXML
	private void handleZoomOut(final ActionEvent event) {
		stacksPaneController.zoomOut();
	}
	
	@FXML
	private void handleExit(final ActionEvent event) {
		System.exit(0);
	}

	public void open(File file) throws FileNotFoundException, IOException {
		new Thread(() -> {
			try {
				StopWatch timer = new StopWatch();
				List<ThreadState> unfilteredStacks = new ThreadDumpParser(new BufferedReader(new FileReader(file))).parseAll();
				timer.startNewLapAndPrintLapTime("Parse");

				Platform.runLater(() -> {
					open(unfilteredStacks);
					timer.startNewLapAndPrintLapTime("GUI Open");
				});
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}).start();
	}

	public void open(String traces) throws IOException {
        List<ThreadState> stacks = new ThreadDumpParser(traces).parseAll();
		open(stacks);
	}
	
	private void open(List<ThreadState> stacks) {
		stacksPaneController = new StackpaneController(stacks);
		
		BorderPane.setAlignment(stacksPaneController.getNode(), Pos.TOP_LEFT);
		stacksParent.setCenter(stacksPaneController.getNode());
	}
	
}
