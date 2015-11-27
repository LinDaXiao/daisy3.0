package com.readtxt;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class ConractWav {
	private static List<AudioInputStream> audioInputStreamsList = new ArrayList<AudioInputStream>();
	public static void main(String args[]){
			
		String wavFile1 = "E:\\Workspaces\\MyEclipse\\ReadTxt\\1_1.wav";
	    String wavFile2 = "E:\\Workspaces\\MyEclipse\\ReadTxt\\2_1.wav";
	    try {

		    AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(wavFile1)));
			audioInputStreamsList.add(stream);

			stream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(wavFile2)));
			audioInputStreamsList.add(stream);

		    stream.close();
		    
		    
		    AudioInputStream appendedFiles = 
                            new AudioInputStream(
                                new SequenceInputStream(audioInputStreamsList.get(0), audioInputStreamsList.get(1)),     
                                audioInputStreamsList.get(0).getFormat(), 
                                audioInputStreamsList.get(0).getFrameLength() + audioInputStreamsList.get(1).getFrameLength());
		   
		    String dir = "E:\\Workspaces\\MyEclipse\\ReadTxt\\test1.wav";

		    AudioSystem.write(appendedFiles, 
                            AudioFileFormat.Type.WAVE, 
                            new File(dir));
		   
	    } catch (Exception e) {
		    e.printStackTrace();
	    }
	}
}
