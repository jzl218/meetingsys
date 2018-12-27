package api.utils;

import java.text.SimpleDateFormat;

public class TimeUtils {
    public static Long getTodayZeroPointTimestamps(){
        Long currentTimestamps=System.currentTimeMillis();
        Long oneDayTimestamps= Long.valueOf(60*60*24*1000);
        return currentTimestamps-(currentTimestamps+60*60*8*1000)%oneDayTimestamps;
    }

    public static Long getOneDayZeroPointTimestamps(long time){
        Long oneDayTimestamps= Long.valueOf(60*60*24*1000);
        return time-(time+60*60*8*1000)%oneDayTimestamps;
    }

    public static Long getOneDayTimestamps(long time){
        Long oneDayTimestamps= Long.valueOf(60*60*24*1000);
        return (time+60*60*8*1000)%oneDayTimestamps;
    }

    public static String getDataE(long time){
        SimpleDateFormat sdw=new SimpleDateFormat("E");
        return sdw.format(time);
    }


    public static String getDataState(String opentime,String day){
        String num=null;
        if (day.equals("Mon")){
            num=opentime.substring(0,1);
        }
        if (day.equals("Tue")){
            num=opentime.substring(2,3);
        }
        if (day.equals("Wed")){
            num=opentime.substring(4,5);
        }
        if (day.equals("Thu")){
            num=opentime.substring(6,7);
        }
        if (day.equals("Fri")){
            num=opentime.substring(8,9);
        }
        if (day.equals("Sat")){
            num=opentime.substring(10,11);
        }
        if (day.equals("Sun")){
            num=opentime.substring(12,13);
        }
        return num;
    }




}
