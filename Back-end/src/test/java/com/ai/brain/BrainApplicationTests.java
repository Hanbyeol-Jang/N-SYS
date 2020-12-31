package com.ai.brain;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.ai.brain.repository.UserinfoRepository;
import com.ai.brain.vo.Userinfo;

@SpringBootTest
class BrainApplicationTests {
	@Autowired
	private UserinfoRepository userRepository;
	
    @Test
    void contextLoads() {
    }
    
    @Test
    public void create(){
    	String str = "email";
//    	Optional<Userinfo> user = userRepository.findByUId(str);
//        Userinfo user = new Userinfo();
//        user.setUId("email");
//        user.setUName("jimin");
//        user.setUPw("password");
//
//        Userinfo newUser = userRepository.save(user);
//        System.out.println("newUser : " + user);
    }

}
