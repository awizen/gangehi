package com.awizen.gangehi.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

import lombok.Data;

@Data
@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
@SuppressWarnings("serial")
public abstract class AbstractEntity implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
}
