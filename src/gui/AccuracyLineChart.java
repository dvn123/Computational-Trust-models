package gui;

/**

 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.

 * All rights reserved. Use is subject to license terms.

 */

import java.io.File;
import java.io.IOException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;

import util.Constants;
import agents.QuestionAgent;



/**

 * A simulated stock line chart.

 *

 * @see javafx.scene.chart.Chart

 * @see javafx.scene.chart.LineChart

 * @see javafx.scene.chart.NumberAxis

 * @see javafx.scene.chart.XYChart

 */

public class AccuracyLineChart extends Application {



	private XYChart.Series<Number,Number> sinalphaDataSeries;

	private XYChart.Series<Number,Number> fireDataSeries;

	private XYChart.Series<Number,Number> randomDataSeries;

	private NumberAxis xAxis;

	private Timeline animation;

	private int timeInHours = 0;

	private double prevY = 10;

	private double y = 10;



	private void init(Stage primaryStage) {


		Scene scene = new Scene(createChart());
		primaryStage.setScene(scene);

		

		// create timeline to add new data every 60th of second

		animation = new Timeline();

		animation.getKeyFrames().add(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {

			@Override public void handle(ActionEvent actionEvent) {
				System.out.println(timeInHours);
				
				nextTime();
				plotTime();
				
				if(timeInHours % Constants.TIME_BETWEEN_PICTURES == 0 || timeInHours == Constants.NUMBER_OF_QUESTIONS) {
					WritableImage snapShot = scene.snapshot(null);

					try {
						ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null), "png", new File(MainWindow.folder + "/picture_" + timeInHours + ".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}

		}));


		animation.setCycleCount(Constants.NUMBER_OF_QUESTIONS - 1);

	}



	protected LineChart<Number, Number> createChart() {

		xAxis = new NumberAxis(0,Constants.GRAPH_MAX_X,1);

		final NumberAxis yAxis = new NumberAxis(0,100,10);

		final LineChart<Number,Number> lc = new LineChart<Number,Number>(xAxis,yAxis);

		// setup chart

		lc.setId("lineStockDemo");

		lc.setCreateSymbols(false);

		lc.setAnimated(false);

		lc.setLegendVisible(false);

		lc.setTitle("Agents accuracy over time");

		//my properties



		xAxis.setLabel("Time (s)");

		xAxis.setForceZeroInRange(false);

		yAxis.setLabel("Accuracy (%)");

		yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis,"",null));

		// add starting data

		sinalphaDataSeries = new XYChart.Series<Number,Number>();

		sinalphaDataSeries.setName("Sinalpha");

		fireDataSeries = new XYChart.Series<Number,Number>();

		fireDataSeries.setName("FIRE");

		randomDataSeries = new XYChart.Series<Number,Number>();
		randomDataSeries.setName("Random");

		// create some starting data

		sinalphaDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,0));

		fireDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,0));

		randomDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,0));



		nextTime();

		plotTime();

		
		lc.getData().add(fireDataSeries);

		lc.getData().add(sinalphaDataSeries);
		lc.getData().add(randomDataSeries);

		lc.setLegendVisible(true);
		
		lc.setPrefSize(1200, 600);

		return lc;

	}



	private void nextTime() {

		if (timeInHours < Constants.NUMBER_OF_QUESTIONS)
			timeInHours++;

	}



	private void plotTime() {

		if ((timeInHours % 1) == 0) {

			// change of hour

			double oldY = y;

			y = prevY - 10 + (Math.random()*20);

			prevY = oldY;

			while (y < 10 || y > 90) y = y - 10 + (Math.random()*20);


			// after x hours delete old data

			if (timeInHours > Constants.GRAPH_MAX_X + 1) sinalphaDataSeries.getData().remove(0);

			// every hour after x move range 1 hour

			if (timeInHours > Constants.GRAPH_MAX_X) {

				xAxis.setLowerBound(xAxis.getLowerBound()+1);

				xAxis.setUpperBound(xAxis.getUpperBound()+1);

			}

		}


		// after x time delete old data

		double x =  QuestionAgent.getAgentRatio("fire");
		double y =  QuestionAgent.getAgentRatio("sinalpha");
		double z =  QuestionAgent.getAgentRatio("random");

		fireDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,x));
		sinalphaDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,y));
		randomDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,z));

		if (timeInHours > Constants.GRAPH_MAX_X + 1) fireDataSeries.getData().remove(0);
		if (timeInHours > Constants.GRAPH_MAX_X + 1) randomDataSeries.getData().remove(0);

	}



	public void play() {

		animation.play();

	}



	@Override public void stop() {

		animation.pause();

	}    



	@Override public void start(Stage primaryStage) throws Exception {

		init(primaryStage);

		primaryStage.show();
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		play();

	}

}