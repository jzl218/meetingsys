package api.web;

import api.Dto.JoinDto;
import api.Dto.MeetingDto;
import api.Dto.OrderDto;
import api.entity.Meeting;
import api.entity.MeetingAcoount;
import api.repository.MeetingAccountRepository;
import api.repository.MeetingRepository;
import api.sevice.AccountProviderImpl;
import api.utils.BeanUtils;
import api.utils.ResultUtil;
import api.vo.MeetingVO;
import api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.naming.spi.DirStateFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
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

    @GetMapping("/selectnoverify")
    public Result selectMeeting(HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        List<Meeting> allmeetings=meetingRepository.findAllByOrderByStarttime();
        List<Meeting> meetings=allmeetings.stream().filter(meeting -> {
            return meeting.getState()==0;
        }).collect(Collectors.toList());
        if (meetings!=null)
            return ResultUtil.Success(meetings);
        else return ResultUtil.Error("失败");
    }

    @GetMapping("/selectverify")
    public Result selectMeeetingverify(HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        List<Meeting> allmeetings=meetingRepository.findAllByOrderByStarttime();
        List<Meeting> meetings=allmeetings.stream().filter(meeting -> {
            return meeting.getState()!=0;
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
        meetingRepository.save(meetingvo);
        return ResultUtil.Success();
    }





    @GetMapping("/selectbyroom")
    public Result selectbyroom(int room,HttpServletRequest session){
        if (session.getSession().getAttribute("admin")==null){
            return ResultUtil.Error("请先登录");
        }
        Meeting meeting=meetingRepository.findByRoom(room);
        if (meeting!=null)
            return ResultUtil.Success(meeting);
        else return ResultUtil.Error("失败");
    }


    @PostMapping("/add")//TODO
    public Result addMeeting(@RequestBody MeetingDto meetingDto){
        Long starttime=meetingDto.getStarttime();
        Long endtime=meetingDto.getEndtime();
        List<Meeting> meetings=meetingRepository.findAll();
        List<Meeting> meetingslist=meetings.stream().map(meeting -> {
            Long nstarttime=meeting.getStarttime()-20*60*1000;
            Long nendttime=meeting.getEndtime()+20*60*1000;
            if ((starttime<nendttime&&starttime>nstarttime)||(endtime>nstarttime&&endtime<nendttime))
                return meeting;
            else return null;
        }).collect(Collectors.toList());
        if (meetingslist!=null){
            Meeting meeting=new Meeting();
            BeanUtils.copyProperties(meetingDto,meeting);
            meeting.setInvitecode(UUID.randomUUID().toString());
            meeting.setState(0);
            meeting.setOriginator(accountProvider.getNowAccout().getAccount());
            meeting.setIsentered(0);
            meetingRepository.save(meeting);
            return ResultUtil.Success(meeting);
        }
        else return ResultUtil.Error("该时间段已被预定");
    }



    @GetMapping("/join")//TODO
    public Result joinMeeting(String inviteCode){

        if (meetingRepository.findByInvitecode(inviteCode)==null){
            return  ResultUtil.Error("邀请码错误");
        }
        Meeting meeting=meetingRepository.findByInvitecode(inviteCode);
        MeetingAcoount meetingAcoount=new MeetingAcoount();
        meetingAcoount.setMeeting(meeting.getId());
        meetingAcoount.setAccount(accountProvider.getNowAccout().getId());
        meetingAcoount.setSigntime(0);
        if (meetingAccountRepository.save(meetingAcoount)!=null)
            return ResultUtil.Success();
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
                    return meetingVO;
                }).collect(Collectors.toList());
        return ResultUtil.Success(meetings);
    }



    @PostMapping("/selectorder")//TODO
    public Result selectOrder(@RequestBody OrderDto orderDto) {
        int state=orderDto.getState();
        int size=orderDto.getSize();
        int page=orderDto.getPage();
        String account=accountProvider.getNowAccout().getId();
        List<Meeting> meetings=meetingRepository.findByOriginatorAndState(state,account,page,size);
        List<MeetingVO> meetingVOS=meetings.stream()
                .map(meeting -> {
                    MeetingVO meetingVO=new MeetingVO();
                    BeanUtils.copyProperties(meeting,meetingVO);
                    return meetingVO;
                }).collect(Collectors.toList());
        return ResultUtil.Success(meetingVOS);
    }

    @PostMapping("/selectjoin")//TODO
    public Result selectJoin(@RequestBody OrderDto orderDto){
        int state=orderDto.getState();
        int size=orderDto.getSize();
        int page=orderDto.getPage();
        String account=accountProvider.getNowAccout().getId();
        List<MeetingAcoount> meetingAcoounts=meetingAccountRepository.findByAccount(account);
        List<Integer> id=meetingAcoounts.stream()
                .map(meetingAcoount -> {
                    return meetingAcoount.getMeeting();
                }).collect(Collectors.toList());
        List<Meeting> meetings=meetingRepository.findByIdIn(state,id,page,size);
        List<MeetingVO> meetingVOS=meetings.stream()
                .map(meeting -> {
                    MeetingVO meetingVO=new MeetingVO();
                    BeanUtils.copyProperties(meeting,meetingVO);
                    return meetingVO;
                }).collect(Collectors.toList());
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



}
