package api.repository;

import api.Entity.MeetingAcoount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Repository
public interface MeetingAccountRepository extends JpaRepository<MeetingAcoount,Integer> {
    List<MeetingAcoount> findByAccount(String account);

    List<MeetingAcoount> findByMeeting(int meeting);

    void deleteByAccount(String account);

    void deleteByMeetingAndAccount(int meeting,String account);

    MeetingAcoount findByMeetingAndAccount(int meeting,String account);
}
