package se.mju.stackanalyzer.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import se.mju.stackanalyzer.StackTracesStatistics;
import se.mju.stackanalyzer.ThreadDumpParser;
import se.mju.stackanalyzer.model.ThreadState;

public class ThreadDumpAnalyzerController {
	@FXML private BorderPane root;
	@FXML private BorderPane stacksParent;

	@FXML
	private void handleOpen(final ActionEvent event) throws FileNotFoundException, IOException {
		File file = new FileChooser().showOpenDialog(root.getScene().getWindow());
		open(file);
	}
	
	@FXML
	private void handleExit(final ActionEvent event) {
		System.exit(0);
	}

	public void open(File f) throws FileNotFoundException, IOException {
        open(new ThreadDumpParser(new BufferedReader(new FileReader(f))).parseAll());
	}

	public void open(String traces) throws IOException {
        open(new ThreadDumpParser(traces).parseAll());
	}
	
	private void open(List<ThreadState> stacks) {
		StackTracesStatistics stats = new StackTracesStatistics(stacks);
		StacksPane rootStack = new StacksPane(null, 1, stats);
		ChildResizePane stacksPane = new ChildResizePane(rootStack);
		new StackpaneController(stacksPane, rootStack);
		
		BorderPane.setAlignment(stacksPane, Pos.TOP_LEFT);
		stacksParent.setCenter(stacksPane);
	}


}
