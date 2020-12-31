package com.ai.brain.service;

import com.ai.brain.repository.GameRepository;
import com.ai.brain.vo.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    @Autowired
    private GameRepository gameRepository;

    public List<Game> selectAll(){
//        List<Game> list = gameRepository.findAll();
        return gameRepository.findAll();
    }

    public Game selectGame(int gId){
        return gameRepository.findById(gId).get();
    }
}
