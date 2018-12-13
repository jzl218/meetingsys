package api.repository;

import api.entity.Meeting;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.ws.rs.POST;
import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting,Integer> {
    List<Meeting> findAllByOrderByStarttime();

    Meeting findByRoom(int room);

    Meeting findByIdAndInvitecode(int room,String Invitecode);

    Meeting findById(int meeting);

    List<Meeting> findByStateLessThanEqual(int state);

    Meeting findByInvitecode(String invitecode);

    @Query(value = "select * from meeting where state=? and originator=? order by starttime desc limit ?,?",nativeQuery = true)
    List<Meeting> findByOriginatorAndState(int state,String originator,int page,int size);

    @Query(value = "select * from meeting where state=? and id in ? order by starttime desc limit ?,?",nativeQuery = true)
    List<Meeting> findByIdIn(int state,List<Integer> id,int page,int size);


    List<Meeting> findByOriginatorAndState(String originator,int state);

    List<Meeting> findByIdInAndState(List<Integer> id,int state);
}
