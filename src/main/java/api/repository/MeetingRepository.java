package api.repository;

import api.Entity.Meeting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting,Integer> {
    List<Meeting> findAllByOrderByStarttime();

    Meeting findByRoom(String room);

    Meeting findByIdAndInvitecode(String room,String Invitecode);

    Meeting findById(int meeting);

    List<Meeting> findByStateLessThanEqual(int state);

    Meeting findByInvitecode(String invitecode);

    Meeting findByOriginator(String id);

    Meeting findByIdAndOriginator(int id,String originator);

    @Query(value = "select * from meeting where state=? and originator=? order by starttime desc limit ?,?",nativeQuery = true)
    List<Meeting> findByOriginatorAndState(int state,String originator,int page,int size);

    @Query(value = "select * from meeting where state=:state and id in (:id) order by starttime desc limit :page,:sice",nativeQuery = true)
    List<Meeting> findByIdIn(@Param("state") int state, @Param("id") List<Integer> id, @Param("page") int page, @Param("sice") int size);


    List<Meeting> findByOriginatorAndState(String originator,int state);

    List<Meeting> findByIdInAndState(List<Integer> id,int state);

    List<Meeting> findByStarttimeBetween(long starttime ,long endtime);

}
