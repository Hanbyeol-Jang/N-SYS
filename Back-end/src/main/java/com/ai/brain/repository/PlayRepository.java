package com.ai.brain.repository;

import com.ai.brain.vo.PlayVo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayRepository extends JpaRepository<PlayVo, Integer> {

    List<PlayVo> findByGaId(int gaId);

}
