package com.dm.Application;

import javafx.concurrent.Task;
import org.apache.commons.io.FilenameUtils;

import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by ps on 14/1/17.
 */
public class Download extends Task<Void>{
    private String url;
    public Double progress;
    private int filesize;
    private STATE state;
    private Observer observer;
    public  Object monitor;
    private String ThreadName;
    private int id;

    public static enum STATE{
        DOWNLOADING,PAUSE,STOP;
    }
    public Download(String url,Observer observer,Object monitor){
        this.url=url;
        this.observer=observer;
        this.monitor=monitor;
        progress=new Double(0.0d);
    }

    @Override
    protected Void call() throws Exception {
        progress=0.5;
        updateProgress(0,0);
        System.out.println(this.ThreadName+":"+"Starting download.");
        //String url="http://download.ap.bittorrent.com/track/beta/endpoint/utserver/os/linux-x64-debian-7-0";
        /**
         *http://files.downloadnow.com/s/software/13/48/25/84/teracopy.exe?token=1487910840_d606c36f1e9948ec5a3dbf16b98e4872&fileName=teracopy.exe
         */
        byte buff[];
        int index=0;
        final int MAX_SIZE=1024;
        RandomAccessFile accessFile=null;
        InputStream stream=null;
        int downloaded=0;
        HttpURLConnection http=null;
        try {
            URL url1=new URL(getUrl());
            http= (HttpURLConnection) url1.openConnection();
            http.connect();
            String raw=http.getHeaderField("Content-Disposition");
            String filename=getFileName(getUrl(),raw);
            System.out.println(filename);
            System.out.println(http.getResponseCode());
            if(http.getResponseCode()!=200){
            }
            stream=http.getInputStream();
            filesize=http.getContentLength();
            System.out.println(filesize);
            accessFile=new RandomAccessFile(DownloadManager.DOWNLOAD_DIR+filename,"rw");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(!geState().equals(STATE.STOP)) {

            while (geState().equals(STATE.DOWNLOADING)) {
                if ((int) (filesize - downloaded) > MAX_SIZE) {
                    buff = new byte[MAX_SIZE];
                } else {
                    buff = new byte[(int) (filesize - downloaded)];
                }
                try {
                    index = stream.read(buff);

                    if (index == -1) {
                        System.out.println(this.ThreadName+":"+"Breaked.");
                        setState(STATE.STOP);
                        break;
                    }
                    accessFile.write(buff, 0, index);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                downloaded += index;
               this.progress+=downloaded;
               updateProgress(downloaded,filesize);
            }
            if (geState().equals(STATE.PAUSE)) {
                updateProgress(downloaded,filesize);
                synchronized (monitor) {
                    try {
                        System.out.println(this.ThreadName+":"+"Task Paused.");
                        monitor.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println(this.ThreadName+":"+"Task Stopped.");
        //updateProgress(1,1);
        http.disconnect();
        accessFile.close();
        return null;
    }

    public synchronized void setState(STATE state){
        this.state=state;
    }
    public String getFileName(String url,String rawHeader){
        String filename="";
        filename=FilenameUtils.getName(url);
        int pos=url.indexOf('?');
        if(pos!=-1){
            filename=filename.substring(0,filename.indexOf('?'));
            System.out.println(filename);
        }
        if(filename.indexOf(".")==-1){
            if(rawHeader!=null && rawHeader.indexOf("=")!=-1){
                String args[]=rawHeader.split("=");
                if(args.length>=1){
                    filename=args[1];
                }
                else{
                    filename=DownloadManager.DEFAULT_FILENAME;
                }
            }
        }
        return filename;
    }

    public Object getMonitor() {
        return monitor;
    }
    public synchronized Download.STATE geState() {
        return state;
    }
    public String getUrl(){
        return url;
    }

    public void setThreadName(String threadName){
        this.ThreadName=threadName;
    }

    public void notifyObserver(String message,int id){
        observer.notifyObserver(message,id);
    }

}
