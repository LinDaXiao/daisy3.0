package com.readtxt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Speech {
	private static Speech speech;
	
	public static Speech getSpeechObj(){
		if(speech == null){
			speech = new Speech();
		}
		return speech;
	}
	
	/**   
     * 将产生的wav文件转换为容量较小的mp3格式   
     * @param   wavFile    要转换的wav文件名    
     */    
    public static double wav2Mp3(File wavFile){
    	String[] tmpStr = wavFile.getPath().split("[.]");
        String tarFileName = tmpStr[0] + ".mp3";
        Runtime run = null;  
        double mp3Duration = 0.0;
        try {  
            run = Runtime.getRuntime();  
            long start=System.currentTimeMillis(); 
            //调用解码器来将wav文件转换为mp3文件  
            Process p=run.exec("lame -b 16 "+wavFile.getName()+" "+tarFileName); //16为码率，可自行修改  
            //释放进程  
            p.getOutputStream().close();  
            p.getInputStream().close();  
            p.getErrorStream().close();  
            p.waitFor();  
            long end=System.currentTimeMillis();  
            System.out.println("convert need costs:"+(end-start)+"ms");
        } catch (Exception e) {  
            e.printStackTrace();  
        }finally{  
            //最后都要执行的语句  
            //run调用lame解码器最后释放内存  
            run.freeMemory();  
        }
        return mp3Duration;
    }
    
    /**
     * 获得wav文件的时长
     * @param file wav文件
     */
    public static double getWavDuration(File file) {
    	double durationInSeconds = 0.0;
    	AudioInputStream audioInputStream;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(file)));
			AudioFormat format = audioInputStream.getFormat();
	    	long frames = audioInputStream.getFrameLength();
	    	durationInSeconds = (frames+0.0) / format.getFrameRate();
	    	audioInputStream.close();
	    	
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return durationInSeconds;
    }
}
