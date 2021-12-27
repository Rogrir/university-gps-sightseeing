package com.attraction.ranking;

import java.util.List;
import java.util.stream.Collectors;

import com.attraction.user.UserObjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RankingService {

  @Autowired
  UserObjectService userService;

  List<RankingEntity> loadSortedList() {
    return userService.getAllUsers().stream().sorted()
        .map(x -> new RankingEntity(x.getUsername(), x.getPoints(), x.getAvatar(), x.getRole())).collect(Collectors.toList());
  }
}