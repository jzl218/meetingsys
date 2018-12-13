package api.repository;

import api.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room,Integer> {
    Room findById(int room);
    List<Room> findRoomsBySizeGreaterThanEqual(int size);
    List<Room> findByIdNotInOrderById(ArrayList list);
}
