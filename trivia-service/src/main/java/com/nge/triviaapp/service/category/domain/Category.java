package com.nge.triviaapp.service.category.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name="category")
@Data
@EqualsAndHashCode(exclude="questions")
public class Category {
	
	@Id
	@SequenceGenerator(name="category_seq_gen", sequenceName="category_seq")
	@GeneratedValue(generator="category_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="category_name")
	private String name;
	
	@ManyToOne
	@JoinColumn(name="category_round")
	private Round round;
	
	@OneToMany(mappedBy="category", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Question> questions;
}