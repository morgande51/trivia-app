package com.nge.triviaapp.domain;

import java.io.Serializable;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.util.AnnotationLiteral;
import javax.json.bind.annotation.JsonbTransient;
import javax.json.bind.annotation.JsonbVisibility;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;

@Entity
@Table(name="contestant")
@NamedQueries({
	@NamedQuery(name="Contestant.findFromEmail", query="select c from Contestant c where c.email = :email"),
	@NamedQuery(name="Contestant.findAll", query="select c from Contestant c")
})
@JsonbVisibility(FieldVisibilityStrategy.class)
@Data
@EqualsAndHashCode(exclude= {"totalScore", "fullName", "passwordHash", "scores"})
@ToString(exclude="scores")
@Log
public class Contestant implements Principal, ActiveDomain, Serializable {
	
	@Id
	@SequenceGenerator(name="contestant_seq_gen", sequenceName="contestant_seq")
	@GeneratedValue(generator="contestant_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="email")
	private String email;
	
	@JsonbTransient
	@Column(name="password", updatable=false)
	private String passwordHash;
	
	@OneToMany(mappedBy="contestant", cascade=CascadeType.ALL, orphanRemoval=true)
	@JsonbTransient
	private Set<ContestantScore> scores;
	
	@Transient
	@Setter(AccessLevel.NONE)
	private int totalScore;
	
	@Transient
	@Setter(AccessLevel.NONE)
	private String fullName;
	
	@PostLoad
	public void init() {
		fullName = firstName + " " + lastName;
		updateTotalScore();
	}
	
	public void updateScore(Question question) {
		int value = question.getValue();
		ContestantScore scoreData = findScoreFor(question);
	
		if (scoreData == null) {
			scoreData = ContestantScore.newInstance(question.getCategory().getRound(), this);
			if (scores == null) {
				scores = new HashSet<ContestantScore>();
			}
			scores.add(scoreData);
			log.info("Contestant score: " + scoreData);
		}
		int score = scoreData.getScore();
		
		if (question.getAnswerType() == QuestionAnswerType.CORRECT) {
			question.setAnsweredBy(this);
			score += value;
		}
		else if (question.getAnswerType() == QuestionAnswerType.INCORRECT) {
			question.setAnsweredBy(this);
			score -= value;
		}
		
		scoreData.setScore(score);
		updateTotalScore();
	}
	
	protected ContestantScore findScoreFor(Question question) {
		Round targetRound = question.getCategory().getRound();
		ContestantScore score = scores.stream()
				.filter(s -> s.getRound().equals(targetRound) && s.getContestant().equals(this))
				.findAny()
				.orElse(null);
		return score;
	}
	
	protected void updateTotalScore() {
		totalScore = scores.stream().mapToInt(ContestantScore::getScore).sum();
	}
	
	@Override
	public String getName() {
		return email;
	}
	
	public static <T extends Principal> Contestant as(T user) {
		return (Contestant) user;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <D> D as() {
		return (D) this;
	}
	
	@Override
	public AnnotationLiteral<Active> getLiteral() {
		return new ContestantLiternal();
	}
	
	class ContestantLiternal extends ActiveActionLiteral {
		
		@Override
		public Class<? extends ActiveDomain> value() {
			return Contestant.class;
		}
		
		private static final long serialVersionUID = 1L;
	}
	
	private static final long serialVersionUID = 1L;
}