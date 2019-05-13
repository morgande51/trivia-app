package com.nge.triviaapp.domain;

import java.io.Serializable;

import javax.enterprise.util.AnnotationLiteral;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeAdapter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import static com.nge.triviaapp.security.TriviaSecurity.*;

import com.nge.triviaapp.security.SecuredByRoles;
import com.nge.triviaapp.security.SecuredProperty;

import lombok.Data;

@Entity
@Table(name="question")
@NamedQueries({
	@NamedQuery(name="Question.findByRoundCategory", query="select q from Question q where q.category.id = :categoryId and q.category.round.id = :roundId")	
})
@Data
//@JsonbVisibility(SecuredPropertyVisibilityStategy.class)
public class Question implements ActiveDomain, SecuredProperty, Serializable {

	@Id
	@SequenceGenerator(name="question_seq_gen", sequenceName="question_seq")
	@GeneratedValue(generator="question_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="question_text", nullable=false)
	private String text;
	
	@JsonbProperty
	@SecuredByRoles({HOST_ROLE, ADMIN_ROLE})
	@Column(name="answer", nullable=false)
	private String answer;
	
	@Column(name="value", nullable=false)
	private Integer value;
	
	@ManyToOne
	@JoinColumn(name="contestant_id")
	@JsonbTypeAdapter(SimpleContestantAdapter.class)
	private Contestant answeredBy;
	
	@ManyToOne
	@JoinColumn(name="category_id", nullable=false)
	@JsonbTypeAdapter(SimpleCategoryAdapter.class)
	private Category category;
	
	@Enumerated(EnumType.STRING)
	@Column(name="answerType")
	private QuestionAnswerType answerType;
	
	@SuppressWarnings("unchecked")
	@Override
	public <D> D as() {
		return (D) this;
	}
	
	public AnnotationLiteral<Active> getLiteral() {
		return new QuestionLiteral();
	}
	
	class QuestionLiteral extends ActiveActionLiteral {
	
		@Override
		public Class<? extends ActiveDomain> value() {
			return Question.class;
		}
		
		private static final long serialVersionUID = 1L;		
	}
	
	private static final long serialVersionUID = 1L;
}