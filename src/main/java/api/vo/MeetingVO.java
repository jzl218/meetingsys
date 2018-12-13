package api.vo;

import lombok.Data;

@Data
public class MeetingVO {
    private Integer id;


    private String theme;


    private long starttime;


    private long endtime;


    private int room;


    private int originator;


    private int issigned;


    private int state;


}
