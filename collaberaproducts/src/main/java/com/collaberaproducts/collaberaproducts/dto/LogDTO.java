package com.collaberaproducts.collaberaproducts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;

@AllArgsConstructor
@Data
public class LogDTO {
    private String message;
    private Date dateTime;
}
