package com.jcp.omni.snap.resource;

import com.jcp.omni.snap.dto.ChatsDto;
import com.jcp.omni.snap.dto.MessageDto;
import com.jcp.omni.snap.dto.RequestDto;
import com.jcp.omni.snap.service.SnapService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/")
public class SnapResource {

  private static final Logger logger = LoggerFactory.getLogger(SnapResource.class);

  @Autowired
  SnapService snapService;

  @RequestMapping(value = "/chat", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MessageDto> postMessage(@RequestBody RequestDto requestDto) {

    logger.info("Incoming post call to insert text = {} ", requestDto);
    String id = snapService.insert(requestDto);
    logger.info("got response = " + id);
    MessageDto messageDto = new MessageDto();
    messageDto.setId(id);

    return new ResponseEntity<MessageDto>(messageDto, HttpStatus.CREATED);
  }

  @RequestMapping(value = "/chat/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MessageDto> getMessage(@PathVariable String id) {

    logger.info("incoming get call = {} ", id);
    MessageDto messageDto = snapService.getEntryById(id);
    logger.info("got response = " + messageDto);

    return new ResponseEntity<MessageDto>(messageDto, HttpStatus.OK);
  }

  @RequestMapping(value = "/chats/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ChatsDto>> getUserTexts(@PathVariable String username) {

    logger.info("incoming get call = {} ", username);
    List<ChatsDto> chatsDtoList = snapService.getEntryByUser(username);
    logger.info("got response = " + chatsDtoList);

    return new ResponseEntity<List<ChatsDto>>(chatsDtoList, HttpStatus.OK);
  }
}
