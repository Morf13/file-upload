package com.fileloader.service;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="fileline")
public class FileLineModel {

  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private Long id;
  
  private String content;
 

  protected FileLineModel() {}

  public FileLineModel(String content) {
    this.content = content;
 
  }

public String getContent() {
	return content;
}

public void setContent(String content) {
	this.content = content;
}

}


