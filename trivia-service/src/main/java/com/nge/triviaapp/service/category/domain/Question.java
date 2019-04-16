package com.nge.triviaapp.service.category.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="question")
@Data
public class Question {

	@Id
	@SequenceGenerator(name="question_seq_gen", sequenceName="question_seq")
	@GeneratedValue(generator="question_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="question_text")
	private String text;
	
	@Column(name="answer")
	private String answer;
	
	@Column(name="value")
	private Integer value;
	
	@ManyToOne
	@JoinColumn(name="contestant_id", nullable=true)
	private Contestant answeredBy;
	
	@ManyToOne
	@JoinColumn(name="category_id")
	private Category category;
}