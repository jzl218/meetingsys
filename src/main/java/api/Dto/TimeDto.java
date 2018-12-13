package api.Dto;

import lombok.Data;

import javax.ws.rs.POST;

@Data
public class TimeDto {
    private long starttime;
    private long endtime;
}
