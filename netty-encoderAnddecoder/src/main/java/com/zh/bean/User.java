package com.zh.bean;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
  private String name;
  private Date birthday;
  private Long age;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getBirthday() {
    return birthday;
  }

  public void setBirthday(Date birthday) {
    this.birthday = birthday;
  }

  public Long getAge() {
    return age;
  }

  public void setAge(Long age) {
    this.age = age;
  }

  @Override
  public String toString() {
    return "User{" +
      "name='" + name + '\'' +
      ", birthday=" + birthday +
      ", age=" + age +
      '}';
  }
}
