package se.mju.stackanalyzer.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
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
				InputStream in = getInputStream(file);
				List<ThreadState> unfilteredStacks = new ThreadDumpParser(new BufferedReader(new InputStreamReader(in, "UTF-8"))).parseAll();
				timer.startNewLapAndPrintLapTime("Parse");

				Platform.runLater(() -> {
					open(unfilteredStacks);
					timer.startNewLapAndPrintLapTime("GUI Open");
					Window window = root.getScene().getWindow();
					if (window instanceof Stage) {
						((Stage) window).setTitle("Thread Dump Analyzer: " + file.getName());
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}).start();
	}

	private InputStream getInputStream(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		if (file.getName().endsWith(".gz")) {
			in = new GZIPInputStream(in);
		}
		return in;
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
