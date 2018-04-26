package com.jcp.omni.snap.dao.impl;

import com.jcp.omni.snap.dto.ChatsDto;
import com.jcp.omni.snap.dao.MessageDao;
import com.jcp.omni.snap.dto.MessageDto;
import com.jcp.omni.snap.dto.RequestDto;
import com.jcp.omni.snap.util.CassandraConnection;
import com.jcp.omni.snap.util.IdGeneration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Dao to connect to query Cassandra
 */
@Service
@Slf4j
public class MessageDaoImpl implements MessageDao {

  @Autowired
  CassandraConnection cassandraConnection;

  @Autowired
  IdGeneration idGeneration;

  @Value("${keyspace}")
  String keyspace;

  @Value("${host}")
  String cassNode1;

  /*
    Method insertNewText
    Parameters: RequestDto
    Output: String (id)
    Description: /chat
  */
  public String insertNewText(RequestDto requestDto) {
    List<ChatsDto> chatsDtoList = new ArrayList<ChatsDto>();
    String userName = requestDto.getUsername();
    String text = requestDto.getText();
    String ttl = Integer.toString(requestDto.getTimeout());
    String id = idGeneration.generateUniqueId();
    try {

      log.info("Incoming request to insert text for username = {} ", userName);

      com.datastax.driver.core.Session session = cassandraConnection.createConnection(cassNode1, keyspace);

      Select selectQuery = QueryBuilder.select().all().from(keyspace, "message_by_user");
      Select.Where selectWhere = selectQuery.where();
      Clause clause = QueryBuilder.eq("username", userName);
      selectWhere.and(clause);

      com.datastax.driver.core.ResultSet resultSet = session.execute(selectQuery);
      log.info("resultset = " + resultSet);
      List<String> idList = new ArrayList<String>();
      for (Row row : resultSet) {
        log.debug(" looping thru resultset - id= {}, username = {} ", row.getList("id", String.class),
            row.getString("username"));
        idList = row.getList("id", String.class);
      }

      List<String> newIdList = new ArrayList<String>();

      for (String textId : idList) {
        String str = "'";
        str = str + textId + "'";
        newIdList.add(str);
      }
      String str1 = "'" + id + "'";
      newIdList.add(str1);

      StringBuilder insertQuery1 = new StringBuilder("UPDATE ")
          .append("message_by_user ").append("set ").append("id=")
          .append(newIdList).append(" where ").append("username='").append(userName).append("'").append(";");

      String query1 = insertQuery1.toString();
      com.datastax.driver.core.ResultSet resultSet1 = session.execute(query1);

      StringBuilder insertQuery2 = new StringBuilder("INSERT INTO ")
          .append("messages ").append("(id, username, text, storage_type, ttl, timestamp) ")
          .append("VALUES ('").append(id).append("', '").append(userName).append("', '").append(text).append("', '")
          .append("HOT', '").append(ttl).append("', ").append("dateof(now())").append(");");

      String query2 = insertQuery2.toString();
      com.datastax.driver.core.ResultSet resultSet2 = session.execute(query2);


    } catch (Exception e) {
      log.info("Exception occurred = " + e);
    }
    return id;
  }

  /*
    Method queryById
    Parameters: id
    Output: MessageDto
    Description: /chat/{id}
  */
  public MessageDto queryById(String id) {
    MessageDto messageDto = new MessageDto();
    try {

      log.info("incoming request to query by Id = {} ", id);

      com.datastax.driver.core.Session session = cassandraConnection.createConnection(cassNode1, keyspace);

      messageDto = detailById(id, messageDto, keyspace, session);
      log.debug("message dto after resultset = " + messageDto);

    } catch (Exception e) {
      log.error("Exception Occurred = {} ", e);
    }
    return messageDto;
  }

