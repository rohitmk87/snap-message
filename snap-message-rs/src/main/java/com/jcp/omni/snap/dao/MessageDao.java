package com.jcp.omni.snap.dao;

import com.jcp.omni.snap.dto.ChatsDto;
import com.jcp.omni.snap.dto.MessageDto;
import com.jcp.omni.snap.dto.RequestDto;

import java.util.List;

/**
 * Dao interface
 */
public interface MessageDao {
  String insertNewText(RequestDto requestDto);

  MessageDto queryById(String id);

  List<ChatsDto> queryByUser(String userName);
}
