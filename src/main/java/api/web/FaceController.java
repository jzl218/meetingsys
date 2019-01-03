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
import api.vo.FaceVO;
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
    private final double passrate = 74.1;
    private double passmax=72;

    @PostMapping("/facecompare")
    public Result faceCompare(@RequestBody ImgDto imgDto) throws IOException {
        List<MeetingAcoount> meetingAcoounts=meetingAccountRepository.findByMeeting(imgDto.getMeeting());
        List<String> id=meetingAcoounts.stream()
                .map(meetingAcoount -> {
                    return meetingAcoount.getAccount();
                }).collect(Collectors.toList());
        List<Account> accounts=accountRepository.findByIdIn(id);
        List<ImageInfo> imageInfos=new LinkedList<>();
        String img1=PicUtil.decode64(imgDto.getImg1());
        String img2=PicUtil.decode64(imgDto.getImg1());
        String img3=PicUtil.decode64(imgDto.getImg1());
        String img4=PicUtil.decode64(imgDto.getImg1());
        String img5=PicUtil.decode64(imgDto.getImg1());
        ImageInfo imageInfo1 = face.getRGBData(new File(img1));
        ImageInfo imageInfo2 = face.getRGBData(new File(img2));
        ImageInfo imageInfo3 = face.getRGBData(new File(img3));
        ImageInfo imageInfo4 = face.getRGBData(new File(img4));
        ImageInfo imageInfo5 = face.getRGBData(new File(img5));
        new File(img1).delete();
        new File(img2).delete();
        new File(img3).delete();
        new File(img4).delete();
        new File(img5).delete();

        imageInfos.add(imageInfo1);
        imageInfos.add(imageInfo2);
        imageInfos.add(imageInfo3);
        imageInfos.add(imageInfo4);
        imageInfos.add(imageInfo5);
        FaceEngine faceEngine = new FaceEngine();
        faceEngine.active(addId, sdkKet);
        faceEngine.init(face.getengineConfig());
        Iterator iterator = imageInfos.iterator();
        int size = imageInfos.size();
        List<RateVO> rateVOS=new LinkedList<>();
        for (int q = 0; q < size; q++) {
            try{
            ImageInfo imageInfo=imageInfos.get(q);
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

                }
                rateVOS.add(rateVO);
            }
        }catch (IndexOutOfBoundsException e){
                continue;
            }
        }

        List<RateVO> passrateVOs=rateVOS.stream().filter(rateVO -> {
                return 100*rateVO.getData()>=passrate;
            }).collect(Collectors.toList());
        if (passrateVOs.size()>=3) {
            FaceVO faceVO=new FaceVO();
            int Isentered=meetingRepository.findById(imgDto.getMeeting()).getIsentered();
            if (Isentered==0){
                MeetingAcoount meetingAcoount=meetingAccountRepository.findByMeetingAndAccount(imgDto.getMeeting(),accountRepository.findById(passrateVOs.get(0).getId()).getName());
                meetingAcoount.setSigntime(new Date().getTime());
                meetingAccountRepository.save(meetingAcoount);
            }
            faceVO.setIsEntered(Isentered);
            faceVO.setName(accountRepository.findById(passrateVOs.get(0).getId()).getName());
            return ResultUtil.Success(faceVO);
        }
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

    @GetMapping("/facetest2")
    public Result faceTest2(){
        for (int i=49;i<=49;i++){
            Account account=new Account();
            String birthpath="pictest/";
            File file=new File(birthpath+i+"/");
            String name[]=  file.list();
            ImageInfo imageInfo = face.getRGBData(new File(birthpath+i+"/"+name[2]));
            FaceEngine faceEngine = new FaceEngine();
            faceEngine.active(addId, sdkKet);
            faceEngine.init(face.getengineConfig());
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            faceEngine.detectFaces(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList);
            FaceFeature faceFeature = new FaceFeature();
            faceEngine.extractFaceFeature(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
            byte[] data=faceFeature.getFeatureData();
            account.setFace(data);
            account.setName(i+"name");
            account.setPassword(i+"pass");
            account.setId(i+"id");
            accountRepository.save(account);
        }
        return ResultUtil.Success();

    }
//    @GetMapping("/facecomparetest")
//    public void faceComparetest() throws IOException {
//        double TP = 0, FP = 0, FN = 0, TN = 0,P = 0,R = 0;
//        List<Account> accounts = accountRepository.findAll();
//        Map sorthashMap = new HashMap();
//        List<ImageInfo> imageInfos = new LinkedList<>();
//        String birthpath = "pictest/";
//        String name[] = null;
//        FaceEngine faceEngine = new FaceEngine();
//        faceEngine.active(addId, sdkKet);
//        faceEngine.init(face.getengineConfig());
//        List list = new ArrayList();
//        List sortlist = new ArrayList();
//        double value = 0;
//        String maxKey = null;
//        List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
//        for (; passrate < 74.1; passrate += 0.1) {
//            P=0;
//            R=0;
//            TP = 0;
//            FP = 0;
//            FN = 0;
//            TN = 0;
//
//            for (int p = 1; p <= 7; p++) {
//                for (int i = 1; i <= 123; i++) {
//                    imageInfos.clear();
//                    File file = new File(birthpath + i + "/");
//                    name = file.list();
//                    for (int a = 2 + 5 * (p - 1); a <= 6+ 5 * (p - 1); a++) {
//                        imageInfos.add(face.getRGBData(new File(birthpath + i + "/" + name[a])));
//                    }
//                    List<RateVO> rateVOS = new LinkedList<>();
//                    Iterator iterator = imageInfos.iterator();
//                    int size = imageInfos.size();
//                    int flag = 0;
//                    for (int q = 0; q < size; q++) {
//                        ImageInfo imageInfo = imageInfos.get(q);
//                        faceInfoList.clear();
//                        try {
//                            faceEngine.detectFaces(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList);
//                            FaceFeature faceFeature = new FaceFeature();
//                            faceEngine.extractFaceFeature(imageInfo.getRgbData(), imageInfo.getWidth(), imageInfo.getHeight(), ImageFormat.CP_PAF_BGR24, faceInfoList.get(0), faceFeature);
//                            FaceFeature targetFaceFeature = new FaceFeature();
//                            targetFaceFeature.setFeatureData(faceFeature.getFeatureData());
//                            Map hashMap = new HashMap();
//                            accounts.stream().forEach(account -> {
//                                FaceFeature sourceFaceFeature = new FaceFeature();
//                                sourceFaceFeature.setFeatureData(account.getFace());
//                                FaceSimilar faceSimilar = new FaceSimilar();
//                                int result = faceEngine.compareFaceFeature(targetFaceFeature, sourceFaceFeature, faceSimilar);
//                                hashMap.put(account.getId(), faceSimilar.getScore());
//                            });
//                            value = 0;
//                            maxKey = null;
//                            RateVO rateVO = new RateVO();
//                            list.clear();
//                            Iterator ite = hashMap.entrySet().iterator();
//                            while (ite.hasNext()) {
//                                Map.Entry entry = (Map.Entry) ite.next();
//                                value = Double.parseDouble(entry.getValue().toString());
//                                list.add(entry.getValue());
//                                Collections.sort(list);
//                                if (value == Double.parseDouble(list.get(list.size() - 1).toString())) {
//                                    maxKey = entry.getKey().toString();
//                                    rateVO.setId(maxKey);
//                                    rateVO.setData(value);
//                                }
//                            }
//                            rateVOS.add(rateVO);
//                        } catch (IndexOutOfBoundsException e) {
//
//                            if (flag > 2) {
//                                TN++;
//                                break;
//                            }
//                            flag++;
//                            continue;
//                        }
//                    }
//                    System.out.println(rateVOS);
//                    int[] passrateVOs = {0};
//                    rateVOS.stream().forEach(rateVO -> {
//                        if (100 * rateVO.getData() > passrate)
//                            passrateVOs[0] = passrateVOs[0] + 1;
//                    });
//
//                    if (passrateVOs[0] >= 3) {
//                        if (rateVOS.get(0).getId().equals(i + "id"))
//                            TP++;
//                        else FP++;
//                    } else if (rateVOS.get(0).getId().equals(i + "id"))
//                        FN++;
//                    else TN++;
//                }
//                //人脸对比
//            }
//            P = TP / (TP + FP);
//            R = TP / (TP + FN);
//            sorthashMap.put(passrate,2 * R * P / (R + P));
//            System.out.println("P=" + P);
//            System.out.println("R=" + R);
//            System.out.println("passrate="+passrate);
//            System.out.println("F1SCORE=" + 2 * R * P / (R + P));
//
//        }
//            System.out.println(sorthashMap);
//
//    }

}

