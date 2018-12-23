package api.vo;

import lombok.Data;

@Data
public class MeetingVO {
    private Integer id;


    private String theme;


    private long starttime;


    private long endtime;


    private String room;


    private String originator;


    private String originatorName;


    private int issigned;


    private int state;


    private int size;


}
