package api.repository;

import api.entity.MeetingAcoount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingAccountRepository extends JpaRepository<MeetingAcoount,Integer> {
    List<MeetingAcoount> findByAccount(String account);


}
