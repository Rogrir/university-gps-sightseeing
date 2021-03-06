package com.attraction.monument;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Value;

@Document("monuments")
@Value
public class Monument {

  @Id
  Integer id;

  String name;
  Integer priority;
  String description;
  String type;
  String photo;
  String city;
  String mapUrl;
  String longitude;
  String latitude;
}
