package api.web;

import api.Dto.BoundDto;
import api.Dto.RoomDto;
import api.Dto.TimeDto;
import api.entity.Meeting;
import api.entity.Room;
import api.repository.MeetingRepository;
import api.repository.RoomRepository;
import api.utils.BeanUtils;
import api.utils.ResultUtil;
import api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MeetingRepository meetingRepository;

    @PostMapping("/add")
    public Result addRoom(@RequestBody Room roomDto, HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        Room room=new Room();
        BeanUtils.copyProperties(roomDto,room);
        Room roomVo=roomRepository.save(room);
        if (roomVo!=null)
        return  ResultUtil.Success(roomVo);
        else {return ResultUtil.Error("添加失败");}
    }


    @GetMapping("/delete")
    public Result deleteRoom(int room,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        Room delroom=roomRepository.findById(room);
        roomRepository.delete(delroom);
        return ResultUtil.Success(delroom);
    }

    @PostMapping("/update")
    public Result updateRoom(@RequestBody Room updateRoom,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        int room=updateRoom.getId();
        Room uproom=roomRepository.findById(room);
        BeanUtils.copyProperties(updateRoom,uproom);
        Room roomvo=roomRepository.save(uproom);
        if (roomvo!=null)
            return ResultUtil.Success(roomvo);
        else return ResultUtil.Error("更新失败");

    }

    @GetMapping("/select")
    public Result selectRoom(HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        List<Room> rooms=roomRepository.findAll();
        return ResultUtil.Success(rooms);
    }

        @GetMapping("/aselectbyid")
    public Result aselectRoomById(int room,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        return selectRoomById(room);
    }

    @GetMapping("/aselectbysize")
    public Result aselectRoomBySize(int size,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        return selectRoomBySize(size);
    }


    @GetMapping("/uselectbyid")//TODO
    public Result uselectRoomById(int room){
        return selectRoomById(room);
    }

    @GetMapping("/uselectbysize")//TODO
    public Result uslelectRoomBySize(int size){
        return selectRoomBySize(size);
    }

    @GetMapping("/selectAll")//TODO
    public Result selectAll(){
        List<Room> rooms=roomRepository.findAll();
        return ResultUtil.Success(rooms);
    }

    @PostMapping("/selectbytime")//TODO
    public Result  selectRoomByMeetingTime(@RequestBody TimeDto timeDto) {
        Long starttime = timeDto.getStarttime();
        Long endtime = timeDto.getEndtime();
        ArrayList list = new ArrayList();
        List<Meeting> meetings = meetingRepository.findByStateLessThanEqual(3);
        meetings.stream().forEach(meeting -> {
            Long nstarttime = meeting.getStarttime() - 20 * 60 * 1000;
            Long nendttime = meeting.getEndtime() + 20 * 60 * 1000;
            if ((starttime < nendttime && starttime > nstarttime) || (endtime > nstarttime && endtime < nendttime))
                list.add(meeting.getId());
        });
        List<Room> rooms=roomRepository.findByIdNotInOrderById(list);
        return ResultUtil.Success(rooms);
    }





    public Result selectRoomById(int room){
        Room finroom=roomRepository.findById(room);
        if (finroom!=null){
            return ResultUtil.Success(finroom);
        }
        else return ResultUtil.Error("找不到会议室");

    }

    public Result selectRoomBySize(int size){
        List<Room> rooms=roomRepository.findRoomsBySizeGreaterThanEqual(size);
        if (rooms!=null)
            return ResultUtil.Success(rooms);
        else return ResultUtil.Error("找不到会议室");
    }

    @PostMapping("/boundroom")
    public Result roomBound(@RequestBody BoundDto boundDto,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        Room room=roomRepository.findById(boundDto.getRoom());
        if (room != null) {
            room.setDevice(boundDto.getDevice());
            roomRepository.save(room);
            return ResultUtil.Success();
        }
        else return ResultUtil.Error("失败");

    }





}
