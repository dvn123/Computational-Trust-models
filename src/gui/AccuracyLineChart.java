package gui;

/**

 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.

 * All rights reserved. Use is subject to license terms.

 */

import java.util.Random;

import agents.QuestionAgent;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import javafx.util.Duration;



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

	private NumberAxis xAxis;

	private Timeline animation;



	private double hours = 0;

	private double minutes = 0;

	private double timeInHours = 0;

	private double prevY = 10;

	private double y = 10;



	private void init(Stage primaryStage) {

		Group root = new Group();

		primaryStage.setScene(new Scene(root));

		root.getChildren().add(createChart());

		// create timeline to add new data every 60th of second

		animation = new Timeline();

		animation.getKeyFrames().add(new KeyFrame(Duration.millis(1000), new EventHandler<ActionEvent>() {

			@Override public void handle(ActionEvent actionEvent) {

				// 6 minutes data per frame

				for(int count=0; count < 1; count++) {

					nextTime();


					plotTime();

				}

			}

		}));
		

		animation.setCycleCount(Animation.INDEFINITE);

	}



	protected LineChart<Number, Number> createChart() {

		xAxis = new NumberAxis(0,24,1);

		final NumberAxis yAxis = new NumberAxis(0,100,10);

		final LineChart<Number,Number> lc = new LineChart<Number,Number>(xAxis,yAxis);

		// setup chart

		lc.setId("lineStockDemo");

		lc.setCreateSymbols(false);

		lc.setAnimated(false);

		lc.setLegendVisible(false);

		lc.setTitle("Agents accuracy over time");
		
		//my properties
		
		

		xAxis.setLabel("Time");

		xAxis.setForceZeroInRange(false);

		yAxis.setLabel("Accuracy");

		yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis,"%",null));

		// add starting data

		sinalphaDataSeries = new XYChart.Series<Number,Number>();

		sinalphaDataSeries.setName("Sinalpha");

		fireDataSeries = new XYChart.Series<Number,Number>();

		fireDataSeries.setName("Fire");

		// create some starting data

		sinalphaDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,0));

		fireDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,0));

		// for (double m=0; m<(60); m++) {

		nextTime();

		plotTime();

		// }

		lc.getData().add(fireDataSeries);

		lc.getData().add(sinalphaDataSeries);
		lc.setLegendVisible(true);
		
		System.err.println(lc.getPrefHeight());
		System.err.println(lc.getPrefWidth());

		return lc;

	}



	private void nextTime() {

		/* if (minutes == 59) {

            hours ++;

            minutes = 0;

        } else {

            minutes ++;

        }

        timeInHours = hours + ((1d/60d)*minutes);*/
		timeInHours++;

	}



	private void plotTime() {

		if ((timeInHours % 1) == 0) {

			// change of hour

			double oldY = y;

			y = prevY - 10 + (Math.random()*20);

			prevY = oldY;

			while (y < 10 || y > 90) y = y - 10 + (Math.random()*20);

			//hourDataSeries.getData().add(new XYChart.Data<Number, Number>(timeInHours, prevY));

			// after 25hours delete old data

			if (timeInHours > 25) sinalphaDataSeries.getData().remove(0);

			// every hour after 24 move range 1 hour

			if (timeInHours > 24) {

				xAxis.setLowerBound(xAxis.getLowerBound()+1);

				xAxis.setUpperBound(xAxis.getUpperBound()+1);

			}

		}

		/*   double min = (timeInHours % 1);

        double randomPickVariance = Math.random();

        if (randomPickVariance < 0.3) {

            double minY = prevY + ((y-prevY) * min) - 4 + (Math.random()*8);

            minuteDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,minY));

        } else if (randomPickVariance < 0.7) {

            double minY = prevY + ((y-prevY) * min) - 6 + (Math.random()*12);

            minuteDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,minY));

        } else if (randomPickVariance < 0.95) {

            double minY = prevY + ((y-prevY) * min) - 10 + (Math.random()*20);

            minuteDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,minY));

        } else {

            double minY = prevY + ((y-prevY) * min) - 15 + (Math.random()*30);

            minuteDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,minY));

        }*/

		// after 25hours delete old data

		double x =  QuestionAgent.getAgentRatio("fire");
		double y =  QuestionAgent.getAgentRatio("sinalpha");
		
		fireDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,x));
		sinalphaDataSeries.getData().add(new XYChart.Data<Number,Number>(timeInHours,y));

		if (timeInHours > 25) fireDataSeries.getData().remove(0);

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

	/*public static void main(String[] args) { launch(args); }*/

}