package api.web;

import api.Dto.MeetingDto;
import api.Dto.OrderDto;
import api.Entity.Meeting;
import api.Entity.MeetingAcoount;
import api.Entity.Opentime;
import api.repository.*;
import api.sevice.AccountProviderImpl;
import api.utils.BeanUtils;
import api.utils.ResultUtil;
import api.utils.TimeUtils;
import api.vo.MeetingVO;
import api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private AccountProviderImpl accountProvider;
    @Autowired
    private MeetingAccountRepository meetingAccountRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private OpentimeRepository opentimeRepository;

    @GetMapping("/selectnoverify")
    public Result selectMeeting(HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        List<Meeting> allmeetings=meetingRepository.findAllByOrderByStarttime();
        List<Meeting> meetings1=allmeetings.stream().filter(meeting -> {
            return meeting.getState()==0;
        }).collect(Collectors.toList());
        List<MeetingVO> meetings=meetings1.stream().map(meeting -> {
            MeetingVO meetingVO=new MeetingVO();
            BeanUtils.copyProperties(meeting,meetingVO);
            meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
            return meetingVO;
        }).collect(Collectors.toList());
        if (meetings!=null)
            return ResultUtil.Success(meetings);
        else return ResultUtil.Error("失败");
    }

    @GetMapping("/selectpass")
    public Result selectMeeetingverify(HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        List<Meeting> allmeetings=meetingRepository.findAllByOrderByStarttime();
        List<Meeting> meetings1=allmeetings.stream().filter(meeting -> {
            return meeting.getState()==1;
        }).collect(Collectors.toList());
        List<MeetingVO> meetings=meetings1.stream().map(meeting -> {
            MeetingVO meetingVO=new MeetingVO();
            BeanUtils.copyProperties(meeting,meetingVO);
            meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
            return meetingVO;
        }).collect(Collectors.toList());
        if (meetings!=null)
            return ResultUtil.Success(meetings);
        else return ResultUtil.Error("失败");

    }

    @GetMapping("/selectnopass")
    public Result selectMeeetingNopass(HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        List<Meeting> allmeetings=meetingRepository.findAllByOrderByStarttime();
        List<Meeting> meetings1=allmeetings.stream().filter(meeting -> {
            return meeting.getState()==2;
        }).collect(Collectors.toList());
        List<MeetingVO> meetings=meetings1.stream().map(meeting -> {
            MeetingVO meetingVO=new MeetingVO();
            BeanUtils.copyProperties(meeting,meetingVO);
            meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
            return meetingVO;
        }).collect(Collectors.toList());
        if (meetings!=null)
            return ResultUtil.Success(meetings);
        else return ResultUtil.Error("失败");

    }
    @GetMapping("/verifymeeting")
    public Result verifyMeeting(int meeting,HttpServletRequest request) {
        if (request.getSession().getAttribute("admin") == null) {
            return ResultUtil.Error("请先登录");
        }
        Meeting meetingvo=meetingRepository.findById(meeting);
        meetingvo.setState(1);
        MeetingVO meetingVO=new MeetingVO();
        BeanUtils.copyProperties(meeting,meetingVO);
        meetingVO.setOriginatorName(accountRepository.findById(meetingvo.getOriginator()).getName());
        return ResultUtil.Success();
    }





    @GetMapping("/selectbyroom")
    public Result selectbyroom(String room,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        Meeting meeting=meetingRepository.findByRoom(room);
        if (meeting!=null)
            return ResultUtil.Success(meeting);
        else return ResultUtil.Error("无此会议室");
    }



    @PostMapping("/add")//TODO
    public Result addMeeting(@RequestBody MeetingDto meetingDto){
        String room=meetingDto.getRoom();
        Long starttime=meetingDto.getStarttime()-20*60*1000;
        Long endtime=meetingDto.getEndtime()+20*60*1000;
        List<Meeting> meetings=meetingRepository.findByStateLessThanEqualAndStarttimeBetweenAndEndtimeBetween(3,starttime,endtime,starttime,endtime) ;
        if (meetings.size()==0){
            Meeting meeting=new Meeting();
            BeanUtils.copyProperties(meetingDto,meeting);
            meeting.setInvitecode(UUID.randomUUID().toString());
            meeting.setState(0);
            meeting.setOriginator(accountProvider.getNowAccout().getId());
            meeting.setIsentered(0);
            meeting.setStarttime(meetingDto.getStarttime());
            meeting.setEndtime(meetingDto.getEndtime());
            meeting.setTheme(meetingDto.getTheme());
            MeetingAcoount meetingAcoount=new MeetingAcoount();
            meetingAcoount.setAccount(meeting.getOriginator());
            meetingAcoount.setSigntime(0);

            meetingRepository.save(meeting);
            int id=meetingRepository.findByOriginatorAndStarttime(meeting.getOriginator(),meetingDto.getStarttime()).getId();
            meetingAcoount.setMeeting(id);
            meetingAcoount.setMeeting(meeting.getId());
            meetingAccountRepository.save(meetingAcoount);
            return ResultUtil.Success(meeting);
        }
        else return ResultUtil.Error("该时间段已被预定");
    }



    @GetMapping("/join")//TODO
    public Result joinMeeting(String inviteCode){
        MeetingAcoount meetingAcoount=new MeetingAcoount();
        if (meetingRepository.findByInvitecode(inviteCode)==null){
            return  ResultUtil.Error("邀请码错误");
        }
        Meeting meeting=meetingRepository.findByInvitecode(inviteCode);
        if (meeting.getState()==1&&meeting.getIsentered()==1){
        meetingAcoount.setMeeting(meeting.getId());
        meetingAcoount.setAccount(accountProvider.getNowAccout().getId());
        meetingAcoount.setSigntime(0);
        meetingAccountRepository.save(meetingAcoount);
            return ResultUtil.Success();
        }
        else return ResultUtil.Error("加入失败");

    }
    //查看自己会议
    @GetMapping("/mymeeting")//TODO
    public Result selectMyMeeting(){
        String account=accountProvider.getNowAccout().getId();
        List<MeetingAcoount> meetingAcoounts=meetingAccountRepository.findByAccount(account);
        List<MeetingVO> meetings=meetingAcoounts.stream()
                .map(meetingAcoount -> {
                    Meeting meeting=meetingRepository.findById(meetingAcoount.getMeeting());
                    MeetingVO meetingVO=new MeetingVO();
                    BeanUtils.copyProperties(meeting,meetingVO);
                    meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
                    return meetingVO;
                }).collect(Collectors.toList());
        return ResultUtil.Success(meetings);
    }



    @PostMapping("/selectorder")//TODO
    public Result selectOrder(@RequestBody OrderDto orderDto) {
        int state=orderDto.getState();
        int size=orderDto.getSize();
        int page=orderDto.getPage()*size;
        String account=accountProvider.getNowAccout().getId();
        List<Meeting> meetings=meetingRepository.findByOriginatorAndState(state,account,page,size);
        List<MeetingVO> meetingVOS=meetings.stream()
                .map(meeting -> {
                    MeetingVO meetingVO=new MeetingVO();
                    BeanUtils.copyProperties(meeting,meetingVO);
                    meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
                    return meetingVO;
                }).collect(Collectors.toList());
        if (meetingVOS.size()==0)
            return ResultUtil.Error("已没有更多会议");
        return ResultUtil.Success(meetingVOS);
    }


    @GetMapping("/selectbyorder")//TODO
    public Result selectOrder() {
        String account=accountProvider.getNowAccout().getId();
        List<Meeting> meetings=meetingRepository.findByOriginatorOrderByStarttimeDesc(account);
        List<MeetingVO> meetingVOS=meetings.stream()
                .map(meeting -> {
                    MeetingVO meetingVO=new MeetingVO();
                    BeanUtils.copyProperties(meeting,meetingVO);
                    meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
                    return meetingVO;
                }).collect(Collectors.toList());
        if (meetingVOS.size()==0)
            return ResultUtil.Error("已没有更多会议");
        return ResultUtil.Success(meetingVOS);
    }

    @PostMapping("/selectallorder")//TODO
    public Result selectAllOrder(@RequestBody OrderDto orderDto) {
        int size=orderDto.getSize();
        int page=orderDto.getPage()*size;
        String account=accountProvider.getNowAccout().getId();
        List<Meeting> meetings=meetingRepository.findByOriginatorAndState(account,page,size);
        List<MeetingVO> meetingVOS=meetings.stream()
                .map(meeting -> {
                    MeetingVO meetingVO=new MeetingVO();
                    BeanUtils.copyProperties(meeting,meetingVO);
                    meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
                    return meetingVO;
                }).collect(Collectors.toList());
        if (meetingVOS.size()==0)
            return ResultUtil.Error("已没有更多会议");
        return ResultUtil.Success(meetingVOS);
    }



    @PostMapping("/selectjoin")//TODO
    public Result selectJoin(@RequestBody OrderDto orderDto){
        int state=orderDto.getState();
        int size=orderDto.getSize();
        int page=orderDto.getPage()*size;
        String account=accountProvider.getNowAccout().getId();
        List<MeetingAcoount> meetingAcoounts=meetingAccountRepository.findByAccount(account);
        List<Integer> id=meetingAcoounts.stream()
                .map(meetingAcoount -> {
                    return meetingAcoount.getMeeting();
                }).collect(Collectors.toList());
        List<Meeting> meetings=meetingRepository.findByIdIn(state,id,page,size);
        List<MeetingVO> meetingVOS=meetings.stream().filter(meeting -> {
            return !meeting.getOriginator().equals(accountProvider.getNowAccout().getAccount());
        })
                .map(meeting -> {
                    MeetingVO meetingVO=new MeetingVO();
                    BeanUtils.copyProperties(meeting,meetingVO);
                    meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
                    return meetingVO;
                }).collect(Collectors.toList());
        if (meetingVOS.size()==0)
            return ResultUtil.Error("已没有更多会议");
        return ResultUtil.Success(meetingVOS);
    }



    @GetMapping("/selectall")//TODO
    public Result selectAll(){
        String account=accountProvider.getNowAccout().getId();
        List<MeetingAcoount> meetingAcoounts=meetingAccountRepository.findByAccount(account);
        List<Integer> id=meetingAcoounts.stream()
                .map(meetingAcoount -> {
                    return meetingAcoount.getMeeting();
                }).collect(Collectors.toList());
        List<Meeting> meetings=meetingRepository.findByIdIn(id);
        List<MeetingVO> meetingVOS=meetings.stream().map(meeting -> {
            MeetingVO meetingVO=new MeetingVO();
            BeanUtils.copyProperties(meeting,meetingVO);
            meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
            return meetingVO;
        }).collect(Collectors.toList());
        if (meetingVOS.size()==0)
            return ResultUtil.Error("已没有更多会议");
        return ResultUtil.Success(meetingVOS);
    }

    @GetMapping("/selectbyjoin")//TODO
    public Result selectJoin(){
        String account=accountProvider.getNowAccout().getId();
        List<MeetingAcoount> meetingAcoounts=meetingAccountRepository.findByAccount(account);
        List<Integer> id=meetingAcoounts.stream()
                .map(meetingAcoount -> {
                    return meetingAcoount.getMeeting();
                }).collect(Collectors.toList());
        List<Meeting> meetings=meetingRepository.findByIdIn(id);
        List<MeetingVO> meetingVOS=meetings.stream().filter(meeting -> {
            return !meeting.getOriginator().equals(accountProvider.getNowAccout().getAccount());
        }).map(meeting -> {
                    MeetingVO meetingVO=new MeetingVO();
                    BeanUtils.copyProperties(meeting,meetingVO);
                    meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
                    return meetingVO;
                }).collect(Collectors.toList());
        if (meetingVOS.size()==0)
            return ResultUtil.Error("已没有更多会议");
        return ResultUtil.Success(meetingVOS);
    }

    @PostMapping("/selectalljoin")//TODO
    public Result selectAllJoin(@RequestBody OrderDto orderDto){
        int size=orderDto.getSize();
        int page=orderDto.getPage()*size;
        String account=accountProvider.getNowAccout().getId();
        List<MeetingAcoount> meetingAcoounts=meetingAccountRepository.findByAccount(account);
        List<Integer> id=meetingAcoounts.stream()
                .map(meetingAcoount -> {
                    return meetingAcoount.getMeeting();
                }).collect(Collectors.toList());
        List<Meeting> meetings=meetingRepository.findByIdIn(id,page,size);
        List<MeetingVO> meetingVOS=meetings.stream().filter(meeting -> {
            return !meeting.getOriginator().equals(accountProvider.getNowAccout().getAccount());
        })
                .map(meeting -> {
                    MeetingVO meetingVO=new MeetingVO();
                    BeanUtils.copyProperties(meeting,meetingVO);
                    meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
                    return meetingVO;
                }).collect(Collectors.toList());
        if (meetingVOS.size()==0)
            return ResultUtil.Error("已没有更多会议");
        return ResultUtil.Success(meetingVOS);
    }



    @GetMapping("/getordercount")//TODO
    public Result getOrdercount(int state){
        String account=accountProvider.getNowAccout().getId();
        return ResultUtil.Success(meetingRepository.findByOriginatorAndState(account,state).size());

    }

    @GetMapping("/getjoincount")//TODO
    public Result getJoinCount(int state){
        String account=accountProvider.getNowAccout().getId();
        List<MeetingAcoount> meetingAcoounts=meetingAccountRepository.findByAccount(account);
        List<Integer> id=meetingAcoounts.stream()
                .map(meetingAcoount -> {
                    return meetingAcoount.getMeeting();
                }).collect(Collectors.toList());
        return ResultUtil.Success(meetingRepository.findByIdInAndState(id,state).size());
    }


    @GetMapping("/selectbyinvitecode")//TODO
    public Result selectByInviteCode(String inviteCode){
        if (meetingRepository.findByInvitecode(inviteCode)==null)
            return ResultUtil.Error("邀请码错误");
        Meeting meeting=meetingRepository.findByInvitecode(inviteCode);
        if (meeting.getState()!=1)
            return ResultUtil.Error("会议未审核或已结束");
        MeetingVO meetingVO=new MeetingVO();
        BeanUtils.copyProperties(meeting,meetingVO);
        meetingVO.setOriginatorName(accountRepository.findById(accountProvider.getNowAccout().getId()).getName());
        return ResultUtil.Success(meetingVO);
    }

    @GetMapping("/cancel")//TODO
    public Result cancelMeeting(int meeting){
        String id=accountProvider.getNowAccout().getId();
        if (meetingRepository.findByIdAndOriginator(meeting,id)==null){
            return ResultUtil.Error("失败");
        }
        Meeting meeting1=meetingRepository.findByIdAndOriginator(meeting,id);
        meeting1.setState(5);
        if (meetingRepository.save(meeting1)==null)
            return ResultUtil.Error("取消失败");
        else return ResultUtil.Success();
    }


    @PostMapping("/update")//TODO
    public Result updateMeeting(@RequestBody Meeting meeting,HttpServletRequest session){
       if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        if (meetingRepository.findById(meeting.getId()).get()==null)
            return ResultUtil.Error("找不到会议");
        Meeting meeting1=meetingRepository.findById(meeting.getId()).get();
        BeanUtils.copyProperties(meeting,meeting1);
        if (meetingRepository.save(meeting1)!=null)
            return ResultUtil.Success(meeting1);
        else return ResultUtil.Error("更新失败");

    }


    @GetMapping("/getall")
    public Result getAll(HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        List<Meeting> meetings=meetingRepository.findByStateLessThanEqual(3);
        List<MeetingVO> meetingVOS=meetings.stream().map(meeting -> {
            MeetingVO meetingVO=new MeetingVO();
            BeanUtils.copyProperties(meeting,meetingVO);
            meetingVO.setOriginatorName(accountRepository.findById(meeting.getOriginator()).getName());
            return meetingVO;
        }).collect(Collectors.toList());
       return ResultUtil.Success(meetingVOS);

    }


}
