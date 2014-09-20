package cellsociety_team02;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GridView {

	private Scene myScene;
	public static final Dimension DEFAULT_SIZE = new Dimension(800, 600);
	public static final String DEFAULT_RESOURCE_PACKAGE = "resources/";
	public static final String PROTOCOL_PREFIX = "http://";
	public static final String BLANK = " ";

	//private Label myStatus;

	private ResourceBundle myResources;
	private GridModel myModel;
	private Timeline myAnimation;
	private double myInterval;
	private List<Rectangle> myRectangleList;
	private BorderPane root;
	
	
	public GridView (GridModel model, String language) {
		myModel = model;
		myResources = ResourceBundle.getBundle(DEFAULT_RESOURCE_PACKAGE + language);
		myAnimation = new Timeline();
		myInterval = 1.0;
		myRectangleList = new ArrayList<Rectangle>();

		load();
	}

	private XMLParser loadFileToParser() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML", "*.xml"));
		fileChooser.setInitialDirectory(new File(System.getProperty("user.dir"))); // Needs to be tested on Macs +"\\xml files"
		fileChooser.setTitle("Choose XML Source File");
		File file = fileChooser.showOpenDialog(new Stage());
		XMLParser xml = new XMLParser(file);
		return xml;
	}

	private Node makeButtons() {
		HBox box = new HBox();

		Button loadButton = makeButton("LoadButton", new EventHandler<ActionEvent>() {
			@Override
			public void handle (ActionEvent event) {
				load();
			}
		});
		box.getChildren().add(loadButton);
		
		Button stepButton = makeButton("StepButton", new EventHandler<ActionEvent>() {
			@Override
			public void handle (ActionEvent event) {
				step();
			}
		});
		box.getChildren().add(stepButton);
		
		return box;
	}

	private void load(){
		XMLParser parser = loadFileToParser();
		parser.initialize();
		Map<String,String> parameters = parser.makeParameterMap();
		List<Cell> initialCells = parser.makeCells();
		List<Patch> initialPatches = parser.makePatches();
		myModel.initialize(initialCells, initialPatches, parameters);
		startAnimation();
	}

	private Button makeButton (String property, EventHandler<ActionEvent> handler) {
		Button result = new Button();
		result.setText(myResources.getString(property));
		result.setOnAction(handler);
		return result;
	}

	private Node makeGraph() {
		// TODO Auto-generated method stub
		return null;
	}


	public void initialize() {
		root = new BorderPane();
		root.setTop(makeGraph());
		root.setBottom(makeButtons());
		myScene = new Scene(root, DEFAULT_SIZE.width, DEFAULT_SIZE.height);
	}

	public Scene getScene() {
		return myScene;
	}
	
	
	private void startAnimation() {
		myAnimation.stop();
		KeyFrame frame = startHandler(myInterval);
		myAnimation.setCycleCount(Timeline.INDEFINITE);
		myAnimation.getKeyFrames().clear();
		myAnimation.getKeyFrames().add(frame);
		myAnimation.play();
	}
	
	public KeyFrame startHandler(double interval) {
		KeyFrame kf = new KeyFrame(Duration.seconds(interval), new EventHandler<ActionEvent>() {
	    @Override
	    	public void handle(ActionEvent event) {
	    		myModel.update();
	    		root.setCenter(makeGrid());
	    	}
	    });
		return kf;
	}
	
	public void updateSpeed(double value) {
		myInterval = value;
		startAnimation();
	}
	
	public void step(){
		myModel.update();
		makeGrid();
	}
	
	private Node makeGrid() {
		Group g = new Group();
		myRectangleList.clear();
		for (Cell c: myModel.getCells()) {
			Rectangle newDisplay = new Rectangle(c.getCurrentX()*30, c.getCurrentY()*30, 30, 30);
			newDisplay.setFill(c.getColor());
			myRectangleList.add(newDisplay);
			g.getChildren().add(newDisplay);
		}
		return g;
	}
}
