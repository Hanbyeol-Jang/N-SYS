package com.ai.brain.controller;

import com.ai.brain.service.UserinfoService;
import com.ai.brain.vo.UserIdPw;
import com.ai.brain.vo.Userinfo;
import com.ai.brain.util.JwtTokenProvider;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.User;

import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Base64.Encoder;
import java.util.Base64;
import java.util.HashMap;


@RestController
@CrossOrigin(origins = {"*"}, maxAge = 6000)
@RequestMapping("Userinfo")
public class UserinfoController {

    @Autowired
    private UserinfoService userinfoService;

    //    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/join")
    @ApiOperation(value = "회원 가입")
    public ResponseEntity<HashMap<String, Object>> join(@RequestBody UserIdPw userIdPw) {
        System.out.println("join Controller");
        System.out.println(userIdPw.getUId()); // 회원가입 하는 유저의 아이디 확인
        System.out.println(userIdPw.getUPw()); // 회원가입 하는 유저의 비밀번호 확인
        System.out.println(userIdPw.getUName()); // 회원가입 하는 유저의 닉네임 확인
        System.out.println(userIdPw.getUImage()); // 회원가입 하는 유저의 프로필이미지경로 확인
        try {

            HashMap<String, Object> map = new HashMap<>();
            Userinfo userinfo = userinfoService.join(userIdPw);

            // id, 닉네임 중복 체크
            if (userinfo == null) {
                map.put("Userinfo", "fail");
            } else {
                map.put("Userinfo", userinfo);
            }

            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/userinfo/{upk}")
    @ApiOperation(value = "회원 pk 로 회원 정보 가져오기")
    public ResponseEntity<HashMap<String, Object>> getUserinfo(@PathVariable("upk") int upk) {
        System.out.println("getUserinfo Controller");
        try {

            HashMap<String, Object> map = new HashMap<>();
            Optional<Userinfo> userinfo = userinfoService.getUserinfo(upk);
            map.put("Userinfo", userinfo.get());

            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping(value = "/updateName")
    @ApiOperation(value = "닉네임 변경하기")
    public ResponseEntity<HashMap<String, Object>> updateId(@RequestBody Userinfo userinfo) {
        System.out.println("updateId Controller");
        try {
            HashMap<String, Object> map = new HashMap<>();

            Userinfo user = userinfoService.updateId(userinfo);

            // 닉네임 중복 체크
            if (user == null) {
                map.put("Userinfo", "fail");
            } else {
                map.put("Userinfo", userinfo);
            }

            // 닉네임 중복 체크
            if (user == null) {
                map.put("Userinfo", "fail");
            } else {
                map.put("Userinfo", userinfo);
            }
            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping(value = "/updatepw")
    @ApiOperation(value = "pw 변경하기")
    public ResponseEntity<HashMap<String, Object>> updatePw(@RequestBody Userinfo userinfo) {
        System.out.println("updatePw Controller");
        try {
            HashMap<String, Object> map = new HashMap<>();
            userinfoService.updatePw(userinfo);
            map.put("Userinfo", userinfo);

            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/deleteaccount")
    @ApiOperation(value = "회원 탈퇴")
    public ResponseEntity<HashMap<String, Object>> deleteAccount(@RequestBody Userinfo userinfo) {
        System.out.println("deleteAccount Controller");
        try {
            HashMap<String, Object> map = new HashMap<>();
            userinfoService.deleteAccount(userinfo);
            map.put("Userinfo", "deleted!");

            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인")
    public ResponseEntity<HashMap<String, Object>> login(@RequestBody UserIdPw userIdPw, HttpServletRequest request) {
        System.out.println("login Controller");
        System.out.println(userIdPw.getUId()); // 로그인 하는 유저의 아이디 확인
        System.out.println(userIdPw.getUPw()); // 로그인 하는 유저의 비밀번호 확인
        String loginId = userIdPw.getUId();
        String loginPw = userIdPw.getUPw();
        try {
            HashMap<String, Object> map = new HashMap<>();

            Userinfo userinfo = userinfoService.getUserinfo(loginId, loginPw);

            if (userinfo != null) { // 로그인 성공시
                String token = userinfoService.createToken(userinfo); // 토큰을 생성해서
                map.put("Userinfo", token); // 토큰 저장 및 리턴
                map.put("Upk", userinfo.getUPk()); // 유저번호 저장 및 리턴
                map.put("Uname", userinfo.getUiName()); // 닉네임 저장 및 리턴
                map.put("Uid", userinfo.getUiId()); // 이메일아이디 저장 및 리턴
                if(userinfo.getUiImage().equals("/img/person.9f2af2d1.png")) {
                    map.put("srcImage", userinfo.getUiImage()); // 프로필이미지저장경로 저장 및 리턴
                } else {
                    // avatarImage 작업코드
                    map.put("NOTSameDefaultImageSrc", userinfo.getUiImage());
                    // 이미지를 지정한 경로에서 불러와서(이미지가 저장되어있어야함) FileInputStream으로 읽은 후 InputStream으로 저장.
                    InputStream imageStream = new FileInputStream(userinfo.getUiImage());
                    // InputStream으로 읽어들인 이미지를 ByteArray형태로 변환.
                    byte[] imageByteArray = IOUtils.toByteArray(imageStream);
                    imageStream.close();
                    System.out.println(imageByteArray);
                    // Base64형태로 인코딩해주는 encoder 객체를 생성.
                    Encoder encoder = Base64.getEncoder();
                    // 위에서  ByteArray형태로 변환된 이미지를 Base64형태로 인코딩.
                    byte[] baseIncodingBytes = encoder.encode(imageByteArray);
                    // Base64형태로 인코딩된 이미지 파일을 String으로 바꿈.
                    String base64 = new String(baseIncodingBytes);
//                    System.out.println(base64); // 잘 인코딩 됐는지 확인.
                    //imgsrc라는 String 변수에 이미지의 총 src를 전부 작성. 이를 response.data를 읽어들인다면 그 자체로 이미지파일이다.
                    String imgsrc = "data:" + userinfo.getUiImgtype() + ";base64," + base64;
                    // 이미지 파일의 src를 반환하면 끝.
//				System.out.println(imgsrc);
                    map.put("srcImage", imgsrc);
                }
            } else {
                map.put("Userinfo", "fail");
            }

            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/logout")
    @ApiOperation(value = "로그아웃")
    public ResponseEntity<HashMap<String, Object>> logout() {
        System.out.println("logout Controller");
        try {
            HashMap<String, Object> map = new HashMap<>();

            map.put("logout", "1");

            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/findpw")
    @ApiOperation(value = "이메일을 통한 비밀번호 변경")
    public ResponseEntity<HashMap<String, Object>> findpw(@RequestBody UserIdPw userIdPw) {
        System.out.println("findpw Controller");
        System.out.println(userIdPw.getUId()); // 비밀번호찾기 하는 유저의 아이디 확인
        String uId = userIdPw.getUId();
        try {
            HashMap<String, Object> map = new HashMap<>();
            Userinfo userinfo = userinfoService.getUserinfo(uId);

            // id, 닉네임 중복 체크
            if (userinfo == null) {
                map.put("Userinfo", "fail");
            } else {
                map.put("Uname", userinfo.getUiName());
                map.put("Uid", userinfo.getUiId());
                map.put("message", "가입된 이메일입니다.");
            }

            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // 단순히 이미지 파일 받아와서 백엔드로 정보만 출력하는 기능
    @ResponseBody
    @PostMapping("/fileUpload")
    public String fileUpload(@RequestParam MultipartFile image) throws IOException {
        System.out.println("1. file : " + image);
        System.out.println("2. file의 파라미터 이름 : " + image.getName());
        System.out.println("3. file의 사이즈 : " + image.getSize());
        System.out.println("4. file의 실제이름 : " + image.getOriginalFilename());
        byte[] data = image.getBytes();
        System.out.println("5. file의 실제 내용 : " + data);
        System.out.println("6. file의 타입 : " + image.getContentType());
        String str = "";
        if (image.getName().equals("image") || image.getName().equals("png") || image.getName().equals("jpg")) {
            str = image.getName() + "업로드 성공!";
        }
        return str;
    }

    // 받아온 이미지 파일을 특정 경로에 저장. 경로 반환.
    @PostMapping(value="/profileimgsave", produces = MediaType.IMAGE_JPEG_VALUE)
    public @ResponseBody String profileimgsave(@RequestParam MultipartFile image) throws IOException {
        String str = "";
        String dest3 = "";
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMddHHmmss");
        Date time = new Date();
        String time1 = format1.format(time);

//		System.out.println(time1);
        if (image != null && !image.isEmpty()) {
            File dest1 = new File("/home/ubuntu/image/");
//			File dest1 = new File("/SSAFY/profile/");
            File dest2 = new File("/home/ubuntu/image/" + time1 + "_" + image.getOriginalFilename());
//			File dest2 = new File("/SSAFY/profile/" + time1 + "_" + image.getOriginalFilename());
            if (dest1.mkdirs()) {
                System.out.println("디렉토리 생성 성공");
            } else {
                System.out.println("디렉토리 생성 실패 또는 기존에 이미 디렉토리가 존재합니다.");
            }
            try {
                System.out.println(dest2.getPath());
                image.transferTo(dest2);
                str = "저장성공";
                System.out.println(str);
                System.out.println("dest2 : " + dest2);
                dest3 = "" + dest2;
                System.out.println(dest3);
            } catch (IllegalStateException e) {
                str = "저장실패";
                System.out.println(str);
                e.printStackTrace();
            } catch (IOException e) {
                str = "저장실패";
                System.out.println(str);
                e.printStackTrace();
            }
        }

        // 이미지를 지정한 경로에서 불러와서(이미지가 저장되어있어야함) FileInputStream으로 읽은 후 InputStream으로 저장.
        InputStream imageStream = new FileInputStream("/home/ubuntu/image/" + time1 + "_" + image.getOriginalFilename());
//        InputStream imageStream = new FileInputStream("/SSAFY/profile/" + time1 + "_" + image.getOriginalFilename());
        // InputStream으로 읽어들인 이미지를 ByteArray형태로 변환.
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();
        System.out.println(imageByteArray);
        // Base64형태로 인코딩해주는 encoder 객체를 생성.
        Base64.Encoder encoder = Base64.getEncoder();
        // 위에서  ByteArray형태로 변환된 이미지를 Base64형태로 인코딩.
        byte[] baseIncodingBytes = encoder.encode(imageByteArray);

//		System.out.println(new String(baseIncodingBytes)); // 잘 인코딩 됐는지 확인.
        // Base64형태로 인코딩된 이미지 파일을 String으로 바꿈.
        String base64 = new String(baseIncodingBytes);
        // imgsrc라는 String 변수에 이미지의 총 src를 전부 작성. 이를 response.data를 읽어들인다면 그 자체로 이미지파일이다.
        String imgsrc = "data:" + image.getContentType() + ";base64," + base64;
        // 이미지 파일의 src를 반환하면 끝.
//		System.out.println(imgsrc);


        //그러나 이 단계에선 img파일의 전체src는 상당히 길다. DB저장 axios를 보내기 위해 지금은 이미지 저장 경로만 보낸다.
        return (dest3);
    }

    @ResponseBody
    @PostMapping("/updateProfile")
    public String updateProfile(@RequestBody Userinfo userinfo) throws IOException {
        System.out.println("userinfo.getUiImage() : " + userinfo.getUiImage()); // 잘 받아왔는지 확인.

        // 이미지를 지정한 경로에서 불러와서(이미지가 저장되어있어야함) FileInputStream으로 읽은 후 InputStream으로 저장.
        InputStream imageStream = new FileInputStream(userinfo.getUiImage());
        // InputStream으로 읽어들인 이미지를 ByteArray형태로 변환.
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();
        System.out.println(imageByteArray);
        // Base64형태로 인코딩해주는 encoder 객체를 생성.
        Encoder encoder = Base64.getEncoder();
        // 위에서  ByteArray형태로 변환된 이미지를 Base64형태로 인코딩.
        byte[] baseIncodingBytes = encoder.encode(imageByteArray);
        // Base64형태로 인코딩된 이미지 파일을 String으로 바꿈.
        String base64 = new String(baseIncodingBytes);
        System.out.println(base64); // 잘 인코딩 됐는지 확인.
        // imgsrc라는 String 변수에 이미지의 총 src를 전부 작성. 이를 response.data를 읽어들인다면 그 자체로 이미지파일이다.
//		String imgsrc = "data:" + image.getContentType() + ";base64," + base64;
        // 이미지 파일의 src를 반환하면 끝.
//		System.out.println(imgsrc);

        int isokupdateProfile = userinfoService.updateProfile(userinfo);
        if(isokupdateProfile == 1) {
            System.out.println("Profile 업데이트 성공");
        }
        if (isokupdateProfile != 1) {
            System.out.println("Profile 업데이트 실패");
        }

        return base64;
    }
}
