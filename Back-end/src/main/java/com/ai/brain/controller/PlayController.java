package com.ai.brain.controller;

import com.ai.brain.repository.PlayRepository;
import com.ai.brain.service.PlayService;
import com.ai.brain.service.UserinfoService;
import com.ai.brain.vo.PlayVo;
import com.ai.brain.vo.Userinfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Column;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@RestController
@CrossOrigin(origins = {"*"}, maxAge = 6000)
@RequestMapping("Play")
public class PlayController {
    @Autowired
    private PlayService playService;
    @Autowired
    private UserinfoService userinfoService;

    @PostMapping("/snake")
    @ApiOperation(value = "뱀 게임 끝")
    public ResponseEntity<HashMap<String, Object>> snake(@RequestBody PlayVo playVo) {
        System.out.println("login Controller");
        try {
            HashMap<String, Object> map = new HashMap<>();
            // play 저장
            playService.theEnd(playVo);
            // rank 확인
            int rank = playService.rankCheck(playVo.getGaId(), playVo.getPlScore());
            Optional<Userinfo>userinfo = userinfoService.getUserinfo(playVo.getUiPk());

            String name = userinfo.get().getUiName();

            map.put("name", name);
            map.put("score", playVo.getPlScore());
            map.put("rank", rank);

            return new ResponseEntity<HashMap<String, Object>>(map, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

}
