package api.Entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Opentime {
    @Id
    private int id;

    @Column
    private long opens;

    @Column
    private long opene;

    @Column
    private long openas;

    @Column
    private long openae;
}
