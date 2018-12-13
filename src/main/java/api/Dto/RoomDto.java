package api.Dto;

import lombok.Data;

import javax.persistence.Column;

@Data
public class RoomDto {
    private String opentime;


    private int size;


    private int device;
}
