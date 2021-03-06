package api.Entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Meeting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String theme;

    @Column
    private long starttime;

    @Column
    private long endtime;

    @Column
    private String room;

    @Column
    private String originator;

    @Column
    private int issigned;

    @Column
    private int state;

    @Column
    private int isentered;

    @Column
    private String invitecode;

    @Column
    private int size;

}
