package api.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Room {

    @Id
    private Integer id;

//开放时间
    @Column
    private String opentime;

    @Column
    private int size;

    @Column
    private int device;

}
