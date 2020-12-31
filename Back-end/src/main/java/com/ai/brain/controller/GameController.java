package com.ai.brain.controller;


import com.ai.brain.service.GameService;
import com.ai.brain.vo.Game;
import com.ai.brain.vo.Userinfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RequestMapping(value="game")
public class GameController {

    @Autowired
    private GameService gameSerivce;

    @GetMapping("/list")
    @ApiOperation(value = "모든 게임 정보 가져오기")
    public ResponseEntity<List<Game>> getGames(){
        List<Game> list = gameSerivce.selectAll();
        return new ResponseEntity<List<Game>>(list, HttpStatus.OK);
    }

    @GetMapping("/{gId}")
    @ApiOperation(value = "해당 번호의 게임 정보 가져오기")
    public ResponseEntity<Game> getGame(@PathVariable int gId){
        Game game = gameSerivce.selectGame(gId);
        return new ResponseEntity<Game>(game,HttpStatus.OK);
    }

}
