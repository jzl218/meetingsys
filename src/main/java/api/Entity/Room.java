package api.Entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Room {

    @Id
    private String id;

//开放时间
    @Column
    private String opentime="00000000000000";

    @Column
    private int size;

    @Column
    private int device;

}
