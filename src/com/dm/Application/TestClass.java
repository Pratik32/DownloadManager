package com.dm.Application;

import javafx.application.Application;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class TestClass extends Application {

    @Override
    public void start(Stage primaryStage) {
        TableView<TestTask> table = new TableView<TestTask>();
        TableColumn<TestTask, String> statusCol = new TableColumn("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<TestTask, String>(
                "message"));
        statusCol.setPrefWidth(75);

        TestTask testTask=new TestTask(100,100,0.0);
        table.getItems().add(testTask);
        TableColumn<TestTask, Double> progressCol = new TableColumn("Progress");
        progressCol.setCellValueFactory(new PropertyValueFactory<TestTask, Double>(
                "progress"));
        progressCol
                .setCellFactory(ProgressBarTableCell.<TestTask> forTableColumn());

        table.getColumns().addAll(statusCol, progressCol);

        BorderPane root = new BorderPane();
        root.setCenter(table);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        Thread thread=new Thread(testTask);
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }

    static class TestTask extends Task<Void> implements Runnable{

        private final int waitTime; // milliseconds
        private final int pauseTime; // milliseconds
        public double progress;

        public static final int NUM_ITERATIONS = 100;

        TestTask(int waitTime, int pauseTime,double progress) {
            this.progress =progress;
            this.waitTime = waitTime;
            this.pauseTime = pauseTime;
        }

        @Override
        protected Void call() throws Exception {

            StackTraceElement[] elements=Thread.currentThread().getStackTrace();
            for(StackTraceElement e:elements){
                System.out.println(e.toString());
            }
            System.out.println("Welcome");
           // this.updateProgress(ProgressIndicator.INDETERMINATE_PROGRESS, 1);
            this.updateMessage("Waiting...");
            Thread.sleep(waitTime);
            this.updateMessage("Running...");
            updateProgress(progress,NUM_ITERATIONS);
            Thread.sleep(pauseTime);
            this.updateMessage("Done");
            this.updateProgress(1, 1);
            return null;
        }
        public void run(){

            StackTraceElement[] elements=Thread.currentThread().getStackTrace();
            for(StackTraceElement e:elements){
                System.out.println(e.toString());
            }
            int index=0;
            int max=100;
            while(index<max){
                progress=index*1.0;
                index++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}