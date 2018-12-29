package api.repository;

import api.Entity.Opentime;
import lombok.Data;
import org.apache.hadoop.hdfs.web.resources.GetOpParam;
import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OpentimeRepository extends JpaRepository<Opentime,Integer> {
    Opentime findById(int id);

    @Query(value = "select * from opentime where opens<=? and opene>=?",nativeQuery = true)
    Opentime findByOpensLessThanAndOpeneGreaterThan(long start,long end);

    @Query(value = "select * from opentime where openas<=? and openae>=?",nativeQuery = true)
    Opentime findByOpenasLessThanAndOpenaeGreaterThan(long start,long end);

}
