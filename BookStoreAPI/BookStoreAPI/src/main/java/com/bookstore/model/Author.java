package com.bookstore.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    String name;
    Date birthday;
}
