package api.repository;

import api.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room,Integer> {
    Room findById(String room);
    List<Room> findRoomsBySizeGreaterThanEqual(int size);


    List<Room> findByIdNotInOrderById(List<String> list);

    List<Room> findByIdNotInAndSizeGreaterThanEqualOrderById(List<String> list,int size);
}
