package api.Dto;

import lombok.Data;

import javax.persistence.Column;
@Data
public class MeetingDto {

    private String theme;


    private long starttime;


    private long endtime;


    private int room;




    private int issigned;


    private int isentered;


}
