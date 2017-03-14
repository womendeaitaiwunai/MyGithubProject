package com.pixelall.mylibrary;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.pixelall.mylibrary.MachineUtils.getTypeBRAND;
import static com.pixelall.mylibrary.MachineUtils.getTypeMODEL;


public class PixelLogUtil {

    public static final String TAG = "PixelLog";
	public class LOG_FILE_TYPE{
		public static final int SECOND = 1;
		public static final int MINUTE = 2;
		public static final int HOUR = 3;
	}

    // PixelLog实例
    private static PixelLogUtil INSTANCE = new PixelLogUtil();
    private Context mContext;
    //用来存储设备信息和异常信息
    private Map<String, String> infos = new HashMap<String, String>();
    //用于格式化日期,作为日志文件名的一部分
    private SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.CHINA);
    private SimpleDateFormat mFormatter = new SimpleDateFormat("yyyy年MM月dd日HH时mm分", Locale.CHINA);
    private SimpleDateFormat hFormatter = new SimpleDateFormat("yyyy年MM月dd日HH时", Locale.CHINA);


    private PixelLogUtil() {super();}

    public static PixelLogUtil getInstance() {
        if(INSTANCE==null) {
            INSTANCE = new PixelLogUtil();
        }
        return INSTANCE;
    }
    
    public void init(Context context) {
        mContext = context;
    }
    
    public void writeMessage(String strMsg,int LOG_FILE_TYPE){
    	File logFile = getPixelLogFile(LOG_FILE_TYPE);
        //到这里，日志的写入文件已经准备好了，可以开始写入操作
    	if(logFile!=null){
            FileWriter     fileWriter = null;
            BufferedWriter bufferedWriter = null;
            try {
            	fileWriter = new FileWriter(logFile,true ); // 续写不覆盖
                bufferedWriter = new BufferedWriter( fileWriter );
                // 得到当前日期时间的指定格式字符串
                String strDateTimeLogHead = sFormatter.format( new Date() );
                // 将日期时间头与日志信息体结合起来
                strMsg = "write in "+strDateTimeLogHead + "\r\n"+ strMsg + "\r\n\r\n";
               
                bufferedWriter.write( strMsg );
                bufferedWriter.flush();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}finally{
    			if ( null != fileWriter ){
    	            try {
    	            	fileWriter.close();
    	            }catch (IOException e) {
    	                e.printStackTrace();
    	            }
    	        }
    			if ( null != bufferedWriter ){
    	            try {
    	            	bufferedWriter.close();
    	            }catch (IOException e) {
    	                e.printStackTrace();
    	            }
    	        }
    		}
    	}else{
    		Log.w(TAG, "没有发现正常挂载的外置存储");
    		Log.w(TAG, "LogMessage:"+strMsg);
    	}
    }

    
    
	private File getPixelLogFile(int type) {
		String time = "pixel";
		switch (type) {
		case LOG_FILE_TYPE.MINUTE:
			time = mFormatter.format(new Date());
			break;
		case LOG_FILE_TYPE.HOUR:
			time = hFormatter.format(new Date());
			break;
		case LOG_FILE_TYPE.SECOND:
			time = sFormatter.format(new Date());
			break;
		}
		String fileName = getTypeBRAND()+"_"+getTypeMODEL()+"_"+time + ".log";
		File logFile = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory()+"/PixelLog/";
            File dir = new File(path);
            if (!dir.exists()) {
                if(dir.mkdirs()){
                	logFile = new File(path+fileName);
                	if (!logFile.exists()){
                		try {
    						logFile.createNewFile();
    					} catch (IOException e) {
    						e.printStackTrace();
    					}
                	}
                }
            }else{
            	logFile = new File(path+fileName);
            	if (!logFile.exists()){
            		try {
						logFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            }
            return logFile;
		}else{
            //没挂载正常外置存储
		}
		return null;
	}
}
