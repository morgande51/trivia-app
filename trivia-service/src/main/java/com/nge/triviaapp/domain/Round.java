package com.nge.triviaapp.domain;

import java.io.Serializable;
import java.util.Set;

import javax.enterprise.util.AnnotationLiteral;
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
public class Round implements Active, Serializable {
	
	@Id
	@SequenceGenerator(name="round_seq_gen", sequenceName="round_seq")
	@GeneratedValue(generator="round_seq_gen", strategy=GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name="round_name")
	private String name;
	
	@OneToMany(mappedBy="round", cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Category> categories;
	
	@SuppressWarnings("unchecked")
	@Override
	public <D> D as() {
		return (D) this;
	}
	
	@Override
	public AnnotationLiteral<ActiveUpdate> getLiteral() {
		return new RoundLiteral();
	}
	
	class RoundLiteral extends AnnotationLiteral<ActiveUpdate> implements ActiveUpdate {
		
		@Override
		public Class<? extends Active> value() {
			return Round.class;
		}
		
		private static final long serialVersionUID = 1L;
	}
	
	private static final long serialVersionUID = 1L;
}