package com.jcp.omni.snap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageDto {
  private String id;
  private String username;
  private String text;
  private String expDate;
  private List<ChatsDto> chatsDtoList;
  private String expiration_date;
}
