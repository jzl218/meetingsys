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
        return time-getOneDayZeroPointTimestamps(time)-60*1000*60*8;
    }

    public static String getDataE(long time){
        SimpleDateFormat sdw=new SimpleDateFormat("E");
        return sdw.format(time);
    }


    public static String getDataState(String opentime,String day){

        System.out.println(opentime.substring(0,2));
        System.out.println(opentime.substring(2,4));
        System.out.println(opentime.substring(4,6));
        System.out.println(opentime.substring(6,8));
        System.out.println(opentime.substring(8,10));
        System.out.println(opentime.substring(10,12));
        System.out.println(opentime.substring(12,14));
        String num=null;
        if (day.equals("Mon")){
            num=opentime.substring(0,2);
        }
        if (day.equals("Tue")){
            num=opentime.substring(2,4);
        }
        if (day.equals("Wed")){
            num=opentime.substring(4,6);
        }
        if (day.equals("Thu")){
            num=opentime.substring(6,8);
        }
        if (day.equals("Fri")){
            num=opentime.substring(8,10);
        }
        if (day.equals("Sat")){
            num=opentime.substring(10,12);
        }
        if (day.equals("Sun")){
            num=opentime.substring(12,14);
        }
        return num;
    }




}
