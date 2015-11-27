package com.readtxt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iflytek.cloud.speech.SpeechConstant;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechSynthesizer;
import com.iflytek.cloud.speech.SpeechUtility;
import com.iflytek.cloud.speech.SynthesizeToUriListener;


public class Sentence {
	private static Sentence sentence;
	private static int paraCount = 0;//用来计段数
	private static String txtLine = null;
	private static BufferedReader bufferedReader;
	private static SpeechSynthesizer speechSynthesizer;
	private int count = 0;//用来计句子数
	private static List<Map<String, Object>> wavList = new ArrayList<Map<String,Object>>();//存放wav文件名和时长
	private static double txtAllDuration = 0.0;//初始化整章语音的时长
	private static List<AudioInputStream> audioInputStreamsList = new ArrayList<AudioInputStream>();
	static Logger logger = LogManager.getLogger(Sentence.class.getName());
	public static Sentence getSentenceObj(){
		if(sentence == null){
			sentence = new Sentence();
		}
		return sentence;
	}
	
	//读取文本文件
	public static void readTxtFile(String filePath) throws IOException{
		String encoding = "GBK";
		File file = new File(filePath);

		if(file.isFile() && file.exists()){
			InputStreamReader inputStreamReader = new InputStreamReader(
					new FileInputStream(file), encoding);
			bufferedReader = new BufferedReader(inputStreamReader);
			getLine();
		}
	}
	
