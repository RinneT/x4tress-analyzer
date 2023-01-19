package org.soh.x4.x4tress_analyzer.gui;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.soh.x4.x4tress_analyzer.model.Component;
import org.soh.x4.x4tress_analyzer.model.DataStorage;
import org.soh.x4.x4tress_analyzer.model.GlobalEvent;
import org.soh.x4.x4tress_analyzer.model.ProcessedEvent;
import org.soh.x4.x4tress_analyzer.pocessor.EventProcessor_En;
import org.soh.x4.x4tress_analyzer.savegame.SaveGameLoader;
import org.xml.sax.SAXException;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * JavaFX App / UI to simulate x4tress
 * 
 * @author Son of Hubert
 */
public class App extends Application {

	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	private static boolean showGridLines = false;
	private TableView<Component> objectList = null;
	private TableView<GlobalEvent> globalEventList = null;
	private TableView<GlobalEvent> globalEventFilteredList = null;
	private TableView<ProcessedEvent> processedEventList = null;
	private TextField objectFilter = null;
	private TextArea unitText;
	private MenuBar menuBar = null;

	private SaveGameLoader saveGameLoader = new SaveGameLoader();

	/**
	 * Launch the application.
	 * 
	 * @param args <i>-g</i> to show grid lines in the Gui
	 */
	public static void main(String[] args) {
		if (args != null && args.length == 1 && "-g".equalsIgnoreCase(args[0])) {
			showGridLines = true;
		}
		LOGGER.info("Started SoH X4Tress Analyzer with Java Version " + System.getProperty("java.version"));
		launch();
	}

	@Override
	public void start(@SuppressWarnings("exports") Stage stage) {
		final FileChooser fileChooser = new FileChooser();

		// Create the layout and components
		// Create the parent grid
		GridPane gridLeft = createGridPane();

		// Create the Object List and its filter
		gridLeft = createObjectList(gridLeft);

		GridPane gridCenter = createGridPane();
		gridCenter = createGlobalEventsFilteredList(gridCenter);

		GridPane gridBottom = createGridPane();
		gridBottom = createGlobalEventsList(gridBottom);
		gridBottom = createUnitText(gridBottom);

		GridPane gridRight = createGridPane();
		gridRight = createProcessedEventsList(gridRight);

		// Create and show the parent scene
		BorderPane rootBox = new BorderPane();
		rootBox.setTop(createMenuBar(fileChooser, stage));
		rootBox.setLeft(gridLeft);
		rootBox.setCenter(gridCenter);
		rootBox.setRight(gridRight);
		rootBox.setBottom(gridBottom);
		var scene = new Scene(rootBox);
		stage.setTitle("SoH X4Tress Analyzer");
		stage.setScene(scene);
		stage.show();

		// Set the initial Focus on the object list and select the first item
		if (objectList != null) {
			objectList.requestFocus();
		}
	}

	/**
	 * Create the basic layout Grid Pane
	 * 
	 * @return the grid pane
	 */
	private GridPane createGridPane() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
		// Activate for development purposes
		grid.setGridLinesVisible(showGridLines);

