package com.ai.brain.service;

import com.ai.brain.repository.UserinfoRepository;
import com.ai.brain.util.JwtTokenProvider;
import com.ai.brain.vo.UserIdPw;
import com.ai.brain.vo.Userinfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class UserinfoService {

    @Autowired
    private UserinfoRepository userinfoRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 회원 가입
    public Userinfo join(UserIdPw userIdPw) {
        System.out.println("join Service");

        // id, 닉네임 중복 체크
        boolean flag = checked(userIdPw);

        if (flag) {
            return null;
        } else {
            Userinfo userinfo = new Userinfo();
            userinfo.setUiId(userIdPw.getUId());
            userinfo.setUiPw(userIdPw.getUPw());
            userinfo.setUiName((userIdPw.getUName()));
            userinfo.setUiImage((userIdPw.getUImage()));
            userinfo.setUiImgtype((userIdPw.getUImgtype()));
            return userinfoRepository.save(userinfo);
        }
    }


    // pk 로 회원 정보 가져오기
    public Optional<Userinfo> getUserinfo(int uPk) {
        System.out.println("getUserinfo Service");
        return userinfoRepository.findById(uPk);
    }

    // 닉네임 변경하기
    public Userinfo updateId(Userinfo userinfo) {
        System.out.println("updateId Service");
        UserIdPw userIdPw = new UserIdPw();

        userIdPw.setUName(userinfo.getUiName());
        Userinfo change_userinfo_name = new Userinfo();
        change_userinfo_name = getUserinfo(userinfo.getUiId());
        // 닉네임 중복 체크
        boolean flag = checked(userIdPw);

        if (flag) {
            return null;
        } else {
            change_userinfo_name.setUiName(userinfo.getUiName());
            return userinfoRepository.save(change_userinfo_name);
        }
    }

    // pw 변경하기
    public Userinfo updatePw(Userinfo userinfo) {
        System.out.println("updatePw Service");
        Userinfo change_userinfo_pw = new Userinfo();
        change_userinfo_pw = getUserinfo(userinfo.getUiId());
        change_userinfo_pw.setUiPw(userinfo.getUiPw());
        return userinfoRepository.save(change_userinfo_pw);
    }

    // 회원 탈퇴
    public void deleteAccount(Userinfo userinfo) {
        System.out.println("deleteAccount Service");
        userinfo = getUserinfo(userinfo.getUiId());
        userinfoRepository.deleteById(userinfo.getUPk());
    }

    // 로그인
//    public boolean login(String loginId, String loginPw) {
//        System.out.println("login Service");
//        List<Userinfo> list = userinfoRepository.findAll();
//
//        // id 확인
//        boolean flag = false;
//        for (int i = 0; i < list.size(); i++) {
//            if (list.get(i).getUId().equals(loginId)) {
//                // pw 확인
//                if (list.get(i).getUPw().equals(loginPw)) {
//                    flag = true;
//                } else {
//                    flag = false;
//                }
//                break;
//            }
//        }
//
//        return flag;
//    }

    // id 중복 검사
    public boolean checked(UserIdPw userIdPw) {
        List<Userinfo> list = userinfoRepository.findAll();
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            // id 중복 검사
            if (list.get(i).getUiId().equals(userIdPw.getUId())) {
                flag = true;
                break;
            }

            // 닉네임 중복 검사
            if (list.get(i).getUiName().equals(userIdPw.getUName())) {
                flag = true;
                break;
            }
        }

        return flag;
    }

    public Userinfo getUserinfo(String loginId, String loginPw){
        List<Userinfo> list = userinfoRepository.findAll();
        Userinfo userinfo = new Userinfo();
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            // id, pw db 검사
            if (list.get(i).getUiId().equals(loginId)) {
                if (list.get(i).getUiPw().equals(loginPw)) {
                    flag = true;
                    userinfo.setUPk(list.get(i).getUPk());
                    userinfo.setUiName(list.get(i).getUiName());
                    userinfo.setUiId(list.get(i).getUiId());
                    userinfo.setUiPw(list.get(i).getUiPw());
                    userinfo.setUiImage(list.get(i).getUiImage());
                    userinfo.setUiImgtype(list.get(i).getUiImgtype());
                    break;
                }
            }
        }

        if (flag){
            return userinfo;
        }else {
            return null;
        }
    }

    // id, pw db 검사
    public boolean checkedIdPw(UserIdPw userIdPw) {
        List<Userinfo> list = userinfoRepository.findAll();

        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            // id, pw db 검사
            if (list.get(i).getUiId().equals(userIdPw.getUId())) {
                if (list.get(i).getUiPw().equals(userIdPw.getUPw())) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    public String createToken(Userinfo userinfo) {
        System.out.println("createToken Service");
        Optional<Userinfo> member = Optional.empty();
        try {
            member = userinfoRepository.findById(userinfo.getUPk());
        } catch (Exception e) {
            e.printStackTrace();
        }

        String token = "";
        if (!userinfo.getUiPw().equals(member.get().getUiPw())) {
        } else {
            token = jwtTokenProvider.createToken(userinfo);
        }
        return token;
    }

    // 로그아웃
    public void logout() {

    }

//    public List<Userinfo> findAll() {
//        List<Userinfo> list = userinfoRepository.findAll();
//        return list;
//    }

    // 아이디(이메일)로 회원정보 가져오기
    public Userinfo getUserinfo(String loginId){
        List<Userinfo> list = userinfoRepository.findAll();
        Userinfo userinfo = new Userinfo();
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            // id db 검사
            if (list.get(i).getUiId().equals(loginId)) {
                flag = true;
                userinfo.setUPk(list.get(i).getUPk());
                userinfo.setUiName(list.get(i).getUiName());
                userinfo.setUiId(list.get(i).getUiId());
                userinfo.setUiPw(list.get(i).getUiPw());
                userinfo.setUiImage(list.get(i).getUiImage());
                userinfo.setUiImgtype(list.get(i).getUiImgtype());
                break;
            }
        }
        if (flag){
            return userinfo;
        }else {
            return null;
        }
    }

    public int updateProfile(Userinfo userinfo){
        System.out.println("프로필을 업데이트 합니다.");
        Userinfo userinfo_update = new Userinfo();
        userinfo_update = getUserinfo(userinfo.getUiId());
        userinfo_update.setUiImage(userinfo.getUiImage());
        userinfo_update.setUiImgtype(userinfo.getUiImgtype());
        userinfoRepository.save(userinfo_update);
        return 1;
    }
}
