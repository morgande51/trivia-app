package com.nge.triviaapp.domain;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.JsonObject;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name="category")
@NamedQueries({
	@NamedQuery(name="Category.findByRound", query="select c from Category c where c.round.id = :roundId")
})
@JsonbVisibility(FieldVisibilityStrategy.class)
@Data
@EqualsAndHashCode(exclude="questions")
@ToString(exclude="questions")
public class Category implements Serializable {
	
	@Id
	@SequenceGenerator(name="category_seq_gen", sequenceName="category_seq")
	@GeneratedValue(generator="category_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="category_name")
	private String name;
	
	@ManyToOne
	@JoinColumn(name="category_round")
	@JsonbTransient
	private Round round;
	
	@OneToMany(mappedBy="category", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonbTransient
	private Set<Question> questions;
	
	public static Category createFrom(JsonObject jsonObj, Round round) {
		Category category = new Category();
		category.setRound(round);
		category.setName(jsonObj.getString("categoryName"));
		Set<Question> questions = jsonObj.getJsonArray("questions").stream().map(q -> Question.createFrom(q.asJsonObject(), category)).collect(Collectors.toSet());
		category.setQuestions(questions);
		return category;
	}
	
	private static final long serialVersionUID = 1L;
}