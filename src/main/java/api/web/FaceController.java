package api.web;

import api.Dto.ImgDto;
import api.base.ImageInfo;
import api.Entity.Account;
import api.Entity.MeetingAcoount;
import api.repository.AccountRepository;
import api.repository.MeetingAccountRepository;
import api.repository.MeetingRepository;
import api.sevice.FaceImpl;
import api.utils.PicUtil;
import api.utils.ResultUtil;
import api.vo.RateVO;
import api.vo.Result;
import com.arcsoft.face.*;
import com.arcsoft.face.enums.ImageFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/face")
public class FaceController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private FaceImpl face;
    @Autowired
    private MeetingRepository meetingRepository;
    @Autowired
    private MeetingAccountRepository meetingAccountRepository;

    public String addId = "6NicX292WuD2npxeHZPaCQSVbfuN6DionSMfu2XRdwep";
    public String sdkKet = "HMXxTNqu3rnhbhGZB3wNURFCLVb4tiriTMXpxnytMKHM";
    private double passrate = 80;
    private double passmax=90;

    @GetMapping("/facecompare")
    public Result faceCompare(@RequestBody ImgDto imgDto) throws IOException {
        List<MeetingAcoount> meetingAcoounts=meetingAccountRepository.findByMeeting(imgDto.getMeeting());
        List<String> id=meetingAcoounts.stream()
                .map(meetingAcoount -> {
                    return meetingAcoount.getAccount();
                }).collect(Collectors.toList());
        List<Account> accounts=accountRepository.findByIdIn(id);
        List<ImageInfo> imageInfos=new LinkedList<>();
        ImageInfo imageInfo1 = face.getRGBData(PicUtil.decode64(imgDto.getImg1()));
        ImageInfo imageInfo2 = face.getRGBData(PicUtil.decode64(imgDto.getImg2()));
        ImageInfo imageInfo3 = face.getRGBData(PicUtil.decode64(imgDto.getImg3()));
        ImageInfo imageInfo4 = face.getRGBData(PicUtil.decode64(imgDto.getImg4()));
        ImageInfo imageInfo5 = face.getRGBData(PicUtil.decode64(imgDto.getImg5()));
        imageInfos.add(imageInfo1);
        imageInfos.add(imageInfo2);
        imageInfos.add(imageInfo3);
        imageInfos.add(imageInfo4);
        imageInfos.add(imageInfo5);
        FaceEngine faceEngine = new FaceEngine();
        faceEngine.active(addId, sdkKet);
        faceEngine.init(face.getengineConfig());
        List<RateVO> rateVOS=new LinkedList<>();
        imageInfos.stream().forEach(imageInfo -> {
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            faceEngine.detectFaces(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList);
            FaceFeature faceFeature = new FaceFeature();
            faceEngine.extractFaceFeature(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
            FaceFeature targetFaceFeature = new FaceFeature();
            targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
            Map hashMap = new HashMap();
            accounts.stream().forEach(account -> {
                FaceFeature sourceFaceFeature = new FaceFeature();
                sourceFaceFeature.setFeatureData(account.getFace());
                FaceSimilar faceSimilar = new FaceSimilar();
                int result=faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
                hashMap.put(account.getId(), faceSimilar.getScore());
            });
            double value = 0;
            String maxKey = null;
            RateVO rateVO=new RateVO();
            List list = new ArrayList();
            Iterator ite = hashMap.entrySet().iterator();
            while (ite.hasNext()) {
                Map.Entry entry = (Map.Entry) ite.next();
                value = Double.parseDouble(entry.getValue().toString());
                list.add(entry.getValue());
                Collections.sort(list);
                if (value == Double.parseDouble(list.get(list.size() - 1).toString())) {
                    maxKey = entry.getKey().toString();
                    rateVO.setId(maxKey);
                    rateVO.setData(value);
                    rateVOS.add(rateVO);
                }
            }
        });

        List<RateVO> passrateVOs=rateVOS.stream().map(rateVO -> {
                if (rateVO.getData()>=passrate);
                return rateVO   ;
            }).collect(Collectors.toList());
        List<Double> passrates=rateVOS.stream().map(rateVO -> {
            return rateVO.getData();
        }).collect(Collectors.toList());
        Collections.sort(passrates);
        if (passrateVOs.size()>=3&&passrates.get(passrates.size()-1)>=passmax)
            return ResultUtil.Success(rateVOS.get(1).getId());
        else return ResultUtil.Error("识别失败或您没有参加此会议");

        //人脸对比
    }



    @GetMapping("/facetest")
    public Result faceTest() {
        for (int i = 1; i <= 6; i++) {
            Account account = new Account();
            account.setId(i + "test");
            account.setPassword(i + "test");
            String birpath = "pictest/";
            String filename = i + ".jpg";
            ImageInfo imageInfo = face.getRGBData(new File(birpath+filename));
            FaceEngine faceEngine = new FaceEngine();
            faceEngine.active(addId, sdkKet);
            faceEngine.init(face.getengineConfig());
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            faceEngine.detectFaces(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList);
            FaceFeature faceFeature = new FaceFeature();
            faceEngine.extractFaceFeature(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
            byte[] data=faceFeature.getFeatureData();
            account.setFace(data);
            accountRepository.save(account);
        }
            return ResultUtil.Success();
    }

}
