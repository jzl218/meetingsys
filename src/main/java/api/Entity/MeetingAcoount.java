package api.Entity;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity
@IdClass(ConsumerGroupMapPK1.class)
@Data
public class MeetingAcoount implements Serializable {
    @Id
    private int meeting;

    @Id
    private String account;

    @Column
    private long signtime;

}
