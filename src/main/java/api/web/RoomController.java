package api.web;

import api.Dto.BoundDto;
import api.Dto.OpenTimeDto;
import api.Dto.TimeDto;
import api.Entity.Meeting;
import api.Entity.Opentime;
import api.Entity.Room;
import api.repository.AccountRepository;
import api.repository.MeetingRepository;
import api.repository.OpentimeRepository;
import api.repository.RoomRepository;
import api.utils.BeanUtils;
import api.utils.ResultUtil;
import api.utils.TimeUtils;
import api.vo.MeetingVO;
import api.vo.Result;
import org.omg.PortableServer.LIFESPAN_POLICY_ID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/room")
public class RoomController {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private OpentimeRepository opentimeRepository;


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
    public Result deleteRoom(String room,HttpServletRequest session){
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
        String room=updateRoom.getId();
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
    public Result aselectRoomById(String room,HttpServletRequest session){
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
    public Result uselectRoomById(String room){
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
        String dayE=TimeUtils.getDataE(timeDto.getStarttime());
        List<String> rooms2 = new ArrayList<>();
        List<Room> rooms1=roomRepository.findAll();
            rooms1.stream().forEach(room -> {
            String opentime = room.getOpentime();
            String state = TimeUtils.getDataState(opentime, TimeUtils.getDataE(timeDto.getStarttime()));

            if (TimeUtils.getOneDayTimestamps(timeDto.getStarttime()) <= 60 * 1000 * 60 * 4 &&
                    TimeUtils.getOneDayTimestamps(timeDto.getEndtime()) <= 60 * 1000 * 60 * 4) {
                if (state.charAt(0) == '0') {
                    rooms2.add(room.getId());
                } else if (opentimeRepository.findByOpensLessThanAndOpeneGreaterThan(timeDto.getStarttime(), timeDto.getEndtime()) == null)
                    rooms2.add(room.getId());
            }
            if (TimeUtils.getOneDayTimestamps(timeDto.getStarttime()) >= 60 * 1000 * 60 * 4 &&
                    TimeUtils.getOneDayTimestamps(timeDto.getEndtime()) >= 60 * 1000 * 60 * 4) {
                if (state.charAt(1) == '0') {
                    rooms2.add(room.getId());
                } else if (opentimeRepository.findByOpenasLessThanAndOpenaeGreaterThan(timeDto.getStarttime(), timeDto.getEndtime()) == null)
                    rooms2.add(room.getId());
            }
        });
        Long nstarttime = timeDto.getStarttime()-20*60*1000;
        Long nendtime = timeDto.getEndtime()+20*60*1000;
        List<Meeting> meetings = meetingRepository.findByStateLessThanEqualAndStarttimeBetweenAndEndtimeBetween(3,nstarttime,nendtime,nstarttime,nendtime);
        if (meetings.size()==0){
            return ResultUtil.Success(roomRepository.findAll());
        }
        meetings.stream().forEach(meeting -> {
            rooms2.add(meeting.getRoom());
        });

        List<Room> rooms=roomRepository.findByIdNotInOrderById(rooms2);

        return ResultUtil.Success(rooms);
    }


    
    @PostMapping("/selectbyall")
    public Result selectByAll(@RequestBody Meeting meetingDto){
        Long nstarttime = meetingDto.getStarttime()-20*60*1000;
        Long nendtime = meetingDto.getEndtime()+20*60*1000;
        List<Meeting> meetings = meetingRepository.findByStateLessThanEqualAndStarttimeBetweenAndEndtimeBetween(3,nstarttime,nendtime,nstarttime,nendtime);
        if (meetings.size()==0){
            return ResultUtil.Success(roomRepository.findRoomsBySizeGreaterThanEqual(meetingDto.getSize()));
        }
        List<String> list =meetings.stream().filter(meeting ->{return meeting.getSize()>=meetingDto.getSize();}).map(meeting -> {
            return meeting.getRoom();
        }).collect(Collectors.toList());

        List<Room> rooms=roomRepository.findByIdNotInOrderById(list);
        return ResultUtil.Success(rooms);
    }

    public Result selectRoomById(String room){
        Room finroom=roomRepository.findById(room);
        if (finroom!=null){
            return ResultUtil.Success(finroom);
        }
        else return ResultUtil.Error("找不到会议室");

    }

    public Result selectRoomBySize(int size){
        List<Room> rooms=roomRepository.findRoomsBySizeGreaterThanEqual(size);
        if (rooms.size()!=0)
            return ResultUtil.Success(rooms);
        else return ResultUtil.Error("没有满足条件的会议室");
    }

    @PostMapping("/boundroom")
    public Result roomBound(@RequestBody BoundDto boundDto,HttpServletRequest session){
        Room room=roomRepository.findById(boundDto.getRoom());
        if (room != null) {
            room.setDevice(boundDto.getDevice());
            roomRepository.save(room);
            return ResultUtil.Success();
        }
        else return ResultUtil.Error("失败");

    }


    @GetMapping("/todayuse")
    public Result todayUse(String room){
        Long todayzero= TimeUtils.getTodayZeroPointTimestamps();
        Long after=todayzero+60*60*24*1000;
        if(meetingRepository.findByRoomAndStarttimeBetweenOrderByStarttime(room,todayzero,after)==null)
            return ResultUtil.Error("当天没有会议");
        List<Meeting> meetings=meetingRepository.findByRoomAndStarttimeBetweenOrderByStarttime(room,todayzero,after);
        List<MeetingVO> meetings1=meetings.stream().map(meeting -> {
            MeetingVO meetingVO=new MeetingVO();
            BeanUtils.copyProperties(meeting,meetingVO);
            meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
            return meetingVO;
        }).collect(Collectors.toList());
        return ResultUtil.Success(meetings1);
    }


    @GetMapping("/nowuse")
    public Result nowUse(String room){
        Long currentTimestamps=System.currentTimeMillis();
        if (meetingRepository.findByRoomAndStarttimeLessThanAndEndtimeGreaterThan(room,currentTimestamps,currentTimestamps)==null)
            return ResultUtil.Error("现在没有会议");
        Meeting meeting=meetingRepository.findByRoomAndStarttimeLessThanAndEndtimeGreaterThan(room,currentTimestamps,currentTimestamps);
        MeetingVO meetingVO=new MeetingVO();
        BeanUtils.copyProperties(meeting,meetingVO);
        meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
        return ResultUtil.Success(meetingVO);
    }

    @PostMapping("/settime")
    public Result setTime(@RequestBody Opentime opentime,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        opentime.setId(1);
        if (opentimeRepository.save(opentime)==null){
            return ResultUtil.Error("更新失败");
        }
        else return ResultUtil.Success();
    }


    @GetMapping("/selecttime")
    public Result selectTime(HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        if (opentimeRepository.findById(1)==null){
            return ResultUtil.Error("您还未设置时间");
        }
        else return ResultUtil.Success(opentimeRepository.findById(1));
    }

    @PostMapping("/selectbyopentime")
    public Result selectByOpenTime(@RequestBody OpenTimeDto openTimeDto,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        List<Room> rooms=roomRepository.findAll();
        List<Room> rooms1=rooms.stream().filter(room -> {
            String opentime=room.getOpentime();
            String state=TimeUtils.getDataState(opentime,openTimeDto.getWeek());
            return state.charAt(openTimeDto.getMoring())==1;
        }).collect(Collectors.toList());
        if (rooms1.size()==0){
            return ResultUtil.Error("没有开放的会议室");
        }
        else return ResultUtil.Success(rooms1);
    }


}
