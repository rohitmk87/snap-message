package com.jcp.omni.snap.service;

import com.jcp.omni.snap.dto.ChatsDto;
import com.jcp.omni.snap.dao.MessageDao;
import com.jcp.omni.snap.dto.MessageDto;
import com.jcp.omni.snap.dto.RequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SnapService {

  @Autowired
  MessageDao messageDao;

  public String insert(RequestDto requestDto) {
    log.info("Getting account {} with all fields", requestDto);
    String id = messageDao.insertNewText(requestDto);

    return id;
  }

  public MessageDto getEntryById(String id) {
    log.info("Getting account {} with all fields", id);
    MessageDto messageDto = messageDao.queryById(id);

    return messageDto;
  }

  public List<ChatsDto> getEntryByUser(String username) {
    log.info("Getting account {} with all fields", username);
    List<ChatsDto> chatsDtoList = messageDao.queryByUser(username);

    return chatsDtoList;
  }

}