		return grid;
	}

	/**
	 * Initialize the menu bar
	 * 
	 * @return the MenuBar
	 */
	private MenuBar createMenuBar(FileChooser fileChooser, Stage stage) {
		menuBar = new MenuBar();
		Menu fileMenu = new Menu("File");

		// Load Savegame logic
		MenuItem loadSaveGame = new MenuItem("Load Savegame");
		loadSaveGame.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open X4 Savegame");
				fileChooser.getExtensionFilters().add(new ExtensionFilter("X4 Savegame *.gz | *.xml", "*.gz", "*.xml"));
				fileChooser.getExtensionFilters().add(new ExtensionFilter("X4 compressed Savegame *.gz", "*.gz"));
				fileChooser.getExtensionFilters().add(new ExtensionFilter("X4 uncompressed Savegame *.xml", "*.xml"));
				File file = fileChooser.showOpenDialog(stage);
				if (file != null) {
					loadSaveGame(file);
				}

			}
		});
		fileMenu.getItems().add(loadSaveGame);

		menuBar.getMenus().add(fileMenu);
		return menuBar;
	}

	/**
	 * Create the Object List and its Filter
	 * 
	 * @param grid
	 * @return The Grid Pane containing the Object List and Filter
	 */
	@SuppressWarnings("unchecked")
	private GridPane createObjectList(GridPane grid) {
		// Add the Object List
		objectList = new TableView<>();
		TableColumn<Component, String> objClassCol = new TableColumn<>("Class");
		objClassCol.setCellValueFactory(new PropertyValueFactory<Component, String>("objectClass"));
		TableColumn<Component, String> objCodeCol = new TableColumn<>("Code");
		objCodeCol.setCellValueFactory(new PropertyValueFactory<Component, String>("objectCode"));
		TableColumn<Component, String> objOwnerCol = new TableColumn<>("Owner");
		objOwnerCol.setCellValueFactory(new PropertyValueFactory<Component, String>("objectOwner"));

		objectList.getColumns().addAll(objClassCol, objCodeCol, objOwnerCol);

		// Add the Filter text field for Objects
		objectFilter = new TextField();
		objectFilter.setPromptText("Filter object Name");

		grid.add(objectFilter, 0, 0);
		grid.add(objectList, 0, 1);

		return grid;
	}

	/**
	 * Create the unfiltered Global Events List and its Filter
	 * 
	 * @param grid
	 * @return The Grid Pane containing the Global Events List and Filter
	 */
	private GridPane createGlobalEventsFilteredList(GridPane grid) {
		// Add the Global Events List
		globalEventFilteredList = GlobalEvent.createUITable();

		grid.add(globalEventFilteredList, 0, 0);

		return grid;
	}

	/**
	 * Create the Global Events List without Filter
	 * 
	 * @param grid
	 * @return The Grid Pane containing the full Global Events List
	 */
	private GridPane createGlobalEventsList(GridPane grid) {
		// Add the Global Events List
		globalEventList = GlobalEvent.createUITable();

		grid.add(globalEventList, 0, 0);

		return grid;
	}
	
	/**
	 * Create the Text Area to display the final text for a given Unit
	 * 
	 * @param grid the gridpane
	 * @return The Grid Pane containing the Text Area
	 */
	private GridPane createUnitText(GridPane grid) {
		unitText = new TextArea();

		grid.add(unitText, 1, 0);

		return grid;
	}

	/**
	 * Create the Processed Events List without Filter
	 * 
	 * @param grid
	 * @return The Grid Pane containing the full Processed Events List
	 */
	private GridPane createProcessedEventsList(GridPane grid) {
		// Add the Global Events List
		processedEventList = ProcessedEvent.createUITable();

		grid.add(processedEventList, 0, 0);

		return grid;
	}

	/**
	 * Load an X4 savegame file and populate the UI objects
	 * 
	 * @param file
	 */
	private void loadSaveGame(File file) {
		String errorMessage = null;
		if (file == null) {
			errorMessage = "Error: File is null!";
			showError(errorMessage, null);
			LOGGER.error(errorMessage);
		}
		try {
			DataStorage saveGameData = saveGameLoader.loadFile(file);
			if (saveGameData == null) {
				errorMessage = "No data loaded!";
				showError(errorMessage, null);
				LOGGER.error(errorMessage);
				return;
			}

			// Populate the X4Objects list
			ObservableList<Component> x4Objects = FXCollections.observableArrayList();
			for (Component comp : saveGameData.getObjectList()) {
				x4Objects.add(comp);
			}

			FilteredList<Component> filteredX4Objects = new FilteredList<>(x4Objects, s -> true);
			objectList.setItems(filteredX4Objects);

			objectFilter.textProperty().addListener(obs -> {
				String filter = objectFilter.getText();
				if (filter == null || filter.length() == 0) {
					filteredX4Objects.setPredicate(s -> true);
				} else {
					filteredX4Objects.setPredicate(s -> s.contains(filter));
				}
			});

			// Populate the unfiltered Global Events list
			ObservableList<GlobalEvent> sohGlobalEvents = FXCollections.observableArrayList();
			for (GlobalEvent event : saveGameData.getGlobalEvents()) {
				sohGlobalEvents.add(event);
			}

			globalEventList.setItems(sohGlobalEvents);

			// Populate the filtered Global Events list
			FilteredList<GlobalEvent> filteredGlobalEvents = new FilteredList<>(sohGlobalEvents, s -> true);
			globalEventFilteredList.setItems(filteredGlobalEvents);


			// Populate the Processed Events list
			ObservableList<ProcessedEvent> sohProcessedEvents = FXCollections.observableArrayList();
			for (ProcessedEvent event : saveGameData.getProcessedEvents()) {
				sohProcessedEvents.add(event);
			}
			FilteredList<ProcessedEvent> filteredProcessedEvents = new FilteredList<>(sohProcessedEvents, s -> true);
			processedEventList.setItems(filteredProcessedEvents);
			
			objectList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
				if (newSelection != null) {
					String objectCode = newSelection.getObjectCode();
					// Set the global Events
					filteredGlobalEvents.setPredicate(s -> s.matchesUnit(objectCode));
					filteredProcessedEvents.setPredicate(s -> s.matchesUnit(objectCode));
					
					// Generate the final Unit Text
					EventProcessor_En processor = new EventProcessor_En(saveGameData.getPlayerName());
					Iterator<ProcessedEvent> eventIterator = filteredProcessedEvents.iterator();
					while (eventIterator.hasNext()) {
						ProcessedEvent event = eventIterator.next();
						unitText.setText(processor.processEvent(event, objectCode).getDisplayText());
					}
				}
			});

		} catch (ParserConfigurationException e) {
			errorMessage = "Error trying to initialize xml parser!";
			showError(errorMessage, e);
			LOGGER.error(errorMessage, e);
		} catch (SAXException e) {
			errorMessage = "Error trying to parse savegame " + file.getAbsolutePath() + "! Is it a valid X4 savegame?";
			showError(errorMessage, e);
			LOGGER.error(errorMessage, e);
		} catch (IOException e) {
			errorMessage = "Error trying to open file " + file.getAbsolutePath() + "! Is it a valid X4 savegame?";
			showError(errorMessage, e);
			LOGGER.error(errorMessage, e);
		}
	}

	/**
	 * Display an error message popup
	 * 
	 * @param errorMessage the error message
	 * @param e            the exception. Can be null!
	 */
	private void showError(String errorMessage, Exception e) {
		if (e != null) {
			errorMessage += "\nError message: " + e.getMessage();
		}
		Alert alert = new Alert(AlertType.ERROR, errorMessage, ButtonType.OK);
		alert.showAndWait();
	}

}