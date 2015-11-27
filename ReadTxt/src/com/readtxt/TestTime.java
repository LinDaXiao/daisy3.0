package com.readtxt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;


public class TestTime {
	public static AudioInputStream appendedFiles;
	private static List<AudioInputStream> audioInputStreamsList = new ArrayList<AudioInputStream>();
		//private static Logger logger = LogManager.getLogger(TestTime.class.getName());//Logger.getLogger(TestTime.class);
		public static void main(String args[]){
			
			
			
			String wavFile1 = "E:\\Workspaces\\MyEclipse\\ReadTxt\\1_1.wav";
		    String wavFile2 = "E:\\Workspaces\\MyEclipse\\ReadTxt\\2_1.wav";
		    String wavFile3 = "E:\\Workspaces\\MyEclipse\\ReadTxt\\2_2.wav";
		    String wavFile4 = "E:\\Workspaces\\MyEclipse\\ReadTxt\\2_3.wav";
		    try {
			    AudioInputStream clip1 = AudioSystem.getAudioInputStream(new File(wavFile1));
			    AudioInputStream clip2 = AudioSystem.getAudioInputStream(new File(wavFile2));
			    AudioInputStream clip3 = AudioSystem.getAudioInputStream(new File(wavFile3));
			    
			    AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(wavFile1)));
				audioInputStreamsList.add(stream);
				stream.close();
				stream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(wavFile2)));
				audioInputStreamsList.add(stream);
				stream.close();
				stream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(wavFile3)));
				audioInputStreamsList.add(stream);
				
			    /*System.out.println(audioInputStreamsList.get(0));
			    InputStream inputStream = new SequenceInputStream(audioInputStreamsList.get(0), audioInputStreamsList.get(1));
			    System.out.println(inputStream);*/
			    
			    //stream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(wavFile4)));
			    //audioInputStreamsList.add(stream);
				//stream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(wavFile3)));
			    stream.close();
			    
			    AudioInputStream stream1 = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(wavFile3)));
			    appendedFiles = 
	                            new AudioInputStream(
	                                new SequenceInputStream(new SequenceInputStream(audioInputStreamsList.get(0), audioInputStreamsList.get(1)),audioInputStreamsList.get(2)),     
	                                audioInputStreamsList.get(0).getFormat(), 
	                                audioInputStreamsList.get(0).getFrameLength() + audioInputStreamsList.get(1).getFrameLength() + audioInputStreamsList.get(2).getFrameLength());
			   
			    System.out.println(audioInputStreamsList.get(0).getFrameLength() + audioInputStreamsList.get(1).getFrameLength() + audioInputStreamsList.get(2).getFrameLength());
			    
			    System.out.println(appendedFiles.toString());
			    String dir = "E:\\Workspaces\\MyEclipse\\ReadTxt\\test.wav";
			    String dir1 = "E:\\Workspaces\\MyEclipse\\ReadTxt\\test1.wav";
			    
			    AudioSystem.write(appendedFiles, 
	                            AudioFileFormat.Type.WAVE, 
	                            new File(dir));
			    //AudioSystem.write(appendedFiles, AudioFileFormat.Type.WAVE, new OutputStream()) ;
			     
			    //合并多个文件
			   /* AudioInputStream appendOneFile = new AudioInputStream(new SequenceInputStream(clip1,null),
			    								clip1.getFormat(),
			    								clip1.getFrameLength());
			    AudioSystem.write(appendOneFile, AudioFileFormat.Type.WAVE, new File(dir1));*/
			    
			    
			   /* File wavFile = new File(dir);
			    System.out.println(Speech.getWavDuration(wavFile));
			    double time1 = Speech.getWavDuration(new File(wavFile1)) + Speech.getWavDuration(new File(wavFile2)) + Speech.getWavDuration(new File(wavFile3));
			    System.out.println(time1);*/
		    } catch (Exception e) {
			    e.printStackTrace();
		    }
		}
		
	/*	
	    */
    
}


