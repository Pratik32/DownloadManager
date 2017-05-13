package com.dm.Application;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ps on 5/1/17.
 */
public class DownloadManager{
    List<Download> downloads;
    public List<Object> threadmonitors;

    private UIThread thread;
    private static DownloadManager manager;
    protected static final String DOWNLOAD_DIR="/home/ps/DownloadManager/Downloads/";
    protected static final String DEFAULT_FILENAME="default";

    private DownloadManager(){
        downloads=new ArrayList<Download>();
        threadmonitors=new ArrayList<>();
    }
    public static DownloadManager getInstance(){
        if(manager==null){
            manager=new DownloadManager();
        }
        return manager;
    }
    public boolean addDownload(String url){
        if(!validateUrl(url)){
            return false;
        }
        Object object=new Object();
        this.threadmonitors.add(object);
        Download download=new Download(url,getThread(),object);
        this.downloads.add(download);
        scheduleDownload(download);
        return true;
    }
    private void  scheduleDownload(Download download){

        download.setState(Download.STATE.DOWNLOADING);
        Thread thread=new Thread(download);
        download.setThreadName(thread.getName());
        thread.start();
    }
    public void deleteDownload(int i){
        System.out.println(i);
        Download download=getDownloads().get(i);
        if(download.getStatus().equals(Download.STATE.STOP)){
            threadmonitors.remove(i);
            downloads.remove(i);
            return;
        }
        download.setState(Download.STATE.STOP);
    }
    private boolean validateUrl(String url){

        boolean result;
        if(url.length()<=10){
            result=false;
            return result;
        }
        if(url.substring(0,4).equals("http") || url.substring(0,5).equals("https")){
            result=true;
        }
        else{
            result=false;
        }
        return result;

    }
    public void pauseDownload(int i){
        Download download=downloads.get(i);
        download.setState(Download.STATE.PAUSE);
    }
    public void resumeDownload(int i){
        Download download=downloads.get(i);
        download.setState(Download.STATE.DOWNLOADING);
        Object object=this.threadmonitors.get(i);
        synchronized (object) {
            object.notify();
        }
    }
    public UIThread getThread() {
        return thread;
    }

    public void setThread(UIThread thread) {
        this.thread = thread;
    }

    public List<Download> getDownloads() {
        return downloads;
    }
}