	//按段读文本文件，读下一段
	public static void getLine(){
		logger.info(Thread.currentThread().getName());
		try {
			if((txtLine = bufferedReader.readLine()) != null){

				String tmp = txtLine.replaceAll("　", " ").trim();
				if(!tmp.equals("")){
					paraCount++;
					System.out.println("*************"+paraCount);
					sepLine(tmp);//给一段分句子
				}else{
					getLine();
				}
			}else{
				System.out.println("*************终于读完啦");
				SynthesizeAllWav();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//将每段分为句子
	public static void sepLine(String txtLine){
		String parts[] = txtLine.split("(?<=[.!?。？！])\\s*(?=(?:[\"“(（].*[\"”）)]|[^\"”“()（）])*$)");
		//将分好的句子转为语音
		getSentenceObj().Synthesize(parts, paraCount+"");
		for(int i = 0; i < parts.length; i++) {
			System.out.println(parts[i]);
		}
	}
	
	/*********************转换音频**********************/
	/**
	 * 合成
	 */
	public void Synthesize(final String[] txt, final String mainName) {
		//创建SpeechSynthesizer对象
		speechSynthesizer = SpeechSynthesizer.createSynthesizer();
		
		//合成参数设置
		speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");// 设置发音人
		speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");// 设置语速，范围0~100
		speechSynthesizer.setParameter(SpeechConstant.PITCH, "50");// 设置语调，范围0~100
		speechSynthesizer.setParameter(SpeechConstant.VOLUME, "50");// 设置音量，范围0~100
		
		/**
		 * 合成监听器
		 * 1.普通生成pcm
		 * 2.借助工具类把合成后的pcm音频转换成wav，再将wav转换成mp3
		 */
		SynthesizeToUriListener synthesizeToUriListener = new SynthesizeToUriListener() {
			
		    // progress为合成进度0~100
		    public void onBufferProgress(int progress) {
		    	DebugLog.Log("*************合成进度*************" + progress);
		    	logger.info("*************合成进度*************" + progress);
		    	
		    }

		    // 会话合成完成回调接口
		    // uri为合成保存地址， error为错误信息，为null时表示合成会话成功
		    public void onSynthesizeCompleted(String uri, SpeechError error) {
					logger.info(Thread.currentThread().getName() + "start");
					
					if (error == null) {
						File wavFile = new File(uri);
						if (wavFile.exists() && wavFile.isFile()) {
							try {
								// 生成的PCM文件写入WAV头部，得到wav文件
								WavWriter wavWriter;
								wavWriter = new WavWriter(wavFile, 16000);
								wavWriter.writeHeader();
								DebugLog.Log("*************合成成功*************");
								DebugLog.Log("合成音频生成路径：" + uri);
								
								logger.info("*************合成成功*************");
								logger.info("合成音频生成路径：" + uri);
								wavWriter.close();

								//将wav文件流存到list中
								try {
									AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(wavFile)));
									audioInputStreamsList.add(stream);
									stream.close();
								} catch (UnsupportedAudioFileException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
								//将wav文件信息存到list中
								Map<String, Object> map = new HashMap<String, Object>();
			                    map.put("wav_name", wavFile.getName());
			                    map.put("wav_duration", Speech.getWavDuration(wavFile));
			                    wavList.add(map);
								txtAllDuration += Speech.getWavDuration(wavFile);
								
								System.out.println(wavFile.getAbsoluteFile());
								//delFile(wavFile);

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								
							}
						}
						
					} else {
						DebugLog.Log("*************" + error.getErrorCode() + "*************");
						logger.error("*************" + error.getErrorCode() + "*************");
					}
					
					if (count < txt.length - 1) {//将每段按句子转化成音频
						count++;//句子标记，第几个句子
						speechSynthesizer.synthesizeToUri(txt[count],mainName + "_" + (count + 1) + ".wav",this);
					} else {//一段结束
						count = 0;
						getLine();
					}
					
					logger.info(Thread.currentThread().getName() + " end ");
		    }
		};
		
		// 设置合成音频保存位置（可自定义保存位置），默认保存在“./iflytek.pcm”
		speechSynthesizer.synthesizeToUri(txt[0], mainName+"_"+"1.wav",
				synthesizeToUriListener);
	}
	
	/**
	 * 将所有音频合成一段音频
	 * @throws IOException 
	 */
	 public static void SynthesizeAllWav() throws IOException
	 {

			logger.info("comein !!!!!!!!!");
			System.out.println(audioInputStreamsList.size());
			InputStream inputStream =audioInputStreamsList.get(0);
			inputStream = new SequenceInputStream(inputStream,audioInputStreamsList.get(1));
			AudioInputStream appendedFiles = new AudioInputStream(inputStream,audioInputStreamsList.get(0).getFormat(),audioInputStreamsList.get(0).getFrameLength() + audioInputStreamsList.get(1).getFrameLength());
			String dir = "E:\\Workspaces\\MyEclipse\\ReadTxt\\test1.wav";
			AudioSystem.write(appendedFiles, AudioFileFormat.Type.WAVE, new File(dir));
			
			
			/*String wavFile1 = "E:\\Workspaces\\MyEclipse\\ReadTxt\\1_1.wav";
		    String wavFile2 = "E:\\Workspaces\\MyEclipse\\ReadTxt\\2_1.wav";
		    AudioInputStream appendedFiles = null;
		    try {
				AudioInputStream clip1 = AudioSystem.getAudioInputStream(new File(wavFile1));
				AudioInputStream clip2 = AudioSystem.getAudioInputStream(new File(wavFile2));
				InputStream inputStream = new SequenceInputStream(clip1,clip2);
				appendedFiles = new AudioInputStream(inputStream,clip1.getFormat(),clip1.getFrameLength() + clip2.getFrameLength());

			} catch (UnsupportedAudioFileException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   
			String dir = "E:\\Workspaces\\MyEclipse\\ReadTxt\\test1.wav";
			AudioSystem.write(appendedFiles, AudioFileFormat.Type.WAVE, new File(dir));*/
			
		 /*	
			InputStream inputStream = null;					
			String dir = "test.wav";
			
			Iterator<AudioInputStream> it = audioInputStreamsList.iterator();
			long length = 0;
			int index = 0;
			while(it.hasNext()){
				AudioInputStream audioInputStream = it.next();
				if(index == 0) 
					inputStream = audioInputStream;
				else
					inputStream = new SequenceInputStream(inputStream, audioInputStream);
				length += audioInputStream.getFrameLength();
				
			}
			System.out.println("length = " + length);
			AudioInputStream appendedFiles = new AudioInputStream(inputStream,audioInputStreamsList.get(0).getFormat(),length);
			

			try {
				AudioSystem.write(appendedFiles, AudioFileFormat.Type.WAVE, new File(dir));
			} catch (IOException e) {
				System.out.println("Another thread's stream already closed");
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
			DebugLog.Log("listsize = " + audioInputStreamsList.size());
			DebugLog.Log("txtWavDuration = " + txtAllDuration);
			DebugLog.Log("duration = " + Speech.getWavDuration(new File(dir)));
			
			double allWavDuration = 0.0;
			Iterator<Map<String, Object>> iter = wavList.iterator();
			
			while(iter.hasNext()){
				
			    Map<String, Object> mapTmp =iter.next();
			    String wavName=(String) mapTmp.get("wav_name");
			    double wavDuration= (Double)mapTmp.get("wav_duration");
			    allWavDuration += wavDuration;
			    DebugLog.Log("wavName="+wavName+"\nwavDuration="+wavDuration);
			    DebugLog.Log("所有时长加起来有："+allWavDuration);
			    
			}
			DebugLog.Log("整章语音时长为："+txtAllDuration);*/
			
			
	 }
	
	
	/**
	 * 删除文件
	 * @param file 要删除的文件
	 */
	public static void delFile(File file) {

		if(file.getAbsoluteFile().delete()){//删除文件
        	DebugLog.Log("成功删除文件"+file.getName());
        	logger.info("成功删除文件"+file.getName());
        }else{
        	DebugLog.Log("无法删除文件"+file.getName()+"，可能被占用");
        	logger.info("无法删除文件"+file.getName()+"，可能被占用");
        }
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String filePath = "txt1.txt";
		try {
			//初始化语音服务
			SpeechUtility.createUtility(SpeechConstant.APPID+"=56531a92");//5627956e   563df3a0
			readTxtFile(filePath);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("读取文件失败");
			e.printStackTrace();
		}
	}

}