  private MessageDto detailById(String id, MessageDto messageDto, String keyspace, com.datastax.driver.core.Session session) {

    com.datastax.driver.core.ResultSet results;
    Select selectQuery = QueryBuilder.select().all().from(keyspace, "messages");

    Select.Where selectWhere = selectQuery.where();
    Clause clause = QueryBuilder.eq("id", id);
    selectWhere.and(clause);

    results = session.execute(selectQuery);

    log.debug("resultset = " + results);

    for (Row row : results) {
      log.info(" looping thru resultset - id= {}, username = {}, text = {} ", row.getString("id"),
          row.getString("username"), row.getString("text"));
      messageDto.setUsername(row.getString("username"));
      messageDto.setText(row.getString("text"));

      String ttl = row.getString("ttl");
      Date textDate = row.getDate("timestamp");
      Date currentDate = new Date();
      long ttlLong = Long.parseLong(ttl) * 1000;
      long timeDiff = currentDate.getTime() - textDate.getTime();

      Date expirationDate = new Date(textDate.getTime() + ttlLong);
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
      String expiry = simpleDateFormat.format(expirationDate);
      messageDto.setExpiration_date(expiry);

      if (timeDiff > ttlLong) {
        //chnage Storage type
        StringBuilder updateQuery = new StringBuilder("UPDATE ")
            .append("messages ").append("set ").append("storage_type=")
            .append("'COLD'").append(" where ").append("id='").append(id).append("'").append(";");

        String query1 = updateQuery.toString();
        com.datastax.driver.core.ResultSet resultSet = session.execute(query1);

      }
    }
    return messageDto;
  }

  /*
    Method queryByUser
    Parameters: userName
    Output: List<ChatsDto>
    Description: /chats/{username}
  */
  public List<ChatsDto> queryByUser(String userName) {
    List<ChatsDto> chatsDtoList = new ArrayList<ChatsDto>();
    try {

      log.info("printing id in cassandraConnection = {} ", userName);

      com.datastax.driver.core.Session session = cassandraConnection.createConnection(cassNode1, keyspace);

      com.datastax.driver.core.ResultSet results;
      Select selectQuery = QueryBuilder.select().all().from(keyspace, "message_by_user");
      Select.Where selectWhere = selectQuery.where();
      Clause clause = QueryBuilder.eq("username", userName);
      selectWhere.and(clause);

      results = session.execute(selectQuery);
      log.info("resultset = " + results);
      List<String> idList = null;
      List<String> storageIdList = null;
      String ttl;
      for (Row row : results) {
        log.info(" looping thru resultset - id= {}, username = {} ", row.getList("id", String.class),
            row.getString("username"));
        idList = row.getList("id", String.class);
      }
      MessageDto messageDto = new MessageDto();
      for (String id : idList) {
        messageDto = detailByIdHot(id, messageDto, keyspace, session);
        if (!StringUtils.isEmpty(messageDto.getId())) {
          ChatsDto chatsDto = new ChatsDto();
          chatsDto.setId(messageDto.getId());
          chatsDto.setText(messageDto.getText());
          chatsDtoList.add(chatsDto);

          //chnage Storage type
          StringBuilder updateQuery = new StringBuilder("UPDATE ")
              .append("messages ").append("set ").append("storage_type=")
              .append("'COLD'").append(" where ").append("id='").append(id).append("'").append(";");

          String query1 = updateQuery.toString();
          com.datastax.driver.core.ResultSet resultSet = session.execute(query1);
        }
      }
      log.info("message dto after resultset = " + chatsDtoList);

    } catch (Exception e) {
      log.error("Exception Occurred = {} ", e);
    }
    return chatsDtoList;
  }


  private MessageDto detailByIdHot(String id, MessageDto messageDto, String keyspace, com.datastax.driver.core.Session session) {

    com.datastax.driver.core.ResultSet results;
    Select selectQuery = QueryBuilder.select().all().from(keyspace, "messages");

    Select.Where selectWhere = selectQuery.where();
    Clause clause = QueryBuilder.eq("id", id);
    selectWhere.and(clause);

    results = session.execute(selectQuery);

    log.info("resultset = " + results);

    for (Row row : results) {
      log.info(" looping thru resultset - id= {}, username = {}, text = {} ", row.getString("id"),
          row.getString("username"), row.getString("text"));
      String ttl = row.getString("ttl");
      Date textDate = row.getDate("timestamp");
      Date currentDate = new Date();
      long ttlLong = Long.parseLong(ttl) * 1000;
      long timeDiff = currentDate.getTime() - textDate.getTime();

      if (row.getString("storage_type").equalsIgnoreCase("HOT")
          && (timeDiff < ttlLong)) {
        messageDto.setId(row.getString("id"));
        messageDto.setText(row.getString("text"));
      }

    }
    return messageDto;
  }

}
