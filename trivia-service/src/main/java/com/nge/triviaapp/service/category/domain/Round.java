package com.nge.triviaapp.service.category.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="round")
@Data
@EqualsAndHashCode(exclude="categories")
public class Round {
	
	@Id
	@SequenceGenerator(name="round_seq_gen", sequenceName="round_seq")
	@GeneratedValue(generator="round_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="round_name")
	private String name;
	
	@OneToMany(mappedBy="round", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Category> categories;
}