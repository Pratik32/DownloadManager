package com.dm.Application;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ps on 27/1/17.
 */
public class UIThread extends Application implements Observer ,Initializable {

    public TextField field;
    public Button resume;
    public Button pause;
    public Button delete;
    public Button start;
    public HBox viewArea;
    public  TableView<Download> view;
    public TableColumn<Download,String> name;
    public TableColumn<Download, Double> Progress;



    public static DownloadManager manager=DownloadManager.getInstance();
    public static int downloads=0;
    public boolean startDownload(String url){
        boolean result=manager.addDownload(url);
        return result;
    }
    public void pauseDownload(int i){
        System.out.println("UIThread.pauseDownload()");
        manager.pauseDownload(i);
    }
    public void resumeDownload(int i){
        System.out.println("UIThread.resumeDownload()");
        manager.resumeDownload(i);
    }

    public void onStartButtonClicked(){
        System.out.println("UIThread.onStartButtonClicked()");
        String url=field.getText();
        System.out.println(url);
        boolean result=startDownload(url);
        Download download=manager.getDownloads().get(downloads++);
        view.getItems().add(download);
        if (result){
            field.setText("Downloading...");
        }
        else{
            field.setText("Invalid Url.");
        }
    }
    public void addProgressBar(Download download){

    }
    public void onPauseButtonClicked(){
        System.out.println("UIThread.onToggleButtonClicked()");
        int index=getSelecteditem();
        pauseDownload(index);
        field.setText("Download Paused.");

    }
    public int getSelecteditem(){
        ObservableList<Download> alldownloads=view.getItems();
        Download download=view.getSelectionModel().getSelectedItem();
        int index=alldownloads.indexOf(download);
        return index;
    }
    public void deleteDownload(int i){
        System.out.println("UIThread.deleteDownload()");
        downloads--;
        manager.deleteDownload(i);

    }
    public static void main(String[] args) {
        launch(args);
    }
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root= loader.load();
        UIThread thread=loader.getController();
        manager.setThread(thread);
        Scene scene=new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    @Override
    public void notifyObserver(String message,int id) {
        System.out.println(message);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        name.setCellValueFactory(new PropertyValueFactory<>("url"));
        Progress.setCellValueFactory(new PropertyValueFactory<Download, Double>("progress"));
        Progress.setCellFactory(ProgressBarTableCell.forTableColumn());
        ContextMenu menu=new ContextMenu();
        MenuItem pause=new MenuItem("Pause");
        pause.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index=getSelecteditem();
                manager.pauseDownload(index);
            }
        });

        MenuItem resume=new MenuItem("Resume");
        resume.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index=getSelecteditem();
                manager.resumeDownload(index);
            }
        });

        MenuItem delete=new MenuItem("Delete");
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Download> alldownloads,selected;
                alldownloads=view.getItems();
                selected=view.getSelectionModel().getSelectedItems();
                Download download=view.getSelectionModel().getSelectedItem();
                int index=getSelecteditem();
                downloads--;
                manager.deleteDownload(index);
                alldownloads.remove(download);
                selected.forEach(alldownloads::remove);
            }
        });
        menu.getItems().addAll(pause,resume,delete);
        view.setContextMenu(menu);
    }

}
